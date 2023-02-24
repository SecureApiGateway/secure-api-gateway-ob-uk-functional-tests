package com.forgerock.uk.openbanking.tests.functional.account.scheduled.payments.legacy

import assertk.assertThat
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.securebanking.framework.configuration.USER_ACCOUNT_ID
import com.forgerock.securebanking.framework.configuration.psu
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.uk.openbanking.support.account.AccountAS
import com.forgerock.uk.openbanking.support.account.AccountFactory
import com.forgerock.uk.openbanking.support.account.AccountRS
import com.forgerock.uk.openbanking.support.discovery.accountAndTransaction3_1
import com.forgerock.uk.openbanking.support.discovery.accountAndTransaction3_1_2
import com.forgerock.uk.openbanking.support.discovery.accountAndTransaction3_1_4
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.account.*
import uk.org.openbanking.datamodel.account.OBExternalPermissions1Code.READACCOUNTSDETAIL
import uk.org.openbanking.datamodel.account.OBExternalPermissions1Code.READSCHEDULEDPAYMENTSDETAIL

class LegacyGetAccountScheduledPaymentsTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountScheduledPayments"],
        apis = ["scheduled-payments"],
        compatibleVersions = ["v.3.0"]
    )
    @Test
    fun shouldGetAccountScheduledPayments_v3_1() {
        // Given
        val consentRequest = OBReadConsent1().data(
            OBReadData1()
                .permissions(listOf(READACCOUNTSDETAIL, READSCHEDULEDPAYMENTSDETAIL))
        )
            .risk(OBRisk2())
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
        val result = AccountRS().getAccountsData<OBReadScheduledPayment2>(
            AccountFactory.urlWithAccountId(
                accountAndTransaction3_1.Links.links.GetAccountScheduledPayments,
                USER_ACCOUNT_ID
            ), accessToken
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.scheduledPayment).isNotEmpty()
    }

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.2",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountScheduledPayments"],
        apis = ["scheduled-payments"],
        compatibleVersions = ["v.3.1.1"]
    )
    @Test
    fun shouldGetAccountScheduledPayments_v3_1_2() {
        // Given
        val consentRequest = OBReadConsent1().data(
            OBReadData1()
                .permissions(listOf(READACCOUNTSDETAIL, READSCHEDULEDPAYMENTSDETAIL))
        )
            .risk(OBRisk2())
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
        val result = AccountRS().getAccountsData<OBReadScheduledPayment2>(
            AccountFactory.urlWithAccountId(
                accountAndTransaction3_1_2.Links.links.GetAccountScheduledPayments,
                USER_ACCOUNT_ID
            ), accessToken
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.scheduledPayment).isNotEmpty()
    }

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.4",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountScheduledPayments"],
        apis = ["scheduled-payments"],
        compatibleVersions = ["v.3.1.3"]
    )
    @Test
    fun shouldGetAccountScheduledPayments_v3_1_4() {
        // Given
        val consentRequest = OBReadConsent1().data(
            OBReadData1()
                .permissions(listOf(READACCOUNTSDETAIL, READSCHEDULEDPAYMENTSDETAIL))
        )
            .risk(OBRisk2())
        val consent = AccountRS().consent<OBReadConsentResponse1>(
            accountAndTransaction3_1_4.Links.links.CreateAccountAccessConsent,
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
            AccountFactory.urlWithAccountId(
                accountAndTransaction3_1_4.Links.links.GetAccountScheduledPayments,
                USER_ACCOUNT_ID
            ), accessToken
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.scheduledPayment).isNotEmpty()
    }
}
