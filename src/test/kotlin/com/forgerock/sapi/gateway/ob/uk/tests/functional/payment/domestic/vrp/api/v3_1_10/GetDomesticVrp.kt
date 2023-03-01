package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.vrp.api.v3_1_10


import assertk.assertThat
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.sapi.gateway.framework.conditions.Status
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.support.discovery.getPaymentsApiLinks
import com.forgerock.sapi.gateway.ob.uk.support.payment.PaymentFactory
import com.forgerock.sapi.gateway.ob.uk.support.payment.defaultPaymentScopesForAccessToken
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import org.assertj.core.api.Assertions
import uk.org.openbanking.datamodel.vrp.OBDomesticVRPResponse
import uk.org.openbanking.testsupport.vrp.OBDomesticVrpConsentRequestTestDataFactory

class GetDomesticVrp(val version: OBVersion, val tppResource: CreateTppCallback.TppResource) {

    private val createDomesticVrpPaymentApi = CreateDomesticVrp(version, tppResource)
    private val paymentLinks = getPaymentsApiLinks(version)
    private val paymentApiClient = tppResource.tpp.paymentApiClient

    fun getDomesticVrpPaymentsTest() {
        // Given
        val consentRequest = OBDomesticVrpConsentRequestTestDataFactory.aValidOBDomesticVRPConsentRequest()
        val paymentResponse = createDomesticVrpPaymentApi.submitPayment(consentRequest)

        // When
        val getPaymentResponse = getDomesticVrpPayment(paymentResponse)

        // Then
        assertThat(getPaymentResponse).isNotNull()
        assertThat(getPaymentResponse.data.domesticVRPId).isNotEmpty()
        assertThat(getPaymentResponse.data.creationDateTime).isNotNull()
        assertThat(getPaymentResponse.data.charges).isNotNull().isNotEmpty()
        Assertions.assertThat(getPaymentResponse.data.status.toString()).`is`(Status.paymentCondition)
    }

    private fun getDomesticVrpPayment(paymentResponse: OBDomesticVRPResponse): OBDomesticVRPResponse {
        val getDomesticVrpPaymentUrl = PaymentFactory.urlWithDomesticVrpPaymentId(
            paymentLinks.GetDomesticVrpPayment,
            paymentResponse.data.domesticVRPId
        )
        return paymentApiClient.sendGetRequest(
            getDomesticVrpPaymentUrl,
            tppResource.tpp.getClientCredentialsAccessToken(defaultPaymentScopesForAccessToken)
        )
    }

}
