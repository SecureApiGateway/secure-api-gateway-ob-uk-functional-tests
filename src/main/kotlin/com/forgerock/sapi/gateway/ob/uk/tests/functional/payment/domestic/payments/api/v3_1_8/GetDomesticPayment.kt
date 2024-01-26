package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.payments.api.v3_1_8

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.sapi.gateway.framework.conditions.Status
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.framework.consent.ConsentFactoryRegistryHolder
import com.forgerock.sapi.gateway.ob.uk.framework.consent.payment.OBWriteDomesticConsent4Factory
import com.forgerock.sapi.gateway.ob.uk.support.discovery.getPaymentsApiLinks
import com.forgerock.sapi.gateway.ob.uk.support.payment.PaymentFactory
import com.forgerock.sapi.gateway.ob.uk.support.payment.defaultPaymentScopesForAccessToken
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.payments.consents.api.v3_1_8.CreateDomesticPaymentsConsents
import org.assertj.core.api.Assertions
import uk.org.openbanking.datamodel.common.OBReadRefundAccount
import uk.org.openbanking.datamodel.payment.OBWriteDomesticResponse5

class GetDomesticPayment(
    val version: OBVersion,
    val tppResource: CreateTppCallback.TppResource
) {

    private val createDomesticPaymentsConsentsApi = CreateDomesticPaymentsConsents(version, tppResource)
    private val createDomesticPaymentApi = CreateDomesticPayment(version, tppResource)
    private val paymentLinks = getPaymentsApiLinks(version)
    private val paymentApiClient = tppResource.tpp.paymentApiClient
    private val consentFactory: OBWriteDomesticConsent4Factory = ConsentFactoryRegistryHolder.consentFactoryRegistry.getConsentFactory(
        OBWriteDomesticConsent4Factory::class.java)

    fun getDomesticPaymentsTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val paymentResponse = createDomesticPaymentApi.submitPayment(consentRequest)

        // When
        val getPaymentResponse = getDomesticPayment(paymentResponse)

        // Then
        assertThat(getPaymentResponse).isNotNull()
        assertThat(getPaymentResponse.data.domesticPaymentId).isNotEmpty()
        assertThat(getPaymentResponse.data.creationDateTime).isNotNull()
        assertThat(getPaymentResponse.data.charges).isEmpty()
        Assertions.assertThat(getPaymentResponse.data.status.toString()).`is`(Status.paymentCondition)
    }

    fun shouldGetDomesticPayments_withReadRefundTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        consentRequest.data.readRefundAccount = OBReadRefundAccount.YES

        val (consent, accessTokenAuthorizationCode) = createDomesticPaymentsConsentsApi.createDomesticPaymentsConsentAndAuthorize(
            consentRequest
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        assertThat(consent.data.readRefundAccount).isEqualTo(OBReadRefundAccount.YES)
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        val paymentResponse = createDomesticPaymentApi.submitPayment(consent.data.consentId, consentRequest, accessTokenAuthorizationCode)

        // When
        val getPaymentResponse = getDomesticPayment(paymentResponse)

        // Then
        assertThat(getPaymentResponse).isNotNull()
        assertThat(getPaymentResponse.data.domesticPaymentId).isNotEmpty()
        assertThat(getPaymentResponse.data.creationDateTime).isNotNull()
        assertThat(getPaymentResponse.data.refund).isNotNull()
        assertThat(getPaymentResponse.data.refund.account).isNotNull()
        Assertions.assertThat(getPaymentResponse.data.status.toString()).`is`(Status.paymentCondition)
    }

    private fun getDomesticPayment(paymentResponse: OBWriteDomesticResponse5): OBWriteDomesticResponse5 {
        val getDomesticPaymentUrl = PaymentFactory.urlWithDomesticPaymentId(
            paymentLinks.GetDomesticPayment,
            paymentResponse.data.domesticPaymentId
        )
        return paymentApiClient.sendGetRequest(
            getDomesticPaymentUrl,
            tppResource.tpp.getClientCredentialsAccessToken(defaultPaymentScopesForAccessToken)
        )
    }
}
