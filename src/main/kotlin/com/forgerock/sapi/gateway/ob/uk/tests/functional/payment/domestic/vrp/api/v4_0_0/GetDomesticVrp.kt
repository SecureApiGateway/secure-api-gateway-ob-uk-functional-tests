package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.vrp.api.v4_0_0


import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import com.forgerock.sapi.gateway.framework.conditions.Status
import com.forgerock.sapi.gateway.framework.configuration.PSU_DEBTOR_ACCOUNT_IDENTIFICATION
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.framework.consent.ConsentFactoryRegistryHolder
import com.forgerock.sapi.gateway.ob.uk.framework.consent.payment.OBDomesticVRPConsentRequestFactoryV4
import com.forgerock.sapi.gateway.ob.uk.support.discovery.getPaymentsApiLinks
import com.forgerock.sapi.gateway.ob.uk.support.payment.PaymentFactoryV4
import com.forgerock.sapi.gateway.ob.uk.support.payment.defaultPaymentScopesForAccessToken
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import org.assertj.core.api.Assertions
import uk.org.openbanking.datamodel.v4.vrp.OBDomesticVRPConsentRequestDataReadRefundAccount
import uk.org.openbanking.datamodel.v4.vrp.OBDomesticVRPResponse

class GetDomesticVrp(val version: OBVersion, val tppResource: CreateTppCallback.TppResource) {

    private val createDomesticVrpPaymentApi = CreateDomesticVrp(version, tppResource)
    private val paymentLinks = getPaymentsApiLinks(version)
    private val paymentApiClient = tppResource.tpp.paymentApiClient
    private val consentFactory: OBDomesticVRPConsentRequestFactoryV4 = ConsentFactoryRegistryHolder.consentFactoryRegistry.getConsentFactory(
        OBDomesticVRPConsentRequestFactoryV4::class.java)

    fun getDomesticVrpPaymentsWithRefundAccountTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        consentRequest.data.readRefundAccount(OBDomesticVRPConsentRequestDataReadRefundAccount.YES)
        val paymentResponse = createDomesticVrpPaymentApi.submitPayment(consentRequest)

        // When
        val getPaymentResponse = getDomesticVrpPayment(paymentResponse)

        // Then
        assertThat(getPaymentResponse).isNotNull()
        assertThat(getPaymentResponse.data.domesticVRPId).isNotEmpty()
        assertThat(getPaymentResponse.data.creationDateTime).isNotNull()
        assertThat(getPaymentResponse.data.charges).isNotNull().isNotEmpty()
        Assertions.assertThat(getPaymentResponse.data.status.toString()).`is`(Status.paymentCondition)
        assertThat(getPaymentResponse.data.refund).isNotNull()
        assertThat(getPaymentResponse.data.refund.identification).isEqualTo(PSU_DEBTOR_ACCOUNT_IDENTIFICATION)
    }

    fun getDomesticVrpPaymentsTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        consentRequest.data.readRefundAccount(OBDomesticVRPConsentRequestDataReadRefundAccount.NO)
        val paymentResponse = createDomesticVrpPaymentApi.submitPayment(consentRequest)

        // When
        val getPaymentResponse = getDomesticVrpPayment(paymentResponse)

        // Then
        assertThat(getPaymentResponse).isNotNull()
        assertThat(getPaymentResponse.data.domesticVRPId).isNotEmpty()
        assertThat(getPaymentResponse.data.creationDateTime).isNotNull()
        assertThat(getPaymentResponse.data.charges).isNotNull().isNotEmpty()
        Assertions.assertThat(getPaymentResponse.data.status.toString()).`is`(Status.paymentCondition)
        assertThat(getPaymentResponse.data.refund).isNull()
    }

    private fun getDomesticVrpPayment(paymentResponse: OBDomesticVRPResponse): OBDomesticVRPResponse {
        val getDomesticVrpPaymentUrl = PaymentFactoryV4.urlWithDomesticVrpPaymentId(
            paymentLinks.GetDomesticVRPPayment,
            paymentResponse.data.domesticVRPId
        )
        return paymentApiClient.sendGetRequest(
            getDomesticVrpPaymentUrl,
            tppResource.tpp.getClientCredentialsAccessToken(defaultPaymentScopesForAccessToken)
        )
    }

}
