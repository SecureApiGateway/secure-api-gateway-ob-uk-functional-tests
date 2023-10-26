package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.standing.orders.api.v3_1_8

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
import com.forgerock.sapi.gateway.ob.uk.framework.consent.payment.OBWriteInternationalStandingOrderConsent6Factory
import com.forgerock.sapi.gateway.ob.uk.support.discovery.getPaymentsApiLinks
import com.forgerock.sapi.gateway.ob.uk.support.payment.*
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.standing.orders.consents.api.v3_1_8.CreateInternationalStandingOrderConsents
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.github.kittinunf.fuel.core.FuelError
import org.assertj.core.api.Assertions
import uk.org.openbanking.datamodel.payment.*
import java.util.UUID

class CreateInternationalStandingOrder(val version: OBVersion, val tppResource: CreateTppCallback.TppResource) {

    private val createInternationalStandingOrderConsentsApi =
        CreateInternationalStandingOrderConsents(version, tppResource)
    private val paymentApiClient = tppResource.tpp.paymentApiClient
    private val paymentLinks = getPaymentsApiLinks(version)
    private val createPaymentUrl = paymentLinks.CreateInternationalStandingOrder
    private val consentFactory = ConsentFactoryRegistryHolder.consentFactoryRegistry.getConsentFactory(
        OBWriteInternationalStandingOrderConsent6Factory::class.java
    )

