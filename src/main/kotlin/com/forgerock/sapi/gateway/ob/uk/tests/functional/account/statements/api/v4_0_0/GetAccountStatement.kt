package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.statements.api.v4_0_0

import assertk.assertThat
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.support.account.AccountRS
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.access.BaseAccountApi4_0_0
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import uk.org.openbanking.datamodel.v4.account.OBInternalPermissions1Code
import uk.org.openbanking.datamodel.v4.account.OBReadAccount6
import uk.org.openbanking.datamodel.v4.account.OBReadStatement2

class GetAccountStatement(version: OBVersion, tppResource: CreateTppCallback.TppResource) :
        BaseAccountApi4_0_0(version, tppResource) {

    fun shouldGetAccountStatementTest() {
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