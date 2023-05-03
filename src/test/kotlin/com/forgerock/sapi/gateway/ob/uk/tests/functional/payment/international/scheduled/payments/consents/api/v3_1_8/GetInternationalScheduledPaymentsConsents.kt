package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.scheduled.payments.consents.api.v3_1_8

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.support.discovery.getPaymentsApiLinks
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import uk.org.openbanking.datamodel.payment.OBExchangeRateType2Code
import uk.org.openbanking.testsupport.payment.OBWriteInternationalScheduledConsentTestDataFactory

class GetInternationalScheduledPaymentsConsents(
    val version: OBVersion,
    val tppResource: CreateTppCallback.TppResource
) {
    private val createInternationalScheduledPaymentsConsents =
        CreateInternationalScheduledPaymentsConsents(version, tppResource)
    private val paymentLinks = getPaymentsApiLinks(version)

    fun shouldGetInternationalScheduledPaymentsConsents_rateType_AGREED_Test() {
        // Given
        val consentRequest =
            OBWriteInternationalScheduledConsentTestDataFactory.aValidOBWriteInternationalScheduledConsent5()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.AGREED
        // When
        val consentResponse =
            createInternationalScheduledPaymentsConsents.createInternationalScheduledPaymentConsent(consentRequest)

        // Then
        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.risk).isNotNull()
        assertThat(consentResponse.data.initiation.exchangeRateInformation.rateType).isEqualTo(OBExchangeRateType2Code.AGREED)
    }

    fun shouldGetInternationalScheduledPaymentsConsents_rateType_ACTUAL_Test() {
        // Given
        val consentRequest =
            OBWriteInternationalScheduledConsentTestDataFactory.aValidOBWriteInternationalScheduledConsent5()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.ACTUAL
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null
        // When
        val consentResponse =
            createInternationalScheduledPaymentsConsents.createInternationalScheduledPaymentConsent(consentRequest)

        // Then
        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.risk).isNotNull()
        assertThat(consentResponse.data.initiation.exchangeRateInformation.rateType).isEqualTo(OBExchangeRateType2Code.ACTUAL)
    }

    fun shouldGetInternationalScheduledPaymentsConsents_rateType_INDICATIVE_Test() {
        // Given
        val consentRequest =
            OBWriteInternationalScheduledConsentTestDataFactory.aValidOBWriteInternationalScheduledConsent5()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.INDICATIVE
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null

        // When
        val consentResponse =
            createInternationalScheduledPaymentsConsents.createInternationalScheduledPaymentConsent(consentRequest)

        // Then
        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.risk).isNotNull()
        assertThat(consentResponse.data.initiation.exchangeRateInformation.rateType).isEqualTo(OBExchangeRateType2Code.INDICATIVE)
    }
}