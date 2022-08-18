package com.forgerock.uk.openbanking.tests.functional.account.access.consents.api.v3_1_8

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.matchesPredicate
import com.forgerock.securebanking.framework.conditions.Status
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.support.account.AccountFactory
import com.forgerock.uk.openbanking.support.account.AccountRS
import com.forgerock.uk.openbanking.support.discovery.getAccountsApiLinks
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import uk.org.openbanking.datamodel.account.OBExternalPermissions1Code
import uk.org.openbanking.datamodel.account.OBReadConsentResponse1

class AccountAccessConsent(val version: OBVersion, val tppResource: CreateTppCallback.TppResource) {

    private val accountsApiLinks = getAccountsApiLinks(version)

    fun createAccountAccessConsentTest() {
        // Given
        val permissions = listOf(OBExternalPermissions1Code.READACCOUNTSDETAIL)
        // When
        val consentResponse = createAccountAccessConsent(permissions)

        // Then
        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertEquals(consentResponse.data.permissions, permissions)
        Assertions.assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)
    }

    fun deleteAccountAccessConsentTest() {
        // Given
        val consent = createAccountAccessConsent(listOf(OBExternalPermissions1Code.READACCOUNTSDETAIL))
        // When
        val deletedConsent = deleteAccountAccessConsent(consent.data.consentId)

        // Then
        assertThat(deletedConsent).isNotNull()
        assertThat(deletedConsent.data).isNotNull()
        assertThat(deletedConsent.data.consentId).isEqualTo(consent.data.consentId)
        Assertions.assertThat(deletedConsent.data.status.toString()).`is`(Status.consentCondition)

        // Verify we cannot get the consent anymore
        val error = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            getAccountAccessConsent(
                consent.data.consentId
            )
        }
        assertThat(error.message).matchesPredicate { msg -> msg!!.contains("\"ErrorCode\":\"UK.OBIE.NotFound\",\"Message\":\"Resource not found\"") }
    }

    fun getAccountAccessConsentTest() {
        // Given
        val originalConsentResponse = createAccountAccessConsent(listOf(OBExternalPermissions1Code.READACCOUNTSDETAIL))
        // When
        val latestConsentResponse = getAccountAccessConsent(originalConsentResponse.data.consentId)
        // Then
        assertEquals(originalConsentResponse, latestConsentResponse)
    }

    fun createAccountAccessConsent(permissions: List<OBExternalPermissions1Code>): OBReadConsentResponse1 {
        val consentRequest = AccountFactory.obReadConsent1(permissions)
        return AccountRS().consent(
            accountsApiLinks.CreateAccountAccessConsent,
            consentRequest,
            tppResource.tpp
        )
    }

    fun deleteAccountAccessConsent(consentId: String): OBReadConsentResponse1 {
        return AccountRS().deleteConsent(
            AccountFactory.urlWithConsentId(
                accountsApiLinks.DeleteAccountAccessConsent,
                consentId
            ),
            tppResource.tpp
        )
    }

    fun getAccountAccessConsent(consentId: String): OBReadConsentResponse1 {
        return AccountRS().getConsent(
            AccountFactory.urlWithConsentId(
                accountsApiLinks.GetAccountAccessConsent,
                consentId
            ),
            tppResource.tpp
        )
    }
}
