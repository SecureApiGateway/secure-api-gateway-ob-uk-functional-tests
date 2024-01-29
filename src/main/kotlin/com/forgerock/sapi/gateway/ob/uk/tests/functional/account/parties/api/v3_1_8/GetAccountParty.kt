package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.parties.api.v3_1_8

import assertk.assertThat
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotEqualTo
import assertk.assertions.isNotNull
import com.forgerock.sapi.gateway.framework.configuration.psu
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.support.account.AccountFactory
import com.forgerock.sapi.gateway.ob.uk.support.account.AccountRS
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.access.BaseAccountApi3_1_8
import uk.org.openbanking.datamodel.account.*
import uk.org.openbanking.datamodel.common.OBExternalPermissions1Code

class GetAccountParty(version: OBVersion, tppResource: CreateTppCallback.TppResource): BaseAccountApi3_1_8(version, tppResource) {

    fun shouldGetAccountPartyTest() {
        // Given
        val permissions = listOf(OBExternalPermissions1Code.READPARTY, OBExternalPermissions1Code.READACCOUNTSDETAIL)
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