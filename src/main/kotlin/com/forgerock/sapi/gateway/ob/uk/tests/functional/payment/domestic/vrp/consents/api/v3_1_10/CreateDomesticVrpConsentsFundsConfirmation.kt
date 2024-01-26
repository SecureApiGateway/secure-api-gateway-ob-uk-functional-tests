package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.vrp.consents.api.v3_1_10

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEqualTo
import assertk.assertions.isNotNull
import com.forgerock.sapi.gateway.framework.data.AccessToken
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.framework.consent.ConsentFactoryRegistryHolder
import com.forgerock.sapi.gateway.ob.uk.framework.consent.payment.OBDomesticVRPConsentRequestFactory
import com.forgerock.sapi.gateway.ob.uk.support.discovery.getPaymentsApiLinks
import com.forgerock.sapi.gateway.ob.uk.support.payment.PaymentFactory
import com.forgerock.sapi.gateway.ob.uk.support.payment.defaultPaymentScopesForAccessToken
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.github.kittinunf.fuel.core.FuelError
import uk.org.openbanking.datamodel.vrp.OBDomesticVRPConsentRequest
import uk.org.openbanking.datamodel.vrp.OBDomesticVRPConsentResponse
import uk.org.openbanking.datamodel.vrp.OBPAFundsAvailableResult1FundsAvailable
import uk.org.openbanking.datamodel.vrp.OBVRPFundsConfirmationRequest
import uk.org.openbanking.datamodel.vrp.OBVRPFundsConfirmationRequestData
import uk.org.openbanking.datamodel.vrp.OBVRPFundsConfirmationResponse

class CreateDomesticVrpConsentsFundsConfirmation(
    val version: OBVersion,
    val tppResource: CreateTppCallback.TppResource
) {
    private val createDomesticVrpConsentsApi = CreateDomesticVrpConsents(version, tppResource)
    private val paymentLinks = getPaymentsApiLinks(version)
    private val paymentApiClient = tppResource.tpp.paymentApiClient
    private val consentFactory: OBDomesticVRPConsentRequestFactory = ConsentFactoryRegistryHolder.consentFactoryRegistry.getConsentFactory(
        OBDomesticVRPConsentRequestFactory::class.java)

    fun shouldCreateDomesticVRPConsentsConsentIdFundsConfirmation_NotAvailable() {
        // Given
        val consentRequest = consentFactory.createConsent()
        consentRequest.data.controlParameters.maximumIndividualAmount.amount("1000000")

        // When
        val (result, consent) = createConsentAndPostFundsConfirmation(consentRequest)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isEqualTo(OBPAFundsAvailableResult1FundsAvailable.NOTAVAILABLE)
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    fun shouldCreateDomesticVRPConsentsConsentIdFundsConfirmation_throwsWrongGrantType() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val (consent, _) = createDomesticVrpConsentsApi.createDomesticVrpConsentAndAuthorize(
            consentRequest
        )

        // client_credentials access token must not allow us to get the funds confirmation
        val accessTokenClientCredentials = tppResource.tpp.getClientCredentialsAccessToken(
            defaultPaymentScopesForAccessToken
        )
        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            postFundsConfirmation(consent, accessTokenClientCredentials)
        }
        // Then
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
    }

    fun shouldCreateDomesticVRPConsentsConsentIdFundsConfirmation_available() {
        // Given
        val consentRequest = consentFactory.createConsent()
        consentRequest.data.controlParameters.maximumIndividualAmount.amount("5")
        // When
        val (result, consent) = createConsentAndPostFundsConfirmation(consentRequest)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isEqualTo(OBPAFundsAvailableResult1FundsAvailable.AVAILABLE)
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    fun shouldFailIfAccessTokenConsentIdDoesNotMatchFundsConfPostRequestConsentId() {
        // Submit a valid funds conf request first
        val consentRequest = consentFactory.createConsent()
        consentRequest.data.controlParameters.maximumIndividualAmount.amount("5")
        val (result, firstConsentResponse) = createConsentAndPostFundsConfirmation(consentRequest)
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isEqualTo(OBPAFundsAvailableResult1FundsAvailable.AVAILABLE)
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()

        // Create and authorise a second consent
        val (secondConsentResponse, secondConsentAccessToken) = createDomesticVrpConsentsApi.createDomesticVrpConsentAndAuthorize(
            consentRequest
        )

        // sanity check that consentIds are different
        assertThat(firstConsentResponse.data.consentId).isNotEqualTo(secondConsentResponse.data.consentId);

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            // Attempt to get funds conf for the first consent using an accessToken for the second
            postFundsConfirmation(firstConsentResponse, secondConsentAccessToken)
        }
        // Then
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
    }

    private fun createConsentAndPostFundsConfirmation(consentRequest: OBDomesticVRPConsentRequest): Pair<OBVRPFundsConfirmationResponse, OBDomesticVRPConsentResponse> {
        val (consentResponse, accessTokenAuthorizationCode) = createDomesticVrpConsentsApi.createDomesticVrpConsentAndAuthorize(
            consentRequest
        )
        return postFundsConfirmation(consentResponse, accessTokenAuthorizationCode) to consentResponse
    }

    private fun postFundsConfirmation(
        consentResponse: OBDomesticVRPConsentResponse,
        accessTokenAuthorizationCode: AccessToken
    ): OBVRPFundsConfirmationResponse {

        return paymentApiClient.sendPostRequest(
            PaymentFactory.urlWithConsentId(
                paymentLinks.CreateDomesticVRPConsentsConsentIdFundsConfirmation,
                consentResponse.data.consentId
            ),
            accessTokenAuthorizationCode,
            createFundsConfirmationRequest(consentResponse)
        )
    }

    private fun createFundsConfirmationRequest(consentResponse: OBDomesticVRPConsentResponse): OBVRPFundsConfirmationRequest {
        return OBVRPFundsConfirmationRequest().data(
            OBVRPFundsConfirmationRequestData().consentId(consentResponse.data.consentId)
                .instructedAmount(
                    consentResponse.data.controlParameters.maximumIndividualAmount
                )
        )
    }

}

