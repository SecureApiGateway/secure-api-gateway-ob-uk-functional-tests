package com.forgerock.securebanking.account

import assertk.assertThat
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.securebanking.account.AccountFactory.Companion.urlWithAccountId
import com.forgerock.securebanking.discovery.accountAndTransaction3_1_1
import com.forgerock.securebanking.discovery.accountAndTransaction3_1_6
import com.forgerock.securebanking.junit.CreateTppCallback
import com.forgerock.securebanking.junit.EnabledIfVersion
import com.forgerock.securebanking.psu
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.account.*
import uk.org.openbanking.datamodel.account.OBExternalPermissions1Code.*

class GetTransactionsTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.1",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountTransactions"],
        apis = ["transactions"]
    )
    @Test
    fun shouldGetAccountTransactions_v3_1_1() {
        // Given
        val consentRequest = OBReadConsent1().data(
            OBReadData1()
                .permissions(
                    listOf(
                        READACCOUNTSDETAIL,
                        READTRANSACTIONSCREDITS,
                        READTRANSACTIONSDEBITS,
                        READTRANSACTIONSDETAIL
                    )
                )
        )
            .risk(OBRisk2())
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
        val accountId = AccountRS().getFirstAccountId(accountAndTransaction3_1_1.Links.links.GetAccounts, accessToken)

        // When
        val result = AccountRS().getAccountData<OBReadTransaction5>(
            urlWithAccountId(
                accountAndTransaction3_1_1.Links.links.GetAccountTransactions,
                accountId
            ), accessToken
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.transaction).isNotEmpty()
    }

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.6",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountTransactions"],
        apis = ["transactions"]
    )
    @Test
    fun shouldGetAccountTransactions_v3_1_6() {
        // Given
        val consentRequest = OBReadConsent1().data(
            OBReadData1()
                .permissions(
                    listOf(
                        READACCOUNTSDETAIL,
                        READTRANSACTIONSCREDITS,
                        READTRANSACTIONSDEBITS,
                        READTRANSACTIONSDETAIL
                    )
                )
        )
            .risk(OBRisk2())
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
        val accountId = AccountRS().getFirstAccountId(accountAndTransaction3_1_6.Links.links.GetAccounts, accessToken)

        // When
        val result = AccountRS().getAccountData<OBReadTransaction6>(
            urlWithAccountId(
                accountAndTransaction3_1_6.Links.links.GetAccountTransactions,
                accountId
            ), accessToken
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.transaction).isNotEmpty()
    }
}
