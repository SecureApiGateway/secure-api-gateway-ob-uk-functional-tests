package com.forgerock.uk.openbanking.tests.functional.account.parties.api.v3_1_8

import assertk.assertThat
import assertk.assertions.isNotEqualTo
import assertk.assertions.isNotNull
import com.forgerock.securebanking.framework.configuration.psu
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.support.account.AccountRS
import com.forgerock.uk.openbanking.support.discovery.accountAndTransaction3_1_8
import com.forgerock.uk.openbanking.tests.functional.account.access.BaseAccountApi3_1_8
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
            accountAndTransaction3_1_8.Links.links.GetAccounts,
            accessToken
        )
        assertThat(psuId).isNotEqualTo("")
        psu.user.uid = psuId

        // when
        val party =
            AccountRS().getAccountsDataEndUser<OBReadParty2>(
                accountAndTransaction3_1_8.Links.links.GetParty,
                accessToken
            )

        // Then
        assertThat(party).isNotNull()
        assertThat(party.data.party).isNotNull()
    }
}