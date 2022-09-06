package com.forgerock.uk.openbanking.tests.functional.payment.international.scheduled.payments.api.v3_1_8

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
import com.forgerock.uk.openbanking.framework.errors.*
import com.forgerock.uk.openbanking.support.discovery.getPaymentsApiLinks
import com.forgerock.uk.openbanking.support.payment.BadJwsSignatureProducer
import com.forgerock.uk.openbanking.support.payment.DefaultJwsSignatureProducer
import com.forgerock.uk.openbanking.support.payment.InvalidKidJwsSignatureProducer
import com.forgerock.uk.openbanking.support.payment.PaymentFactory.Companion.mapOBWriteInternationalScheduledConsentResponse6DataInitiationToOBWriteInternationalScheduled3DataInitiation
import com.forgerock.uk.openbanking.tests.functional.payment.international.scheduled.payments.consents.api.v3_1_8.CreateInternationalScheduledPaymentsConsents
import com.github.kittinunf.fuel.core.FuelError
import org.assertj.core.api.Assertions
import uk.org.openbanking.datamodel.payment.*
import uk.org.openbanking.testsupport.payment.OBWriteInternationalScheduledConsentTestDataFactory
import uk.org.openbanking.testsupport.payment.OBWriteInternationalScheduledConsentTestDataFactory.aValidOBWriteInternationalScheduledConsent5MandatoryFields

class CreateInternationalScheduledPayment(val version: OBVersion, val tppResource: CreateTppCallback.TppResource) {

    private val createInternationalScheduledPaymentsConsents =
        CreateInternationalScheduledPaymentsConsents(version, tppResource)
    private val paymentLinks = getPaymentsApiLinks(version)
    private val createPaymentUrl = paymentLinks.CreateInternationalScheduledPayment
    private val paymentApiClient = tppResource.tpp.paymentApiClient

