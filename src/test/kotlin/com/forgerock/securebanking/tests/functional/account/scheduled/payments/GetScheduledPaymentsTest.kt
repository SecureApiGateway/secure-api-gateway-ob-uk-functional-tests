package com.forgerock.securebanking.tests.functional.account.scheduled.payments

import assertk.assertThat
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.securebanking.framework.configuration.psu
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.support.account.AccountAS
import com.forgerock.securebanking.support.account.AccountFactory.Companion.obReadConsent1
import com.forgerock.securebanking.support.account.AccountRS
import com.forgerock.securebanking.support.discovery.accountAndTransaction3_1
import com.forgerock.securebanking.support.discovery.accountAndTransaction3_1_6
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.account.OBExternalPermissions1Code.READSCHEDULEDPAYMENTSDETAIL
import uk.org.openbanking.datamodel.account.OBReadConsentResponse1
import uk.org.openbanking.datamodel.account.OBReadScheduledPayment3

class GetScheduledPaymentsTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1",
        operations = ["CreateAccountAccessConsent", "GetScheduledPayments"],
        apis = ["scheduled-payments"]
    )
    @Test
    fun shouldGetScheduledPayments_v3_1() {
        // Given
        val consentRequest = obReadConsent1(listOf(READSCHEDULEDPAYMENTSDETAIL))
        val consent = AccountRS().consent<OBReadConsentResponse1>(
            accountAndTransaction3_1.Links.links.CreateAccountAccessConsent,
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
        val result = AccountRS().getAccountsData<OBReadScheduledPayment3>(
            accountAndTransaction3_1.Links.links.GetScheduledPayments,
            accessToken
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.scheduledPayment).isNotEmpty()
    }

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.6",
        operations = ["CreateAccountAccessConsent", "GetScheduledPayments"],
        apis = ["scheduled-payments"]
    )
    @Test
    fun shouldGetScheduledPayments_v3_1_6() {
        // Given
        val consentRequest = obReadConsent1(listOf(READSCHEDULEDPAYMENTSDETAIL))
        val consent = AccountRS().consent<OBReadConsentResponse1>(
            accountAndTransaction3_1_6.Links.links.CreateAccountAccessConsent,
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
        val result = AccountRS().getAccountsData<OBReadScheduledPayment3>(
            accountAndTransaction3_1_6.Links.links.GetScheduledPayments,
            accessToken
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.scheduledPayment).isNotEmpty()
    }
}
