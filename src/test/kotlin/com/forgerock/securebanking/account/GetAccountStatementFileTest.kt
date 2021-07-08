package com.forgerock.securebanking.account

import assertk.assertThat
import assertk.assertions.*
import com.forgerock.securebanking.account.AccountFactory.Companion.obReadConsent1
import com.forgerock.securebanking.discovery.accountAndTransaction3_1
import com.forgerock.securebanking.discovery.accountAndTransaction3_1_6
import com.forgerock.securebanking.junit.CreateTppCallback
import com.forgerock.securebanking.junit.EnabledIfVersion
import com.forgerock.securebanking.psu
import com.github.kittinunf.fuel.core.FuelError
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.account.OBExternalPermissions1Code
import uk.org.openbanking.datamodel.account.OBReadAccount3
import uk.org.openbanking.datamodel.account.OBReadConsentResponse1

class GetAccountStatementFileTest(val tppResource: CreateTppCallback.TppResource) {
    companion object {
        // There is no requirement to validate the statement id passed in the request when the endpoint is invoked
        var MANDATORY_UNUSED_STATEMENT_ID = "1234134132431"
    }

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountStatementFile"]
    )
    @Test
    fun shouldGetStatementFile_v3_1() {
        // Given
        val consentRequest = obReadConsent1(
            listOf(
                OBExternalPermissions1Code.READSTATEMENTSBASIC,
                OBExternalPermissions1Code.READSTATEMENTSDETAIL,
                OBExternalPermissions1Code.READACCOUNTSDETAIL
            )
        )
        val consent = AccountRS().consent<OBReadConsentResponse1>(
            accountAndTransaction3_1.Links.links.CreateAccountAccessConsent,
            consentRequest,
            tppResource.tpp
        )
        val accessToken = AccountAS().headlessAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        val accounts =
            AccountRS().getAccountData<OBReadAccount3>(accountAndTransaction3_1.Links.links.GetAccounts, accessToken)

        assertThat(accounts.data).isNotNull()
        assertThat(accounts.data.account[0].accountId).isNotNull()

        val accountDataUrl = accountAndTransaction3_1.Links.links.GetAccountStatementFile
            .replace("{AccountId}", accounts.data.account[0].accountId)
            .replace("{StatementId}", MANDATORY_UNUSED_STATEMENT_ID)
        // When
        val result = AccountRS().getAccountStatementFileData(accountDataUrl, accessToken, "application/pdf")

        // Then
        // Depends of the configuration of the sandbox the result can be the below:
        // - '404 - Not found': If the resource PDF hasn't provided and configured on the sandbox.
        // - byteArray: If the resource PDF has provided and configured on the sandbox
        if (result.second.statusCode == 404) {
            assertThat(result.second.responseMessage.toLowerCase()).contains("not found")
        } else {
            assertThat(result.third.get()).isNotNull().isNotEmpty()
            assertThat(result.third.get()).isInstanceOf(ByteArray::class.java)
        }
    }

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountStatementFile"]
    )
    @Test
    fun shouldGet_badRequest_StatementFile_v3_1() {
        // Given
        val consentRequest = obReadConsent1(
            listOf(
                OBExternalPermissions1Code.READSTATEMENTSBASIC,
                OBExternalPermissions1Code.READSTATEMENTSDETAIL,
                OBExternalPermissions1Code.READACCOUNTSDETAIL
            )
        )
        val consent = AccountRS().consent<OBReadConsentResponse1>(
            accountAndTransaction3_1.Links.links.CreateAccountAccessConsent,
            consentRequest,
            tppResource.tpp
        )
        val accessToken = AccountAS().headlessAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        val accounts =
            AccountRS().getAccountData<OBReadAccount3>(accountAndTransaction3_1.Links.links.GetAccounts, accessToken)

        assertThat(accounts.data).isNotNull()
        assertThat(accounts.data.account[0].accountId).isNotNull()

        val accountDataUrl = accountAndTransaction3_1.Links.links.GetAccountStatementFile
            .replace("{AccountId}", accounts.data.account[0].accountId)
            .replace("{StatementId}", MANDATORY_UNUSED_STATEMENT_ID)
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
        apiVersion = "v3.1.6",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountStatementFile"]
    )
    @Test
    fun shouldGetStatementFile_v3_1_6() {
        // Given
        val consentRequest = obReadConsent1(
            listOf(
                OBExternalPermissions1Code.READSTATEMENTSBASIC,
                OBExternalPermissions1Code.READSTATEMENTSDETAIL,
                OBExternalPermissions1Code.READACCOUNTSDETAIL
            )
        )
        val consent = AccountRS().consent<OBReadConsentResponse1>(
            accountAndTransaction3_1_6.Links.links.CreateAccountAccessConsent,
            consentRequest,
            tppResource.tpp
        )
        val accessToken = AccountAS().headlessAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        val accounts =
            AccountRS().getAccountData<OBReadAccount3>(accountAndTransaction3_1_6.Links.links.GetAccounts, accessToken)

        assertThat(accounts.data).isNotNull()
        assertThat(accounts.data.account[0].accountId).isNotNull()

        val accountDataUrl = accountAndTransaction3_1_6.Links.links.GetAccountStatementFile
            .replace("{AccountId}", accounts.data.account[0].accountId)
            .replace("{StatementId}", MANDATORY_UNUSED_STATEMENT_ID)
        // When
        val result = AccountRS().getAccountStatementFileData(accountDataUrl, accessToken, "application/pdf")

        // Then
        // Depends of the configuration of the sandbox the result can be the below:
        // - '404 - Not found': If the resource PDF hasn't provided and configured on the sandbox.
        // - byteArray: If the resource PDF has provided and configured on the sandbox.
        if (result.second.statusCode == 404) {
            assertThat(result.second.responseMessage.toLowerCase()).contains("not found")
        } else {
            assertThat(result.third.get()).isNotNull().isNotEmpty()
            assertThat(result.third.get()).isInstanceOf(ByteArray::class.java)
        }
    }

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.6",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountStatementFile"]
    )
    @Test
    fun shouldGet_badRequest_StatementFile_v3_1_6() {
        // Given
        val consentRequest = obReadConsent1(
            listOf(
                OBExternalPermissions1Code.READSTATEMENTSBASIC,
                OBExternalPermissions1Code.READSTATEMENTSDETAIL,
                OBExternalPermissions1Code.READACCOUNTSDETAIL
            )
        )
        val consent = AccountRS().consent<OBReadConsentResponse1>(
            accountAndTransaction3_1_6.Links.links.CreateAccountAccessConsent,
            consentRequest,
            tppResource.tpp
        )
        val accessToken = AccountAS().headlessAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        val accounts =
            AccountRS().getAccountData<OBReadAccount3>(accountAndTransaction3_1_6.Links.links.GetAccounts, accessToken)

        assertThat(accounts.data).isNotNull()
        assertThat(accounts.data.account[0].accountId).isNotNull()

        val accountDataUrl = accountAndTransaction3_1_6.Links.links.GetAccountStatementFile
            .replace("{AccountId}", accounts.data.account[0].accountId)
            .replace("{StatementId}", MANDATORY_UNUSED_STATEMENT_ID)
        // When
        val result = AccountRS().getAccountStatementFileData(accountDataUrl, accessToken, "*/*")

        // Then
        assertThat((result.third.component2() as FuelError).response.statusCode).isEqualTo(400)
        val body = String((result.third.component2() as FuelError).response.data)
        assertThat(body).contains("OBRI.Request.Invalid")
        assertThat(body).contains("Invalid header 'Accept' the only supported value for this operation is 'application/pdf'")
    }
}
