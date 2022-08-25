package com.forgerock.uk.openbanking.tests.functional.payment.international.payments.api.v3_1_8

import assertk.assertThat
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.securebanking.framework.conditions.Status
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.support.discovery.getPaymentsApiLinks
import com.forgerock.uk.openbanking.support.payment.PaymentFactory
import com.forgerock.uk.openbanking.support.payment.defaultPaymentScopesForAccessToken
import org.assertj.core.api.Assertions
import uk.org.openbanking.datamodel.payment.*
import uk.org.openbanking.testsupport.payment.OBWriteInternationalConsentTestDataFactory

class GetInternationalPayment(
    val version: OBVersion,
    val tppResource: CreateTppCallback.TppResource
) {
    private val createInternationalPayment = CreateInternationalPayment(version, tppResource)
    private val paymentLinks = getPaymentsApiLinks(version)
    private val paymentApiClient = tppResource.tpp.paymentApiClient

    fun getInternationalPayments_rateType_AGREED_Test() {
        // Given
        val consentRequest = OBWriteInternationalConsentTestDataFactory.aValidOBWriteInternationalConsent5()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.AGREED
        val paymentResponse = createInternationalPayment.submitPayment(consentRequest)

        // When
        val result = getInternationalPayment(paymentResponse)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.internationalPaymentId).isNotEmpty()
        assertThat(result.data.creationDateTime).isNotNull()
        Assertions.assertThat(result.data.status.toString()).`is`(Status.paymentCondition)
        assertThat(result.data.exchangeRateInformation.exchangeRate).isNotNull()
    }

    fun getInternationalPayments_rateType_ACTUAL_Test() {
        // Given
        val consentRequest = OBWriteInternationalConsentTestDataFactory.aValidOBWriteInternationalConsent5()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.ACTUAL
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null

        val paymentResponse = createInternationalPayment.submitPayment(consentRequest)

        // When
        val result = getInternationalPayment(paymentResponse)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.internationalPaymentId).isNotEmpty()
        assertThat(result.data.creationDateTime).isNotNull()
        Assertions.assertThat(result.data.status.toString()).`is`(Status.paymentCondition)
    }

    fun getInternationalPayments_rateType_INDICATIVE_Test() {
        // Given
        val consentRequest = OBWriteInternationalConsentTestDataFactory.aValidOBWriteInternationalConsent5()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.INDICATIVE
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null

        val paymentResponse = createInternationalPayment.submitPayment(consentRequest)

        // When
        val result = getInternationalPayment(paymentResponse)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.internationalPaymentId).isNotEmpty()
        assertThat(result.data.creationDateTime).isNotNull()
        Assertions.assertThat(result.data.status.toString()).`is`(Status.paymentCondition)
    }

    fun getInternationalPayments_mandatoryFields_Test() {
        // Given
        val consentRequest =
            OBWriteInternationalConsentTestDataFactory.aValidOBWriteInternationalConsent5MandatoryFields()

        val paymentResponse = createInternationalPayment.submitPayment(consentRequest)

        // When
        val result = getInternationalPayment(paymentResponse)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.internationalPaymentId).isNotEmpty()
        assertThat(result.data.creationDateTime).isNotNull()
        Assertions.assertThat(result.data.status.toString()).`is`(Status.paymentCondition)
    }

    fun shouldGetInternationalPayments_withReadRefund_Test() {
        // Given
        val consentRequest = OBWriteInternationalConsentTestDataFactory.aValidOBWriteInternationalConsent5()
        consentRequest.data.readRefundAccount = OBReadRefundAccountEnum.YES

        val paymentResponse = createInternationalPayment.submitPayment(consentRequest)

        // When
        val result = getInternationalPayment(paymentResponse)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.internationalPaymentId).isNotEmpty()
        assertThat(result.data.creationDateTime).isNotNull()
        //TODO: Waiting for the fix from the issue: https://github.com/SecureBankingAccessToolkit/SecureBankingAccessToolkit/issues/241
//        assertThat(result.data.refund.account.identification).isEqualTo(consent.data.initiation.debtorAccount.identification)
        Assertions.assertThat(result.data.status.toString()).`is`(Status.paymentCondition)
    }

    private fun getInternationalPayment(paymentResponse: OBWriteInternationalResponse5): OBWriteInternationalResponse5 {
        val getDomesticPaymentUrl = PaymentFactory.urlWithInternationalPaymentId(
            paymentLinks.GetInternationalPayment,
            paymentResponse.data.internationalPaymentId
        )
        return paymentApiClient.sendGetRequest(
            getDomesticPaymentUrl,
            tppResource.tpp.getClientCredentialsAccessToken(defaultPaymentScopesForAccessToken)
        )
    }
}