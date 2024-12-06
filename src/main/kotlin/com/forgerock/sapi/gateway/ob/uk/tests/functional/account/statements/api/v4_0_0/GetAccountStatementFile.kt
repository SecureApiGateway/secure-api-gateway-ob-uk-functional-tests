package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.statements.api.v4_0_0

import assertk.assertThat
import assertk.assertions.*
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.support.account.AccountRS
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.access.BaseAccountApi4_0_0
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.github.kittinunf.fuel.core.FuelError
import uk.org.openbanking.datamodel.v4.account.OBInternalPermissions1Code
import uk.org.openbanking.datamodel.v4.account.OBReadAccount6
import uk.org.openbanking.datamodel.v4.account.OBReadStatement2

class GetAccountStatementFile(version: OBVersion, tppResource: CreateTppCallback.TppResource) :
        BaseAccountApi4_0_0(version, tppResource) {

    private val statementConsentPermissions = listOf(
            OBInternalPermissions1Code.READSTATEMENTSBASIC,
            OBInternalPermissions1Code.READSTATEMENTSDETAIL,
            OBInternalPermissions1Code.READACCOUNTSDETAIL
    )

    fun shouldGet_badRequest_StatementFileTest() {
        // Given
        val (_, accessToken) = accountAccessConsentApi.createConsentAndGetAccessToken(statementConsentPermissions)
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

        val accountDataUrl = accountsApiLinks.GetAccountStatementFile
                .replace("{AccountId}", accounts.data.account[0].accountId)
                .replace("{StatementId}", resultGetAccountStatements.data.statement[0].statementId)

        // When
        val result = AccountRS().getAccountStatementFileData(accountDataUrl, accessToken, "*/*")

        // Then
        assertThat((result.third.component2() as FuelError).response.statusCode).isEqualTo(400)
        val body = String((result.third.component2() as FuelError).response.data)
        assertThat(body).contains("U006")
        assertThat(body).contains("Invalid header 'Accept' the only supported value for this operation is 'application/pdf'")
    }

    fun shouldGetStatementFileTest() {
        // Given
        val (_, accessToken) = accountAccessConsentApi.createConsentAndGetAccessToken(statementConsentPermissions)
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

        val accountDataUrl = accountsApiLinks.GetAccountStatementFile
                .replace("{AccountId}", accounts.data.account[0].accountId)
                .replace("{StatementId}", resultGetAccountStatements.data.statement[0].statementId)

        // When
        val result = AccountRS().getAccountStatementFileData(accountDataUrl, accessToken, "application/pdf")

        // Then
        assertThat(result.third.get()).isNotNull().isNotEmpty()
        assertThat(result.third.get()).isInstanceOf(ByteArray::class.java)
    }
}