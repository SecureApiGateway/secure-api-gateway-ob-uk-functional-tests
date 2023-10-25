package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.scheduled.payments.api.v3_1_8

import com.forgerock.sapi.gateway.framework.conditions.Status
import com.forgerock.sapi.gateway.framework.data.AccessToken
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.http.fuel.defaultMapper
import com.forgerock.sapi.gateway.ob.uk.common.error.OBRIErrorType
import com.forgerock.sapi.gateway.ob.uk.framework.consent.ConsentFactoryRegistryHolder
import com.forgerock.sapi.gateway.ob.uk.framework.consent.payment.OBWriteDomesticConsent4Factory
import com.forgerock.sapi.gateway.ob.uk.framework.consent.payment.OBWriteDomesticScheduledConsent4Factory
import com.forgerock.sapi.gateway.ob.uk.framework.constants.INVALID_CONSENT_ID
import com.forgerock.sapi.gateway.ob.uk.support.discovery.getPaymentsApiLinks
import com.forgerock.sapi.gateway.ob.uk.support.payment.*
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.scheduled.payments.consents.api.v3_1_8.CreateDomesticScheduledPaymentsConsents
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.github.kittinunf.fuel.core.FuelError
import org.assertj.core.api.Assertions.assertThat
import uk.org.openbanking.datamodel.payment.*
import uk.org.openbanking.testsupport.payment.OBWriteDomesticScheduledConsentTestDataFactory
import java.util.UUID

class CreateDomesticScheduledPayment(val version: OBVersion, val tppResource: CreateTppCallback.TppResource) {

    private val createDomesticScheduledPaymentsConsents = CreateDomesticScheduledPaymentsConsents(version, tppResource)
    private val paymentApiClient = tppResource.tpp.paymentApiClient
    private val paymentLinks = getPaymentsApiLinks(version)
    private val createPaymentUrl = paymentLinks.CreateDomesticScheduledPayment
    private val consentFactory =
        ConsentFactoryRegistryHolder.consentFactoryRegistry.getConsentFactory(OBWriteDomesticScheduledConsent4Factory::class.java)

    fun createDomesticScheduledPaymentsTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        // When
        val paymentResponse = submitPayment(consentRequest)

