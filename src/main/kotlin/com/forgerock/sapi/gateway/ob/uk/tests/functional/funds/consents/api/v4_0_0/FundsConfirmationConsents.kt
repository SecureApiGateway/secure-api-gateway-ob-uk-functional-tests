package com.forgerock.sapi.gateway.ob.uk.tests.functional.funds.consents.api.v4_0_0

import assertk.assertThat
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import assertk.assertions.matchesPredicate
import com.forgerock.sapi.gateway.framework.conditions.StatusV4
import com.forgerock.sapi.gateway.framework.configuration.psu
import com.forgerock.sapi.gateway.framework.data.AccessToken
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.support.discovery.getFundsApiLinks
import com.forgerock.sapi.gateway.ob.uk.support.funds.FundsConfirmationAS
import com.forgerock.sapi.gateway.ob.uk.support.funds.v4.FundsConfirmationConsentFactory
import com.forgerock.sapi.gateway.ob.uk.support.funds.v4.FundsConfirmationConsentFactory.Companion.obFundsConfirmationConsent1
import com.forgerock.sapi.gateway.ob.uk.support.funds.FundsConfirmationRS
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import org.assertj.core.api.Assertions
import uk.org.openbanking.datamodel.v4.fund.OBFundsConfirmationConsent1
import uk.org.openbanking.datamodel.v4.fund.OBFundsConfirmationConsentResponse1

class FundsConfirmationConsents(val version: OBVersion, val tppResource: CreateTppCallback.TppResource) {

    private val fundsConfirmationApiLinks = getFundsApiLinks(version)

    fun createFundsConfirmationConsentTest() {
        // Given
        val consentRequest = obFundsConfirmationConsent1()
        val consent = createConsent(consentRequest)
        // Then
        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(StatusV4.consentCondition)
    }

    fun deleteFundsConfirmationConsentTest() {
        // Given
        val consentRequest = obFundsConfirmationConsent1()
        val consent = createConsent(consentRequest)
        // When
        deleteConsent(consent.data.consentId)
        // Verify we cannot get the consent anymore
        val error = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            getConsent(
                    consent.data.consentId
            )
        }
        assertThat(error.message).matchesPredicate { msg -> msg!!.contains("\"ErrorCode\":\"OBRI.Consent.Not.Found\",\"Message\":\"Consent not found\"") }
    }

    fun getFundsConfirmationConsentTest() {
        // Given
        val consentRequest = obFundsConfirmationConsent1()
        val originalConsentResponse = createConsent(consentRequest)
        // When
        val latestConsentResponse = getConsent(originalConsentResponse.data.consentId)
        // Then
        org.junit.jupiter.api.Assertions.assertEquals(originalConsentResponse, latestConsentResponse)
    }

    private fun getConsent(consentId: String): OBFundsConfirmationConsentResponse1 {
        return FundsConfirmationRS().getConsent(
                FundsConfirmationConsentFactory.urlWithConsentId(
                        fundsConfirmationApiLinks.GetFundsConfirmationConsent,
                        consentId
                ),
                tppResource.tpp
        )
    }

    private fun deleteConsent(consentId: String) {
        FundsConfirmationRS().deleteConsent(
                FundsConfirmationConsentFactory.urlWithConsentId(
                        fundsConfirmationApiLinks.DeleteFundsConfirmationConsent,
                        consentId
                ),
                tppResource.tpp
        )
    }

    private fun createConsent(consentRequest: OBFundsConfirmationConsent1): OBFundsConfirmationConsentResponse1 {
        return FundsConfirmationRS().consent(
                fundsConfirmationApiLinks.CreateFundsConfirmationConsent,
                consentRequest,
                tppResource.tpp
        )
    }

    fun createConsentAndGetAccessToken(consentRequest: OBFundsConfirmationConsent1): Pair<OBFundsConfirmationConsentResponse1, AccessToken> {
        val consent = createConsent(consentRequest)
        val accessTokenAuthorizationCode = FundsConfirmationAS().authorizeConsent(
                consent.data.consentId,
                tppResource.tpp.registrationResponse,
                psu,
                tppResource.tpp
        )
        return consent to accessTokenAuthorizationCode
    }

    fun createConsentRejectedAndGetAccessToken(consentRequest: OBFundsConfirmationConsent1): Pair<OBFundsConfirmationConsentResponse1, AccessToken> {
        val consent = createConsent(consentRequest)
        val accessTokenAuthorizationCode = FundsConfirmationAS().authorizeConsent(
                consent.data.consentId,
                tppResource.tpp.registrationResponse,
                psu,
                tppResource.tpp,
                "Rejected"
        )
        return consent to accessTokenAuthorizationCode
    }

}