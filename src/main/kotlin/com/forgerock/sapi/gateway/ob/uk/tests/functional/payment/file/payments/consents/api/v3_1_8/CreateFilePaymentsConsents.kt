package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.file.payments.consents.api.v3_1_8

import assertk.assertThat
import assertk.assertions.*
import com.forgerock.sapi.gateway.framework.conditions.Status
import com.forgerock.sapi.gateway.framework.configuration.psu
import com.forgerock.sapi.gateway.framework.data.AccessToken
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.support.discovery.getPaymentsApiLinks
import com.forgerock.sapi.gateway.ob.uk.support.payment.*
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.github.kittinunf.fuel.core.FuelError
import org.apache.http.entity.ContentType
import org.assertj.core.api.Assertions
import uk.org.openbanking.datamodel.payment.OBWriteDomestic2DataInitiationDebtorAccount
import uk.org.openbanking.datamodel.payment.OBWriteFileConsent3
import uk.org.openbanking.datamodel.payment.OBWriteFileConsentResponse4
import java.math.BigDecimal
import java.util.*

class CreateFilePaymentsConsents(val version: OBVersion, val tppResource: CreateTppCallback.TppResource) {

    private val paymentLinks = getPaymentsApiLinks(version)
    private val paymentApiClient = tppResource.tpp.paymentApiClient

