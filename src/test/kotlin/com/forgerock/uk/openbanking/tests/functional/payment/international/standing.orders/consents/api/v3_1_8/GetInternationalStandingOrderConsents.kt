package com.forgerock.uk.openbanking.tests.functional.payment.international.standing.orders.consents.api.v3_1_8

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.securebanking.framework.conditions.Status
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import org.assertj.core.api.Assertions
import uk.org.openbanking.testsupport.payment.OBWriteInternationalStandingOrderConsentTestDataFactory

class GetInternationalStandingOrderConsents(val version: OBVersion, val tppResource: CreateTppCallback.TppResource) {

    private val createInternationalStandingOrderConsentsApi = CreateInternationalStandingOrderConsents(version, tppResource)

    fun shouldGetInternationalStandingOrdersConsents_Test() {
        // Given
        val consentRequest =
            OBWriteInternationalStandingOrderConsentTestDataFactory.aValidOBWriteInternationalStandingOrderConsent6()
        val consent =
            createInternationalStandingOrderConsentsApi.createInternationalStandingOrderConsent(consentRequest)

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()

        // When
        val result = createInternationalStandingOrderConsentsApi.getPatchedConsent(consent)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.risk).isNotNull()
        assertThat(result.data).isEqualTo(consent.data)
        assertThat(result.risk).isEqualTo(consent.risk)
    }
}