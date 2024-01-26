package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.scheduled.payments.api.v3_1_8

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.sapi.gateway.framework.conditions.Status
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.framework.consent.ConsentFactoryRegistryHolder
import com.forgerock.sapi.gateway.ob.uk.framework.consent.payment.OBWriteInternationalScheduledConsent5Factory
import com.forgerock.sapi.gateway.ob.uk.support.discovery.getPaymentsApiLinks
import com.forgerock.sapi.gateway.ob.uk.support.payment.PaymentFactory
import com.forgerock.sapi.gateway.ob.uk.support.payment.defaultPaymentScopesForAccessToken
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import org.assertj.core.api.Assertions
import uk.org.openbanking.datamodel.payment.OBExchangeRateType
import uk.org.openbanking.datamodel.common.OBReadRefundAccount
import uk.org.openbanking.datamodel.payment.OBWriteInternationalScheduledResponse6

class GetInternationalScheduledPayment(val version: OBVersion, val tppResource: CreateTppCallback.TppResource) {

    private val createInternationalScheduledPayment =
        CreateInternationalScheduledPayment(version, tppResource)
    private val paymentLinks = getPaymentsApiLinks(version)
    private val paymentApiClient = tppResource.tpp.paymentApiClient
    private val consentFactory = ConsentFactoryRegistryHolder.consentFactoryRegistry.getConsentFactory(
        OBWriteInternationalScheduledConsent5Factory::class.java
    )


    fun getInternationalScheduledPayments_rateType_AGREED_Test() {
        // Given
        val consentRequest = consentFactory.createConsent()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType.AGREED
        val paymentResponse = createInternationalScheduledPayment.submitPayment(consentRequest)

        // When
        val result = getInternationalScheduledPayment(paymentResponse)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.internationalScheduledPaymentId).isNotEmpty()
        assertThat(result.data.creationDateTime).isNotNull()
        assertThat(result.data.charges).isNotNull().isEmpty()
        Assertions.assertThat(result.data.status.toString()).`is`(Status.paymentCondition)
        assertThat(result.data.exchangeRateInformation.exchangeRate).isNotNull()
    }

    fun getInternationalScheduledPayments_rateType_ACTUAL_Test() {
        // Given
        val consentRequest = consentFactory.createConsent()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType.ACTUAL
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null

        val paymentResponse = createInternationalScheduledPayment.submitPayment(consentRequest)

        // When
        val result = getInternationalScheduledPayment(paymentResponse)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.internationalScheduledPaymentId).isNotEmpty()
        assertThat(result.data.creationDateTime).isNotNull()
        Assertions.assertThat(result.data.status.toString()).`is`(Status.paymentCondition)
    }

    fun getInternationalScheduledPayments_rateType_INDICATIVE_Test() {
        // Given
        val consentRequest = consentFactory.createConsent()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType.INDICATIVE
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null

        val paymentResponse = createInternationalScheduledPayment.submitPayment(consentRequest)

        // When
        val result = getInternationalScheduledPayment(paymentResponse)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.internationalScheduledPaymentId).isNotEmpty()
        assertThat(result.data.creationDateTime).isNotNull()
        Assertions.assertThat(result.data.status.toString()).`is`(Status.paymentCondition)
    }

    fun getInternationalScheduledPayments_mandatoryFields_Test() {
        // Given
        val consentRequest = consentFactory.createConsentWithOnlyMandatoryFieldsPopulated()

        val paymentResponse = createInternationalScheduledPayment.submitPayment(consentRequest)

        // When
        val result = getInternationalScheduledPayment(paymentResponse)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.internationalScheduledPaymentId).isNotEmpty()
        assertThat(result.data.creationDateTime).isNotNull()
        Assertions.assertThat(result.data.status.toString()).`is`(Status.paymentCondition)
    }

    fun shouldGetInternationalScheduledPayments_withReadRefund_Test() {
        // Given
        val consentRequest = consentFactory.createConsent()
        consentRequest.data.readRefundAccount = OBReadRefundAccount.YES

        val paymentResponse = createInternationalScheduledPayment.submitPayment(consentRequest)

        // When
        val result = getInternationalScheduledPayment(paymentResponse)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.internationalScheduledPaymentId).isNotEmpty()
        assertThat(result.data.creationDateTime).isNotNull()
        assertThat(result.data.refund).isNotNull()
        assertThat(result.data.refund.account).isNotNull()
        Assertions.assertThat(result.data.status.toString()).`is`(Status.paymentCondition)
    }

    private fun getInternationalScheduledPayment(paymentResponse: OBWriteInternationalScheduledResponse6): OBWriteInternationalScheduledResponse6 {
        val getDomesticPaymentUrl = PaymentFactory.urlWithInternationalScheduledPaymentId(
            paymentLinks.GetInternationalScheduledPayment,
            paymentResponse.data.internationalScheduledPaymentId
        )
        return paymentApiClient.sendGetRequest(
            getDomesticPaymentUrl,
            tppResource.tpp.getClientCredentialsAccessToken(defaultPaymentScopesForAccessToken)
        )
    }
}
