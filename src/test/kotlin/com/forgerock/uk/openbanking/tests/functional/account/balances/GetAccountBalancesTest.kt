package com.forgerock.uk.openbanking.tests.functional.account.balances

import assertk.assertThat
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.securebanking.framework.configuration.psu
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.support.account.AccountAS
import com.forgerock.uk.openbanking.support.account.AccountFactory.Companion.urlWithAccountId
import com.forgerock.uk.openbanking.support.account.AccountRS
import com.forgerock.uk.openbanking.support.discovery.getAccountsApiLinks
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.account.*
import uk.org.openbanking.datamodel.account.OBExternalPermissions1Code.READACCOUNTSDETAIL
import uk.org.openbanking.datamodel.account.OBExternalPermissions1Code.READBALANCES

class GetAccountBalancesTest(val tppResource: CreateTppCallback.TppResource) {

    private fun shouldGetBalances(version: OBVersion) {
        val accountsApiLinks = getAccountsApiLinks(version)

        // Given
        val consentRequest = OBReadConsent1().data(
            OBReadData1()
                .permissions(listOf(READACCOUNTSDETAIL, READBALANCES))
        )
            .risk(OBRisk2())
        val consent = AccountRS().consent<OBReadConsentResponse1>(
            accountsApiLinks.CreateAccountAccessConsent,
            consentRequest,
            tppResource.tpp
        )
        val accessToken = AccountAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        val accountId = AccountRS().getFirstAccountId(accountsApiLinks.GetAccounts, accessToken)

        // When
        val result = AccountRS().getAccountsData<OBReadBalance1>(
            urlWithAccountId(
                accountsApiLinks.GetAccountBalances,
                accountId
            ), accessToken
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.balance).isNotEmpty()
    }

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.2",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountBalances"],
        apis = ["balances"],
        compatibleVersions = ["v.3.1.1", "v.3.1", "v.3.0"]
    )
    @Test
    fun shouldGetBalances_v3_1_2() {
       shouldGetBalances(OBVersion.v3_1_2)
    }

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.8",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountBalances"],
        apis = ["balances"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5", "v.3.1.4", "v.3.1.3"]
    )
    @Test
    fun shouldGetBalances_v3_1_8() {
        shouldGetBalances(OBVersion.v3_1_8)
    }

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.9",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountBalances"],
        apis = ["balances"],
        compatibleVersions = ["v3.1.8", "v.3.1.7", "v.3.1.6", "v.3.1.5", "v.3.1.4", "v.3.1.3"]
    )
    @Test
    fun shouldGetBalances_v3_1_9() {
        shouldGetBalances(OBVersion.v3_1_9)
    }
}
