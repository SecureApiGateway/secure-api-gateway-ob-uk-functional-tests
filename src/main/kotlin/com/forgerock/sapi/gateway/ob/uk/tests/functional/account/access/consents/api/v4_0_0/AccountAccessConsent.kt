package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.access.consents.api.v4_0_0

import assertk.assertThat
import assertk.assertions.isNotNull
import assertk.assertions.matchesPredicate
import com.forgerock.sapi.gateway.framework.conditions.Status
import com.forgerock.sapi.gateway.framework.conditions.StatusV4
import com.forgerock.sapi.gateway.framework.configuration.psu
import com.forgerock.sapi.gateway.framework.data.AccessToken
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.support.account.AccountAS
import com.forgerock.sapi.gateway.ob.uk.support.account.AccountRS
import com.forgerock.sapi.gateway.ob.uk.support.account.v4.AccountFactory
import com.forgerock.sapi.gateway.ob.uk.support.discovery.getAccountsApiLinks
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.access.consents.v4_0_0.AccountAccessConsentApi
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import uk.org.openbanking.datamodel.v3.common.OBExternalPermissions1Code
import uk.org.openbanking.datamodel.v4.account.OBInternalPermissions1Code
import uk.org.openbanking.datamodel.v4.account.OBReadConsentResponse1

class AccountAccessConsent(val version: OBVersion, val tppResource: CreateTppCallback.TppResource): AccountAccessConsentApi {

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
        Assertions.assertThat(consentResponse.data.status.toString()).`is`(StatusV4.consentCondition)
    }

    fun createAccountAccessConsent_consentV3Test() {
        // Given
        val permissions = listOf(OBExternalPermissions1Code.READACCOUNTSDETAIL)
        // When
        val consentResponse = createConsentV3(permissions)

        // Then
        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertEquals(consentResponse.data.permissions.toString(), permissions.toString())
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
        val consentRequest = AccountFactory.obReadConsent1(permissions)
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

    fun createConsentV3(permissions: List<OBExternalPermissions1Code>): uk.org.openbanking.datamodel.v3.account.OBReadConsentResponse1 {
        val consentRequest = com.forgerock.sapi.gateway.ob.uk.support.account.AccountFactory.obReadConsent1(permissions)
        return AccountRS().consent(
            getAccountsApiLinks(OBVersion.v3_1_10).CreateAccountAccessConsent,
            consentRequest,
            tppResource.tpp
        )
    }

    fun createConsentAndGetAccessToken_consentV3(): Pair<uk.org.openbanking.datamodel.v3.account.OBReadConsentResponse1, AccessToken> {
        val consent = createConsentV3(listOf(OBExternalPermissions1Code.READACCOUNTSDETAIL))
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
