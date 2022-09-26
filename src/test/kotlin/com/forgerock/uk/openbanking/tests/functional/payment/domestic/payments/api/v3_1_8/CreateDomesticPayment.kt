package com.forgerock.uk.openbanking.tests.functional.payment.domestic.payments.api.v3_1_8

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.securebanking.framework.conditions.Status
import com.forgerock.securebanking.framework.data.AccessToken
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.http.fuel.defaultMapper
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.framework.constants.INVALID_CONSENT_ID
import com.forgerock.uk.openbanking.framework.errors.INVALID_FORMAT_DETACHED_JWS_ERROR
import com.forgerock.uk.openbanking.framework.errors.NO_DETACHED_JWS
import com.forgerock.uk.openbanking.framework.errors.PAYMENT_SUBMISSION_ALREADY_EXISTS
import com.forgerock.uk.openbanking.framework.errors.UNAUTHORIZED
import com.forgerock.uk.openbanking.support.discovery.getPaymentsApiLinks
import com.forgerock.uk.openbanking.support.payment.BadJwsSignatureProducer
import com.forgerock.uk.openbanking.support.payment.DefaultJwsSignatureProducer
import com.forgerock.uk.openbanking.support.payment.InvalidKidJwsSignatureProducer
import com.forgerock.uk.openbanking.support.payment.PaymentFactory
import com.forgerock.uk.openbanking.tests.functional.payment.domestic.payments.consents.api.v3_1_8.CreateDomesticPaymentsConsents
import com.github.kittinunf.fuel.core.FuelError
import org.assertj.core.api.Assertions
import uk.org.openbanking.datamodel.payment.*
import uk.org.openbanking.testsupport.payment.OBWriteDomesticConsentTestDataFactory

