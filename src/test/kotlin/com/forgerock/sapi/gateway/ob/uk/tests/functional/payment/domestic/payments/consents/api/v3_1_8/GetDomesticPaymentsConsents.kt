package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.payments.consents.api.v3_1_8

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import com.forgerock.sapi.gateway.framework.conditions.Status
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.support.discovery.getPaymentsApiLinks
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import org.assertj.core.api.Assertions
import uk.org.openbanking.testsupport.payment.OBWriteDomesticConsentTestDataFactory

class GetDomesticPaymentsConsents(val version: OBVersion, val tppResource: CreateTppCallback.TppResource) {

    private val createDomesticPaymentsConsentsApi = CreateDomesticPaymentsConsents(version, tppResource)
    private val paymentLinks = getPaymentsApiLinks(version)
    
    fun shouldGetDomesticPaymentsConsents() {
        // Given
        val consentRequest = OBWriteDomesticConsentTestDataFactory.aValidOBWriteDomesticConsent4()
        val consent = createDomesticPaymentsConsentsApi.createDomesticPaymentsConsent(consentRequest)

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()

        // When
        val result = createDomesticPaymentsConsentsApi.getPatchedConsent(consent)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.risk).isNotNull()
        assertThat(result.data).isEqualTo(consent.data)
        assertThat(result.risk).isEqualTo(consent.risk)
    }

    fun shouldGetDomesticPaymentsConsents_withoutOptionalDebtorAccountTest() {
        // Given
        val consentRequest = OBWriteDomesticConsentTestDataFactory.aValidOBWriteDomesticConsent4()
        consentRequest.data.initiation.debtorAccount(null)

        val consent = createDomesticPaymentsConsentsApi.createDomesticPaymentsConsent(consentRequest)

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()
        assertThat(consent.data.initiation.debtorAccount).isNull()

        // When
        val result = createDomesticPaymentsConsentsApi.getPatchedConsent(consent)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.risk).isNotNull()
        assertThat(result.data).isEqualTo(consent.data)
        assertThat(result.risk).isEqualTo(consent.risk)
        assertThat(result.data.initiation.debtorAccount).isNull()
    }
}