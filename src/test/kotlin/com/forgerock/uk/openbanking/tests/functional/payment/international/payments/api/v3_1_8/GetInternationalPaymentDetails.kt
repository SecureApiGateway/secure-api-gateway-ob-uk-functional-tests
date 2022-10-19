package com.forgerock.uk.openbanking.tests.functional.payment.international.payments.api.v3_1_8

import assertk.assertThat
import assertk.assertions.isNotNull
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.support.discovery.getPaymentsApiLinks
import com.forgerock.uk.openbanking.support.payment.PaymentFactory
import com.forgerock.uk.openbanking.support.payment.defaultPaymentScopesForAccessToken
import uk.org.openbanking.datamodel.payment.OBExchangeRateType2Code
import uk.org.openbanking.datamodel.payment.OBWriteInternationalResponse5
import uk.org.openbanking.datamodel.payment.OBWritePaymentDetailsResponse1
import uk.org.openbanking.testsupport.payment.OBWriteInternationalConsentTestDataFactory

class GetInternationalPaymentDetails(
    val version: OBVersion,
    val tppResource: CreateTppCallback.TppResource
) {
    private val createInternationalPayment = CreateInternationalPayment(version, tppResource)
    private val paymentLinks = getPaymentsApiLinks(version)
    private val paymentApiClient = tppResource.tpp.paymentApiClient

    fun getInternationalPaymentInternationalPaymentIdPaymentDetails_rateType_AGREED_Test() {
        // Given
        val consentRequest = OBWriteInternationalConsentTestDataFactory.aValidOBWriteInternationalConsent5()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.AGREED
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
        val consentRequest = OBWriteInternationalConsentTestDataFactory.aValidOBWriteInternationalConsent5()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.ACTUAL
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
        val consentRequest = OBWriteInternationalConsentTestDataFactory.aValidOBWriteInternationalConsent5()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.INDICATIVE
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
            OBWriteInternationalConsentTestDataFactory.aValidOBWriteInternationalConsent5MandatoryFields()
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