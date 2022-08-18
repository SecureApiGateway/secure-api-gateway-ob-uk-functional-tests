package com.forgerock.uk.openbanking.tests.functional.account.balances.api.v3_1_8

import assertk.assertThat
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.securebanking.framework.configuration.psu
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.support.account.AccountAS
import com.forgerock.uk.openbanking.support.account.AccountFactory
import com.forgerock.uk.openbanking.support.account.AccountRS
import com.forgerock.uk.openbanking.support.discovery.getAccountsApiLinks
import com.forgerock.uk.openbanking.tests.functional.account.access.consents.api.v3_1_8.AccountAccessConsent
import uk.org.openbanking.datamodel.account.OBExternalPermissions1Code
import uk.org.openbanking.datamodel.account.OBReadBalance1

class GetAccountBalances(val version: OBVersion, val tppResource: CreateTppCallback.TppResource) {

    private val accountsApiLinks = getAccountsApiLinks(version)
    private val accountAccessConsentApi = AccountAccessConsent(version, tppResource)

    fun shouldGetAccountBalancesTest() {
        // Given
        val permissions = listOf(
            OBExternalPermissions1Code.READACCOUNTSDETAIL,
            OBExternalPermissions1Code.READBALANCES
        )
        val consent = accountAccessConsentApi.createAccountAccessConsent(permissions)

        val accessToken = AccountAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        val accountId = AccountRS().getFirstAccountId(accountsApiLinks.GetAccounts, accessToken)

        // When
        val result = AccountRS().getAccountsData<OBReadBalance1>(
            AccountFactory.urlWithAccountId(
                accountsApiLinks.GetAccountBalances,
                accountId
            ), accessToken
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.balance).isNotEmpty()
    }
}
