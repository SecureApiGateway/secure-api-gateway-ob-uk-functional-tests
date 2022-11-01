package com.forgerock.uk.openbanking.tests.functional.payment.file.payments.consents.api.v3_1_8

import assertk.assertThat
import assertk.assertions.*
import com.forgerock.securebanking.framework.conditions.Status
import com.forgerock.securebanking.framework.configuration.psu
import com.forgerock.securebanking.framework.data.AccessToken
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.framework.errors.*
import com.forgerock.uk.openbanking.support.discovery.getPaymentsApiLinks
import com.forgerock.uk.openbanking.support.payment.*
import com.github.kittinunf.fuel.core.FuelError
import org.assertj.core.api.Assertions
import uk.org.openbanking.datamodel.payment.OBWriteFileConsent3
import uk.org.openbanking.datamodel.payment.OBWriteFileConsentResponse4
import java.math.BigDecimal
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

class CreateFilePaymentsConsents(val version: OBVersion, val tppResource: CreateTppCallback.TppResource) {

    private val paymentLinks = getPaymentsApiLinks(version)
    private val paymentApiClient = tppResource.tpp.paymentApiClient

    fun createFilePaymentsConsentsTest() {
        // Given
        val fileContent = PaymentFactory.getXMLFileAsString()
        val consentRequest = PaymentFactory.createOBWriteFileConsent3WithFileInfo(
            fileContent,
            PaymentFileType.UK_OBIE_PAIN_001_001_008.type
        )
        val consent = createFilePaymentConsent(consentRequest)

        // Then
        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
    }

    fun submitXMLFileTest() {
        // Given
        val fileContent = PaymentFactory.getXMLFileAsString()
        val consentRequest = PaymentFactory.createOBWriteFileConsent3WithFileInfo(
            fileContent,
            PaymentFileType.UK_OBIE_PAIN_001_001_008.type
        )

        // when
        val consent = sendSubmitFileRequest(consentRequest, fileContent)

        // Then
        assertThat(consent).isNotNull()
        assertThat(consent).isTrue()
    }

    fun submitJSONFileTest() {
        // Given
        val fileContent = PaymentFactory.getJSONFileAsString()
        val fileJson = JsonFilePayment.fromJson(fileContent)

        val controlSum = fileJson.data.domesticPayments.stream().map(DomesticPayment::instructedAmount)
            .map(InstructedAmount::amount)
            .map(String::toBigDecimal)
            .reduce(BigDecimal.ZERO, BigDecimal::add).toPlainString()

        val numberOfPayments = fileJson.data.domesticPayments.size
        val fileHash = PaymentFactory.computeSHA256FullHash(fileContent)

        val consentRequest = PaymentFactory.createJsonOBWriteFileConsent3WithFileInfo(
            fileHash,
            controlSum,
            numberOfPayments.toString(),
            PaymentFileType.UK_OBIE_PAYMENT_INITIATION_V3_1.type
        )

        // when
        val consent = sendSubmitJsonFileRequest(consentRequest, fileContent)

        // Then
        assertThat(consent).isNotNull()
        assertThat(consent).isTrue()
    }

    fun createFilePaymentsConsents_mandatoryFields() {
        // Given
        val fileContent = PaymentFactory.getXMLFileAsString()
        val consentRequest = PaymentFactory.createOBWriteFileConsent3WithMandatoryFieldsAndFileInfo(
            fileContent,
            PaymentFileType.UK_OBIE_PAIN_001_001_008.type
        )

        // when
        val consent = createFilePaymentConsent(consentRequest)

        // Then
        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
    }

    fun shouldCreateFilePaymentsConsents_throwsSendInvalidFormatDetachedJwsTest() {
        // Given
        val fileContent = PaymentFactory.getXMLFileAsString()
        val consentRequest = PaymentFactory.createOBWriteFileConsent3WithFileInfo(
            fileContent,
            PaymentFileType.UK_OBIE_PAIN_001_001_008.type
        )

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            buildCreateConsentRequest(consentRequest).configureJwsSignatureProducer(BadJwsSignatureProducer())
                .sendRequest()
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(INVALID_FORMAT_DETACHED_JWS_ERROR)
    }

    fun shouldCreateFilePaymentsConsents_throwsNoDetachedJwsTest() {
        // Given
        val fileContent = PaymentFactory.getXMLFileAsString()
        val consentRequest = PaymentFactory.createOBWriteFileConsent3WithFileInfo(
            fileContent,
            PaymentFileType.UK_OBIE_PAIN_001_001_008.type
        )

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            buildCreateConsentRequest(consentRequest).configureJwsSignatureProducer(null).sendRequest()
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(NO_DETACHED_JWS)
    }

    fun shouldCreateFilePaymentsConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJwsTest() {
        // Given
        val fileContent = PaymentFactory.getXMLFileAsString()
        val consentRequest = PaymentFactory.createOBWriteFileConsent3WithFileInfo(
            fileContent,
            PaymentFileType.UK_OBIE_PAIN_001_001_008.type
        )

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            buildCreateConsentRequest(consentRequest).configureJwsSignatureProducer(
                DefaultJwsSignatureProducer(
                    tppResource.tpp,
                    false
                )
            ).sendRequest()
        }

        // Then
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
    }

    fun shouldCreateFilePaymentsConsents_throwsSendInvalidKidDetachedJwsTest() {
        // Given
        val fileContent = PaymentFactory.getXMLFileAsString()
        val consentRequest = PaymentFactory.createOBWriteFileConsent3WithFileInfo(
            fileContent,
            PaymentFileType.UK_OBIE_PAIN_001_001_008.type
        )

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            buildCreateConsentRequest(consentRequest).configureJwsSignatureProducer(
                InvalidKidJwsSignatureProducer(
                    tppResource.tpp
                )
            ).sendRequest()
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
    }

    fun shouldCreateFilePaymentsConsents_throwsRejectedConsentTest() {
        // Given
        val fileContent = PaymentFactory.getXMLFileAsString()
        val consentRequest = PaymentFactory.createOBWriteFileConsent3WithFileInfo(
            fileContent,
            PaymentFileType.UK_OBIE_PAIN_001_001_008.type
        )

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            createFilePaymentConsentAndReject(
                consentRequest
            )
        }

        // Then
        assertThat(exception.message.toString()).contains(CONSENT_NOT_AUTHORISED)
    }

    fun createFilePaymentConsent(consentRequest: OBWriteFileConsent3): OBWriteFileConsentResponse4 {
        return buildCreateConsentRequest(consentRequest).sendRequest()
    }

    private fun buildCreateConsentRequest(
        consent: OBWriteFileConsent3
    ) = paymentApiClient.newPostRequestBuilder(
        paymentLinks.CreateFilePaymentConsent,
        tppResource.tpp.getClientCredentialsAccessToken(defaultPaymentScopesForAccessToken),
        consent
    )

    private fun sendSubmitFileRequest(consentRequest: OBWriteFileConsent3, fileContent: String): Boolean {
        val consent = createFilePaymentConsent(consentRequest)
        return buildSubmitFileRequest(fileContent, consent.data.consentId).sendFileRequest()
    }

    private fun buildSubmitFileRequest(
        consent: String,
        consentId: @NotNull @Size(max = 128, min = 1) String
    ) = paymentApiClient.newFilePostRequestBuilder(
        PaymentFactory.urlWithFilePaymentSubmitFileId(paymentLinks.CreateFilePaymentFile, consentId),
        tppResource.tpp.getClientCredentialsAccessToken(defaultPaymentScopesForAccessToken),
        consent
    )

    private fun sendSubmitJsonFileRequest(consentRequest: OBWriteFileConsent3, fileContent: String): Boolean {
        val consent = createFilePaymentConsent(consentRequest)
        return buildSubmitJsonFileRequest(fileContent, consent.data.consentId).sendJsonFileRequest()
    }

    private fun buildSubmitJsonFileRequest(
        consent: String,
        consentId: @NotNull @Size(max = 128, min = 1) String
    ) = paymentApiClient.newJsonFilePostRequestBuilder(
        PaymentFactory.urlWithFilePaymentSubmitFileId(paymentLinks.CreateFilePaymentFile, consentId),
        tppResource.tpp.getClientCredentialsAccessToken(defaultPaymentScopesForAccessToken),
        consent
    )

    fun createFilePaymentConsentAndAuthorize(consentRequest: OBWriteFileConsent3): Pair<OBWriteFileConsentResponse4, AccessToken> {
        val consent = createFilePaymentConsent(consentRequest)
        val accessTokenAuthorizationCode = PaymentAS().authorizeConsent(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        return consent to accessTokenAuthorizationCode
    }

    private fun createFilePaymentConsentAndReject(consentRequest: OBWriteFileConsent3): Pair<OBWriteFileConsentResponse4, AccessToken> {
        val consent = createFilePaymentConsent(consentRequest)
        val accessTokenAuthorizationCode = PaymentAS().authorizeConsent(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp,
            "Rejected"
        )
        return consent to accessTokenAuthorizationCode
    }

    fun getPatchedConsent(consent: OBWriteFileConsentResponse4): OBWriteFileConsentResponse4 {
        val patchedConsent = paymentApiClient.getConsent<OBWriteFileConsentResponse4>(
            paymentLinks.GetFilePaymentConsent,
            consent.data.consentId,
            tppResource.tpp.getClientCredentialsAccessToken(defaultPaymentScopesForAccessToken)
        )
        assertThat(patchedConsent).isNotNull()
        assertThat(patchedConsent.data).isNotNull()
        assertThat(patchedConsent.data.consentId).isNotEmpty()
        Assertions.assertThat(patchedConsent.data.status.toString()).`is`(Status.consentCondition)
        return patchedConsent
    }

}