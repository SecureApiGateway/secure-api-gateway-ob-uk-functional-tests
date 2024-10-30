package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.access.consents.api.v4_0_0

import assertk.assertThat
import assertk.assertions.isNotNull
import assertk.assertions.matchesPredicate
import com.forgerock.sapi.gateway.framework.conditions.Status
import com.forgerock.sapi.gateway.framework.configuration.psu
import com.forgerock.sapi.gateway.framework.data.AccessToken
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.support.account.AccountAS
import com.forgerock.sapi.gateway.ob.uk.support.account.AccountFactoryV4
import com.forgerock.sapi.gateway.ob.uk.support.account.AccountRS
import com.forgerock.sapi.gateway.ob.uk.support.discovery.getAccountsApiLinks
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.access.consents.AccountAccessConsentApiV4
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import uk.org.openbanking.datamodel.v4.account.OBInternalPermissions1Code
import uk.org.openbanking.datamodel.v4.account.OBReadConsentResponse1

class AccountAccessConsent(val version: OBVersion, val tppResource: CreateTppCallback.TppResource): AccountAccessConsentApiV4 {

    private val accountsApiLinks = getAccountsApiLinks(version)

    fun createAccountAccessConsentTest() {
        // Given
        val permissions = listOf(OBInternalPermissions1Code.READACCOUNTSDETAIL)
        // When
        val consentResponse = createConsent(permissions)

        // Then
        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertEquals(consentResponse.data.permissions, permissions)
        Assertions.assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)
    }

    fun deleteAccountAccessConsentTest() {
        // Given
        val consent = createConsent(listOf(OBInternalPermissions1Code.READACCOUNTSDETAIL))
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

    fun getAccountAccessConsentTest() {
        // Given
        val originalConsentResponse = createConsent(listOf(OBInternalPermissions1Code.READACCOUNTSDETAIL))
        // When
        val latestConsentResponse = getConsent(originalConsentResponse.data.consentId)
        // Then
        assertEquals(originalConsentResponse, latestConsentResponse)
    }

    override fun createConsent(permissions: List<OBInternalPermissions1Code>): OBReadConsentResponse1 {
        val consentRequest = AccountFactoryV4.obReadConsent1(permissions)
        return AccountRS().consent(
            accountsApiLinks.CreateAccountAccessConsent,
            consentRequest,
            tppResource.tpp
        )
    }

    override fun createConsentAndGetAccessToken(permissions: List<OBInternalPermissions1Code>): Pair<OBReadConsentResponse1, AccessToken> {
        val consent = createConsent(permissions)
        val accessToken = AccountAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        return consent to accessToken
    }

    override fun deleteConsent(consentId: String) {
        AccountRS().deleteConsent(
            AccountFactoryV4.urlWithConsentId(
                accountsApiLinks.DeleteAccountAccessConsent,
                consentId
            ),
            tppResource.tpp
        )
    }

    override fun getConsent(consentId: String): OBReadConsentResponse1 {
        return AccountRS().getConsent(
            AccountFactoryV4.urlWithConsentId(
                accountsApiLinks.GetAccountAccessConsent,
                consentId
            ),
            tppResource.tpp
        )
    }
}
