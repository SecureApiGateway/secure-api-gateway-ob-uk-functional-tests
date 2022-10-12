package com.forgerock.uk.openbanking.tests.functional.account.access.consents.api.v3_1_8

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.matchesPredicate
import com.forgerock.securebanking.framework.conditions.Status
import com.forgerock.securebanking.framework.configuration.psu
import com.forgerock.securebanking.framework.data.AccessToken
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.support.account.AccountAS
import com.forgerock.uk.openbanking.support.account.AccountFactory
import com.forgerock.uk.openbanking.support.account.AccountRS
import com.forgerock.uk.openbanking.support.discovery.getAccountsApiLinks
import com.forgerock.uk.openbanking.tests.functional.account.access.consents.AccountAccessConsentApi
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import uk.org.openbanking.datamodel.account.OBExternalPermissions1Code
import uk.org.openbanking.datamodel.account.OBReadConsentResponse1

class AccountAccessConsent(val version: OBVersion, val tppResource: CreateTppCallback.TppResource): AccountAccessConsentApi {

    private val accountsApiLinks = getAccountsApiLinks(version)

    fun createAccountAccessConsentTest() {
        // Given
        val permissions = listOf(OBExternalPermissions1Code.READACCOUNTSDETAIL)
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
        val consent = createConsent(listOf(OBExternalPermissions1Code.READACCOUNTSDETAIL))
        // When
        deleteConsent(consent.data.consentId)

        // Verify we cannot get the consent anymore
        val error = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            getConsent(
                consent.data.consentId
            )
        }
        assertThat(error.message).matchesPredicate { msg -> msg!!.contains("\"ErrorCode\":\"UK.OBIE.NotFound\",\"Message\":\"Resource not found\"") }
    }

    fun getAccountAccessConsentTest() {
        // Given
        val originalConsentResponse = createConsent(listOf(OBExternalPermissions1Code.READACCOUNTSDETAIL))
        // When
        val latestConsentResponse = getConsent(originalConsentResponse.data.consentId)
        // Then
        assertEquals(originalConsentResponse, latestConsentResponse)
    }

    override fun createConsent(permissions: List<OBExternalPermissions1Code>): OBReadConsentResponse1 {
        val consentRequest = AccountFactory.obReadConsent1(permissions)
        return AccountRS().consent(
            accountsApiLinks.CreateAccountAccessConsent,
            consentRequest,
            tppResource.tpp
        )
    }

    override fun createConsentAndGetAccessToken(permissions: List<OBExternalPermissions1Code>): Pair<OBReadConsentResponse1, AccessToken> {
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
            AccountFactory.urlWithConsentId(
                accountsApiLinks.DeleteAccountAccessConsent,
                consentId
            ),
            tppResource.tpp
        )
    }

    override fun getConsent(consentId: String): OBReadConsentResponse1 {
        return AccountRS().getConsent(
            AccountFactory.urlWithConsentId(
                accountsApiLinks.GetAccountAccessConsent,
                consentId
            ),
            tppResource.tpp
        )
    }
}
