package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.parties.api.v4_0_0

import assertk.assertThat
import assertk.assertions.isNotEqualTo
import assertk.assertions.isNotNull
import com.forgerock.sapi.gateway.framework.configuration.psu
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.support.account.AccountRS
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.access.BaseAccountApi4_0_0
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import uk.org.openbanking.datamodel.v4.account.*

class GetParty(version: OBVersion, tppResource: CreateTppCallback.TppResource): BaseAccountApi4_0_0(version, tppResource) {
    fun shouldGetPartyTest() {
        // Given
        val permissions = listOf(
            OBInternalPermissions1Code.READPARTYPSU,
            OBInternalPermissions1Code.READACCOUNTSDETAIL, OBInternalPermissions1Code.READPARTY
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