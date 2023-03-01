package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.direct.debits.legacy

import assertk.assertThat
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.sapi.gateway.framework.configuration.USER_ACCOUNT_ID
import com.forgerock.sapi.gateway.framework.configuration.psu
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.ob.uk.support.account.AccountAS
import com.forgerock.sapi.gateway.ob.uk.support.account.AccountFactory
import com.forgerock.sapi.gateway.ob.uk.support.account.AccountFactory.Companion.obReadConsent1
import com.forgerock.sapi.gateway.ob.uk.support.account.AccountRS
import com.forgerock.sapi.gateway.ob.uk.support.discovery.accountAndTransaction3_1_2
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.account.OBExternalPermissions1Code.READACCOUNTSDETAIL
import uk.org.openbanking.datamodel.account.OBExternalPermissions1Code.READDIRECTDEBITS
import uk.org.openbanking.datamodel.account.OBReadConsentResponse1
import uk.org.openbanking.datamodel.account.OBReadDirectDebit1

class LegacyGetAccountDirectDebitsTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.2",
        operations = ["CreateAccountAccessConsent", "GetAccountDirectDebits"],
        apis = ["direct-debits"],
        compatibleVersions = ["v.3.1.1", "v.3.1", "v.3.0"]
    )
    @Test
    fun shouldGetAccountDirectDebits_v3_1_2() {
        // Given
        val consentRequest = obReadConsent1(listOf(READACCOUNTSDETAIL, READDIRECTDEBITS))
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
        val result = AccountRS().getAccountsData<OBReadDirectDebit1>(
            AccountFactory.urlWithAccountId(
                accountAndTransaction3_1_2.Links.links.GetAccountDirectDebits,
                USER_ACCOUNT_ID
            ),
            accessToken
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.directDebit).isNotEmpty()
    }
}
