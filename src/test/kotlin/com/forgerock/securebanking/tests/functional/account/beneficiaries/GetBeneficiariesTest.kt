package com.forgerock.securebanking.tests.functional.account.beneficiaries

import assertk.assertThat
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.securebanking.framework.configuration.psu
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.support.account.AccountAS
import com.forgerock.securebanking.support.account.AccountRS
import com.forgerock.securebanking.support.discovery.accountAndTransaction3_1
import com.forgerock.securebanking.support.discovery.accountAndTransaction3_1_2
import com.forgerock.securebanking.support.discovery.accountAndTransaction3_1_4
import com.forgerock.securebanking.support.discovery.accountAndTransaction3_1_8
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.account.*
import uk.org.openbanking.datamodel.account.OBExternalPermissions1Code.READACCOUNTSDETAIL
import uk.org.openbanking.datamodel.account.OBExternalPermissions1Code.READBENEFICIARIESDETAIL

class GetBeneficiariesTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetBeneficiaries"],
        apis = ["beneficiaries"],
        compatibleVersions = ["v.3.0"]
    )
    @Test
    fun shouldGetBeneficiaries_v3_1() {
        // Given
        val consentRequest = OBReadConsent1().data(
            OBReadData1()
                .permissions(listOf(READACCOUNTSDETAIL, READBENEFICIARIESDETAIL))
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
        val result = AccountRS().getAccountsData<OBReadBeneficiary3>(
            accountAndTransaction3_1.Links.links.GetBeneficiaries,
            accessToken
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.beneficiary).isNotEmpty()
    }

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.2",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetBeneficiaries"],
        apis = ["beneficiaries"],
        compatibleVersions = ["v.3.1.1"]
    )
    @Test
    fun shouldGetBeneficiaries_v3_1_2() {
        // Given
        val consentRequest = OBReadConsent1().data(
            OBReadData1()
                .permissions(listOf(READACCOUNTSDETAIL, READBENEFICIARIESDETAIL))
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
        val result = AccountRS().getAccountsData<OBReadBeneficiary3>(
            accountAndTransaction3_1_2.Links.links.GetBeneficiaries,
            accessToken
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.beneficiary).isNotEmpty()
    }

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.4",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetBeneficiaries"],
        apis = ["beneficiaries"],
        compatibleVersions = ["v.3.1.3"]
    )
    @Test
    fun shouldGetBeneficiaries_v3_1_4() {
        // Given
        val consentRequest = OBReadConsent1().data(
            OBReadData1()
                .permissions(listOf(READACCOUNTSDETAIL, READBENEFICIARIESDETAIL))
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
        val result = AccountRS().getAccountsData<OBReadBeneficiary4>(
            accountAndTransaction3_1_4.Links.links.GetBeneficiaries,
            accessToken
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.beneficiary).isNotEmpty()
    }

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.8",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetBeneficiaries"],
        apis = ["beneficiaries"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldGetBeneficiaries_v3_1_8() {
        // Given
        val consentRequest = OBReadConsent1().data(
            OBReadData1()
                .permissions(listOf(READACCOUNTSDETAIL, READBENEFICIARIESDETAIL))
        )
            .risk(OBRisk2())
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

        // When
        val result = AccountRS().getAccountsData<OBReadBeneficiary5>(
            accountAndTransaction3_1_8.Links.links.GetBeneficiaries,
            accessToken
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.beneficiary).isNotEmpty()
    }

}