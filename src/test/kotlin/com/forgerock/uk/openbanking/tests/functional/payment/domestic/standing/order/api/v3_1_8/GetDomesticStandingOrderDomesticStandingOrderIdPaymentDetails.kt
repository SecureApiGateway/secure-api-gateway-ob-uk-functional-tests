package com.forgerock.uk.openbanking.tests.functional.payment.domestic.standing.order.api.v3_1_8

import assertk.assertThat
import assertk.assertions.isNotNull
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.support.discovery.getPaymentsApiLinks
import com.forgerock.uk.openbanking.support.payment.PaymentFactory
import com.forgerock.uk.openbanking.support.payment.defaultPaymentScopesForAccessToken
import uk.org.openbanking.datamodel.payment.OBWriteDomesticStandingOrderResponse6
import uk.org.openbanking.datamodel.payment.OBWritePaymentDetailsResponse1
import uk.org.openbanking.testsupport.payment.OBWriteDomesticStandingOrderConsentTestDataFactory

class GetDomesticStandingOrderDomesticStandingOrderIdPaymentDetails(
    val version: OBVersion,
    val tppResource: CreateTppCallback.TppResource
) {
    private val createDomesticStandingOrderApi = CreateDomesticStandingOrder(version, tppResource)
    private val paymentLinks = getPaymentsApiLinks(version)
    private val paymentApiClient = tppResource.tpp.paymentApiClient

    fun getDomesticStandingOrderDomesticStandingOrderIdPaymentDetailsTest() {
        // Given
        val consentRequest =
            OBWriteDomesticStandingOrderConsentTestDataFactory.aValidOBWriteDomesticStandingOrderConsent5()
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
            OBWriteDomesticStandingOrderConsentTestDataFactory.aValidOBWriteDomesticStandingOrderConsent5MandatoryFields()
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