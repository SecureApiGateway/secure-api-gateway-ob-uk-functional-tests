package com.forgerock.securebanking.tests.functional.account.products

import assertk.assertThat
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.securebanking.framework.configuration.psu
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.support.account.AccountAS
import com.forgerock.securebanking.support.account.AccountRS
import com.forgerock.securebanking.support.discovery.accountAndTransaction3_1
import com.forgerock.securebanking.support.discovery.accountAndTransaction3_1_1
import com.forgerock.securebanking.support.discovery.accountAndTransaction3_1_6
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.account.*
import uk.org.openbanking.datamodel.account.OBExternalPermissions1Code.READACCOUNTSDETAIL
import uk.org.openbanking.datamodel.account.OBExternalPermissions1Code.READPRODUCTS

class GetProductsTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetProducts"],
        apis = ["transactions"]
    )
    @Test
    fun shouldGetProducts_v3_1() {
        // Given
        val consentRequest = OBReadConsent1().data(
            OBReadData1()
                .permissions(
                    listOf(
                        READACCOUNTSDETAIL, READPRODUCTS
                    )
                )
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
        val result = AccountRS().getAccountsData<OBReadProduct2>(
            accountAndTransaction3_1.Links.links.GetProducts, accessToken
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.product).isNotEmpty()
    }

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.1",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetProducts"],
        apis = ["transactions"]
    )
    @Test
    fun shouldGetProducts_v3_1_1() {
        // Given
        val consentRequest = OBReadConsent1().data(
            OBReadData1()
                .permissions(
                    listOf(
                        READACCOUNTSDETAIL, READPRODUCTS
                    )
                )
        )
            .risk(OBRisk2())
        val consent = AccountRS().consent<OBReadConsentResponse1>(
            accountAndTransaction3_1_1.Links.links.CreateAccountAccessConsent,
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
        val result = AccountRS().getAccountsData<OBReadProduct2>(
            accountAndTransaction3_1_1.Links.links.GetProducts, accessToken
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.product).isNotEmpty()
    }

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.6",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetProducts"],
        apis = ["transactions"]
    )
    @Test
    fun shouldGetProducts_v3_1_6() {
        // Given
        val consentRequest = OBReadConsent1().data(
            OBReadData1()
                .permissions(
                    listOf(
                        READACCOUNTSDETAIL, READPRODUCTS
                    )
                )
        )
            .risk(OBRisk2())
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
        val result = AccountRS().getAccountsData<OBReadProduct2>(
            accountAndTransaction3_1_6.Links.links.GetProducts, accessToken
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.product).isNotEmpty()
    }
}
