package com.forgerock.openbanking.account

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.openbanking.account.AccountFactory.Companion.obReadConsent1
import com.forgerock.openbanking.discovery.*
import com.forgerock.openbanking.junit.CreateTppCallback
import com.forgerock.openbanking.junit.EnabledIfOpenBankingVersion
import com.forgerock.openbanking.psu
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.account.OBExternalPermissions1Code.READACCOUNTSDETAIL
import uk.org.openbanking.datamodel.account.OBReadAccount3
import uk.org.openbanking.datamodel.account.OBReadAccount5
import uk.org.openbanking.datamodel.account.OBReadConsentResponse1

@Tags(Tag("accountTest"))
class GetAccountsTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfOpenBankingVersion(type = "accounts", version = "v3.1")
    @Test
    fun shouldGetAccounts_v3_1() {
        // Given
        val consentRequest = obReadConsent1(listOf(READACCOUNTSDETAIL))
        val consent = AccountRS().consent<OBReadConsentResponse1>(accountAndTransaction3_1.Links.links.CreateAccountAccessConsent, consentRequest, tppResource.tpp)
        val accessToken = AccountAS().headlessAuthentication(consent.data.consentId, tppResource.tpp.registrationResponse, psu, tppResource.tpp)

        // When
        val result = AccountRS().getAccountData<OBReadAccount3>(accountAndTransaction3_1.Links.links.GetAccounts, accessToken)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.account).isNotEmpty()
        assertThat(result.data.account.size).isEqualTo(3)
    }

    @EnabledIfOpenBankingVersion(type = "accounts", version = "v3.1.1")
    @Test
    fun shouldGetAccounts_v3_1_1() {
        // Given
        val consentRequest = obReadConsent1(listOf(READACCOUNTSDETAIL))
        val consent = AccountRS().consent<OBReadConsentResponse1>(accountAndTransaction3_1_1.Links.links.CreateAccountAccessConsent, consentRequest, tppResource.tpp)
        val accessToken = AccountAS().headlessAuthentication(consent.data.consentId, tppResource.tpp.registrationResponse, psu, tppResource.tpp)

        // When
        val result = AccountRS().getAccountData<OBReadAccount3>(accountAndTransaction3_1_1.Links.links.GetAccounts, accessToken)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.account).isNotEmpty()
        assertThat(result.data.account.size).isEqualTo(3)
    }

    @EnabledIfOpenBankingVersion(type = "accounts", version = "v3.1.4")
    @Test
    fun shouldGetAccounts_v3_1_4() {
        // Given
        val consentRequest = obReadConsent1(listOf(READACCOUNTSDETAIL))
        val consent = AccountRS().consent<OBReadConsentResponse1>(accountAndTransaction3_1_4.Links.links.CreateAccountAccessConsent, consentRequest, tppResource.tpp)
        val accessToken = AccountAS().headlessAuthentication(consent.data.consentId, tppResource.tpp.registrationResponse, psu, tppResource.tpp)

        // When
        val result = AccountRS().getAccountData<OBReadAccount5>(accountAndTransaction3_1_4.Links.links.GetAccounts, accessToken)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.account).isNotEmpty()
        assertThat(result.data.account.size).isEqualTo(3)
    }

    @EnabledIfOpenBankingVersion(type = "accounts", version = "v3.1.6")
    @Test
    fun shouldGetAccounts_v3_1_6() {
        // Given
        val consentRequest = obReadConsent1(listOf(READACCOUNTSDETAIL))
        val consent = AccountRS().consent<OBReadConsentResponse1>(accountAndTransaction3_1_6.Links.links.CreateAccountAccessConsent, consentRequest, tppResource.tpp)
        val accessToken = AccountAS().headlessAuthentication(consent.data.consentId, tppResource.tpp.registrationResponse, psu, tppResource.tpp)

        // When
        val result = AccountRS().getAccountData<OBReadAccount5>(accountAndTransaction3_1_6.Links.links.GetAccounts, accessToken)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.account).isNotEmpty()
        assertThat(result.data.account.size).isEqualTo(3)
    }
}