package com.forgerock.securebanking.tests.functional.account.statements

import assertk.assertThat
import assertk.assertions.*
import com.forgerock.securebanking.framework.configuration.psu
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.support.account.AccountAS
import com.forgerock.securebanking.support.account.AccountFactory.Companion.obReadConsent1
import com.forgerock.securebanking.support.account.AccountRS
import com.forgerock.securebanking.support.discovery.accountAndTransaction3_1_2
import com.forgerock.securebanking.support.discovery.accountAndTransaction3_1_8
import com.github.kittinunf.fuel.core.FuelError
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.account.OBExternalPermissions1Code
import uk.org.openbanking.datamodel.account.OBReadAccount3
import uk.org.openbanking.datamodel.account.OBReadConsentResponse1
import uk.org.openbanking.datamodel.account.OBReadStatement2

class GetAccountStatementFileTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.2",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountStatementFile"],
        apis = ["statements"],
        compatibleVersions = ["v.3.1.1", "v.3.1", "v.3.0"]
    )
    @Test
    fun shouldGetStatementFile_v3_1_2() {
        // Given
        val consentRequest = obReadConsent1(
            listOf(
                OBExternalPermissions1Code.READSTATEMENTSBASIC,
                OBExternalPermissions1Code.READSTATEMENTSDETAIL,
                OBExternalPermissions1Code.READACCOUNTSDETAIL
            )
        )
        val consent = AccountRS().consent<OBReadConsentResponse1>(
            accountAndTransaction3_1_2.Links.links.CreateAccountAccessConsent,
            consentRequest,
            tppResource.tpp
        )
        val accessToken = AccountAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        val accounts =
            AccountRS().getAccountsData<OBReadAccount3>(accountAndTransaction3_1_2.Links.links.GetAccounts, accessToken)

        assertThat(accounts.data).isNotNull()
        assertThat(accounts.data.account[0].accountId).isNotNull()

        val accountStatementDataUrl = accountAndTransaction3_1_2.Links.links.GetAccountStatements
            .replace("{AccountId}", accounts.data.account[0].accountId)
        val resultGetAccountStatements = AccountRS().getAccountData<OBReadStatement2>(
            accountStatementDataUrl,
            accessToken,
            accounts.data.account[0].accountId
        )

        assertThat(resultGetAccountStatements).isNotNull()
        assertThat(resultGetAccountStatements.data.statement[0].statementId).isNotEmpty()

        val accountDataUrl = accountAndTransaction3_1_2.Links.links.GetAccountStatementFile
            .replace("{AccountId}", accounts.data.account[0].accountId)
            .replace("{StatementId}", resultGetAccountStatements.data.statement[0].statementId)

        // When
        val result = AccountRS().getAccountStatementFileData(accountDataUrl, accessToken, "application/pdf")

        // Then
            assertThat(result.third.get()).isNotNull().isNotEmpty()
            assertThat(result.third.get()).isInstanceOf(ByteArray::class.java)
    }

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.2",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountStatementFile"],
        apis = ["statements"],
        compatibleVersions = ["v.3.1.1", "v.3.1", "v.3.0"]
    )
    @Test
    fun shouldGet_badRequest_StatementFile_v3_1_2() {
        // Given
        val consentRequest = obReadConsent1(
            listOf(
                OBExternalPermissions1Code.READSTATEMENTSBASIC,
                OBExternalPermissions1Code.READSTATEMENTSDETAIL,
                OBExternalPermissions1Code.READACCOUNTSDETAIL
            )
        )
        val consent = AccountRS().consent<OBReadConsentResponse1>(
            accountAndTransaction3_1_2.Links.links.CreateAccountAccessConsent,
            consentRequest,
            tppResource.tpp
        )
        val accessToken = AccountAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        val accounts =
            AccountRS().getAccountsData<OBReadAccount3>(accountAndTransaction3_1_2.Links.links.GetAccounts, accessToken)

        assertThat(accounts.data).isNotNull()
        assertThat(accounts.data.account[0].accountId).isNotNull()

        val accountStatementDataUrl = accountAndTransaction3_1_2.Links.links.GetAccountStatements
            .replace("{AccountId}", accounts.data.account[0].accountId)
        val resultGetAccountStatements = AccountRS().getAccountData<OBReadStatement2>(
            accountStatementDataUrl,
            accessToken,
            accounts.data.account[0].accountId
        )

        assertThat(resultGetAccountStatements).isNotNull()
        assertThat(resultGetAccountStatements.data.statement[0].statementId).isNotEmpty()

        val accountDataUrl = accountAndTransaction3_1_2.Links.links.GetAccountStatementFile
            .replace("{AccountId}", accounts.data.account[0].accountId)
            .replace("{StatementId}", resultGetAccountStatements.data.statement[0].statementId)

        // When
        val result = AccountRS().getAccountStatementFileData(accountDataUrl, accessToken, "*/*")

        // Then
        assertThat((result.third.component2() as FuelError).response.statusCode).isEqualTo(400)
        val body = String((result.third.component2() as FuelError).response.data)
        assertThat(body).contains("OBRI.Request.Invalid")
        assertThat(body).contains("Invalid header 'Accept' the only supported value for this operation is 'application/pdf'")
    }

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.8",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountStatementFile"],
        apis = ["statements"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5", "v.3.1.4", "v.3.1.3"]
    )
    @Test
    fun shouldGet_badRequest_StatementFile_v3_1_8() {
        // Given
        val consentRequest = obReadConsent1(
            listOf(
                OBExternalPermissions1Code.READSTATEMENTSBASIC,
                OBExternalPermissions1Code.READSTATEMENTSDETAIL,
                OBExternalPermissions1Code.READACCOUNTSDETAIL
            )
        )
        val consent = AccountRS().consent<OBReadConsentResponse1>(
            accountAndTransaction3_1_8.Links.links.CreateAccountAccessConsent,
            consentRequest,
            tppResource.tpp
        )
        val accessToken = AccountAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        val accounts =
            AccountRS().getAccountsData<OBReadAccount3>(accountAndTransaction3_1_8.Links.links.GetAccounts, accessToken)

        assertThat(accounts.data).isNotNull()
        assertThat(accounts.data.account[0].accountId).isNotNull()

        val accountStatementDataUrl = accountAndTransaction3_1_8.Links.links.GetAccountStatements
            .replace("{AccountId}", accounts.data.account[0].accountId)
        val resultGetAccountStatements = AccountRS().getAccountData<OBReadStatement2>(
            accountStatementDataUrl,
            accessToken,
            accounts.data.account[0].accountId
        )

        assertThat(resultGetAccountStatements).isNotNull()
        assertThat(resultGetAccountStatements.data.statement[0].statementId).isNotEmpty()

        val accountDataUrl = accountAndTransaction3_1_8.Links.links.GetAccountStatementFile
            .replace("{AccountId}", accounts.data.account[0].accountId)
            .replace("{StatementId}", resultGetAccountStatements.data.statement[0].statementId)

        // When
        val result = AccountRS().getAccountStatementFileData(accountDataUrl, accessToken, "*/*")

        // Then
        assertThat((result.third.component2() as FuelError).response.statusCode).isEqualTo(400)
        val body = String((result.third.component2() as FuelError).response.data)
        assertThat(body).contains("UK.OBIE.Header.Invalid")
        assertThat(body).contains("Invalid header 'Accept' the only supported value for this operation is 'application/pdf'")
    }

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.8",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountStatementFile"],
        apis = ["statements"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5", "v.3.1.4", "v.3.1.3"]
    )
    @Test
    fun shouldGetStatementFile_v3_1_8() {
        // Given
        val consentRequest = obReadConsent1(
            listOf(
                OBExternalPermissions1Code.READSTATEMENTSBASIC,
                OBExternalPermissions1Code.READSTATEMENTSDETAIL,
                OBExternalPermissions1Code.READACCOUNTSDETAIL
            )
        )
        val consent = AccountRS().consent<OBReadConsentResponse1>(
            accountAndTransaction3_1_8.Links.links.CreateAccountAccessConsent,
            consentRequest,
            tppResource.tpp
        )
        val accessToken = AccountAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        val accounts =
            AccountRS().getAccountsData<OBReadAccount3>(accountAndTransaction3_1_8.Links.links.GetAccounts, accessToken)

        assertThat(accounts.data).isNotNull()
        assertThat(accounts.data.account[0].accountId).isNotNull()

        val accountStatementDataUrl = accountAndTransaction3_1_8.Links.links.GetAccountStatements
            .replace("{AccountId}", accounts.data.account[0].accountId)
        val resultGetAccountStatements = AccountRS().getAccountData<OBReadStatement2>(
            accountStatementDataUrl,
            accessToken,
            accounts.data.account[0].accountId
        )

        assertThat(resultGetAccountStatements).isNotNull()
        assertThat(resultGetAccountStatements.data.statement[0].statementId).isNotEmpty()

        val accountDataUrl = accountAndTransaction3_1_8.Links.links.GetAccountStatementFile
            .replace("{AccountId}", accounts.data.account[0].accountId)
            .replace("{StatementId}", resultGetAccountStatements.data.statement[0].statementId)

        // When
        val result = AccountRS().getAccountStatementFileData(accountDataUrl, accessToken, "application/pdf")

        // Then
        assertThat(result.third.get()).isNotNull().isNotEmpty()
        assertThat(result.third.get()).isInstanceOf(ByteArray::class.java)
    }
}
