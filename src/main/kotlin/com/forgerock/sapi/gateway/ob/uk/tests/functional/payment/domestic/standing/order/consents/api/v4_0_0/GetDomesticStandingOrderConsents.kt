package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.standing.order.consents.api.v4_0_0

import assertk.assertThat
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.sapi.gateway.framework.conditions.StatusV4
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.framework.consent.ConsentFactoryRegistryHolder
import com.forgerock.sapi.gateway.ob.uk.framework.consent.payment.v4.OBWriteDomesticStandingOrderConsent5Factory
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import org.assertj.core.api.Assertions

class GetDomesticStandingOrderConsents(val version: OBVersion, val tppResource: CreateTppCallback.TppResource) {

    private val createDomesticStandingOrderConsentsApi = CreateDomesticStandingOrderConsents(version, tppResource)
    private val consentFactory = ConsentFactoryRegistryHolder.consentFactoryRegistry.getConsentFactory(
        OBWriteDomesticStandingOrderConsent5Factory::class.java
    )

    fun shouldGetDomesticStandingOrdersConsents_Test() {
        // Given
        val consentRequest = consentFactory.createConsent()
        // When
        val consentResponse =
            createDomesticStandingOrderConsentsApi.createDomesticStandingOrderConsent(consentRequest)
        // Then
        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse.data.status.toString()).`is`(StatusV4.consentCondition)
        assertThat(consentResponse.risk).isNotNull()
    }
}