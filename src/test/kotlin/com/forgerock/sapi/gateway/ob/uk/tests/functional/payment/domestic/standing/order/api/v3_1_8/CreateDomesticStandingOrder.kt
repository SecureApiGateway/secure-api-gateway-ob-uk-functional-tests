package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.standing.order.api.v3_1_8

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.sapi.gateway.framework.conditions.Status
import com.forgerock.sapi.gateway.framework.data.AccessToken
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.http.fuel.defaultMapper
import com.forgerock.sapi.gateway.ob.uk.common.error.OBRIErrorType
import com.forgerock.sapi.gateway.ob.uk.framework.consent.ConsentFactoryRegistryHolder
import com.forgerock.sapi.gateway.ob.uk.framework.consent.payment.OBWriteDomesticStandingOrderConsent5Factory
import com.forgerock.sapi.gateway.ob.uk.support.discovery.getPaymentsApiLinks
import com.forgerock.sapi.gateway.ob.uk.support.payment.*
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.standing.order.consents.api.v3_1_8.CreateDomesticStandingOrderConsents
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.github.kittinunf.fuel.core.FuelError
import org.assertj.core.api.Assertions
import uk.org.openbanking.datamodel.payment.*
import java.util.UUID

class CreateDomesticStandingOrder(val version: OBVersion, val tppResource: CreateTppCallback.TppResource) {

    private val createDomesticStandingOrderConsentsApi = CreateDomesticStandingOrderConsents(version, tppResource)
    private val paymentApiClient = tppResource.tpp.paymentApiClient
    private val paymentLinks = getPaymentsApiLinks(version)
    private val createPaymentUrl = paymentLinks.CreateDomesticStandingOrder
    private val consentFactory = ConsentFactoryRegistryHolder.consentFactoryRegistry.getConsentFactory(
        OBWriteDomesticStandingOrderConsent5Factory::class.java
    )

