package com.forgerock.uk.openbanking.tests.functional.account.access.consents

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.forgerock.securebanking.framework.conditions.Status
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.uk.openbanking.support.account.AccountFactory
import com.forgerock.uk.openbanking.support.account.AccountFactory.Companion.obReadConsent1
import com.forgerock.uk.openbanking.support.account.AccountRS
import com.forgerock.uk.openbanking.support.discovery.accountAndTransaction3_1_8
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.account.OBExternalPermissions1Code.READACCOUNTSDETAIL
import uk.org.openbanking.datamodel.account.OBReadConsentResponse1

class GetAccountAccessConsentsTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.8",
        operations = ["GetAccountAccessConsent"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5", "v.3.1.4", "v.3.1.3", "v.3.1.2", "v.3.1.1", "v.3.1", "v.3.0"]
    )
    @Test
    fun getAccountAccessConsents_v3_1_8() {
        // Given
        val consentRequest = obReadConsent1(listOf(READACCOUNTSDETAIL))
        val consent = AccountRS().consent<OBReadConsentResponse1>(
            accountAndTransaction3_1_8.Links.links.CreateAccountAccessConsent,
            consentRequest,
            tppResource.tpp
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotNull()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        // When
        val result =
            AccountRS().getConsent<OBReadConsentResponse1>(
                AccountFactory.urlWithConsentId(
                    accountAndTransaction3_1_8.Links.links.GetAccountAccessConsent,
                    consent.data.consentId
                ),
                tppResource.tpp
            )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.consentId).isEqualTo(consent.data.consentId)
        Assertions.assertThat(result.data.status.toString()).`is`(Status.consentCondition)
    }
}
