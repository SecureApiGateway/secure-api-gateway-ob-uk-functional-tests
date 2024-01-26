package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.scheduled.payments.consents.api.v3_1_8

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.framework.consent.ConsentFactoryRegistryHolder
import com.forgerock.sapi.gateway.ob.uk.framework.consent.payment.OBWriteInternationalScheduledConsent5Factory
import com.forgerock.sapi.gateway.ob.uk.support.discovery.getPaymentsApiLinks
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import uk.org.openbanking.datamodel.payment.OBExchangeRateType

class GetInternationalScheduledPaymentsConsents(
    val version: OBVersion,
    val tppResource: CreateTppCallback.TppResource
) {
    private val createInternationalScheduledPaymentsConsents =
        CreateInternationalScheduledPaymentsConsents(version, tppResource)
    private val paymentLinks = getPaymentsApiLinks(version)
    private val consentFactory = ConsentFactoryRegistryHolder.consentFactoryRegistry.getConsentFactory(
        OBWriteInternationalScheduledConsent5Factory::class.java
    )


    fun shouldGetInternationalScheduledPaymentsConsents_rateType_AGREED_Test() {
        // Given
        val consentRequest =
            consentFactory.createConsent()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType.AGREED
        // When
        val consentResponse =
            createInternationalScheduledPaymentsConsents.createInternationalScheduledPaymentConsent(consentRequest)

        // Then
        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.risk).isNotNull()
        assertThat(consentResponse.data.initiation.exchangeRateInformation.rateType).isEqualTo(OBExchangeRateType.AGREED)
    }

    fun shouldGetInternationalScheduledPaymentsConsents_rateType_ACTUAL_Test() {
        // Given
        val consentRequest =
            consentFactory.createConsent()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType.ACTUAL
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null
        // When
        val consentResponse =
            createInternationalScheduledPaymentsConsents.createInternationalScheduledPaymentConsent(consentRequest)

        // Then
        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.risk).isNotNull()
        assertThat(consentResponse.data.initiation.exchangeRateInformation.rateType).isEqualTo(OBExchangeRateType.ACTUAL)
    }

    fun shouldGetInternationalScheduledPaymentsConsents_rateType_INDICATIVE_Test() {
        // Given
        val consentRequest =
            consentFactory.createConsent()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType.INDICATIVE
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null

        // When
        val consentResponse =
            createInternationalScheduledPaymentsConsents.createInternationalScheduledPaymentConsent(consentRequest)

        // Then
        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.risk).isNotNull()
        assertThat(consentResponse.data.initiation.exchangeRateInformation.rateType).isEqualTo(OBExchangeRateType.INDICATIVE)
    }
}