package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.statements.api.v3_1_8

import assertk.assertThat
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.support.account.AccountRS
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.access.BaseAccountApi3_1_8
import uk.org.openbanking.datamodel.account.OBExternalPermissions1Code
import uk.org.openbanking.datamodel.account.OBReadAccount3
import uk.org.openbanking.datamodel.account.OBReadStatement2

class GetAccountStatement(version: OBVersion, tppResource: CreateTppCallback.TppResource) :
    BaseAccountApi3_1_8(version, tppResource) {

    fun shouldGetAccountStatementTest() {
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

        val accountStatementDataUrl = accountsApiLinks.GetAccountStatements
            .replace("{AccountId}", accounts.data.account[0].accountId)
        val resultGetAccountStatements = AccountRS().getAccountData<OBReadStatement2>(
            accountStatementDataUrl,
            accessToken,
            accounts.data.account[0].accountId
        )

        assertThat(resultGetAccountStatements).isNotNull()
        assertThat(resultGetAccountStatements.data.statement[0].statementId).isNotEmpty()

        val accountDataUrl = accountsApiLinks.GetAccountStatement
            .replace("{AccountId}", accounts.data.account[0].accountId)
            .replace("{StatementId}", resultGetAccountStatements.data.statement[0].statementId)

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