    fun createInternationalStandingOrderTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        // When
        val result = submitStandingOrder(consentRequest)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.charges).isNotNull().isEmpty()
        assertThat(result.data.consentId).isNotEmpty()
    }

    fun shouldCreateInternationalStandingOrder_throwsInvalidInitiationTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val (consentResponse, accessTokenAuthorizationCode) = createInternationalStandingOrderConsentsApi.createInternationalStandingOrderConsentAndAuthorize(
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
            submitStandingOrderForConsent(consentResponse.data.consentId, consentRequest, accessTokenAuthorizationCode)
        }

        // Then
        assertThat(exception.message.toString()).contains(OBRIErrorType.PAYMENT_INVALID_INITIATION.code.value)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(OBRIErrorType.PAYMENT_INVALID_INITIATION.httpStatus.value())
    }

    fun createInternationalStandingOrderWithDebtorAccountTest() {
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
    }

    fun createInternationalStandingOrder_mandatoryFieldsTest() {
        // Given
        val consentRequest = consentFactory.createConsentWithOnlyMandatoryFieldsPopulated()
        // When
        val result = submitStandingOrder(consentRequest)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.consentId).isNotEmpty()
    }

    fun shouldCreateInternationalStandingOrder_throwsStandingOrderAlreadyExistsTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val (consentResponse, accessTokenAuthorizationCode) = createInternationalStandingOrderConsentsApi.createInternationalStandingOrderConsentAndAuthorize(
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

    fun shouldCreateInternationalStandingOrder_throwsSendInvalidFormatDetachedJwsTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val (consentResponse, accessTokenAuthorizationCode) = createInternationalStandingOrderConsentsApi.createInternationalStandingOrderConsentAndAuthorize(
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

    fun shouldCreateInternationalStandingOrder_throwsNoDetachedJwsTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val (consentResponse, accessTokenAuthorizationCode) = createInternationalStandingOrderConsentsApi.createInternationalStandingOrderConsentAndAuthorize(
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

    fun shouldCreateInternationalStandingOrder_throwsNotPermittedB64HeaderAddedInTheDetachedJwsTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val (consentResponse, accessTokenAuthorizationCode) = createInternationalStandingOrderConsentsApi.createInternationalStandingOrderConsentAndAuthorize(
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

    fun shouldCreateInternationalStandingOrder_throwsSendInvalidKidDetachedJwsTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val (consentResponse, accessTokenAuthorizationCode) = createInternationalStandingOrderConsentsApi.createInternationalStandingOrderConsentAndAuthorize(
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

    fun shouldCreateInternationalStandingOrder_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBodyTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val (consentResponse, accessTokenAuthorizationCode) = createInternationalStandingOrderConsentsApi.createInternationalStandingOrderConsentAndAuthorize(
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

    fun shouldCreateInternationalStandingOrder_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBodyTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val (consentResponse, accessTokenAuthorizationCode) = createInternationalStandingOrderConsentsApi.createInternationalStandingOrderConsentAndAuthorize(
            consentRequest
        )

        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = createStandingOrderRequest(consentResponse.data.consentId, consentRequest)

        paymentSubmissionRequest.data.initiation.instructedAmount =
                OBWriteDomestic2DataInitiationInstructedAmount()
                        .amount("123123")
                        .currency("EUR")

        val paymentSubmissionInvalidAmount = createStandingOrderRequest(consentResponse.data.consentId, consentRequest)

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

    fun shouldCreateInternationalStandingOrder_throwsInvalidRiskTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val (consentResponse, authorizationToken) = createInternationalStandingOrderConsentsApi.createInternationalStandingOrderConsentAndAuthorize(
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
        val (consent, authorizationToken) = createInternationalStandingOrderConsentsApi.createInternationalStandingOrderConsentAndAuthorize(
            consentRequest
        )
        val paymentSubmissionRequest = createStandingOrderRequest(consent.data.consentId, consentRequest)

        val idempotencyKey = UUID.randomUUID().toString()
        val firstPaymentResponse:OBWriteInternationalStandingOrderResponse7 = paymentApiClient.newPostRequestBuilder(createPaymentUrl, authorizationToken, paymentSubmissionRequest)
            .addIdempotencyKeyHeader(idempotencyKey)
            .sendRequest()

        Assertions.assertThat(firstPaymentResponse).isNotNull()
        Assertions.assertThat(firstPaymentResponse.data).isNotNull()
        Assertions.assertThat(firstPaymentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(firstPaymentResponse.data.charges).isEmpty()
        Assertions.assertThat(firstPaymentResponse.links.self.toString()).isEqualTo(createPaymentUrl + "/" + firstPaymentResponse.data.internationalStandingOrderId)

        // Submit again with same key
        val secondPaymentResponse:OBWriteInternationalStandingOrderResponse7 = paymentApiClient.newPostRequestBuilder(createPaymentUrl, authorizationToken, paymentSubmissionRequest)
            .addIdempotencyKeyHeader(idempotencyKey)
            .sendRequest()

        Assertions.assertThat(secondPaymentResponse).isEqualTo(firstPaymentResponse)
    }

    fun submitStandingOrder(consentRequest: OBWriteInternationalStandingOrderConsent6): OBWriteInternationalStandingOrderResponse7 {
        val (consentResponse, authorizationToken) = createInternationalStandingOrderConsentsApi.createInternationalStandingOrderConsentAndAuthorize(
            consentRequest
        )
        return submitStandingOrder(consentResponse.data.consentId, consentRequest, authorizationToken)
    }

    fun submitStandingOrder(
            consentId: String,
            consentRequest: OBWriteInternationalStandingOrderConsent6,
        authorizationToken: AccessToken
    ): OBWriteInternationalStandingOrderResponse7 {
        return submitStandingOrderForConsent(consentId, consentRequest, authorizationToken)
    }

    private fun submitStandingOrderForConsent(
            consentId: String,
            consentRequest: OBWriteInternationalStandingOrderConsent6,
        authorizationToken: AccessToken
    ): OBWriteInternationalStandingOrderResponse7 {
        val paymentSubmissionRequest = createStandingOrderRequest(consentId, consentRequest)
        return paymentApiClient.submitPayment(
            createPaymentUrl,
            authorizationToken,
            paymentSubmissionRequest
        )
    }

    private fun createStandingOrderRequest(
            consentId: String,
            consentRequest: OBWriteInternationalStandingOrderConsent6
    ): OBWriteInternationalStandingOrder4 {
        return OBWriteInternationalStandingOrder4().data(
            OBWriteInternationalStandingOrder4Data()
                .consentId(consentId)
                .initiation(PaymentFactory.copyOBWriteInternationalStandingOrder4DataInitiation(consentRequest.data.initiation))
        ).risk(consentRequest.risk)
    }
}