    fun createInternationalScheduledPayment_rateType_AGREED_Test() {
        // Given
        val consentRequest =
            OBWriteInternationalScheduledConsentTestDataFactory.aValidOBWriteInternationalScheduledConsent5()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.AGREED

        // When
        val result = submitPayment(consentRequest)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.consentId).isNotEmpty()
        assertThat(result.data.exchangeRateInformation).isNotNull()
        assertThat(result.data.exchangeRateInformation.rateType).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.rateType)
        assertThat(result.data.exchangeRateInformation.unitCurrency).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.unitCurrency)
        assertThat(result.data.exchangeRateInformation.exchangeRate).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.exchangeRate)
    }

    fun createInternationalScheduledPayment_rateType_ACTUAL_Test() {
        // Given
        val consentRequest =
            OBWriteInternationalScheduledConsentTestDataFactory.aValidOBWriteInternationalScheduledConsent5()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.ACTUAL
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null

        // When
        val result = submitPayment(consentRequest)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.consentId).isNotEmpty()
        assertThat(result.data.exchangeRateInformation).isNotNull()
        assertThat(result.data.exchangeRateInformation.rateType).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.rateType)
        assertThat(result.data.exchangeRateInformation.unitCurrency).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.unitCurrency)
        assertThat(result.data.exchangeRateInformation.exchangeRate).isNotNull()
        assertThat(result.data.exchangeRateInformation.expirationDateTime).isNotNull()
    }

    fun createInternationalScheduledPayment_rateType_INDICATIVE_Test() {
        // Given
        val consentRequest =
            OBWriteInternationalScheduledConsentTestDataFactory.aValidOBWriteInternationalScheduledConsent5()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.INDICATIVE
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null

        // When
        val result = submitPayment(consentRequest)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.consentId).isNotEmpty()
        assertThat(result.data.exchangeRateInformation).isNotNull()
        assertThat(result.data.exchangeRateInformation.rateType).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.rateType)
        assertThat(result.data.exchangeRateInformation.unitCurrency).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.unitCurrency)
        assertThat(result.data.exchangeRateInformation.exchangeRate).isNotNull()
    }

    fun createInternationalScheduledPayment_mandatoryFields_Test() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent5MandatoryFields()

        // When
        val result = submitPayment(consentRequest)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.consentId).isNotEmpty()
        assertThat(result.data.exchangeRateInformation).isNotNull()
        assertThat(result.data.exchangeRateInformation.rateType).isNotNull()
        assertThat(result.data.exchangeRateInformation.unitCurrency).isNotNull()
        assertThat(result.data.exchangeRateInformation.exchangeRate).isNotNull()
    }

    fun shouldCreateInternationalScheduledPayment_throwsInternationalScheduledPaymentAlreadyExists_Test() {
        // Given
        val consentRequest =
            OBWriteInternationalScheduledConsentTestDataFactory.aValidOBWriteInternationalScheduledConsent5()
        val (consent, authorizationToken) = createInternationalScheduledPaymentsConsents.createInternationalScheduledPaymentConsentAndAuthorize(
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

    fun shouldCreateInternationalScheduledPayment_throwsSendInvalidFormatDetachedJws_Test() {
        // Given
        val consentRequest =
            OBWriteInternationalScheduledConsentTestDataFactory.aValidOBWriteInternationalScheduledConsent5()

        val (consent, accessToken) = createInternationalScheduledPaymentsConsents.createInternationalScheduledPaymentConsentAndAuthorize(
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

    fun shouldCreateInternationalScheduledPayment_throwsNoDetachedJws_Test() {
        // Given
        val consentRequest =
            OBWriteInternationalScheduledConsentTestDataFactory.aValidOBWriteInternationalScheduledConsent5()

        val (consent, accessToken) = createInternationalScheduledPaymentsConsents.createInternationalScheduledPaymentConsentAndAuthorize(
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

    fun shouldCreateInternationalScheduledPayment_throwsNotPermittedB64HeaderAddedInTheDetachedJws_Test() {
        // Given
        val consentRequest =
            OBWriteInternationalScheduledConsentTestDataFactory.aValidOBWriteInternationalScheduledConsent5()

        val (consent, accessToken) = createInternationalScheduledPaymentsConsents.createInternationalScheduledPaymentConsentAndAuthorize(
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

    fun shouldCreateInternationalScheduledPayment_throwsSendInvalidKidDetachedJws_Test() {
        // Given
        val consentRequest =
            OBWriteInternationalScheduledConsentTestDataFactory.aValidOBWriteInternationalScheduledConsent5()

        val (consent, accessToken) = createInternationalScheduledPaymentsConsents.createInternationalScheduledPaymentConsentAndAuthorize(
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

    fun shouldCreateInternationalScheduledPayment_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBody_Test() {
        // Given
        val consentRequest =
            OBWriteInternationalScheduledConsentTestDataFactory.aValidOBWriteInternationalScheduledConsent5()
        val (consent, accessToken) = createInternationalScheduledPaymentsConsents.createInternationalScheduledPaymentConsentAndAuthorize(
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

    fun shouldCreateInternationalScheduledPayment_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBody_Test() {
        // Given
        val consentRequest =
            OBWriteInternationalScheduledConsentTestDataFactory.aValidOBWriteInternationalScheduledConsent5()

        val (consent, accessToken) = createInternationalScheduledPaymentsConsents.createInternationalScheduledPaymentConsentAndAuthorize(
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

    fun submitPayment(consentRequest: OBWriteInternationalScheduledConsent5): OBWriteInternationalScheduledResponse5 {
        val (consent, authorizationToken) = createInternationalScheduledPaymentsConsents.createInternationalScheduledPaymentConsentAndAuthorize(
            consentRequest
        )
        return submitPayment(consent, authorizationToken)
    }

    fun submitPayment(
        consentResponse: OBWriteInternationalScheduledConsentResponse6,
        authorizationToken: AccessToken
    ): OBWriteInternationalScheduledResponse5 {
        val patchedConsent = getPatchedConsent(consentResponse)
        return submitPaymentForPatchedConsent(patchedConsent, authorizationToken)
    }

    private fun getPatchedConsent(consent: OBWriteInternationalScheduledConsentResponse6): OBWriteInternationalScheduledConsentResponse6 {
        return createInternationalScheduledPaymentsConsents.getPatchedConsent(consent)
    }

    private fun submitPaymentForPatchedConsent(
        patchedConsent: OBWriteInternationalScheduledConsentResponse6,
        authorizationToken: AccessToken
    ): OBWriteInternationalScheduledResponse5 {
        val paymentSubmissionRequest = createPaymentRequest(patchedConsent)
        return paymentApiClient.submitPayment(
            createPaymentUrl,
            authorizationToken,
            paymentSubmissionRequest
        )
    }

    private fun createPaymentRequest(patchedConsent: OBWriteInternationalScheduledConsentResponse6): OBWriteInternationalScheduled3 {
        return OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(
                    mapOBWriteInternationalScheduledConsentResponse6DataInitiationToOBWriteInternationalScheduled3DataInitiation(
                        patchedConsent.data.initiation
                    )
                )
        )
    }
}