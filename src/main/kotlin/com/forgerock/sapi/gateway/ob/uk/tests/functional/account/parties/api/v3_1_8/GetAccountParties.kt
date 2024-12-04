package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.parties.api.v3_1_8

import assertk.assertThat
import assertk.assertions.isGreaterThanOrEqualTo
import assertk.assertions.isNotNull
import com.forgerock.sapi.gateway.framework.configuration.USER_ACCOUNT_ID
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.support.account.AccountFactory
import com.forgerock.sapi.gateway.ob.uk.support.account.AccountRS
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.access.BaseAccountApi3_1_8
import uk.org.openbanking.datamodel.v3.account.*
import uk.org.openbanking.datamodel.v3.common.OBExternalPermissions1Code

class GetAccountParties(version: OBVersion, tppResource: CreateTppCallback.TppResource): BaseAccountApi3_1_8(version, tppResource) {
    fun shouldGetAccountPartiesTest() {
        // Given
        val permissions = listOf(OBExternalPermissions1Code.READPARTY, OBExternalPermissions1Code.READACCOUNTSBASIC)
        val (_, accessToken) = accountAccessConsentApi.createConsentAndGetAccessToken(permissions)

        // When
        val result = AccountRS().getAccountsDataEndUser<OBReadParty3>(
            AccountFactory.urlWithAccountId(
                accountsApiLinks.GetAccountParties,
                USER_ACCOUNT_ID
            ), accessToken
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.party.size).isGreaterThanOrEqualTo(1)
    }
}