package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.payments.api.v3_1_8

import assertk.assertThat
import assertk.assertions.isNotNull
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.framework.consent.ConsentFactoryRegistryHolder
import com.forgerock.sapi.gateway.ob.uk.framework.consent.payment.OBWriteInternationalConsent5Factory
import com.forgerock.sapi.gateway.ob.uk.support.discovery.getPaymentsApiLinks
import com.forgerock.sapi.gateway.ob.uk.support.payment.PaymentFactory
import com.forgerock.sapi.gateway.ob.uk.support.payment.defaultPaymentScopesForAccessToken
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import uk.org.openbanking.datamodel.payment.OBExchangeRateType
import uk.org.openbanking.datamodel.payment.OBWriteInternationalResponse5
import uk.org.openbanking.datamodel.payment.OBWritePaymentDetailsResponse1

class GetInternationalPaymentDetails(
    val version: OBVersion,
    val tppResource: CreateTppCallback.TppResource
) {
    private val createInternationalPayment = CreateInternationalPayment(version, tppResource)
    private val paymentLinks = getPaymentsApiLinks(version)
    private val paymentApiClient = tppResource.tpp.paymentApiClient
    private val consentFactory = ConsentFactoryRegistryHolder.consentFactoryRegistry.getConsentFactory(
        OBWriteInternationalConsent5Factory::class.java
    )

    fun getInternationalPaymentInternationalPaymentIdPaymentDetails_rateType_AGREED_Test() {
        // Given
        val consentRequest = consentFactory.createConsent()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType.AGREED
        val paymentResponse = createInternationalPayment.submitPayment(consentRequest)

        // When
        val result = getPaymentDetails(paymentResponse)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.paymentStatus).isNotNull()
    }

    fun getInternationalPaymentInternationalPaymentIdPaymentDetails_rateType_ACTUAL_Test() {
        // Given
        val consentRequest = consentFactory.createConsent()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType.ACTUAL
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null

        val paymentResponse = createInternationalPayment.submitPayment(consentRequest)

        // When
        val result = getPaymentDetails(paymentResponse)


        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.paymentStatus).isNotNull()
    }

    fun getInternationalPaymentInternationalPaymentIdPaymentDetails_rateType_INDICATIVE_Test() {
        // Given
        val consentRequest = consentFactory.createConsent()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType.INDICATIVE
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null

        val paymentResponse = createInternationalPayment.submitPayment(consentRequest)

        // When
        val result = getPaymentDetails(paymentResponse)


        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.paymentStatus).isNotNull()
    }

    fun getInternationalPaymentInternationalPaymentIdPaymentDetails_mandatoryFields_Test() {
        // Given
        val consentRequest =
            consentFactory.createConsentWithOnlyMandatoryFieldsPopulated()
        val paymentResponse = createInternationalPayment.submitPayment(consentRequest)

        // When
        val result = getPaymentDetails(paymentResponse)


        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.paymentStatus).isNotNull()
    }

    private fun getPaymentDetails(paymentResponse: OBWriteInternationalResponse5): OBWritePaymentDetailsResponse1 {
        val getInternationalPaymentDetails = PaymentFactory.urlWithInternationalPaymentId(
            paymentLinks.GetInternationalPaymentInternationalPaymentIdPaymentDetails,
            paymentResponse.data.internationalPaymentId
        )
        return paymentApiClient.sendGetRequest(
            getInternationalPaymentDetails,
            tppResource.tpp.getClientCredentialsAccessToken(defaultPaymentScopesForAccessToken)
        )
    }
}