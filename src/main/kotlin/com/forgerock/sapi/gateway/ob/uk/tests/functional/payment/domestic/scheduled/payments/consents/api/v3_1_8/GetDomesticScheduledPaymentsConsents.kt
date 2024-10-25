package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.scheduled.payments.consents.api.v3_1_8

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import com.forgerock.sapi.gateway.framework.conditions.Status
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.framework.consent.ConsentFactoryRegistryHolder
import com.forgerock.sapi.gateway.ob.uk.framework.consent.payment.OBWriteDomesticScheduledConsent4Factory
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import org.assertj.core.api.Assertions
import uk.org.openbanking.testsupport.payment.OBWriteDomesticScheduledConsentTestDataFactory

class GetDomesticScheduledPaymentsConsents(val version: OBVersion, val tppResource: CreateTppCallback.TppResource) {

    private val createDomesticScheduledPaymentsConsents = CreateDomesticScheduledPaymentsConsents(version, tppResource)
    private val consentFactory =
        ConsentFactoryRegistryHolder.consentFactoryRegistry.getConsentFactory(OBWriteDomesticScheduledConsent4Factory::class.java)

    fun shouldGetDomesticScheduledPaymentsConsentsTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        // When
        val consentResponse = createDomesticScheduledPaymentsConsents.createDomesticScheduledPaymentConsent(consentRequest)
        // Then
        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consentResponse.risk).isNotNull()
    }

    fun shouldGetDomesticScheduledPaymentsConsents_withoutOptionalDebtorAccountTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        consentRequest.data.initiation.debtorAccount(null)
        // Then
        val consentResponse = createDomesticScheduledPaymentsConsents.createDomesticScheduledPaymentConsent(consentRequest)

        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consentResponse.risk).isNotNull()
        assertThat(consentResponse.data.initiation.debtorAccount).isNull()
    }
}
