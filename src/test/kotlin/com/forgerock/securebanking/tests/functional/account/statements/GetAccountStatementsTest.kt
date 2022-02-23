package com.forgerock.securebanking.tests.functional.account.statements

import assertk.assertThat
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.securebanking.framework.configuration.psu
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.support.account.AccountAS
import com.forgerock.securebanking.support.account.AccountFactory.Companion.obReadConsent1
import com.forgerock.securebanking.support.account.AccountRS
import com.forgerock.securebanking.support.discovery.accountAndTransaction3_1
import com.forgerock.securebanking.support.discovery.accountAndTransaction3_1_6
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.account.OBExternalPermissions1Code
import uk.org.openbanking.datamodel.account.OBReadAccount3
import uk.org.openbanking.datamodel.account.OBReadConsentResponse1
import uk.org.openbanking.datamodel.account.OBReadStatement2

class GetAccountStatementsTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountStatements"]
    )
    @Test
    fun shouldGetAccountStatements_v3_1() {
        // Given
        val consentRequest = obReadConsent1(
            listOf(
                OBExternalPermissions1Code.READSTATEMENTSBASIC,
                OBExternalPermissions1Code.READSTATEMENTSDETAIL,
                OBExternalPermissions1Code.READACCOUNTSDETAIL
            )
        )
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
        val accounts =
            AccountRS().getAccountsData<OBReadAccount3>(accountAndTransaction3_1.Links.links.GetAccounts, accessToken)

        assertThat(accounts.data).isNotNull()
        assertThat(accounts.data.account[0].accountId).isNotNull()

        val accountDataUrl = accountAndTransaction3_1.Links.links.GetAccountStatements
            .replace("{AccountId}", accounts.data.account[0].accountId)
        // When
        val result = AccountRS().getAccountData<OBReadStatement2>(
            accountDataUrl,
            accessToken,
            accounts.data.account[0].accountId
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.statement).isNotEmpty()
    }

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.6",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountStatements"]
    )
    @Test
    fun shouldGetAccountStatements_v3_1_6() {
        // Given
        val consentRequest = obReadConsent1(
            listOf(
                OBExternalPermissions1Code.READSTATEMENTSBASIC,
                OBExternalPermissions1Code.READSTATEMENTSDETAIL,
                OBExternalPermissions1Code.READACCOUNTSDETAIL
            )
        )
        val consent = AccountRS().consent<OBReadConsentResponse1>(
            accountAndTransaction3_1_6.Links.links.CreateAccountAccessConsent,
            consentRequest,
            tppResource.tpp
        )
        val accessToken = AccountAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        val accounts =
            AccountRS().getAccountsData<OBReadAccount3>(accountAndTransaction3_1.Links.links.GetAccounts, accessToken)

        assertThat(accounts.data).isNotNull()
        assertThat(accounts.data.account[0].accountId).isNotNull()

        val accountDataUrl = accountAndTransaction3_1_6.Links.links.GetAccountStatements
            .replace("{AccountId}", accounts.data.account[0].accountId)
        // When
        val result = AccountRS().getAccountData<OBReadStatement2>(
            accountDataUrl,
            accessToken,
            accounts.data.account[0].accountId
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.statement).isNotEmpty()
    }
}
