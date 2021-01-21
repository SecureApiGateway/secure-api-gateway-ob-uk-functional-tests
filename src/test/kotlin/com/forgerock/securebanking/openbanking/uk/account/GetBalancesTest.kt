package com.forgerock.openbanking.account

import assertk.assertThat
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.openbanking.account.AccountFactory.Companion.urlWithAccountId
import com.forgerock.openbanking.discovery.accountAndTransaction3_1_1
import com.forgerock.openbanking.discovery.accountAndTransaction3_1_6
import com.forgerock.openbanking.junit.CreateTppCallback
import com.forgerock.openbanking.junit.EnabledIfOpenBankingVersion
import com.forgerock.openbanking.psu
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.account.*
import uk.org.openbanking.datamodel.account.OBExternalPermissions1Code.READACCOUNTSDETAIL
import uk.org.openbanking.datamodel.account.OBExternalPermissions1Code.READBALANCES

@Tags(Tag("accountTest"))
class GetBalancesTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfOpenBankingVersion(type = "accounts", version = "v3.1.1", apis = ["balances"])
    @Test
    fun shouldGetBalances_v3_1_1() {
        // Given
        val consentRequest = OBReadConsent1().data(OBReadData1()
                .permissions(listOf(READACCOUNTSDETAIL, READBALANCES)))
                .risk(OBRisk2())
        val consent = AccountRS().consent<OBReadConsentResponse1>(accountAndTransaction3_1_1.Links.links.CreateAccountAccessConsent, consentRequest, tppResource.tpp)
        val accessToken = AccountAS().headlessAuthentication(consent.data.consentId, tppResource.tpp.registrationResponse, psu, tppResource.tpp)
        val accountId = AccountRS().getFirstAccountId(accountAndTransaction3_1_1.Links.links.GetAccounts, accessToken)

        // When
        val result = AccountRS().getAccountData<OBReadBalance1>(urlWithAccountId(accountAndTransaction3_1_1.Links.links.GetAccountBalances, accountId), accessToken)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.balance).isNotEmpty()
    }

    @EnabledIfOpenBankingVersion(type = "accounts", version = "v3.1.6", apis = ["balances"])
    @Test
    fun shouldGetBalances_v3_1_6() {
        // Given
        val consentRequest = OBReadConsent1().data(OBReadData1()
                .permissions(listOf(READACCOUNTSDETAIL, READBALANCES)))
                .risk(OBRisk2())
        val consent = AccountRS().consent<OBReadConsentResponse1>(accountAndTransaction3_1_6.Links.links.CreateAccountAccessConsent, consentRequest, tppResource.tpp)
        val accessToken = AccountAS().headlessAuthentication(consent.data.consentId, tppResource.tpp.registrationResponse, psu, tppResource.tpp)
        val accountId = AccountRS().getFirstAccountId(accountAndTransaction3_1_6.Links.links.GetAccounts, accessToken)

        // When
        val result = AccountRS().getAccountData<OBReadBalance1>(urlWithAccountId(accountAndTransaction3_1_6.Links.links.GetAccountBalances, accountId), accessToken)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.balance).isNotEmpty()
    }
}