class CreateDomesticPayment(
    val version: OBVersion,
    val tppResource: CreateTppCallback.TppResource
) {

    private val createDomesticPaymentsConsentsApi = CreateDomesticPaymentsConsents(version, tppResource)
    private val paymentApiClient = tppResource.tpp.paymentApiClient
    private val paymentLinks = getPaymentsApiLinks(version)
    private val createPaymentUrl = paymentLinks.CreateDomesticPayment

    fun createDomesticPaymentsTest() {
        // Given
        val consentRequest = OBWriteDomesticConsentTestDataFactory.aValidOBWriteDomesticConsent4()
        val result = submitPayment(consentRequest)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.consentId).isNotEmpty()
        assertThat(result.data.charges).isNotNull().isNotEmpty()
    }

    fun shouldCreateDomesticPayments_throwsPaymentAlreadyExistsTest() {
        // Given
        val consentRequest = OBWriteDomesticConsentTestDataFactory.aValidOBWriteDomesticConsent4()
        val (consent, authorizationToken) = createDomesticPaymentsConsentsApi.createDomesticPaymentsConsentAndAuthorize(
            consentRequest
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        // When
        val patchedConsent = getPatchedConsent(consent)
        // Submit first payment
        submitPaymentForPatchedConsent(patchedConsent, authorizationToken)

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            // Verify we fail to submit a second payment
            submitPaymentForPatchedConsent(patchedConsent, authorizationToken)
        }

        // Then
        assertThat(exception.message.toString()).contains(PAYMENT_SUBMISSION_ALREADY_EXISTS)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(403)
    }

    fun shouldCreateDomesticPayments_throwsSendInvalidFormatDetachedJwsTest() {
        // Given
        val consentRequest = OBWriteDomesticConsentTestDataFactory.aValidOBWriteDomesticConsent4()
        val (consent, accessToken) = createDomesticPaymentsConsentsApi.createDomesticPaymentsConsentAndAuthorize(
            consentRequest
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = createPaymentRequest(getPatchedConsent(consent))

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            paymentApiClient.buildSubmitPaymentRequest(createPaymentUrl, accessToken, paymentSubmissionRequest)
                .configureJwsSignatureProducer(BadJwsSignatureProducer()).sendRequest()
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(INVALID_FORMAT_DETACHED_JWS_ERROR)
    }

    fun shouldCreateDomesticPayments_throwsNoDetachedJwsTest() {
        // Given
        val consentRequest = OBWriteDomesticConsentTestDataFactory.aValidOBWriteDomesticConsent4()
        val (consent, accessToken) = createDomesticPaymentsConsentsApi.createDomesticPaymentsConsentAndAuthorize(
            consentRequest
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = createPaymentRequest(getPatchedConsent(consent))

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            paymentApiClient.buildSubmitPaymentRequest(createPaymentUrl, accessToken, paymentSubmissionRequest)
                .configureJwsSignatureProducer(null).sendRequest()
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(NO_DETACHED_JWS)
    }

    fun shouldCreateDomesticPayments_throwsNotPermittedB64HeaderAddedInTheDetachedJwsTest() {
        // Given
        val consentRequest = OBWriteDomesticConsentTestDataFactory.aValidOBWriteDomesticConsent4()
        val (consent, accessToken) = createDomesticPaymentsConsentsApi.createDomesticPaymentsConsentAndAuthorize(
            consentRequest
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = createPaymentRequest(getPatchedConsent(consent))

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            paymentApiClient.buildSubmitPaymentRequest(createPaymentUrl, accessToken, paymentSubmissionRequest)
                .configureJwsSignatureProducer(DefaultJwsSignatureProducer(tppResource.tpp, false)).sendRequest()
        }

        // Then
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
    }

    fun shouldCreateDomesticPayments_throwsSendInvalidKidDetachedJwsTest() {
        // Given
        val consentRequest = OBWriteDomesticConsentTestDataFactory.aValidOBWriteDomesticConsent4()
        val (consent, accessToken) = createDomesticPaymentsConsentsApi.createDomesticPaymentsConsentAndAuthorize(
            consentRequest
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = createPaymentRequest(getPatchedConsent(consent))

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            paymentApiClient.buildSubmitPaymentRequest(createPaymentUrl, accessToken, paymentSubmissionRequest)
                .configureJwsSignatureProducer(InvalidKidJwsSignatureProducer(tppResource.tpp)).sendRequest()
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
    }

    fun shouldCreateDomesticPayments_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBodyTest() {
        // Given
        val consentRequest = OBWriteDomesticConsentTestDataFactory.aValidOBWriteDomesticConsent4()
        val (consent, accessToken) = createDomesticPaymentsConsentsApi.createDomesticPaymentsConsentAndAuthorize(
            consentRequest
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        val patchedConsent = getPatchedConsent(consent)
        val paymentSubmissionRequest = createPaymentRequest(patchedConsent)

        patchedConsent.data.consentId = INVALID_CONSENT_ID
        val paymentSubmissionWithInvalidConsentId = createPaymentRequest(patchedConsent)

        val signatureWithInvalidConsentId = DefaultJwsSignatureProducer(tppResource.tpp).createDetachedSignature(
            defaultMapper.writeValueAsString(paymentSubmissionWithInvalidConsentId)
        )

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            paymentApiClient.buildSubmitPaymentRequest(createPaymentUrl, accessToken, paymentSubmissionRequest)
                .configureJwsSignatureProducer(BadJwsSignatureProducer(signatureWithInvalidConsentId)).sendRequest()
        }

        // Then
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
    }

    fun shouldCreateDomesticPayments_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBodyTest() {
        // Given
        val consentRequest = OBWriteDomesticConsentTestDataFactory.aValidOBWriteDomesticConsent4()
        val (consent, accessToken) = createDomesticPaymentsConsentsApi.createDomesticPaymentsConsentAndAuthorize(
            consentRequest
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        val patchedConsent = getPatchedConsent(consent)
        val paymentSubmissionRequest = createPaymentRequest(patchedConsent)

        patchedConsent.data.initiation.instructedAmount.amount = "123123"
        val paymentSubmissionInvalidAmount = createPaymentRequest(patchedConsent)

        val signatureWithInvalidAmount = DefaultJwsSignatureProducer(tppResource.tpp).createDetachedSignature(
            defaultMapper.writeValueAsString(paymentSubmissionInvalidAmount)
        )

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            paymentApiClient.buildSubmitPaymentRequest(createPaymentUrl, accessToken, paymentSubmissionRequest)
                .configureJwsSignatureProducer(BadJwsSignatureProducer(signatureWithInvalidAmount)).sendRequest()
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
    }

    fun submitPayment(consentRequest: OBWriteDomesticConsent4): OBWriteDomesticResponse5 {
        val (consent, authorizationToken) = createDomesticPaymentsConsentsApi.createDomesticPaymentsConsentAndAuthorize(
            consentRequest
        )
        return submitPayment(consent, authorizationToken)
    }

    fun submitPayment(
        consentResponse: OBWriteDomesticConsentResponse5,
        authorizationToken: AccessToken
    ): OBWriteDomesticResponse5 {
        val patchedConsent = getPatchedConsent(consentResponse)
        return submitPaymentForPatchedConsent(patchedConsent, authorizationToken)
    }

    private fun getPatchedConsent(consent: OBWriteDomesticConsentResponse5): OBWriteDomesticConsentResponse5 {
        return createDomesticPaymentsConsentsApi.getPatchedConsent(consent)
    }

    private fun submitPaymentForPatchedConsent(
        patchedConsent: OBWriteDomesticConsentResponse5,
        authorizationToken: AccessToken
    ): OBWriteDomesticResponse5 {
        val paymentSubmissionRequest = createPaymentRequest(patchedConsent)
        return paymentApiClient.submitPayment(
            createPaymentUrl,
            authorizationToken,
            paymentSubmissionRequest
        )
    }

    private fun createPaymentRequest(patchedConsent: OBWriteDomesticConsentResponse5): OBWriteDomestic2 {
        return OBWriteDomestic2().data(
                OBWriteDomestic2Data()
                        .consentId(patchedConsent.data.consentId)
                        .initiation(PaymentFactory.copyOBWriteDomestic2DataInitiation(patchedConsent.data.initiation))
        ).risk(patchedConsent.risk)
    }
}