    fun createDomesticStandingOrderTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        // When
        val result = submitStandingOrder(consentRequest)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.charges).isNotNull().isEmpty()
        assertThat(result.data.consentId).isNotEmpty()
        assertThat(result.links.self.toString()).isEqualTo(createPaymentUrl + "/" + result.data.domesticStandingOrderId)
    }

    fun createDomesticStandingOrderWithDebtorAccountTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        // optional debtor account
        val debtorAccount = PsuData().getDebtorAccount()
        consentRequest.data.initiation.debtorAccount(
                OBWriteDomesticStandingOrder3DataInitiationDebtorAccount()
                        .identification(debtorAccount?.Identification)
                        .name(debtorAccount?.Name)
                        .schemeName(debtorAccount?.SchemeName)
                        .secondaryIdentification(debtorAccount?.SecondaryIdentification)
        )
        // When
        val result = submitStandingOrder(consentRequest)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.charges).isNotNull().isEmpty()
        assertThat(result.data.consentId).isNotEmpty()
        assertThat(result.links.self.toString()).isEqualTo(createPaymentUrl + "/" + result.data.domesticStandingOrderId)
    }

    fun shouldCreateDomesticStandingOrder_throwsInvalidInitiationTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val (consentResponse, accessTokenAuthorizationCode) = createDomesticStandingOrderConsentsApi.createDomesticStandingOrderConsentAndAuthorize(
                consentRequest
        )

        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

        consentRequest.data.initiation.recurringPaymentAmount = OBWriteDomesticStandingOrder3DataInitiationRecurringPaymentAmount()
            .amount("123123")
            .currency("EUR")

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            submitStandingOrderForConsent(consentResponse.data.consentId, consentRequest, accessTokenAuthorizationCode)
        }

        // Then
        assertThat(exception.message.toString()).contains(OBRIErrorType.PAYMENT_INVALID_INITIATION.code.value)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(OBRIErrorType.PAYMENT_INVALID_INITIATION.httpStatus.value())
    }

    fun createDomesticStandingOrder_mandatoryFieldsTest() {
        // Given
        val consentRequest = consentFactory.createConsentWithOnlyMandatoryFieldsPopulated()
        // When
        val result = submitStandingOrder(consentRequest)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.consentId).isNotEmpty()
    }

    fun shouldCreateDomesticStandingOrder_throwsStandingOrderAlreadyExistsTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val (consentResponse, accessTokenAuthorizationCode) = createDomesticStandingOrderConsentsApi.createDomesticStandingOrderConsentAndAuthorize(
            consentRequest
        )

        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

        // When
        // Submit first payment
        submitStandingOrderForConsent(consentResponse.data.consentId, consentRequest, accessTokenAuthorizationCode)

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            // Verify we fail to submit a second payment
            submitStandingOrderForConsent(consentResponse.data.consentId, consentRequest, accessTokenAuthorizationCode)
        }

        // Then
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.PAYMENT_SUBMISSION_ALREADY_EXISTS)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(403)
    }

    fun shouldCreateDomesticStandingOrder_throwsSendInvalidFormatDetachedJwsTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val (consentResponse, accessTokenAuthorizationCode) = createDomesticStandingOrderConsentsApi.createDomesticStandingOrderConsentAndAuthorize(
            consentRequest
        )

        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = createStandingOrderRequest(consentResponse.data.consentId, consentRequest)

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

    fun shouldCreateDomesticStandingOrder_throwsNoDetachedJwsTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val (consentResponse, accessTokenAuthorizationCode) = createDomesticStandingOrderConsentsApi.createDomesticStandingOrderConsentAndAuthorize(
            consentRequest
        )

        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = createStandingOrderRequest(consentResponse.data.consentId, consentRequest)

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

    fun shouldCreateDomesticStandingOrder_throwsNotPermittedB64HeaderAddedInTheDetachedJwsTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val (consentResponse, accessTokenAuthorizationCode) = createDomesticStandingOrderConsentsApi.createDomesticStandingOrderConsentAndAuthorize(
            consentRequest
        )

        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = createStandingOrderRequest(consentResponse.data.consentId, consentRequest)

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

    fun shouldCreateDomesticStandingOrder_throwsSendInvalidKidDetachedJwsTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val (consentResponse, accessTokenAuthorizationCode) = createDomesticStandingOrderConsentsApi.createDomesticStandingOrderConsentAndAuthorize(
            consentRequest
        )

        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = createStandingOrderRequest(consentResponse.data.consentId, consentRequest)

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

    fun shouldCreateDomesticStandingOrder_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBodyTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val (consentResponse, accessTokenAuthorizationCode) = createDomesticStandingOrderConsentsApi.createDomesticStandingOrderConsentAndAuthorize(
            consentRequest
        )

        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

        val standingOrderSubmissionRequest = createStandingOrderRequest(
                consentResponse.data.consentId, consentRequest
        )

        consentResponse.data.consentId = com.forgerock.sapi.gateway.ob.uk.framework.constants.INVALID_CONSENT_ID
        val standingOrderSubmissionRequestWithInvalidConsentId = createStandingOrderRequest(
                consentResponse.data.consentId, consentRequest
        )

        val signatureWithInvalidConsentId = DefaultJwsSignatureProducer(tppResource.tpp).createDetachedSignature(
            defaultMapper.writeValueAsString(standingOrderSubmissionRequestWithInvalidConsentId)
        )

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            paymentApiClient.buildSubmitPaymentRequest(
                createPaymentUrl,
                accessTokenAuthorizationCode,
                standingOrderSubmissionRequest
            )
                .configureJwsSignatureProducer(BadJwsSignatureProducer(signatureWithInvalidConsentId)).sendRequest()
        }

        // Then
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
    }

    fun shouldCreateDomesticStandingOrder_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBodyTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val (consentResponse, accessTokenAuthorizationCode) = createDomesticStandingOrderConsentsApi.createDomesticStandingOrderConsentAndAuthorize(
            consentRequest
        )

        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = createStandingOrderRequest(consentResponse.data.consentId, consentRequest)


        val paymentSubmissionInvalidAmount = createStandingOrderRequest(consentResponse.data.consentId, consentRequest)
        paymentSubmissionInvalidAmount.data.initiation.firstPaymentAmount =
                OBWriteDomesticStandingOrder3DataInitiationFirstPaymentAmount()
                        .amount("123123")
                        .currency("EUR")

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

    fun shouldCreateDomesticStandingOrder_throwsInvalidRiskTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val (consentResponse, authorizationToken) = createDomesticStandingOrderConsentsApi.createDomesticStandingOrderConsentAndAuthorize(
            consentRequest
        )

        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

        // When

        // Alter Risk Merchant
        consentRequest.risk.merchantCategoryCode = "zzzz"

        // Submit standing order
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            submitStandingOrderForConsent(consentResponse.data.consentId, consentRequest, authorizationToken)
        }

        // Then
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.INVALID_RISK)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
    }

    fun testCreatingPaymentIsIdempotent() {
        val consentRequest = consentFactory.createConsent()
        val (consent, authorizationToken) = createDomesticStandingOrderConsentsApi.createDomesticStandingOrderConsentAndAuthorize(
            consentRequest
        )
        val paymentSubmissionRequest = createStandingOrderRequest(consent.data.consentId, consentRequest)

        val idempotencyKey = UUID.randomUUID().toString()
        val firstPaymentResponse:OBWriteDomesticStandingOrderResponse6 = paymentApiClient.newPostRequestBuilder(createPaymentUrl, authorizationToken, paymentSubmissionRequest)
            .addIdempotencyKeyHeader(idempotencyKey)
            .sendRequest()

        Assertions.assertThat(firstPaymentResponse).isNotNull()
        Assertions.assertThat(firstPaymentResponse.data).isNotNull()
        Assertions.assertThat(firstPaymentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(firstPaymentResponse.links.self.toString()).isEqualTo(createPaymentUrl + "/" + firstPaymentResponse.data.domesticStandingOrderId)

        // Submit again with same key
        val secondPaymentResponse:OBWriteDomesticStandingOrderResponse6 = paymentApiClient.newPostRequestBuilder(createPaymentUrl, authorizationToken, paymentSubmissionRequest)
            .addIdempotencyKeyHeader(idempotencyKey)
            .sendRequest()

        Assertions.assertThat(secondPaymentResponse).isEqualTo(firstPaymentResponse)
    }

    fun submitStandingOrder(consentRequest: OBWriteDomesticStandingOrderConsent5): OBWriteDomesticStandingOrderResponse6 {
        val (consent, authorizationToken) = createDomesticStandingOrderConsentsApi.createDomesticStandingOrderConsentAndAuthorize(
            consentRequest
        )
        return submitStandingOrder(consent.data.consentId, consentRequest, authorizationToken)
    }

    fun submitStandingOrder(
            consentId: String,
            consentRequest: OBWriteDomesticStandingOrderConsent5,
        authorizationToken: AccessToken
    ): OBWriteDomesticStandingOrderResponse6 {
        return submitStandingOrderForConsent(consentId, consentRequest, authorizationToken)
    }

    private fun submitStandingOrderForConsent(
            consentId: String,
            consentRequest: OBWriteDomesticStandingOrderConsent5,
            authorizationToken: AccessToken
    ): OBWriteDomesticStandingOrderResponse6 {
        val paymentSubmissionRequest = createStandingOrderRequest(consentId, consentRequest)
        return paymentApiClient.submitPayment(
            createPaymentUrl,
            authorizationToken,
            paymentSubmissionRequest
        )
    }

    private fun createStandingOrderRequest(
            consentId: String, consentRequest: OBWriteDomesticStandingOrderConsent5): OBWriteDomesticStandingOrder3 {
        return OBWriteDomesticStandingOrder3().data(
            OBWriteDomesticStandingOrder3Data()
                .consentId(consentId)
                .initiation(PaymentFactory.copyOBWriteDomesticStandingOrder3DataInitiation(consentRequest.data.initiation))
        ).risk(consentRequest.risk)
    }
}
