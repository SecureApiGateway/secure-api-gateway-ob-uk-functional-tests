package com.forgerock.uk.openbanking.tests.functional.payment.domestic.vrp.consents.api.v3_1_10

import assertk.assertThat
import assertk.assertions.*
import com.forgerock.securebanking.framework.data.AccessToken
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.framework.errors.INVALID_CONSENT_STATUS
import com.forgerock.uk.openbanking.framework.errors.UNAUTHORIZED
import com.forgerock.uk.openbanking.support.discovery.getPaymentsApiLinks
import com.forgerock.uk.openbanking.support.payment.PaymentFactory
import com.forgerock.uk.openbanking.support.payment.defaultPaymentScopesForAccessToken
import com.github.kittinunf.fuel.core.FuelError
import uk.org.openbanking.datamodel.payment.OBWriteFundsConfirmationResponse1
import uk.org.openbanking.datamodel.vrp.*
import uk.org.openbanking.testsupport.vrp.OBDomesticVrpConsentRequestTestDataFactory.aValidOBDomesticVRPConsentRequest

class GetDomesticVrpConsentsFundsConfirmation(
    val version: OBVersion,
    val tppResource: CreateTppCallback.TppResource
) {
    private val createDomesticVrpConsentsApi = CreateDomesticVrpConsents(version, tppResource)
    private val paymentLinks = getPaymentsApiLinks(version)
    private val paymentApiClient = tppResource.tpp.paymentApiClient

    fun shouldGetDomesticVrpPaymentConsentsFundsConfirmation_false() {
        // Given
        val consentRequest = aValidOBDomesticVRPConsentRequest()
        consentRequest.data.controlParameters.maximumIndividualAmount.amount("1000000")

        // When
        val result = createConsentAndGetFundsConfirmation(consentRequest)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isFalse()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    fun shouldGetDomesticVrpPaymentConsentsFundsConfirmation_throwsWrongGrantType() {
        // Given
        val consentRequest = aValidOBDomesticVRPConsentRequest()
        val (consent, _) = createDomesticVrpConsentsApi.createDomesticVrpConsentAndAuthorize(
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
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
    }

    fun shouldGetDomesticVrpPaymentConsentsFundsConfirmation_throwsInvalidConsentStatus_Test() {
        // Given
        val consentRequest = aValidOBDomesticVRPConsentRequest()
        val (consent, accessTokenAuthorizationCode) = createDomesticVrpConsentsApi.createDomesticVrpConsentAndAuthorize(
            consentRequest
        )
        val patchedConsent = createDomesticVrpConsentsApi.getPatchedConsent(consent)
        val paymentSubmissionRequest = createPaymentRequest(patchedConsent)

        val payment: OBDomesticVRPConsentResponse = paymentApiClient.submitPayment(
            paymentLinks.CreateDomesticVrpPayment,
            accessTokenAuthorizationCode,
            paymentSubmissionRequest
        )

        //An ASPSP can only respond to a funds confirmation request if the domestic-vrp-consent resource has an Authorised status.
        // If the status is not Authorised, an ASPSP must respond with a 400 (Bad Request) and a UK.OBIE.Resource.InvalidConsentStatus error code

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            getFundsConfirmation(consent, accessTokenAuthorizationCode)
        }

        // Then
        assertThat(exception.message.toString()).contains(INVALID_CONSENT_STATUS)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
    }

    fun shouldGetDomesticVrpPaymentConsentsFundsConfirmation_true() {
        // Given
        val consentRequest = aValidOBDomesticVRPConsentRequest()
        consentRequest.data.controlParameters.maximumIndividualAmount.amount("5")
        // When
        val result = createConsentAndGetFundsConfirmation(consentRequest)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isTrue()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    private fun createConsentAndGetFundsConfirmation(consentRequest: OBDomesticVRPConsentRequest): OBWriteFundsConfirmationResponse1 {
        val (consent, accessTokenAuthorizationCode) = createDomesticVrpConsentsApi.createDomesticVrpConsentAndAuthorize(
            consentRequest
        )
        return getFundsConfirmation(consent, accessTokenAuthorizationCode)
    }

    private fun getFundsConfirmation(
        consent: OBDomesticVRPConsentResponse,
        accessTokenAuthorizationCode: AccessToken
    ): OBWriteFundsConfirmationResponse1 {
        return paymentApiClient.sendGetRequest(
            PaymentFactory.urlWithConsentId(
                paymentLinks.CreateDomesticVRPConsentsConsentIdFundsConfirmation,
                consent.data.consentId
            ), accessTokenAuthorizationCode
        )
    }

    private fun createPaymentRequest(patchedConsent: OBDomesticVRPConsentResponse): OBDomesticVRPRequest {
        return OBDomesticVRPRequest().data(
            OBDomesticVRPRequestData()
                .consentId(patchedConsent.data.consentId)
                //.initiation(PaymentFactory.OBDomesticVRPInitiation(patchedConsent.data.initiation))
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)
    }

}