        // Then
        assertThat(paymentResponse).isNotNull()
        assertThat(paymentResponse.data).isNotNull()
        assertThat(paymentResponse.data.charges).isNotNull().isEmpty()
        assertThat(paymentResponse.data.consentId).isNotEmpty()
        assertThat(paymentResponse.links.self.toString()).isEqualTo(createPaymentUrl + "/" + paymentResponse.data.domesticScheduledPaymentId)
    }

    fun createDomesticScheduledPaymentsWithDebtorAccountTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        // optional debtor account
        val debtorAccount = PsuData().getDebtorAccount()
        consentRequest.data.initiation.debtorAccount(
            OBWriteDomestic2DataInitiationDebtorAccount()
                .identification(debtorAccount.Identification)
                .name(debtorAccount.Name)
                .schemeName(debtorAccount.SchemeName)
                .secondaryIdentification(debtorAccount.SecondaryIdentification)
        )

        // When
        val paymentResponse = submitPayment(consentRequest)

        // Then
        assertThat(paymentResponse).isNotNull()
        assertThat(paymentResponse.data).isNotNull()
        assertThat(paymentResponse.data.charges).isNotNull().isEmpty()
        assertThat(paymentResponse.data.consentId).isNotEmpty()
        assertThat(paymentResponse.links.self.toString()).isEqualTo(createPaymentUrl + "/" + paymentResponse.data.domesticScheduledPaymentId)
    }

    fun shouldCreateDomesticScheduledPayments_throwsInvalidInitiation() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val (consentResponse, accessTokenAuthorizationCode) = createDomesticScheduledPaymentsConsents.createDomesticScheduledPaymentConsentAndAuthorize(
            consentRequest
        )
        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

        consentRequest.data.initiation.instructedAmount = OBWriteDomestic2DataInitiationInstructedAmount()
            .amount("123123")
            .currency("EUR")
        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            submitPaymentForConsent(consentResponse.data.consentId, consentRequest, accessTokenAuthorizationCode)
        }

        // Then
        assertThat(exception.message.toString()).contains(OBRIErrorType.PAYMENT_INVALID_INITIATION.code.value)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(OBRIErrorType.PAYMENT_INVALID_INITIATION.httpStatus.value())
    }


    fun shouldCreateDomesticScheduledPayments_throwsPaymentAlreadyExists() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val (consentResponse, accessTokenAuthorizationCode) = createDomesticScheduledPaymentsConsents.createDomesticScheduledPaymentConsentAndAuthorize(
            consentRequest
        )
        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

        // Submit first payment
        submitPaymentForConsent(consentResponse.data.consentId, consentRequest, accessTokenAuthorizationCode)

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            // Verify we fail to submit a second payment
            submitPaymentForConsent(consentResponse.data.consentId, consentRequest, accessTokenAuthorizationCode)
        }

        // Then
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.PAYMENT_SUBMISSION_ALREADY_EXISTS)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(403)
    }

    fun shouldCreateDomesticScheduledPayments_throwsSendInvalidFormatDetachedJws() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val (consentResponse, accessTokenAuthorizationCode) = createDomesticScheduledPaymentsConsents.createDomesticScheduledPaymentConsentAndAuthorize(
            consentRequest
        )

        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = createPaymentRequest(consentResponse.data.consentId, consentRequest)

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

    fun shouldCreateDomesticScheduledPayments_throwsNoDetachedJwsTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val (consentResponse, accessTokenAuthorizationCode) = createDomesticScheduledPaymentsConsents.createDomesticScheduledPaymentConsentAndAuthorize(
            consentRequest
        )

        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = createPaymentRequest(consentResponse.data.consentId, consentRequest)

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

    fun shouldCreateDomesticScheduledPayments_throwsNotPermittedB64HeaderAddedInTheDetachedJwsTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val (consentResponse, accessTokenAuthorizationCode) = createDomesticScheduledPaymentsConsents.createDomesticScheduledPaymentConsentAndAuthorize(
            consentRequest
        )

        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = createPaymentRequest(consentResponse.data.consentId, consentRequest)

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

    fun shouldCreateDomesticScheduledPayments_throwsSendInvalidKidDetachedJwsTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val (consentResponse, accessTokenAuthorizationCode) = createDomesticScheduledPaymentsConsents.createDomesticScheduledPaymentConsentAndAuthorize(
            consentRequest
        )

        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = createPaymentRequest(consentResponse.data.consentId, consentRequest)

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

    fun shouldCreateDomesticScheduledPayments_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBodyTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val (consentResponse, accessTokenAuthorizationCode) = createDomesticScheduledPaymentsConsents.createDomesticScheduledPaymentConsentAndAuthorize(
            consentRequest
        )

        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = createPaymentRequest(consentResponse.data.consentId, consentRequest)

        val paymentSubmissionWithInvalidConsentId = createPaymentRequest(INVALID_CONSENT_ID, consentRequest)

        val signatureWithInvalidConsentId = DefaultJwsSignatureProducer(tppResource.tpp).createDetachedSignature(
            defaultMapper.writeValueAsString(paymentSubmissionWithInvalidConsentId)
        )

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            paymentApiClient.buildSubmitPaymentRequest(
                createPaymentUrl,
                accessTokenAuthorizationCode,
                paymentSubmissionRequest
            )
                .configureJwsSignatureProducer(BadJwsSignatureProducer(signatureWithInvalidConsentId)).sendRequest()
        }

        // Then
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
    }

    fun shouldCreateDomesticScheduledPayments_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBodyTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val (consentResponse, accessTokenAuthorizationCode) = createDomesticScheduledPaymentsConsents.createDomesticScheduledPaymentConsentAndAuthorize(
            consentRequest
        )

        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = createPaymentRequest(consentResponse.data.consentId, consentRequest)

        val paymentSubmissionInvalidAmount = createPaymentRequest(consentResponse.data.consentId, consentRequest)
        paymentSubmissionInvalidAmount.data.initiation.instructedAmount =
            OBWriteDomestic2DataInitiationInstructedAmount()
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

    fun shouldCreateDomesticScheduledPayments_throwsInvalidRiskTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val (consentResponse, authorizationToken) = createDomesticScheduledPaymentsConsents.createDomesticScheduledPaymentConsentAndAuthorize(
            consentRequest
        )

        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

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
        val (consent, authorizationToken) = createDomesticScheduledPaymentsConsents.createDomesticScheduledPaymentConsentAndAuthorize(
            consentRequest
        )
        val paymentSubmissionRequest = createPaymentRequest(consent.data.consentId, consentRequest)

        val idempotencyKey = UUID.randomUUID().toString()
        val firstPaymentResponse:OBWriteDomesticScheduledResponse5 = paymentApiClient.newPostRequestBuilder(createPaymentUrl, authorizationToken, paymentSubmissionRequest)
            .addIdempotencyKeyHeader(idempotencyKey)
            .sendRequest()

        assertThat(firstPaymentResponse).isNotNull()
        assertThat(firstPaymentResponse.data).isNotNull()
        assertThat(firstPaymentResponse.data.consentId).isNotEmpty()
        assertThat(firstPaymentResponse.data.charges).isEmpty()
        assertThat(firstPaymentResponse.links.self.toString()).isEqualTo(createPaymentUrl + "/" + firstPaymentResponse.data.domesticScheduledPaymentId)

        // Submit again with same key
        val secondPaymentResponse:OBWriteDomesticScheduledResponse5 = paymentApiClient.newPostRequestBuilder(createPaymentUrl, authorizationToken, paymentSubmissionRequest)
            .addIdempotencyKeyHeader(idempotencyKey)
            .sendRequest()

        assertThat(secondPaymentResponse).isEqualTo(firstPaymentResponse)
    }

    fun submitPayment(consentRequest: OBWriteDomesticScheduledConsent4): OBWriteDomesticScheduledResponse5 {
        val (consent, authorizationToken) = createDomesticScheduledPaymentsConsents.createDomesticScheduledPaymentConsentAndAuthorize(
            consentRequest
        )
        return submitPayment(consent.data.consentId, consentRequest, authorizationToken)
    }

    fun submitPayment(
        consentId: String,
        consentRequest: OBWriteDomesticScheduledConsent4,
        authorizationToken: AccessToken
    ): OBWriteDomesticScheduledResponse5 {
        return submitPaymentForConsent(consentId, consentRequest, authorizationToken)
    }

    private fun submitPaymentForConsent(
        consentId: String,
        consentRequest: OBWriteDomesticScheduledConsent4,
        authorizationToken: AccessToken
    ): OBWriteDomesticScheduledResponse5 {
        val paymentSubmissionRequest = createPaymentRequest(consentId, consentRequest)
        return paymentApiClient.submitPayment(
            createPaymentUrl,
            authorizationToken,
            paymentSubmissionRequest
        )
    }

    private fun createPaymentRequest(
        consentId: String,
        consentRequest: OBWriteDomesticScheduledConsent4
    ): OBWriteDomesticScheduled2 {
        return OBWriteDomesticScheduled2().data(
            OBWriteDomesticScheduled2Data()
                .consentId(consentId)
                .initiation(PaymentFactory.copyOBWriteDomesticScheduled2DataInitiation(consentRequest.data.initiation))
        ).risk(consentRequest.risk)
    }
}