    fun createFilePaymentsConsentsTest() {
        // Given
        val fileContent = PaymentFactory.getFileAsString(PaymentFactory.FilePaths.XML_FILE_PATH)
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

    fun createFilePaymentsConsentsWithDebtorAccountTest() {
        // Given
        val fileContent = PaymentFactory.getFileAsString(PaymentFactory.FilePaths.XML_FILE_PATH)
        val consentRequest = PaymentFactory.createOBWriteFileConsent3WithFileInfo(
            fileContent,
            PaymentFileType.UK_OBIE_PAIN_001_001_008.type
        )
        // optional debtor account
        val debtorAccount = PsuData().getDebtorAccount()
        consentRequest.data.initiation.debtorAccount(
            OBWriteDomestic2DataInitiationDebtorAccount()
                .identification(debtorAccount?.Identification)
                .name(debtorAccount?.Name)
                .schemeName(debtorAccount?.SchemeName)
                .secondaryIdentification(debtorAccount?.SecondaryIdentification)
        )
        val consent = createFilePaymentConsent(consentRequest)

        // Then
        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
    }

    fun createFilePaymentsConsents_SameIdempotencyKeyMultipleRequestTest() {
        // Given
        val fileContent = PaymentFactory.getFileAsString(PaymentFactory.FilePaths.XML_FILE_PATH)
        val consentRequest = PaymentFactory.createOBWriteFileConsent3WithFileInfo(
            fileContent,
            PaymentFileType.UK_OBIE_PAIN_001_001_008.type
        )
        val idempotencyKey = UUID.randomUUID().toString()
        // when
        // first request
        val consentResponse1 = paymentApiClient.newPostRequestBuilder(
            paymentLinks.CreateFilePaymentConsent,
            tppResource.tpp.getClientCredentialsAccessToken(defaultPaymentScopesForAccessToken),
            consentRequest
        ).addIdempotencyKeyHeader(idempotencyKey).sendRequest<OBWriteFileConsentResponse4>()
        // second request with the same idempotencyKey
        val consentResponse2 = paymentApiClient.newPostRequestBuilder(
            paymentLinks.CreateFilePaymentConsent,
            tppResource.tpp.getClientCredentialsAccessToken(defaultPaymentScopesForAccessToken),
            consentRequest
        ).addIdempotencyKeyHeader(idempotencyKey).sendRequest<OBWriteFileConsentResponse4>()

        // Then
        assertThat(consentResponse1).isNotNull()
        assertThat(consentResponse1.data).isNotNull()
        assertThat(consentResponse1.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse1.data.status.toString()).`is`(Status.consentCondition)

        assertThat(consentResponse2).isNotNull()
        assertThat(consentResponse2.data).isNotNull()
        assertThat(consentResponse2.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse2.data.status.toString()).`is`(Status.consentCondition)

        assertThat(consentResponse1.data.consentId).isEqualTo(consentResponse2.data.consentId)
    }

    fun createFilePaymentConsents_NoIdempotencyKey_throwsBadRequestTest() {
        // Given
        val fileContent = PaymentFactory.getFileAsString(PaymentFactory.FilePaths.XML_FILE_PATH)
        val consentRequest = PaymentFactory.createOBWriteFileConsent3WithFileInfo(
            fileContent,
            PaymentFileType.UK_OBIE_PAIN_001_001_008.type
        )

        // when
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            paymentApiClient.newPostRequestBuilder(
                paymentLinks.CreateFilePaymentConsent,
                tppResource.tpp.getClientCredentialsAccessToken(defaultPaymentScopesForAccessToken),
                consentRequest
            ).deleteIdempotencyKeyHeader().sendRequest<OBWriteFileConsentResponse4>()
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat((exception.cause as FuelError).response.body()).isNotNull()
        assertThat(exception.message.toString()).contains("\"Errors\":[{\"ErrorCode\":\"UK.OBIE.Header.Missing\",\"Message\":\"Required request header 'x-idempotency-key' for method parameter type String is not present")
    }

    fun submitFile_SameIdempotencyKeyMultipleRequestTest() {
        // Given
        val idempotencyKey = UUID.randomUUID().toString()
        val fileContent = PaymentFactory.getFileAsString(PaymentFactory.FilePaths.XML_FILE_PATH)
        val consentRequest = PaymentFactory.createOBWriteFileConsent3WithFileInfo(
            fileContent,
            PaymentFileType.UK_OBIE_PAIN_001_001_008.type
        )

        // when
        val consent = createFilePaymentConsent(consentRequest)
        // first request
        val response1 = paymentApiClient.newFilePostRequestBuilder(
            PaymentFactory.urlWithFilePaymentSubmitFileId(paymentLinks.CreateFilePaymentFile, consent.data.consentId),
            tppResource.tpp.getClientCredentialsAccessToken(defaultPaymentScopesForAccessToken),
            fileContent,
            ContentType.TEXT_XML.mimeType
        ).addIdempotencyKeyHeader(idempotencyKey).sendFileRequest(ContentType.TEXT_XML.mimeType)

        // second request with the same idempotencyKey
        val response2 = paymentApiClient.newFilePostRequestBuilder(
            PaymentFactory.urlWithFilePaymentSubmitFileId(paymentLinks.CreateFilePaymentFile, consent.data.consentId),
            tppResource.tpp.getClientCredentialsAccessToken(defaultPaymentScopesForAccessToken),
            fileContent,
            ContentType.TEXT_XML.mimeType
        ).addIdempotencyKeyHeader(idempotencyKey).sendFileRequest(ContentType.TEXT_XML.mimeType)

        // Then
        assertThat(response1).isNotNull()
        assertThat(response2).isNotNull()
        assertThat(response1).isEqualTo(response2)
    }

    fun submitFile_NoIdempotencyKey_throwsBadRequestTest() {
        // Given
        val fileContent = PaymentFactory.getFileAsString(PaymentFactory.FilePaths.XML_FILE_PATH)
        val consentRequest = PaymentFactory.createOBWriteFileConsent3WithFileInfo(
            fileContent,
            PaymentFileType.UK_OBIE_PAIN_001_001_008.type
        )

        // when
        val consent = createFilePaymentConsent(consentRequest)
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            paymentApiClient.newFilePostRequestBuilder(
                PaymentFactory.urlWithFilePaymentSubmitFileId(
                    paymentLinks.CreateFilePaymentFile,
                    consent.data.consentId
                ),
                tppResource.tpp.getClientCredentialsAccessToken(defaultPaymentScopesForAccessToken),
                consent,
                ContentType.TEXT_XML.mimeType
            ).deleteIdempotencyKeyHeader().sendRequest<Boolean>()
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat((exception.cause as FuelError).response.body()).isNotNull()
        assertThat(exception.message.toString()).contains("\"Errors\":[{\"ErrorCode\":\"UK.OBIE.Header.Missing\",\"Message\":\"Required request header 'x-idempotency-key' for method parameter type String is not present")
    }

    fun submitXMLFileTest() {
        // Given
        val fileContent = PaymentFactory.getFileAsString(PaymentFactory.FilePaths.XML_FILE_PATH)
        val consentRequest = PaymentFactory.createOBWriteFileConsent3WithFileInfo(
            fileContent,
            PaymentFileType.UK_OBIE_PAIN_001_001_008.type
        )

        // when
        val consent = sendSubmitFileRequest(consentRequest, fileContent, ContentType.TEXT_XML.mimeType)

        // Then
        assertThat(consent).isNotNull()
    }

    fun submitJSONFileTest() {
        // Given
        val fileContent = PaymentFactory.getFileAsString(PaymentFactory.FilePaths.JSON_FILE_PATH)
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
        val consent = sendSubmitFileRequest(consentRequest, fileContent, ContentType.APPLICATION_JSON.mimeType)

        // Then
        assertThat(consent).isNotNull()
    }

    fun createFilePaymentsConsents_mandatoryFields() {
        // Given
        val fileContent = PaymentFactory.getFileAsString(PaymentFactory.FilePaths.XML_FILE_PATH)
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
        val fileContent = PaymentFactory.getFileAsString(PaymentFactory.FilePaths.XML_FILE_PATH)
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
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.INVALID_FORMAT_DETACHED_JWS_ERROR)
    }

