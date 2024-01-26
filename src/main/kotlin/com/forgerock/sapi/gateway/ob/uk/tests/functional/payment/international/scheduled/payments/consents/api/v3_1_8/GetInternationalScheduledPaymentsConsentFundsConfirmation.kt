package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.scheduled.payments.consents.api.v3_1_8

import assertk.assertThat
import assertk.assertions.*
import com.forgerock.sapi.gateway.framework.data.AccessToken
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.framework.consent.ConsentFactoryRegistryHolder
import com.forgerock.sapi.gateway.ob.uk.framework.consent.payment.OBWriteInternationalScheduledConsent5Factory
import com.forgerock.sapi.gateway.ob.uk.support.discovery.getPaymentsApiLinks
import com.forgerock.sapi.gateway.ob.uk.support.payment.PaymentFactory
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.github.kittinunf.fuel.core.FuelError
import uk.org.openbanking.datamodel.payment.*

class GetInternationalScheduledPaymentsConsentFundsConfirmation(
    val version: OBVersion,
    val tppResource: CreateTppCallback.TppResource
) {
    private val createInternationalScheduledPaymentsConsents =
        CreateInternationalScheduledPaymentsConsents(version, tppResource)
    private val paymentLinks = getPaymentsApiLinks(version)
    private val paymentApiClient = tppResource.tpp.paymentApiClient
    private val consentFactory = ConsentFactoryRegistryHolder.consentFactoryRegistry.getConsentFactory(
        OBWriteInternationalScheduledConsent5Factory::class.java
    )


    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_true_rateType_AGREED_Test() {
        // Given
        val consentRequest = consentFactory.createConsent()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType.AGREED
        consentRequest.data.initiation.instructedAmount.amount("3")

        // When
        val result = createConsentAndGetFundsConfirmation(consentRequest)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isTrue()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_true_rateType_ACTUAL_Test() {
        // Given
        val consentRequest = consentFactory.createConsent()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType.ACTUAL
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null
        consentRequest.data.initiation.instructedAmount.amount("3")

        // When
        val result = createConsentAndGetFundsConfirmation(consentRequest)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isTrue()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_true_rateType_INDICATIVE_Test() {
        // Given
        val consentRequest = consentFactory.createConsent()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType.INDICATIVE
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null
        consentRequest.data.initiation.instructedAmount.amount("3")

        // When
        val result = createConsentAndGetFundsConfirmation(consentRequest)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isTrue()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_false_rateType_AGREED_Test() {
        // Given
        val consentRequest = consentFactory.createConsent()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType.AGREED
        consentRequest.data.initiation.instructedAmount.amount("1000000")

        // When
        val result = createConsentAndGetFundsConfirmation(consentRequest)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isFalse()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_false_rateType_ACTUAL_Test() {
        // Given
        val consentRequest = consentFactory.createConsent()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType.ACTUAL
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null
        consentRequest.data.initiation.instructedAmount.amount("1000000")

        // When
        val result = createConsentAndGetFundsConfirmation(consentRequest)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isFalse()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_false_rateType_INDICATIVE_Test() {
        // Given
        val consentRequest = consentFactory.createConsent()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType.INDICATIVE
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null
        consentRequest.data.initiation.instructedAmount.amount("1000000")

        // When
        val result = createConsentAndGetFundsConfirmation(consentRequest)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isFalse()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_throwsWrongGrantType_Test() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val (consent, _) = createInternationalScheduledPaymentsConsents.createInternationalScheduledPaymentConsentAndAuthorize(
            consentRequest
        )

        // client_credentials access token must not be allowed to get the funds confirmation
        val accessTokenClientCredentials = paymentApiClient.getClientCredentialsAccessToken(tppResource.tpp)

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            getFundsConfirmation(consent, accessTokenClientCredentials)
        }

        // Then
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
    }

    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_throwsInvalidConsentStatus_Test() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val (consentResponse, authorizationToken) = createInternationalScheduledPaymentsConsents.createInternationalScheduledPaymentConsentAndAuthorize(
            consentRequest
        )

        val paymentSubmissionRequest = createPaymentRequest(consentResponse)

        val payment: OBWriteInternationalScheduledResponse6 = paymentApiClient.submitPayment(
            paymentLinks.CreateInternationalScheduledPayment,
            authorizationToken,
            paymentSubmissionRequest
        )

        //An ASPSP can only respond to a funds confirmation request if the international-payment-consent resource has an Authorised status.
        // If the status is not Authorised, an ASPSP must respond with a 400 (Bad Request) and a UK.OBIE.Resource.InvalidConsentStatus error code

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            getFundsConfirmation(consentResponse, authorizationToken)
        }

        // Then
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.INVALID_CONSENT_STATUS)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
    }

    fun shouldFailIfAccessTokenConsentIdDoesNotMatchRequestUriPathParamConsentId() {
        val consentRequest = consentFactory.createConsent()
        consentRequest.data.initiation.instructedAmount.amount("3")
        val (firstConsent, firstAccessToken) = createInternationalScheduledPaymentsConsents.createInternationalScheduledPaymentConsentAndAuthorize(
            consentRequest
        )
        val fundsConfirmationResult = getFundsConfirmation(firstConsent, firstAccessToken)

        assertThat(fundsConfirmationResult).isNotNull()
        assertThat(fundsConfirmationResult.data).isNotNull()
        assertThat(fundsConfirmationResult.data.fundsAvailableResult).isNotNull()
        assertThat(fundsConfirmationResult.data.fundsAvailableResult.fundsAvailable).isTrue()
        assertThat(fundsConfirmationResult.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()

        // Create a second consent and get a second access token
        val (secondConsent, secondAccessToken) = createInternationalScheduledPaymentsConsents.createInternationalScheduledPaymentConsentAndAuthorize(
            consentRequest
        )

        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            // Attempt to get a funds confirmation using the wrong access token (contains the secondConsentId)
            getFundsConfirmation(firstConsent, secondAccessToken)
        }

        // Then
        assertThat(exception.message.toString()).contains("consentId from the request does not match the openbanking_intent_id claim from the access token")
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)

    }

    private fun createConsentAndGetFundsConfirmation(consentRequest: OBWriteInternationalScheduledConsent5): OBWriteFundsConfirmationResponse1 {
        val (consent, accessTokenAuthorizationCode) = createInternationalScheduledPaymentsConsents.createInternationalScheduledPaymentConsentAndAuthorize(
            consentRequest
        )
        return getFundsConfirmation(consent, accessTokenAuthorizationCode)
    }

    private fun getFundsConfirmation(
        consent: OBWriteInternationalScheduledConsentResponse6,
        accessTokenAuthorizationCode: AccessToken
    ): OBWriteFundsConfirmationResponse1 {
        return paymentApiClient.sendGetRequest(
            PaymentFactory.urlWithConsentId(
                paymentLinks.GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation,
                consent.data.consentId
            ), accessTokenAuthorizationCode
        )
    }

    private fun createPaymentRequest(consentResponse: OBWriteInternationalScheduledConsentResponse6): OBWriteInternationalScheduled3 {
        return OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
                .consentId(consentResponse.data.consentId)
                .initiation(
                    PaymentFactory.mapOBWriteInternationalScheduledConsentResponse6DataInitiationToOBWriteInternationalScheduled3DataInitiation(
                        consentResponse.data.initiation
                    )
                )
        ).risk(consentResponse.risk)
    }
}