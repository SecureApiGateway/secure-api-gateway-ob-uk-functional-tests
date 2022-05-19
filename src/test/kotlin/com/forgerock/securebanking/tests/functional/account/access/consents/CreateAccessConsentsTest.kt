package com.forgerock.securebanking.tests.functional.account.access.consents

import assertk.assertThat
import assertk.assertions.isNotNull
import com.forgerock.securebanking.framework.conditions.Status
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.support.account.AccountFactory.Companion.obReadConsent1
import com.forgerock.securebanking.support.account.AccountRS
import com.forgerock.securebanking.support.discovery.accountAndTransaction3_1_8
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.account.OBExternalPermissions1Code.READACCOUNTSDETAIL
import uk.org.openbanking.datamodel.account.OBReadConsentResponse1

class CreateAccessConsentsTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.8",
        operations = ["CreateAccountAccessConsent"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5", "v.3.1.4", "v.3.1.3", "v.3.1.2", "v.3.1.1", "v.3.1", "v.3.0"]
    )
    @Test
    fun createAccessConsents_v3_1_8() {
        // Given
        val consentRequest = obReadConsent1(listOf(READACCOUNTSDETAIL))

        // When
        val result = AccountRS().consent<OBReadConsentResponse1>(
            accountAndTransaction3_1_8.Links.links.CreateAccountAccessConsent,
            consentRequest,
            tppResource.tpp
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        Assertions.assertThat(result.data.status.toString()).`is`(Status.consentCondition)
    }
}
