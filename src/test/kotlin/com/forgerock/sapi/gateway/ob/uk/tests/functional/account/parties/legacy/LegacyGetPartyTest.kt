package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.parties.legacy

import assertk.assertThat
import assertk.assertions.isNotEqualTo
import assertk.assertions.isNotNull
import com.forgerock.sapi.gateway.framework.configuration.psu
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.ob.uk.support.account.AccountAS
import com.forgerock.sapi.gateway.ob.uk.support.account.AccountRS
import com.forgerock.sapi.gateway.ob.uk.support.discovery.accountAndTransaction3_1
import com.forgerock.sapi.gateway.ob.uk.support.discovery.accountAndTransaction3_1_2
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.account.*
import uk.org.openbanking.datamodel.account.OBExternalPermissions1Code.READACCOUNTSDETAIL
import uk.org.openbanking.datamodel.account.OBExternalPermissions1Code.READPARTYPSU

class LegacyGetPartyTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1",
        operations = ["CreateAccountAccessConsent", "GetParty"],
        apis = ["party"],
        compatibleVersions = ["v.3.0"]
    )
    @Test
    fun shouldGetParty_v3_1() {
        // Given
        val consentRequest = OBReadConsent1().data(
            OBReadData1()
                .permissions(listOf(READPARTYPSU, READACCOUNTSDETAIL, OBExternalPermissions1Code.READPARTY))
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
        val (_, psuId) = AccountRS().getFirstAccountIdAndPsuId(
            accountAndTransaction3_1.Links.links.GetAccounts,
            accessToken
        )
        assertThat(psuId).isNotEqualTo("")
        psu.user.uid = psuId

        // when
        val party =
            AccountRS().getAccountsDataEndUser<OBReadParty1>(
                accountAndTransaction3_1.Links.links.GetParty,
                accessToken
            )

        // Then
        assertThat(party).isNotNull()
        assertThat(party.data.party).isNotNull()
    }

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.2",
        operations = ["CreateAccountAccessConsent", "GetParty"],
        apis = ["party"],
        compatibleVersions = ["v.3.1.1"]
    )
    @Test
    fun shouldGetParty_v3_1_2() {
        // Given
        val consentRequest = OBReadConsent1().data(
            OBReadData1()
                .permissions(listOf(READPARTYPSU, READACCOUNTSDETAIL))
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
        val (_, psuId) = AccountRS().getFirstAccountIdAndPsuId(
            accountAndTransaction3_1_2.Links.links.GetAccounts,
            accessToken
        )
        assertThat(psuId).isNotEqualTo("")
        psu.user.uid = psuId

        // when
        val party =
            AccountRS().getAccountsData<OBReadParty2>(accountAndTransaction3_1_2.Links.links.GetParty, accessToken)

        // Then
        assertThat(party).isNotNull()
        assertThat(party.data.party).isNotNull()
    }
}
