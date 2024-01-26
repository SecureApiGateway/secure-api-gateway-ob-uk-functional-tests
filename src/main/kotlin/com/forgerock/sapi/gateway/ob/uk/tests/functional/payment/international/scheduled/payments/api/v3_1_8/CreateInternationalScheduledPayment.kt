package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.scheduled.payments.api.v3_1_8

import assertk.assertThat
import assertk.assertions.*
import com.forgerock.sapi.gateway.framework.conditions.Status
import com.forgerock.sapi.gateway.framework.data.AccessToken
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.http.fuel.defaultMapper
import com.forgerock.sapi.gateway.ob.uk.common.error.OBRIErrorType
import com.forgerock.sapi.gateway.ob.uk.framework.consent.ConsentFactoryRegistryHolder
import com.forgerock.sapi.gateway.ob.uk.framework.consent.payment.OBWriteInternationalScheduledConsent5Factory
import com.forgerock.sapi.gateway.ob.uk.support.discovery.getPaymentsApiLinks
import com.forgerock.sapi.gateway.ob.uk.support.payment.*
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.scheduled.payments.consents.api.v3_1_8.CreateInternationalScheduledPaymentsConsents
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.github.kittinunf.fuel.core.FuelError
import org.assertj.core.api.Assertions
import uk.org.openbanking.datamodel.payment.*
import java.util.UUID

class CreateInternationalScheduledPayment(val version: OBVersion, val tppResource: CreateTppCallback.TppResource) {

    private val createInternationalScheduledPaymentsConsents =
        CreateInternationalScheduledPaymentsConsents(version, tppResource)
    private val paymentLinks = getPaymentsApiLinks(version)
    private val createPaymentUrl = paymentLinks.CreateInternationalScheduledPayment
    private val paymentApiClient = tppResource.tpp.paymentApiClient
    private val consentFactory = ConsentFactoryRegistryHolder.consentFactoryRegistry.getConsentFactory(
        OBWriteInternationalScheduledConsent5Factory::class.java
    )


