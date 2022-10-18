package com.forgerock.uk.openbanking.tests.functional.payment.international.standing.orders.api.v3_1_8

import assertk.assertThat
import assertk.assertions.isNotNull
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.support.discovery.getPaymentsApiLinks
import com.forgerock.uk.openbanking.support.payment.PaymentFactory
import com.forgerock.uk.openbanking.support.payment.defaultPaymentScopesForAccessToken
import uk.org.openbanking.datamodel.payment.OBWriteInternationalStandingOrderResponse7
import uk.org.openbanking.datamodel.payment.OBWritePaymentDetailsResponse1
import uk.org.openbanking.testsupport.payment.OBWriteInternationalStandingOrderConsentTestDataFactory

class GetInternationalStandingOrderInternationalStandingOrderIdPaymentDetails(
    val version: OBVersion,
    val tppResource: CreateTppCallback.TppResource
) {
    private val createInternationalStandingOrderApi = CreateInternationalStandingOrder(version, tppResource)
    private val paymentLinks = getPaymentsApiLinks(version)
    private val paymentApiClient = tppResource.tpp.paymentApiClient

    fun getInternationalStandingOrderInternationalStandingOrderIdPaymentDetailsTest() {
        // Given
        val consentRequest =
            OBWriteInternationalStandingOrderConsentTestDataFactory.aValidOBWriteInternationalStandingOrderConsent6()
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
        val consentRequest =
            OBWriteInternationalStandingOrderConsentTestDataFactory.aValidOBWriteInternationalStandingOrderConsent6MandatoryFields()
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