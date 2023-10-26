package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.parties.api.v3_1_8

import assertk.assertThat
import assertk.assertions.isNotEqualTo
import assertk.assertions.isNotNull
import com.forgerock.sapi.gateway.framework.configuration.psu
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.support.account.AccountRS
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.access.BaseAccountApi3_1_8
import uk.org.openbanking.datamodel.account.*

class GetParty(version: OBVersion, tppResource: CreateTppCallback.TppResource): BaseAccountApi3_1_8(version, tppResource) {
    fun shouldGetPartyTest() {
        // Given
        val permissions = listOf(
            OBExternalPermissions1Code.READPARTYPSU,
            OBExternalPermissions1Code.READACCOUNTSDETAIL, OBExternalPermissions1Code.READPARTY
        )
        val (_, accessToken) = accountAccessConsentApi.createConsentAndGetAccessToken(permissions)

        val (_, psuId) = AccountRS().getFirstAccountIdAndPsuId(
            accountsApiLinks.GetAccounts,
            accessToken
        )
        assertThat(psuId).isNotEqualTo("")
        psu.user.uid = psuId

        // when
        val party =
            AccountRS().getAccountsDataEndUser<OBReadParty2>(
                accountsApiLinks.GetParty,
                accessToken
            )

        // Then
        assertThat(party).isNotNull()
        assertThat(party.data.party).isNotNull()
    }
}