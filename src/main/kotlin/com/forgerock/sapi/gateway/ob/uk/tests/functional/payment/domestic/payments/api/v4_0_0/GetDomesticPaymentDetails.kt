package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.payments.api.v4_0_0

import assertk.assertThat
import assertk.assertions.isNotNull
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.framework.consent.ConsentFactoryRegistryHolder
import com.forgerock.sapi.gateway.ob.uk.framework.consent.payment.v4.OBWriteDomesticConsent4Factory
import com.forgerock.sapi.gateway.ob.uk.support.discovery.getPaymentsApiLinks
import com.forgerock.sapi.gateway.ob.uk.support.payment.v4.PaymentFactory
import com.forgerock.sapi.gateway.ob.uk.support.payment.defaultPaymentScopesForAccessToken
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import uk.org.openbanking.datamodel.v4.payment.OBWritePaymentDetailsResponse1

class GetDomesticPaymentDetails(
    val version: OBVersion,
    val tppResource: CreateTppCallback.TppResource
) {
    private val createDomesticPaymentApi = CreateDomesticPayment(version, tppResource)
    private val paymentLinks = getPaymentsApiLinks(version)
    private val paymentApiClient = tppResource.tpp.paymentApiClient
    private val consentFactory: OBWriteDomesticConsent4Factory = ConsentFactoryRegistryHolder.consentFactoryRegistry.getConsentFactory(
        OBWriteDomesticConsent4Factory::class.java)

    fun getDomesticPaymentDomesticPaymentIdPaymentDetailsTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val paymentResponse = createDomesticPaymentApi.submitPayment(consentRequest)

        // When
        val getDomesticPaymentDetailsUrl = PaymentFactory.urlWithDomesticPaymentId(
            paymentLinks.GetDomesticPaymentDomesticPaymentIdPaymentDetails,
            paymentResponse.data.domesticPaymentId
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