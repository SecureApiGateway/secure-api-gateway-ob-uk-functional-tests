package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.file.payments.api.v3_1_8

import assertk.assertThat
import assertk.assertions.*
import com.forgerock.sapi.gateway.framework.conditions.Status
import com.forgerock.sapi.gateway.framework.data.AccessToken
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.http.fuel.defaultMapper
import com.forgerock.sapi.gateway.ob.uk.support.discovery.getPaymentsApiLinks
import com.forgerock.sapi.gateway.ob.uk.support.payment.*
import com.forgerock.sapi.gateway.ob.uk.support.payment.PaymentFactory.Companion.mapOBWriteFileConsentResponse4DataInitiationToOBWriteFile2DataInitiation
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.file.payments.consents.api.v3_1_8.CreateFilePaymentsConsents
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.github.kittinunf.fuel.core.FuelError
import org.assertj.core.api.Assertions
import uk.org.openbanking.datamodel.payment.*
import java.math.BigDecimal

class CreateFilePayment(val version: OBVersion, val tppResource: CreateTppCallback.TppResource) {

    private val createFilePaymentConsentsApi = CreateFilePaymentsConsents(version, tppResource)
    private val paymentApiClient = tppResource.tpp.paymentApiClient
    private val paymentLinks = getPaymentsApiLinks(version)
    private val createPaymentUrl = paymentLinks.CreateFilePayment

