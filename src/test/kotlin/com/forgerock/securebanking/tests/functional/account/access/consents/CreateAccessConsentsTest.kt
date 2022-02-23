package com.forgerock.securebanking.tests.functional.account.access.consents

import assertk.assertThat
import assertk.assertions.isNotNull
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.support.account.AccountFactory.Companion.obReadConsent1
import com.forgerock.securebanking.support.account.AccountRS
import com.forgerock.securebanking.support.discovery.accountAndTransaction3_1
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.account.OBExternalPermissions1Code.READACCOUNTSDETAIL
import uk.org.openbanking.datamodel.account.OBReadConsentResponse1

class CreateAccessConsentsTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1",
        operations = ["CreateAccountAccessConsent"]
    )
    @Test
    fun createAccessConsents_v3_1() {
        // Given
        val consentRequest = obReadConsent1(listOf(READACCOUNTSDETAIL))

        // When
        val result = AccountRS().consent<OBReadConsentResponse1>(
            accountAndTransaction3_1.Links.links.CreateAccountAccessConsent,
            consentRequest,
            tppResource.tpp
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
    }
}
