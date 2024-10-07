package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.scheduled.payments.api.v4_0_0

import assertk.assertThat
import assertk.assertions.isNotNull
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.framework.consent.ConsentFactoryRegistryHolder
import com.forgerock.sapi.gateway.ob.uk.framework.consent.payment.OBWriteDomesticScheduledConsent4FactoryV4
import com.forgerock.sapi.gateway.ob.uk.support.discovery.getPaymentsApiLinks
import com.forgerock.sapi.gateway.ob.uk.support.payment.PaymentFactoryV4
import com.forgerock.sapi.gateway.ob.uk.support.payment.defaultPaymentScopesForAccessToken
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import uk.org.openbanking.datamodel.v4.payment.OBWritePaymentDetailsResponse1

class GetDomesticScheduledPaymentDetails(
    val version: OBVersion,
    val tppResource: CreateTppCallback.TppResource
) {
    private val createDomesticScheduledPayments = CreateDomesticScheduledPayment(version, tppResource)
    private val paymentLinks = getPaymentsApiLinks(version)
    private val paymentApiClient = tppResource.tpp.paymentApiClient
    private val consentFactory =
        ConsentFactoryRegistryHolder.consentFactoryRegistry.getConsentFactory(OBWriteDomesticScheduledConsent4FactoryV4::class.java)

    fun getDomesticScheduledPaymentDomesticPaymentIdPaymentDetailsTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val paymentResponse = createDomesticScheduledPayments.submitPayment(consentRequest)

        // When
        val getDomesticPaymentDetailsUrl = PaymentFactoryV4.urlWithDomesticScheduledPaymentId(
            paymentLinks.GetDomesticScheduledPaymentDomesticPaymentIdPaymentDetails,
            paymentResponse.data.domesticScheduledPaymentId
        )
        val paymentDetailsResponse = paymentApiClient.sendGetRequest<OBWritePaymentDetailsResponse1>(
            getDomesticPaymentDetailsUrl,
            tppResource.tpp.getClientCredentialsAccessToken(defaultPaymentScopesForAccessToken)
        )

        // Then
        assertThat(paymentDetailsResponse).isNotNull()
        assertThat(paymentDetailsResponse.data).isNotNull()
        assertThat(paymentDetailsResponse.data.paymentStatus).isNotNull()
    }
}