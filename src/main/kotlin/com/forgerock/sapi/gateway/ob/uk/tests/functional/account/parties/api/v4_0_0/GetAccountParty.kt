package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.parties.api.v4_0_0

import assertk.assertThat
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotEqualTo
import assertk.assertions.isNotNull
import com.forgerock.sapi.gateway.framework.configuration.psu
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.support.account.AccountRS
import com.forgerock.sapi.gateway.ob.uk.support.account.v4.AccountFactory
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.access.BaseAccountApi4_0_0
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import uk.org.openbanking.datamodel.v4.account.OBInternalPermissions1Code
import uk.org.openbanking.datamodel.v4.account.OBReadParty2

class GetAccountParty(version: OBVersion, tppResource: CreateTppCallback.TppResource) : BaseAccountApi4_0_0(version, tppResource) {

    fun shouldGetAccountPartyTest() {
        // Given
        val permissions = listOf(OBInternalPermissions1Code.READPARTY, OBInternalPermissions1Code.READACCOUNTSDETAIL)
        val (_, accessToken) = accountAccessConsentApi.createConsentAndGetAccessToken(permissions)
        val (accountId, psuId) = AccountRS().getFirstAccountIdAndPsuId(
                accountsApiLinks.GetAccounts,
                accessToken
        )
        assertThat(accountId).isNotEqualTo("")
        assertThat(psuId).isNotEqualTo("")
        psu.user.uid = psuId

        // When
        val result = AccountRS().getAccountsDataEndUser<OBReadParty2>(
                AccountFactory.urlWithAccountId(
                        accountsApiLinks.GetAccountParty,
                        accountId
                ), accessToken
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.party.partyId).isNotEmpty()
    }
}