    fun createInternationalScheduledPayment_rateType_AGREED_Test() {
        // Given
        val consentRequest = consentFactory.createConsent()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType.AGREED

        // When
        val result = submitPayment(consentRequest)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.consentId).isNotEmpty()
        assertThat(result.data.charges).isNotNull().isEmpty()
        assertThat(result.data.exchangeRateInformation).isNotNull()
        assertThat(result.data.exchangeRateInformation.rateType).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.rateType)
        assertThat(result.data.exchangeRateInformation.unitCurrency).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.unitCurrency)
        assertThat(result.data.exchangeRateInformation.exchangeRate.compareTo(consentRequest.data.initiation.exchangeRateInformation.exchangeRate)).isEqualTo(0)
    }

    fun shouldCreateInternationalScheduledPayment_throwsInvalidInitiation_Test() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val (consentResponse, authorizationToken) = createInternationalScheduledPaymentsConsents.createInternationalScheduledPaymentConsentAndAuthorize(
            consentRequest
        )

        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

        consentRequest.data.initiation.instructedAmount = OBWriteDomestic2DataInitiationInstructedAmount()
            .amount("123123")
            .currency("EUR")

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            // Verify we fail to submit a second payment
            submitPaymentForConsent(consentResponse.data.consentId, consentRequest, authorizationToken)
        }

        // Then
        assertThat(exception.message.toString()).contains(OBRIErrorType.PAYMENT_INVALID_INITIATION.code.value)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(OBRIErrorType.PAYMENT_INVALID_INITIATION.httpStatus.value())
    }

    fun createInternationalScheduledPayment_withDebtorAccount_Test() {
        // Given
        val consentRequest = consentFactory.createConsent()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType.AGREED
        // optional debtor account
        val debtorAccount = PsuData().getDebtorAccount()
        consentRequest.data.initiation.debtorAccount(
            OBWriteDomestic2DataInitiationDebtorAccount()
                .identification(debtorAccount?.Identification)
                .name(debtorAccount?.Name)
                .schemeName(debtorAccount?.SchemeName)
                .secondaryIdentification(debtorAccount?.SecondaryIdentification)
        )

        // When
        val result = submitPayment(consentRequest)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.consentId).isNotEmpty()
        assertThat(result.data.charges).isNotNull().isEmpty()
        assertThat(result.data.exchangeRateInformation).isNotNull()
        assertThat(result.data.exchangeRateInformation.rateType).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.rateType)
        assertThat(result.data.exchangeRateInformation.unitCurrency).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.unitCurrency)
        assertThat(result.data.exchangeRateInformation.exchangeRate.compareTo(consentRequest.data.initiation.exchangeRateInformation.exchangeRate)).isEqualTo(0)
    }

    fun createInternationalScheduledPayment_rateType_ACTUAL_Test() {
        // Given
        val consentRequest = consentFactory.createConsent()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType.ACTUAL
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null

        // When
        val result = submitPayment(consentRequest)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.consentId).isNotEmpty()
        assertThat(result.data.charges).isNotNull().isEmpty()
        assertThat(result.data.exchangeRateInformation).isNotNull()
        assertThat(result.data.exchangeRateInformation.rateType).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.rateType)
        assertThat(result.data.exchangeRateInformation.unitCurrency).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.unitCurrency)
        assertThat(result.data.exchangeRateInformation.exchangeRate).isNotNull()
        assertThat(result.data.exchangeRateInformation.expirationDateTime).isNotNull()
    }

    fun createInternationalScheduledPayment_rateType_INDICATIVE_Test() {
        // Given
        val consentRequest = consentFactory.createConsent()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType.INDICATIVE
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null

        // When
        val result = submitPayment(consentRequest)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.consentId).isNotEmpty()
        assertThat(result.data.charges).isNotNull().isEmpty()
        assertThat(result.data.exchangeRateInformation).isNotNull()
        assertThat(result.data.exchangeRateInformation.rateType).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.rateType)
        assertThat(result.data.exchangeRateInformation.unitCurrency).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.unitCurrency)
        assertThat(result.data.exchangeRateInformation.exchangeRate).isNotNull()
    }

    fun createInternationalScheduledPayment_mandatoryFields_Test() {
        // Given
        val consentRequest = consentFactory.createConsentWithOnlyMandatoryFieldsPopulated()

        // When
        val (consentResponse, authorizationToken) = createInternationalScheduledPaymentsConsents.createInternationalScheduledPaymentConsentAndAuthorize(
                consentRequest
        )
        val result = submitPayment(consentResponse.data.consentId, consentRequest, authorizationToken)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.consentId).isNotEmpty()
        assertThat(result.data.charges).isNotNull().isEmpty()
        assertThat(result.data.exchangeRateInformation).isNotNull()
        assertThat(result.data.exchangeRateInformation.exchangeRate.compareTo(consentResponse.data.exchangeRateInformation.exchangeRate)).isEqualTo(0)
        assertThat(result.data.exchangeRateInformation.rateType).isEqualTo(consentResponse.data.exchangeRateInformation.rateType)
        assertThat(result.data.exchangeRateInformation.unitCurrency).isEqualTo(consentResponse.data.exchangeRateInformation.unitCurrency)
    }

    fun shouldCreateInternationalScheduledPayment_throwsInternationalScheduledPaymentAlreadyExists_Test() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val (consentResponse, authorizationToken) = createInternationalScheduledPaymentsConsents.createInternationalScheduledPaymentConsentAndAuthorize(
            consentRequest
        )

        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

        // When
        // Submit first payment
        submitPaymentForConsent(consentResponse.data.consentId, consentRequest, authorizationToken)

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            // Verify we fail to submit a second payment
            submitPaymentForConsent(consentResponse.data.consentId, consentRequest, authorizationToken)
        }

        // Then
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.PAYMENT_SUBMISSION_ALREADY_EXISTS)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(403)
    }

    fun shouldCreateInternationalScheduledPayment_throwsSendInvalidFormatDetachedJws_Test() {
        // Given
        val consentRequest = consentFactory.createConsent()

        val (consentResponse, accessToken) = createInternationalScheduledPaymentsConsents.createInternationalScheduledPaymentConsentAndAuthorize(
            consentRequest
        )

        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = createPaymentRequest(consentResponse.data.consentId, consentRequest)

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            paymentApiClient.buildSubmitPaymentRequest(createPaymentUrl, accessToken, paymentSubmissionRequest)
                .configureJwsSignatureProducer(BadJwsSignatureProducer()).sendRequest()
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.INVALID_FORMAT_DETACHED_JWS_ERROR)
    }

    fun shouldCreateInternationalScheduledPayment_throwsNoDetachedJws_Test() {
        // Given
        val consentRequest = consentFactory.createConsent()

        val (consentResponse, accessToken) = createInternationalScheduledPaymentsConsents.createInternationalScheduledPaymentConsentAndAuthorize(
            consentRequest
        )

        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = createPaymentRequest(consentResponse.data.consentId, consentRequest)

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            paymentApiClient.buildSubmitPaymentRequest(createPaymentUrl, accessToken, paymentSubmissionRequest)
                .configureJwsSignatureProducer(null).sendRequest()
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.NO_DETACHED_JWS)
    }

    fun shouldCreateInternationalScheduledPayment_throwsNotPermittedB64HeaderAddedInTheDetachedJws_Test() {
        // Given
        val consentRequest = consentFactory.createConsent()

        val (consentResponse, accessToken) = createInternationalScheduledPaymentsConsents.createInternationalScheduledPaymentConsentAndAuthorize(
            consentRequest
        )

        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = createPaymentRequest(consentResponse.data.consentId, consentRequest)

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            paymentApiClient.buildSubmitPaymentRequest(createPaymentUrl, accessToken, paymentSubmissionRequest)
                .configureJwsSignatureProducer(DefaultJwsSignatureProducer(tppResource.tpp, false)).sendRequest()
        }

        // Then
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
    }

    fun shouldCreateInternationalScheduledPayment_throwsSendInvalidKidDetachedJws_Test() {
        // Given
        val consentRequest = consentFactory.createConsent()

        val (consentResponse, accessToken) = createInternationalScheduledPaymentsConsents.createInternationalScheduledPaymentConsentAndAuthorize(
            consentRequest
        )

        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = createPaymentRequest(consentResponse.data.consentId, consentRequest)

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            paymentApiClient.buildSubmitPaymentRequest(createPaymentUrl, accessToken, paymentSubmissionRequest)
                .configureJwsSignatureProducer(InvalidKidJwsSignatureProducer(tppResource.tpp)).sendRequest()
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
    }

    fun shouldCreateInternationalScheduledPayment_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBody_Test() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val (consentResponse, accessToken) = createInternationalScheduledPaymentsConsents.createInternationalScheduledPaymentConsentAndAuthorize(
            consentRequest
        )

        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = createPaymentRequest(consentResponse.data.consentId, consentRequest)

        consentResponse.data.consentId = com.forgerock.sapi.gateway.ob.uk.framework.constants.INVALID_CONSENT_ID

        val paymentSubmissionWithInvalidConsentId = createPaymentRequest(consentResponse.data.consentId, consentRequest)

        val signatureWithInvalidConsentId = DefaultJwsSignatureProducer(tppResource.tpp).createDetachedSignature(
            defaultMapper.writeValueAsString(paymentSubmissionWithInvalidConsentId)
        )

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            paymentApiClient.buildSubmitPaymentRequest(createPaymentUrl, accessToken, paymentSubmissionRequest)
                .configureJwsSignatureProducer(BadJwsSignatureProducer(signatureWithInvalidConsentId)).sendRequest()
        }

        // Then
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
    }

    fun shouldCreateInternationalScheduledPayment_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBody_Test() {
        // Given
        val consentRequest = consentFactory.createConsent()

        val (consentResponse, accessToken) = createInternationalScheduledPaymentsConsents.createInternationalScheduledPaymentConsentAndAuthorize(
            consentRequest
        )

        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = createPaymentRequest(consentResponse.data.consentId, consentRequest)

        paymentSubmissionRequest.data.initiation.instructedAmount =
            OBWriteDomestic2DataInitiationInstructedAmount()
                .amount("123123")
                .currency("EUR")

        val paymentSubmissionInvalidAmount = createPaymentRequest(consentResponse.data.consentId, consentRequest)

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
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
    }

    fun shouldCreateInternationalScheduledPayments_throwsInvalidRiskTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val (consentResponse, authorizationToken) = createInternationalScheduledPaymentsConsents.createInternationalScheduledPaymentConsentAndAuthorize(
            consentRequest
        )

        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

        // When

        // Alter Risk Merchant
        consentRequest.risk.merchantCategoryCode = "zzzz"

        // Submit payment
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            submitPaymentForConsent(consentResponse.data.consentId, consentRequest, authorizationToken)
        }

        // Then
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.INVALID_RISK)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
    }

    fun testCreatingPaymentIsIdempotent() {
        val consentRequest = consentFactory.createConsent()
        val (consent, authorizationToken) = createInternationalScheduledPaymentsConsents.createInternationalScheduledPaymentConsentAndAuthorize(
            consentRequest
        )
        val paymentSubmissionRequest = createPaymentRequest(consent.data.consentId, consentRequest)

        val idempotencyKey = UUID.randomUUID().toString()
        val firstPaymentResponse:OBWriteInternationalScheduledResponse6 = paymentApiClient.newPostRequestBuilder(createPaymentUrl, authorizationToken, paymentSubmissionRequest)
            .addIdempotencyKeyHeader(idempotencyKey)
            .sendRequest()

        Assertions.assertThat(firstPaymentResponse).isNotNull()
        Assertions.assertThat(firstPaymentResponse.data).isNotNull()
        Assertions.assertThat(firstPaymentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(firstPaymentResponse.data.charges).isEmpty()
        Assertions.assertThat(firstPaymentResponse.links.self.toString()).isEqualTo(createPaymentUrl + "/" + firstPaymentResponse.data.internationalScheduledPaymentId)

        // Submit again with same key
        val secondPaymentResponse:OBWriteInternationalScheduledResponse6 = paymentApiClient.newPostRequestBuilder(createPaymentUrl, authorizationToken, paymentSubmissionRequest)
            .addIdempotencyKeyHeader(idempotencyKey)
            .sendRequest()

        Assertions.assertThat(secondPaymentResponse).isEqualTo(firstPaymentResponse)
    }

    fun submitPayment(consentRequest: OBWriteInternationalScheduledConsent5): OBWriteInternationalScheduledResponse6 {
        val (consentResponse, authorizationToken) = createInternationalScheduledPaymentsConsents.createInternationalScheduledPaymentConsentAndAuthorize(
            consentRequest
        )
        return submitPayment(consentResponse.data.consentId, consentRequest, authorizationToken)
    }

    fun submitPayment(
        consentId: String,
        consentRequest: OBWriteInternationalScheduledConsent5,
        authorizationToken: AccessToken
    ): OBWriteInternationalScheduledResponse6 {
        return submitPaymentForConsent(consentId, consentRequest, authorizationToken)
    }

    private fun submitPaymentForConsent(
        consentId: String,
        consentRequest: OBWriteInternationalScheduledConsent5,
        authorizationToken: AccessToken
    ): OBWriteInternationalScheduledResponse6 {
        val paymentSubmissionRequest = createPaymentRequest(consentId, consentRequest)
        return paymentApiClient.submitPayment(
            createPaymentUrl,
            authorizationToken,
            paymentSubmissionRequest
        )
    }

    private fun createPaymentRequest(
        consentId: String,
        consentRequest: OBWriteInternationalScheduledConsent5
    ): OBWriteInternationalScheduled3 {
        return OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
                .consentId(consentId)
                .initiation(PaymentFactory.copyOBWriteInternationalScheduled3DataInitiation(consentRequest.data.initiation))
        ).risk(consentRequest.risk)
    }
}