    fun shouldCreateFilePaymentsConsents_throwsNoDetachedJwsTest() {
        // Given
        val fileContent = PaymentFactory.getFileAsString(PaymentFactory.FilePaths.XML_FILE_PATH)
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
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.NO_DETACHED_JWS)
    }

    fun shouldCreateFilePaymentsConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJwsTest() {
        // Given
        val fileContent = PaymentFactory.getFileAsString(PaymentFactory.FilePaths.XML_FILE_PATH)
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
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
    }

    fun shouldCreateFilePaymentsConsents_throwsSendInvalidKidDetachedJwsTest() {
        // Given
        val fileContent = PaymentFactory.getFileAsString(PaymentFactory.FilePaths.XML_FILE_PATH)
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
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
    }

    fun shouldCreateFilePaymentsConsents_throwsRejectedConsentTest() {
        // Given
        val fileContent = PaymentFactory.getFileAsString(PaymentFactory.FilePaths.XML_FILE_PATH)
        val fileType = PaymentFileType.UK_OBIE_PAIN_001_001_008
        val consentRequest = PaymentFactory.createOBWriteFileConsent3WithFileInfo(
            fileContent,
            fileType.type
        )

        val consentResponse = sendSubmitFileRequest(consentRequest, fileContent, fileType.mediaType)

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            PaymentAS().authorizeConsent(
                consentResponse.data.consentId,
                tppResource.tpp.registrationResponse,
                psu,
                tppResource.tpp,
                "Rejected"
            )
        }

        // Then
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.CONSENT_NOT_AUTHORISED)
    }

    fun failToCreateConsentForUnsupportedFileType() {
        val fileContent = PaymentFactory.getFileAsString(PaymentFactory.FilePaths.XML_FILE_PATH)
        val fileType = PaymentFileType.UK_OBIE_PAIN_001_001_008
        val consentRequest = PaymentFactory.createOBWriteFileConsent3WithFileInfo(
            fileContent,
            "FR_FORMAT_123"
        )

        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            sendSubmitFileRequest(consentRequest, fileContent, fileType.mediaType)
        }
        assertThat(exception.message.toString()).contains("\"ErrorCode\":\"OBRI.Request.File.Payment.FileType.Not.Supported\"")
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

    private fun sendSubmitFileRequest(
        consentRequest: OBWriteFileConsent3,
        fileContent: String,
        contentType: String
    ): OBWriteFileConsentResponse4 {
        val consent = createFilePaymentConsent(consentRequest)
        buildSubmitFileRequest(fileContent, consent.data.consentId, contentType).sendFileRequest(contentType)
        return consent
    }

    private fun buildSubmitFileRequest(
        fileContent: String,
        consentId: String,
        contentType: String
    ) = paymentApiClient.newFilePostRequestBuilder(
        PaymentFactory.urlWithFilePaymentSubmitFileId(paymentLinks.CreateFilePaymentFile, consentId),
        tppResource.tpp.getClientCredentialsAccessToken(defaultPaymentScopesForAccessToken),
        fileContent,
        contentType
    )

    fun createFilePaymentConsentAndAuthorize(consentRequest: OBWriteFileConsent3, fileContent: String,
                                             contentType: String): Pair<OBWriteFileConsentResponse4, AccessToken> {

        val consentResponse = sendSubmitFileRequest(consentRequest, fileContent, contentType)
        val accessTokenAuthorizationCode = PaymentAS().authorizeConsent(
            consentResponse.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        return consentResponse to accessTokenAuthorizationCode
    }

}