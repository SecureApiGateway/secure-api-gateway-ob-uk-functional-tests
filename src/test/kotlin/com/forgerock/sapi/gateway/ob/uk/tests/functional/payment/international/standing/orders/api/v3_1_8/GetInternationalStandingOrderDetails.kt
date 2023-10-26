package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.standing.orders.api.v3_1_8

import assertk.assertThat
import assertk.assertions.isNotNull
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.framework.consent.ConsentFactoryRegistryHolder
import com.forgerock.sapi.gateway.ob.uk.framework.consent.payment.OBWriteInternationalStandingOrderConsent6Factory
import com.forgerock.sapi.gateway.ob.uk.support.discovery.getPaymentsApiLinks
import com.forgerock.sapi.gateway.ob.uk.support.payment.PaymentFactory
import com.forgerock.sapi.gateway.ob.uk.support.payment.defaultPaymentScopesForAccessToken
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import uk.org.openbanking.datamodel.payment.OBWriteInternationalStandingOrderResponse7
import uk.org.openbanking.datamodel.payment.OBWritePaymentDetailsResponse1

class GetInternationalStandingOrderDetails(
    val version: OBVersion,
    val tppResource: CreateTppCallback.TppResource
) {
    private val createInternationalStandingOrderApi = CreateInternationalStandingOrder(version, tppResource)
    private val paymentLinks = getPaymentsApiLinks(version)
    private val paymentApiClient = tppResource.tpp.paymentApiClient
    private val consentFactory = ConsentFactoryRegistryHolder.consentFactoryRegistry.getConsentFactory(
        OBWriteInternationalStandingOrderConsent6Factory::class.java
    )

    fun getInternationalStandingOrderInternationalStandingOrderIdPaymentDetailsTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val internationalStandingOrderResponse = createInternationalStandingOrderApi.submitStandingOrder(consentRequest)

        // When
        val domesticStandingOrderDetails = getInternationalStandingOrderPaymentDetails(internationalStandingOrderResponse)

        // Then
        assertThat(domesticStandingOrderDetails).isNotNull()
        assertThat(domesticStandingOrderDetails.data).isNotNull()
        assertThat(domesticStandingOrderDetails.data.paymentStatus).isNotNull()
    }

    fun getInternationalStandingOrderInternationalStandingOrderIdPaymentDetails_mandatoryFieldsTest() {
        // Given
        val consentRequest = consentFactory.createConsentWithOnlyMandatoryFieldsPopulated()
        val internationalStandingOrderResponse = createInternationalStandingOrderApi.submitStandingOrder(consentRequest)

        // When
        val domesticStandingOrderDetails = getInternationalStandingOrderPaymentDetails(internationalStandingOrderResponse)

        // Then
        assertThat(domesticStandingOrderDetails).isNotNull()
        assertThat(domesticStandingOrderDetails.data).isNotNull()
        assertThat(domesticStandingOrderDetails.data.paymentStatus).isNotNull()
    }

    private fun getInternationalStandingOrderPaymentDetails(internationalStandingOrderResponse: OBWriteInternationalStandingOrderResponse7): OBWritePaymentDetailsResponse1 {
        val getInternationalStandingOrderDetailsUrl = PaymentFactory.urlWithInternationalStandingOrderPaymentId(
            paymentLinks.GetInternationalStandingOrderInternationalStandingOrderIdPaymentDetails,
            internationalStandingOrderResponse.data.consentId
        )
        return paymentApiClient.sendGetRequest<OBWritePaymentDetailsResponse1>(
            getInternationalStandingOrderDetailsUrl,
            tppResource.tpp.getClientCredentialsAccessToken(defaultPaymentScopesForAccessToken)
        )
    }
}