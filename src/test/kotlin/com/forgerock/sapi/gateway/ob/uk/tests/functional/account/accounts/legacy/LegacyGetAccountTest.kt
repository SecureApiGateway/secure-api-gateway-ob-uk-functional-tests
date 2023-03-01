package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.accounts.legacy

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.sapi.gateway.framework.configuration.USER_ACCOUNT_ID
import com.forgerock.sapi.gateway.framework.configuration.psu
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.ob.uk.support.account.AccountAS
import com.forgerock.sapi.gateway.ob.uk.support.account.AccountFactory.Companion.obReadConsent1
import com.forgerock.sapi.gateway.ob.uk.support.account.AccountRS
import com.forgerock.sapi.gateway.ob.uk.support.discovery.accountAndTransaction3_1_2
import com.forgerock.sapi.gateway.ob.uk.support.discovery.accountAndTransaction3_1_5
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.account.OBExternalPermissions1Code.READACCOUNTSDETAIL
import uk.org.openbanking.datamodel.account.OBReadAccount4
import uk.org.openbanking.datamodel.account.OBReadAccount5
import uk.org.openbanking.datamodel.account.OBReadConsentResponse1

class LegacyGetAccountTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.2",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccount"],
        apis = ["accounts"],
        compatibleVersions = ["v.3.1.1", "v.3.1", "v.3.0"]
    )
    @Test
    fun shouldGetAccount_v3_1_2() {
        // Given
        val consentRequest = obReadConsent1(listOf(READACCOUNTSDETAIL))
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

        // When
        val result =
            AccountRS().getAccountData<OBReadAccount4>(
                accountAndTransaction3_1_2.Links.links.GetAccount,
                accessToken,
                USER_ACCOUNT_ID
            )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.account).isNotEmpty()
        assertThat(result.data.account.size).isEqualTo(1)
        assertThat(result.data.account[0].accountId).isEqualTo(USER_ACCOUNT_ID)
    }

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.5",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccount"],
        apis = ["accounts"],
        compatibleVersions = ["v.3.1.4", "v.3.1.3"]
    )
    @Test
    fun shouldGetAccount_v3_1_5() {
        // Given
        val consentRequest = obReadConsent1(listOf(READACCOUNTSDETAIL))
        val consent = AccountRS().consent<OBReadConsentResponse1>(
            accountAndTransaction3_1_5.Links.links.CreateAccountAccessConsent,
            consentRequest,
            tppResource.tpp
        )
        val accessToken = AccountAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

        // When
        val result =
            AccountRS().getAccountData<OBReadAccount5>(
                accountAndTransaction3_1_5.Links.links.GetAccount,
                accessToken,
                USER_ACCOUNT_ID
            )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.account).isNotEmpty()
        assertThat(result.data.account.size).isEqualTo(1)
        assertThat(result.data.account[0].accountId).isEqualTo(USER_ACCOUNT_ID)
    }
}
