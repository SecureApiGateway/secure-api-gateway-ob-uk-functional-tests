package com.forgerock.uk.openbanking.tests.functional.account.parties.api.v3_1_8

import assertk.assertThat
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotEqualTo
import assertk.assertions.isNotNull
import com.forgerock.securebanking.framework.configuration.psu
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.support.account.AccountFactory
import com.forgerock.uk.openbanking.support.account.AccountRS
import com.forgerock.uk.openbanking.support.discovery.accountAndTransaction3_1_8
import com.forgerock.uk.openbanking.tests.functional.account.access.BaseAccountApi3_1_8
import uk.org.openbanking.datamodel.account.*

class GetAccountParty(version: OBVersion, tppResource: CreateTppCallback.TppResource): BaseAccountApi3_1_8(version, tppResource) {

    fun shouldGetAccountPartyTest() {
        // Given
        val permissions = listOf(OBExternalPermissions1Code.READPARTY, OBExternalPermissions1Code.READACCOUNTSDETAIL)
        val (_, accessToken) = accountAccessConsentApi.createConsentAndGetAccessToken(permissions)
        val (accountId, psuId) = AccountRS().getFirstAccountIdAndPsuId(
            accountAndTransaction3_1_8.Links.links.GetAccounts,
            accessToken
        )
        assertThat(accountId).isNotEqualTo("")
        assertThat(psuId).isNotEqualTo("")
        psu.user.uid = psuId

        // When
        val result = AccountRS().getAccountsDataEndUser<OBReadParty2>(
            AccountFactory.urlWithAccountId(
                accountAndTransaction3_1_8.Links.links.GetAccountParty,
                accountId
            ), accessToken
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.party.partyId).isNotEmpty()
    }
}