    fun createFilePaymentTest() {
        // Given
        val fileContent = PaymentFactory.getFileAsString(PaymentFactory.FilePaths.XML_FILE_PATH)

        val consentRequest = PaymentFactory.createOBWriteFileConsent3WithFileInfo(
                fileContent,
                PaymentFileType.UK_OBIE_PAIN_001_001_008.type
        )
        // When
        val result = submitFilePayment(consentRequest)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.charges).isNotNull().isNotEmpty()
        assertThat(result.data.consentId).isNotEmpty()
        assertThat(result.links.self.toString()).isEqualTo(createPaymentUrl + "/" + result.data.filePaymentId)
    }

    fun createFilePayment_mandatoryFieldsTest() {
        // Given
        val fileContent = PaymentFactory.getFileAsString(PaymentFactory.FilePaths.XML_FILE_PATH)

        val consentRequest = PaymentFactory.createOBWriteFileConsent3WithMandatoryFieldsAndFileInfo(
                fileContent,
                PaymentFileType.UK_OBIE_PAIN_001_001_008.type
        )
        // When
        val result = submitFilePayment(consentRequest)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.consentId).isNotEmpty()
        assertThat(result.data.creationDateTime).isNotNull()
        assertThat(result.data.filePaymentId).isNotNull().isNotEmpty()
        assertThat(result.data.status).isNotNull()
        assertThat(result.data.statusUpdateDateTime).isNotNull()
        assertThat(result.data.initiation.fileHash).isNotNull().isNotEmpty()
        assertThat(result.data.initiation.fileType).isNotNull().isNotEmpty()
        assertThat(result.data.initiation.fileType).isIn("UK.OBIE.PaymentInitiation.3.1", "UK.OBIE.pain.001.001.08")
    }

    fun shouldCreateFilePayment_throwsFilePaymentAlreadyExistsTest() {
        // Given
        val fileContent = PaymentFactory.getFileAsString(PaymentFactory.FilePaths.XML_FILE_PATH)
        val consentRequest = PaymentFactory.createOBWriteFileConsent3WithFileInfo(
                fileContent,
                PaymentFileType.UK_OBIE_PAIN_001_001_008.type
        )

        val (consentResponse, accessTokenAuthorizationCode) = createFilePaymentConsentsApi.createFilePaymentConsentAndAuthorize(
                consentRequest
        )

        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

        // When
        // Submit first payment
        submitFilePaymentForConsent(consentResponse, accessTokenAuthorizationCode)

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            // Verify we fail to submit a second payment
            submitFilePaymentForConsent(consentResponse, accessTokenAuthorizationCode)
        }

        // Then
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.PAYMENT_SUBMISSION_ALREADY_EXISTS)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(403)
    }

    fun shouldCreateFilePayment_throwsSendInvalidFormatDetachedJwsTest() {
        // Given
        val fileContent = PaymentFactory.getFileAsString(PaymentFactory.FilePaths.XML_FILE_PATH)
        val consentRequest = PaymentFactory.createOBWriteFileConsent3WithFileInfo(
                fileContent,
                PaymentFileType.UK_OBIE_PAIN_001_001_008.type
        )

        val (consentResponse, accessTokenAuthorizationCode) = createFilePaymentConsentsApi.createFilePaymentConsentAndAuthorize(
                consentRequest
        )

        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = createFilePaymentRequest(consentResponse)

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            paymentApiClient.buildSubmitPaymentRequest(
                    createPaymentUrl,
                    accessTokenAuthorizationCode,
                    paymentSubmissionRequest
            )
                    .configureJwsSignatureProducer(BadJwsSignatureProducer()).sendRequest()
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.INVALID_FORMAT_DETACHED_JWS_ERROR)
    }

    fun shouldCreateFilePayment_throwsNoDetachedJwsTest() {
        // Given
        val fileContent = PaymentFactory.getFileAsString(PaymentFactory.FilePaths.XML_FILE_PATH)
        val consentRequest = PaymentFactory.createOBWriteFileConsent3WithFileInfo(
                fileContent,
                PaymentFileType.UK_OBIE_PAIN_001_001_008.type
        )

        val (consentResponse, accessTokenAuthorizationCode) = createFilePaymentConsentsApi.createFilePaymentConsentAndAuthorize(
                consentRequest
        )

        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = createFilePaymentRequest(consentResponse)

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            paymentApiClient.buildSubmitPaymentRequest(
                    createPaymentUrl,
                    accessTokenAuthorizationCode,
                    paymentSubmissionRequest
            )
                    .configureJwsSignatureProducer(null).sendRequest()
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.NO_DETACHED_JWS)
    }

    fun shouldCreateFilePayment_throwsNotPermittedB64HeaderAddedInTheDetachedJwsTest() {
        // Given
        val fileContent = PaymentFactory.getFileAsString(PaymentFactory.FilePaths.XML_FILE_PATH)
        val consentRequest = PaymentFactory.createOBWriteFileConsent3WithFileInfo(
                fileContent,
                PaymentFileType.UK_OBIE_PAIN_001_001_008.type
        )

        val (consentResponse, accessTokenAuthorizationCode) = createFilePaymentConsentsApi.createFilePaymentConsentAndAuthorize(
                consentRequest
        )

        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = createFilePaymentRequest(consentResponse)

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            paymentApiClient.buildSubmitPaymentRequest(
                    createPaymentUrl,
                    accessTokenAuthorizationCode,
                    paymentSubmissionRequest
            )
                    .configureJwsSignatureProducer(DefaultJwsSignatureProducer(tppResource.tpp, false)).sendRequest()
        }

        // Then
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
    }

    fun shouldCreateFilePayment_throwsSendInvalidKidDetachedJwsTest() {
        // Given
        val fileContent = PaymentFactory.getFileAsString(PaymentFactory.FilePaths.XML_FILE_PATH)
        val consentRequest = PaymentFactory.createOBWriteFileConsent3WithFileInfo(
                fileContent,
                PaymentFileType.UK_OBIE_PAIN_001_001_008.type
        )

        val (consentResponse, accessTokenAuthorizationCode) = createFilePaymentConsentsApi.createFilePaymentConsentAndAuthorize(
                consentRequest
        )

        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = createFilePaymentRequest(consentResponse)

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            paymentApiClient.buildSubmitPaymentRequest(
                    createPaymentUrl,
                    accessTokenAuthorizationCode,
                    paymentSubmissionRequest
            )
                    .configureJwsSignatureProducer(InvalidKidJwsSignatureProducer(tppResource.tpp)).sendRequest()
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
    }

    fun shouldCreateFilePayment_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBodyTest() {
        // Given
        val fileContent = PaymentFactory.getFileAsString(PaymentFactory.FilePaths.XML_FILE_PATH)
        val consentRequest = PaymentFactory.createOBWriteFileConsent3WithFileInfo(
                fileContent,
                PaymentFileType.UK_OBIE_PAIN_001_001_008.type
        )

        val (consentResponse, accessTokenAuthorizationCode) = createFilePaymentConsentsApi.createFilePaymentConsentAndAuthorize(
                consentRequest
        )

        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

        val filePaymentSubmissionRequest = createFilePaymentRequest(consentResponse)

        consentResponse.data.consentId = com.forgerock.sapi.gateway.ob.uk.framework.constants.INVALID_CONSENT_ID
        val filePaymentSubmissionRequestWithInvalidConsentId = createFilePaymentRequest(consentResponse)

        val signatureWithInvalidConsentId = DefaultJwsSignatureProducer(tppResource.tpp).createDetachedSignature(
                defaultMapper.writeValueAsString(filePaymentSubmissionRequestWithInvalidConsentId)
        )

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            paymentApiClient.buildSubmitPaymentRequest(
                    createPaymentUrl,
                    accessTokenAuthorizationCode,
                    filePaymentSubmissionRequest
            )
                    .configureJwsSignatureProducer(BadJwsSignatureProducer(signatureWithInvalidConsentId)).sendRequest()
        }

        // Then
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
    }

    fun shouldCreateFilePayment_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBodyTest() {
        // Given
        val fileContent = PaymentFactory.getFileAsString(PaymentFactory.FilePaths.XML_FILE_PATH)
        val consentRequest = PaymentFactory.createOBWriteFileConsent3WithFileInfo(
                fileContent,
                PaymentFileType.UK_OBIE_PAIN_001_001_008.type
        )

        val (consentResponse, accessTokenAuthorizationCode) = createFilePaymentConsentsApi.createFilePaymentConsentAndAuthorize(
                consentRequest
        )

        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = createFilePaymentRequest(consentResponse)

        consentResponse.data.initiation.controlSum = BigDecimal("123123")
        val paymentSubmissionInvalidAmount = createFilePaymentRequest(consentResponse)

        val signatureWithInvalidAmount = DefaultJwsSignatureProducer(tppResource.tpp).createDetachedSignature(
                defaultMapper.writeValueAsString(paymentSubmissionInvalidAmount)
        )

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            paymentApiClient.buildSubmitPaymentRequest(
                    createPaymentUrl,
                    accessTokenAuthorizationCode,
                    paymentSubmissionRequest
            )
                    .configureJwsSignatureProducer(BadJwsSignatureProducer(signatureWithInvalidAmount)).sendRequest()
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
    }

    fun submitFilePayment(consentRequest: OBWriteFileConsent3): OBWriteFileResponse3 {
        val (consentResponse, authorizationToken) = createFilePaymentConsentsApi.createFilePaymentConsentAndAuthorize(
                consentRequest
        )
        return submitFilePayment(consentResponse, authorizationToken)
    }

    private fun submitFilePayment(
            consentResponse: OBWriteFileConsentResponse4,
            authorizationToken: AccessToken
    ): OBWriteFileResponse3 {
        return submitFilePaymentForConsent(consentResponse, authorizationToken)
    }

    private fun submitFilePaymentForConsent(
            consentResponse: OBWriteFileConsentResponse4,
            authorizationToken: AccessToken
    ): OBWriteFileResponse3 {
        val paymentSubmissionRequest = createFilePaymentRequest(consentResponse)
        return paymentApiClient.submitPayment(
                createPaymentUrl,
                authorizationToken,
                paymentSubmissionRequest
        )
    }

    private fun createFilePaymentRequest(consentResponse: OBWriteFileConsentResponse4): OBWriteFile2 {
        return OBWriteFile2().data(
                OBWriteFile2Data()
                        .consentId(consentResponse.data.consentId)
                        .initiation(mapOBWriteFileConsentResponse4DataInitiationToOBWriteFile2DataInitiation(consentResponse.data.initiation))
        )
    }
}
