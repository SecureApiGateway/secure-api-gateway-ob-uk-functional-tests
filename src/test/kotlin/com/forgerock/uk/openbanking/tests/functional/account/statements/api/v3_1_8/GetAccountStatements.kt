package com.forgerock.uk.openbanking.tests.functional.account.statements.api.v3_1_8

import assertk.assertThat
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.support.account.AccountRS
import com.forgerock.uk.openbanking.tests.functional.account.access.BaseAccountApi3_1_8
import uk.org.openbanking.datamodel.account.OBExternalPermissions1Code
import uk.org.openbanking.datamodel.account.OBReadAccount3
import uk.org.openbanking.datamodel.account.OBReadStatement2

class GetAccountStatements(version: OBVersion, tppResource: CreateTppCallback.TppResource) :
    BaseAccountApi3_1_8(version, tppResource) {

    fun shouldGetAccountStatementsTest() {
        // Given
        val permissions = listOf(
            OBExternalPermissions1Code.READSTATEMENTSBASIC,
            OBExternalPermissions1Code.READSTATEMENTSDETAIL,
            OBExternalPermissions1Code.READACCOUNTSDETAIL
        )
        val (_, accessToken) = accountAccessConsentApi.createConsentAndGetAccessToken(permissions)
        val accounts =
            AccountRS().getAccountsData<OBReadAccount3>(accountsApiLinks.GetAccounts, accessToken)

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
