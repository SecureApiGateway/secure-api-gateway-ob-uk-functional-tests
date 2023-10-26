package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.payments.consents.api.v3_1_8

import assertk.assertThat
import assertk.assertions.*
import com.forgerock.sapi.gateway.framework.data.AccessToken
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.framework.consent.ConsentFactoryRegistryHolder
import com.forgerock.sapi.gateway.ob.uk.framework.consent.payment.OBWriteDomesticConsent4Factory
import com.forgerock.sapi.gateway.ob.uk.support.discovery.getPaymentsApiLinks
import com.forgerock.sapi.gateway.ob.uk.support.payment.PaymentFactory
import com.forgerock.sapi.gateway.ob.uk.support.payment.defaultPaymentScopesForAccessToken
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.github.kittinunf.fuel.core.FuelError
import uk.org.openbanking.datamodel.payment.*
import uk.org.openbanking.testsupport.payment.OBWriteDomesticConsentTestDataFactory.aValidOBWriteDomesticConsent4

class GetDomesticPaymentsConsentFundsConfirmation(
    val version: OBVersion,
    val tppResource: CreateTppCallback.TppResource
) {
    private val createDomesticPaymentsConsentsApi = CreateDomesticPaymentsConsents(version, tppResource)
    private val paymentLinks = getPaymentsApiLinks(version)
    private val paymentApiClient = tppResource.tpp.paymentApiClient
    private val consentFactory: OBWriteDomesticConsent4Factory = ConsentFactoryRegistryHolder.consentFactoryRegistry.getConsentFactory(
        OBWriteDomesticConsent4Factory::class.java)

    fun shouldGetDomesticPaymentConsentsFundsConfirmation_false() {
        // Given
        val consentRequest = consentFactory.createConsent()
        consentRequest.data.initiation.instructedAmount.amount("1000000")
        // When
        val (result, consent) = createConsentAndGetFundsConfirmation(consentRequest)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isFalse()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
        assertThat(result.links.self.toString()).isEqualTo( PaymentFactory.urlWithConsentId(
                paymentLinks.GetDomesticPaymentConsentsConsentIdFundsConfirmation,
                consent.data.consentId
        ))
    }

    fun shouldGetDomesticPaymentConsentsFundsConfirmation_throwsWrongGrantType() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val (consent, _) = createDomesticPaymentsConsentsApi.createDomesticPaymentsConsentAndAuthorize(
            consentRequest
        )

        // client_credentials access token must not allow us to get the funds confirmation
        val accessTokenClientCredentials = tppResource.tpp.getClientCredentialsAccessToken(
            defaultPaymentScopesForAccessToken
        )
        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            getFundsConfirmation(consent, accessTokenClientCredentials)
        }
        // Then
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
    }

    fun shouldGetDomesticPaymentConsentsFundsConfirmation_throwsInvalidConsentStatus_Test() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val (consentResponse, accessTokenAuthorizationCode) = createDomesticPaymentsConsentsApi.createDomesticPaymentsConsentAndAuthorize(
            consentRequest
        )

        val paymentSubmissionRequest = createPaymentRequest(consentResponse.data.consentId, consentRequest)

        val payment: OBWriteDomesticResponse5 = paymentApiClient.submitPayment(
            paymentLinks.CreateDomesticPayment,
            accessTokenAuthorizationCode,
            paymentSubmissionRequest
        )

        //An ASPSP can only respond to a funds confirmation request if the domestic-payment-consent resource has an Authorised status.
        // If the status is not Authorised, an ASPSP must respond with a 400 (Bad Request) and a UK.OBIE.Resource.InvalidConsentStatus error code

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            getFundsConfirmation(consentResponse, accessTokenAuthorizationCode)
        }

        // Then
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.INVALID_CONSENT_STATUS)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
    }

    fun shouldGetDomesticPaymentConsentsFundsConfirmation_true() {
        // Given
        val consentRequest = consentFactory.createConsent()
        consentRequest.data.initiation.instructedAmount.amount("3")
        // When
        val (result, consent) = createConsentAndGetFundsConfirmation(consentRequest)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isTrue()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
        assertThat(result.links.self.toString()).isEqualTo( PaymentFactory.urlWithConsentId(
                paymentLinks.GetDomesticPaymentConsentsConsentIdFundsConfirmation,
                consent.data.consentId
        ))
    }

    fun shouldFailIfAccessTokenConsentIdDoesNotMatchRequestUriPathParamConsentId() {
        val consentRequest = consentFactory.createConsent()
        consentRequest.data.initiation.instructedAmount.amount("3")
        val (firstConsent, firstAccessToken) = createDomesticPaymentsConsentsApi.createDomesticPaymentsConsentAndAuthorize(
            consentRequest
        )
        val fundsConfirmationResult = getFundsConfirmation(firstConsent, firstAccessToken)

        assertThat(fundsConfirmationResult).isNotNull()
        assertThat(fundsConfirmationResult.data).isNotNull()
        assertThat(fundsConfirmationResult.data.fundsAvailableResult).isNotNull()
        assertThat(fundsConfirmationResult.data.fundsAvailableResult.fundsAvailable).isTrue()
        assertThat(fundsConfirmationResult.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
        assertThat(fundsConfirmationResult.links.self.toString()).isEqualTo( PaymentFactory.urlWithConsentId(
            paymentLinks.GetDomesticPaymentConsentsConsentIdFundsConfirmation,
            firstConsent.data.consentId
        ))

        // Create a second consent and get a second access token
        val (secondConsent, secondAccessToken) = createDomesticPaymentsConsentsApi.createDomesticPaymentsConsentAndAuthorize(
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

    private fun createConsentAndGetFundsConfirmation(consentRequest: OBWriteDomesticConsent4): Pair<OBWriteFundsConfirmationResponse1, OBWriteDomesticConsentResponse5> {
        val (consent, accessTokenAuthorizationCode) = createDomesticPaymentsConsentsApi.createDomesticPaymentsConsentAndAuthorize(
            consentRequest
        )
        return getFundsConfirmation(consent, accessTokenAuthorizationCode) to consent
    }

    private fun getFundsConfirmation(
        consent: OBWriteDomesticConsentResponse5,
        accessTokenAuthorizationCode: AccessToken
    ): OBWriteFundsConfirmationResponse1 {
        return paymentApiClient.sendGetRequest(
            PaymentFactory.urlWithConsentId(
                paymentLinks.GetDomesticPaymentConsentsConsentIdFundsConfirmation,
                consent.data.consentId
            ), accessTokenAuthorizationCode
        )
    }

    private fun createPaymentRequest(consentId:  String, consentRequest: OBWriteDomesticConsent4): OBWriteDomestic2 {
        return OBWriteDomestic2().data(
                OBWriteDomestic2Data()
                        .consentId(consentId)
                        .initiation(PaymentFactory.copyOBWriteDomestic2DataInitiation(consentRequest.data.initiation))
        ).risk(consentRequest.risk)
    }
}