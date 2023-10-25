package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.standing.order.api.v3_1_8

import assertk.assertThat
import assertk.assertions.isNotNull
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.framework.consent.ConsentFactoryRegistryHolder
import com.forgerock.sapi.gateway.ob.uk.framework.consent.payment.OBWriteDomesticStandingOrderConsent5Factory
import com.forgerock.sapi.gateway.ob.uk.support.discovery.getPaymentsApiLinks
import com.forgerock.sapi.gateway.ob.uk.support.payment.PaymentFactory
import com.forgerock.sapi.gateway.ob.uk.support.payment.defaultPaymentScopesForAccessToken
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import uk.org.openbanking.datamodel.payment.OBWriteDomesticStandingOrderResponse6
import uk.org.openbanking.datamodel.payment.OBWritePaymentDetailsResponse1

class GetDomesticStandingOrderDetails(
    val version: OBVersion,
    val tppResource: CreateTppCallback.TppResource
) {
    private val createDomesticStandingOrderApi = CreateDomesticStandingOrder(version, tppResource)
    private val paymentLinks = getPaymentsApiLinks(version)
    private val paymentApiClient = tppResource.tpp.paymentApiClient
    private val consentFactory = ConsentFactoryRegistryHolder.consentFactoryRegistry.getConsentFactory(
        OBWriteDomesticStandingOrderConsent5Factory::class.java
    )

    fun getDomesticStandingOrderDomesticStandingOrderIdPaymentDetailsTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val standingOrderResponse = createDomesticStandingOrderApi.submitStandingOrder(consentRequest)

        // When
        val domesticStandingOrderDetails = getDomesticStandingOrderPaymentDetails(standingOrderResponse)

        // Then
        assertThat(domesticStandingOrderDetails).isNotNull()
        assertThat(domesticStandingOrderDetails.data).isNotNull()
        assertThat(domesticStandingOrderDetails.data.paymentStatus).isNotNull()
    }

    fun getDomesticStandingOrderDomesticStandingOrderIdPaymentDetails_mandatoryFieldsTest() {
        // Given
        val consentRequest =
            consentFactory.createConsentWithOnlyMandatoryFieldsPopulated()
        val standingOrderResponse = createDomesticStandingOrderApi.submitStandingOrder(consentRequest)

        // When
        val domesticStandingOrderDetails = getDomesticStandingOrderPaymentDetails(standingOrderResponse)

        // Then
        assertThat(domesticStandingOrderDetails).isNotNull()
        assertThat(domesticStandingOrderDetails.data).isNotNull()
        assertThat(domesticStandingOrderDetails.data.paymentStatus).isNotNull()
    }

    private fun getDomesticStandingOrderPaymentDetails(standingOrderResponse: OBWriteDomesticStandingOrderResponse6): OBWritePaymentDetailsResponse1 {
        val getDomesticStandingOrderDetailsUrl = PaymentFactory.urlWithDomesticStandingOrderId(
            paymentLinks.GetDomesticStandingOrderDomesticStandingOrderIdPaymentDetails,
            standingOrderResponse.data.domesticStandingOrderId
        )
        return paymentApiClient.sendGetRequest<OBWritePaymentDetailsResponse1>(
            getDomesticStandingOrderDetailsUrl,
            tppResource.tpp.getClientCredentialsAccessToken(defaultPaymentScopesForAccessToken)
        )
    }
}