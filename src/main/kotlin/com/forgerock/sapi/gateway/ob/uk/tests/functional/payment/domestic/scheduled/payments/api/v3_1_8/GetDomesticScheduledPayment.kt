package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.scheduled.payments.api.v3_1_8

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.sapi.gateway.framework.conditions.Status
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.framework.consent.ConsentFactoryRegistryHolder
import com.forgerock.sapi.gateway.ob.uk.framework.consent.payment.OBWriteDomesticScheduledConsent4Factory
import com.forgerock.sapi.gateway.ob.uk.support.discovery.getPaymentsApiLinks
import com.forgerock.sapi.gateway.ob.uk.support.payment.PaymentFactory
import com.forgerock.sapi.gateway.ob.uk.support.payment.defaultPaymentScopesForAccessToken
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.scheduled.payments.consents.api.v3_1_8.CreateDomesticScheduledPaymentsConsents
import org.assertj.core.api.Assertions
import uk.org.openbanking.datamodel.common.OBReadRefundAccount
import uk.org.openbanking.datamodel.payment.OBWriteDomesticScheduledResponse5

class GetDomesticScheduledPayment(val version: OBVersion, val tppResource: CreateTppCallback.TppResource) {

    private val createDomesticScheduledPaymentsConsents = CreateDomesticScheduledPaymentsConsents(version, tppResource)
    private val createDomesticScheduledPayment = CreateDomesticScheduledPayment(version, tppResource)
    private val paymentLinks = getPaymentsApiLinks(version)
    private val paymentApiClient = tppResource.tpp.paymentApiClient
    private val consentFactory =
        ConsentFactoryRegistryHolder.consentFactoryRegistry.getConsentFactory(OBWriteDomesticScheduledConsent4Factory::class.java)

    fun getDomesticScheduledPaymentsTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val paymentResponse = createDomesticScheduledPayment.submitPayment(consentRequest)

        // When
        val getPaymentResponse = getDomesticScheduledPayment(paymentResponse)

        // Then
        assertThat(getPaymentResponse).isNotNull()
        assertThat(getPaymentResponse.data.domesticScheduledPaymentId).isNotEmpty()
        assertThat(getPaymentResponse.data.creationDateTime).isNotNull()
        assertThat(getPaymentResponse.data.charges).isNotNull().isEmpty()
        Assertions.assertThat(getPaymentResponse.data.status.toString()).`is`(Status.paymentCondition)
    }

    fun shouldGetDomesticScheduledPayments_withReadRefundTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        consentRequest.data.readRefundAccount = OBReadRefundAccount.YES
        val (consent, accessTokenAuthorizationCode) = createDomesticScheduledPaymentsConsents.createDomesticScheduledPaymentConsentAndAuthorize(
            consentRequest
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        assertThat(consent.data.readRefundAccount).isEqualTo(OBReadRefundAccount.YES)
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        val paymentResponse = createDomesticScheduledPayment.submitPayment(consentRequest)

        val getPaymentResponse = getDomesticScheduledPayment(paymentResponse)
        // Then
        assertThat(getPaymentResponse).isNotNull()
        assertThat(getPaymentResponse.data.domesticScheduledPaymentId).isNotEmpty()
        assertThat(getPaymentResponse.data.creationDateTime).isNotNull()
        assertThat(getPaymentResponse.data.refund).isNotNull()
        assertThat(getPaymentResponse.data.refund.account).isNotNull()
        Assertions.assertThat(getPaymentResponse.data.status.toString()).`is`(Status.paymentCondition)
    }

    private fun getDomesticScheduledPayment(paymentResponse: OBWriteDomesticScheduledResponse5): OBWriteDomesticScheduledResponse5 {
        val getDomesticPaymentUrl = PaymentFactory.urlWithDomesticScheduledPaymentId(
            paymentLinks.GetDomesticScheduledPayment,
            paymentResponse.data.domesticScheduledPaymentId
        )
        return paymentApiClient.sendGetRequest(
            getDomesticPaymentUrl,
            tppResource.tpp.getClientCredentialsAccessToken(defaultPaymentScopesForAccessToken)
        )
    }
}
