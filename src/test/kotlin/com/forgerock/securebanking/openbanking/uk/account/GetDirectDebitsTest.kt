package com.forgerock.openbanking.account

import assertk.assertThat
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.openbanking.account.AccountFactory.Companion.obReadConsent1
import com.forgerock.openbanking.discovery.accountAndTransaction3_1_6
import com.forgerock.openbanking.junit.CreateTppCallback
import com.forgerock.openbanking.junit.EnabledIfOpenBankingVersion
import com.forgerock.openbanking.psu
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.account.*
import uk.org.openbanking.datamodel.account.OBExternalPermissions1Code.READACCOUNTSDETAIL
import uk.org.openbanking.datamodel.account.OBExternalPermissions1Code.READDIRECTDEBITS

@Tags(Tag("accountTest"))
class GetDirectDebitsTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfOpenBankingVersion(type = "accounts", version = "v3.1.6", apis = ["direct-debits"])
    @Test
    fun shouldGetDirectDebits() {
        // Given
        val consentRequest = obReadConsent1(listOf(READDIRECTDEBITS))
        val consent = AccountRS().consent<OBReadConsentResponse1>(accountAndTransaction3_1_6.Links.links.CreateAccountAccessConsent, consentRequest, tppResource.tpp)
        val accessToken = AccountAS().headlessAuthentication(consent.data.consentId, tppResource.tpp.registrationResponse, psu, tppResource.tpp)

        // When
        val result = AccountRS().getAccountData<OBReadDirectDebit2>(accountAndTransaction3_1_6.Links.links.GetDirectDebits, accessToken)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.directDebit).isNotEmpty()
    }

    @EnabledIfOpenBankingVersion(type = "accounts", version = "v3.1.6", apis = ["direct-debits"])
    @Test
    fun shouldGetAccountDirectDebits() {
        // Given
        val consentRequest = OBReadConsent1().data(OBReadData1()
                .permissions(listOf(READACCOUNTSDETAIL, READDIRECTDEBITS)))
                .risk(OBRisk2())
        val consent = AccountRS().consent<OBReadConsentResponse1>(accountAndTransaction3_1_6.Links.links.CreateAccountAccessConsent, consentRequest, tppResource.tpp)
        val accessToken = AccountAS().headlessAuthentication(consent.data.consentId, tppResource.tpp.registrationResponse, psu, tppResource.tpp)
        val accountId = AccountRS().getFirstAccountId(accountAndTransaction3_1_6.Links.links.GetAccounts, accessToken)

        // When
        val result = AccountRS().getAccountData<OBReadDirectDebit2>(AccountFactory.urlWithAccountId(accountAndTransaction3_1_6.Links.links.GetAccountDirectDebits, accountId), accessToken)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.directDebit).isNotEmpty()
    }
}