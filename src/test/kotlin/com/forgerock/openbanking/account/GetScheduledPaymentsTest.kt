package com.forgerock.openbanking.account

import assertk.assertThat
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.openbanking.account.AccountFactory.Companion.obReadConsent1
import com.forgerock.openbanking.discovery.accountAndTransaction3_1_6
import com.forgerock.openbanking.junit.CreateTppCallback
import com.forgerock.openbanking.junit.EnabledIfOpenBankingVersion
import com.forgerock.openbanking.psu
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.account.*
import uk.org.openbanking.datamodel.account.OBExternalPermissions1Code.READACCOUNTSDETAIL
import uk.org.openbanking.datamodel.account.OBExternalPermissions1Code.READSCHEDULEDPAYMENTSDETAIL

class GetScheduledPaymentsTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfOpenBankingVersion(type = "accounts", version = "v3.1.6", apis = ["scheduled-payments"])
    @Test
    fun shouldGetScheduledPayments() {
        // Given
        val consentRequest = obReadConsent1(listOf(READSCHEDULEDPAYMENTSDETAIL))
        val consent = AccountRS().consent<OBReadConsentResponse1>(accountAndTransaction3_1_6.Links.links.CreateAccountAccessConsent, consentRequest, tppResource.tpp)
        val accessToken = AccountAS().headlessAuthentication(consent.data.consentId, tppResource.tpp.registrationResponse, psu, tppResource.tpp)

        // When
        val result = AccountRS().getAccountData<OBReadScheduledPayment3>(accountAndTransaction3_1_6.Links.links.GetScheduledPayments, accessToken)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.scheduledPayment).isNotEmpty()
    }

    @EnabledIfOpenBankingVersion(type = "accounts", version = "v3.1.6", apis = ["scheduled-payments"])
    @Test
    fun shouldGetAccountScheduledPayments() {
        // Given
        val consentRequest = OBReadConsent1().data(OBReadData1()
                .permissions(listOf(READACCOUNTSDETAIL, READSCHEDULEDPAYMENTSDETAIL)))
                .risk(OBRisk2())
        val consent = AccountRS().consent<OBReadConsentResponse1>(accountAndTransaction3_1_6.Links.links.CreateAccountAccessConsent, consentRequest, tppResource.tpp)
        val accessToken = AccountAS().headlessAuthentication(consent.data.consentId, tppResource.tpp.registrationResponse, psu, tppResource.tpp)
        val accountId = AccountRS().getFirstAccountId(accountAndTransaction3_1_6.Links.links.GetAccounts, accessToken)

        // When
        val result = AccountRS().getAccountData<OBReadScheduledPayment3>(AccountFactory.urlWithAccountId(accountAndTransaction3_1_6.Links.links.GetAccountScheduledPayments, accountId), accessToken)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.scheduledPayment).isNotEmpty()
    }
}