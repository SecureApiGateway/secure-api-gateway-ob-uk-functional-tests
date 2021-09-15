package com.forgerock.securebanking.tests.functional.account;

import assertk.assertThat
import assertk.assertions.isNotNull
import com.forgerock.securebanking.framework.configuration.psu
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.support.account.AccountAS
import com.forgerock.securebanking.support.account.AccountFactory.Companion.urlSubstituted
import com.forgerock.securebanking.support.account.AccountRS
import com.forgerock.securebanking.support.discovery.*
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.account.OBReadConsent1
import uk.org.openbanking.datamodel.account.OBReadConsentResponse1
import uk.org.openbanking.datamodel.account.OBReadData1
import uk.org.openbanking.datamodel.account.OBRisk2

class SimpleAISPTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.1",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetStatements"],
        apis = ["accounts", "statements", "party"]
    )
    @Test
    fun shouldBeSuccessfulForAllAisp_v3_1_1() {
        // Given
        val consentRequest = OBReadConsent1().data(
            OBReadData1()
                .permissions(accountPermissions)
        )
            .risk(OBRisk2())
        val consent = AccountRS().consent<OBReadConsentResponse1>(
            accountAndTransaction3_1_1.Links.links.CreateAccountAccessConsent,
            consentRequest,
            tppResource.tpp
        )
        val accessToken = AccountAS().headlessAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        val accountId = AccountRS().getFirstAccountId(accountAndTransaction3_1_1.Links.links.GetAccounts, accessToken)
        val statementId =
            AccountRS().getFirstStatementId(accountAndTransaction3_1_1.Links.links.GetStatements, accessToken)

        val apisV3_1_1 = rsDiscoveryMap["accounts"]?.filter { "v3.1.1".equals(it.first) }?.first()
        val apisExcludingFileAndConsent =
            apisV3_1_1?.second?.filter { !it.contains("account-access-consents") && !it.endsWith("/file") }
        apisExcludingFileAndConsent?.forEach {
            // When
            val replacedUrl = urlSubstituted(it, mapOf("AccountId" to accountId, "StatementId" to statementId))
            val accountData = AccountRS().getAccountData<Any>(replacedUrl, accessToken)

            // Then
            assertThat(accountData, "Request for ${it}").isNotNull()
        }
    }

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.4",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetStatements"],
        apis = ["accounts", "statements", "party"]
    )
    @Test
    fun shouldBeSuccessfulForAllAisp_v3_1_4() {
        // Given
        val consentRequest = OBReadConsent1().data(
            OBReadData1()
                .permissions(accountPermissions)
        )
            .risk(OBRisk2())
        val consent = AccountRS().consent<OBReadConsentResponse1>(
            accountAndTransaction3_1_4.Links.links.CreateAccountAccessConsent,
            consentRequest,
            tppResource.tpp
        )
        val accessToken = AccountAS().headlessAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        val accountId = AccountRS().getFirstAccountId(accountAndTransaction3_1_4.Links.links.GetAccounts, accessToken)
        val statementId =
            AccountRS().getFirstStatementId(accountAndTransaction3_1_4.Links.links.GetStatements, accessToken)

        val apisV3_1_4 = rsDiscoveryMap["accounts"]?.filter { "v3.1.4".equals(it.first) }?.first()
        val apisExcludingFileAndConsent =
            apisV3_1_4?.second?.filter { !it.contains("account-access-consents") && !it.endsWith("/file") }
        apisExcludingFileAndConsent?.forEach {
            // When
            val replacedUrl = urlSubstituted(it, mapOf("AccountId" to accountId, "StatementId" to statementId))
            val accountData = AccountRS().getAccountData<Any>(replacedUrl, accessToken)

            // Then
            assertThat(accountData, "Request for ${it}").isNotNull()
        }
    }

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.6",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetStatements"],
        apis = ["accounts", "statements", "party"]
    )
    @Test
    fun shouldBeSuccessfulForAllAisp_v3_1_6() {
        // Given
        val consentRequest = OBReadConsent1().data(
            OBReadData1()
                .permissions(accountPermissions)
        )
            .risk(OBRisk2())
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
        val accountId = AccountRS().getFirstAccountId(accountAndTransaction3_1_6.Links.links.GetAccounts, accessToken)
        val statementId =
            AccountRS().getFirstStatementId(accountAndTransaction3_1_6.Links.links.GetStatements, accessToken)

        val apisV3_1_5 = rsDiscoveryMap["accounts"]?.filter { "v3.1.6".equals(it.first) }?.first()
        val apisExcludingFileAndConsent =
            apisV3_1_5?.second?.filter { !it.contains("account-access-consents") && !it.endsWith("/file") }
        apisExcludingFileAndConsent?.forEach {
            // When
            val replacedUrl = urlSubstituted(it, mapOf("AccountId" to accountId, "StatementId" to statementId))
            val accountData = AccountRS().getAccountData<Any>(replacedUrl, accessToken)

            // Then
            assertThat(accountData, "Request for ${it}").isNotNull()
        }
    }
}
