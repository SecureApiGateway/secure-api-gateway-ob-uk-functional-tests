package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.vrp.consents.api.v3_1_10

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import com.forgerock.sapi.gateway.framework.data.AccessToken
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.support.discovery.getPaymentsApiLinks
import com.forgerock.sapi.gateway.ob.uk.support.payment.PaymentFactory
import com.forgerock.sapi.gateway.ob.uk.support.payment.defaultPaymentScopesForAccessToken
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.github.kittinunf.fuel.core.FuelError
import uk.org.openbanking.datamodel.payment.OBWriteFundsConfirmationResponse1
import uk.org.openbanking.datamodel.vrp.OBDomesticVRPConsentRequest
import uk.org.openbanking.datamodel.vrp.OBDomesticVRPConsentResponse
import uk.org.openbanking.datamodel.vrp.OBDomesticVRPRequest
import uk.org.openbanking.datamodel.vrp.OBDomesticVRPRequestData
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
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
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
                paymentLinks.GetDomesticVRPConsentsConsentIdFundsConfirmation,
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

