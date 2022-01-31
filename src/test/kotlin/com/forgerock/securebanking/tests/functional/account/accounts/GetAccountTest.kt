package com.forgerock.securebanking.tests.functional.account.accounts

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.securebanking.framework.configuration.psu
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.support.account.AccountAS
import com.forgerock.securebanking.support.account.AccountFactory.Companion.obReadConsent1
import com.forgerock.securebanking.support.account.AccountRS
import com.forgerock.securebanking.support.discovery.accountAndTransaction3_1
import com.forgerock.securebanking.support.discovery.accountAndTransaction3_1_1
import com.forgerock.securebanking.support.discovery.accountAndTransaction3_1_4
import com.forgerock.securebanking.support.discovery.accountAndTransaction3_1_6
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.account.OBExternalPermissions1Code.READACCOUNTSDETAIL
import uk.org.openbanking.datamodel.account.OBReadAccount3
import uk.org.openbanking.datamodel.account.OBReadAccount5
import uk.org.openbanking.datamodel.account.OBReadConsentResponse1

class GetAccountTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccount"]
    )
    @Test
    fun shouldGetAccount_v3_1() {
        // Given
        val consentRequest = obReadConsent1(listOf(READACCOUNTSDETAIL))
        val consent = AccountRS().consent<OBReadConsentResponse1>(
            accountAndTransaction3_1.Links.links.CreateAccountAccessConsent,
            consentRequest,
            tppResource.tpp
        )
        val accessToken = AccountAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        val accountId = AccountRS().getFirstAccountId(accountAndTransaction3_1.Links.links.GetAccounts, accessToken)

        // When
        val result =
            AccountRS().getAccountData<OBReadAccount3>(
                accountAndTransaction3_1.Links.links.GetAccount,
                accessToken,
                accountId
            )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.account).isNotEmpty()
        assertThat(result.data.account.size).isEqualTo(1)
        assertThat(result.data.account[0].accountId).isEqualTo(accountId)
    }

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.1",
        operations = ["CreateAccountAccessConsent", "GetAccounts"]
    )
    @Test
    fun shouldGetAccount_v3_1_1() {
        // Given
        val consentRequest = obReadConsent1(listOf(READACCOUNTSDETAIL))
        val consent = AccountRS().consent<OBReadConsentResponse1>(
            accountAndTransaction3_1_1.Links.links.CreateAccountAccessConsent,
            consentRequest,
            tppResource.tpp
        )
        val accessToken = AccountAS().headlessAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

        // When
        val accountId = AccountRS().getFirstAccountId(accountAndTransaction3_1_1.Links.links.GetAccounts, accessToken)
        val result =
            AccountRS().getAccountData<OBReadAccount3>(
                accountAndTransaction3_1_1.Links.links.GetAccount,
                accessToken,
                accountId
            )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.account).isNotEmpty()
        assertThat(result.data.account.size).isEqualTo(3)
    }

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.4",
        operations = ["CreateAccountAccessConsent", "GetAccounts"]
    )
    @Test
    fun shouldGetAccount_v3_1_4() {
        // Given
        val consentRequest = obReadConsent1(listOf(READACCOUNTSDETAIL))
        val consent = AccountRS().consent<OBReadConsentResponse1>(
            accountAndTransaction3_1_4.Links.links.CreateAccountAccessConsent,
            consentRequest,
            tppResource.tpp
        )
        val accessToken = AccountAS().headlessAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

        // When
        val accountId = AccountRS().getFirstAccountId(accountAndTransaction3_1_4.Links.links.GetAccounts, accessToken)
        val result =
            AccountRS().getAccountData<OBReadAccount5>(
                accountAndTransaction3_1_4.Links.links.GetAccount,
                accessToken,
                accountId
            )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.account).isNotEmpty()
        assertThat(result.data.account.size).isEqualTo(3)
    }

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.6",
        operations = ["CreateAccountAccessConsent", "GetAccounts"]
    )
    @Test
    fun shouldGetAccount_v3_1_6() {
        // Given
        val consentRequest = obReadConsent1(listOf(READACCOUNTSDETAIL))
        val consent = AccountRS().consent<OBReadConsentResponse1>(
            accountAndTransaction3_1_6.Links.links.CreateAccountAccessConsent,
            consentRequest,
            tppResource.tpp
        )
        val accessToken = AccountAS().headlessAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

        // When
        val accountId = AccountRS().getFirstAccountId(accountAndTransaction3_1_6.Links.links.GetAccounts, accessToken)
        val result =
            AccountRS().getAccountData<OBReadAccount5>(
                accountAndTransaction3_1_6.Links.links.GetAccount,
                accessToken,
                accountId
            )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.account).isNotEmpty()
        assertThat(result.data.account.size).isEqualTo(3)
    }
}
