package com.forgerock.uk.openbanking.tests.functional.account.parties.api.v3_1_8

import assertk.assertThat
import assertk.assertions.isGreaterThanOrEqualTo
import assertk.assertions.isNotNull
import com.forgerock.securebanking.framework.configuration.USER_ACCOUNT_ID
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.support.account.AccountFactory
import com.forgerock.uk.openbanking.support.account.AccountRS
import com.forgerock.uk.openbanking.tests.functional.account.access.BaseAccountApi3_1_8
import uk.org.openbanking.datamodel.account.*

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