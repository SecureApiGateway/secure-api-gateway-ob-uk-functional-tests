package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.statements.api.v4_0_0

import assertk.assertThat
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.support.account.AccountRS
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.access.BaseAccountApi4_0_0
import uk.org.openbanking.datamodel.v4.account.OBReadAccount6
import uk.org.openbanking.datamodel.v4.account.OBReadStatement2
import uk.org.openbanking.datamodel.v4.account.OBInternalPermissions1Code

class GetAccountStatements(version: OBVersion, tppResource: CreateTppCallback.TppResource) :
    BaseAccountApi4_0_0(version, tppResource) {

    fun shouldGetAccountStatementsTest() {
        // Given
        val permissions = listOf(
            OBInternalPermissions1Code.READSTATEMENTSBASIC,
            OBInternalPermissions1Code.READSTATEMENTSDETAIL,
            OBInternalPermissions1Code.READACCOUNTSDETAIL
        )
        val (_, accessToken) = accountAccessConsentApi.createConsentAndGetAccessToken(permissions)
        val accounts =
            AccountRS().getAccountsData<OBReadAccount6>(accountsApiLinks.GetAccounts, accessToken)

        assertThat(accounts.data).isNotNull()
        assertThat(accounts.data.account[0].accountId).isNotNull()

        val accountDataUrl = accountsApiLinks.GetAccountStatements
            .replace("{AccountId}", accounts.data.account[0].accountId)
        // When
        val result = AccountRS().getAccountData<OBReadStatement2>(
            accountDataUrl,
            accessToken,
            accounts.data.account[0].accountId
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.statement).isNotEmpty()
    }
}
