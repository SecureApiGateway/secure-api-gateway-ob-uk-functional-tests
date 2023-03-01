package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.payments.api.v3_1_8

import assertk.assertThat
import assertk.assertions.isNotNull
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.support.discovery.getPaymentsApiLinks
import com.forgerock.sapi.gateway.ob.uk.support.payment.PaymentFactory
import com.forgerock.sapi.gateway.ob.uk.support.payment.defaultPaymentScopesForAccessToken
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import uk.org.openbanking.datamodel.payment.OBWritePaymentDetailsResponse1
import uk.org.openbanking.testsupport.payment.OBWriteDomesticConsentTestDataFactory

class GetDomesticPaymentDetails(
    val version: OBVersion,
    val tppResource: CreateTppCallback.TppResource
) {
    private val createDomesticPaymentApi = CreateDomesticPayment(version, tppResource)
    private val paymentLinks = getPaymentsApiLinks(version)
    private val paymentApiClient = tppResource.tpp.paymentApiClient

    fun getDomesticPaymentDomesticPaymentIdPaymentDetailsTest() {
        // Given
        val consentRequest = OBWriteDomesticConsentTestDataFactory.aValidOBWriteDomesticConsent4()
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