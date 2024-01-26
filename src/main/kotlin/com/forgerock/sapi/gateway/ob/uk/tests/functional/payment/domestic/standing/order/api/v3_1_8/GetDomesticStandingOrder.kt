package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.standing.order.api.v3_1_8

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.sapi.gateway.framework.conditions.Status
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.framework.consent.ConsentFactoryRegistryHolder
import com.forgerock.sapi.gateway.ob.uk.framework.consent.payment.OBWriteDomesticStandingOrderConsent5Factory
import com.forgerock.sapi.gateway.ob.uk.support.discovery.getPaymentsApiLinks
import com.forgerock.sapi.gateway.ob.uk.support.payment.PaymentFactory
import com.forgerock.sapi.gateway.ob.uk.support.payment.defaultPaymentScopesForAccessToken
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.standing.order.consents.api.v3_1_8.CreateDomesticStandingOrderConsents
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import org.assertj.core.api.Assertions
import uk.org.openbanking.datamodel.common.OBReadRefundAccount
import uk.org.openbanking.datamodel.payment.OBWriteDomesticStandingOrderResponse6

class GetDomesticStandingOrder(val version: OBVersion, val tppResource: CreateTppCallback.TppResource) {

    private val createDomesticStandingOrderConsentsApi = CreateDomesticStandingOrderConsents(version, tppResource)
    private val createDomesticStandingOrderApi = CreateDomesticStandingOrder(version, tppResource)
    private val paymentLinks = getPaymentsApiLinks(version)
    private val paymentApiClient = tppResource.tpp.paymentApiClient
    private val consentFactory = ConsentFactoryRegistryHolder.consentFactoryRegistry.getConsentFactory(
        OBWriteDomesticStandingOrderConsent5Factory::class.java
    )

    fun getDomesticStandingOrdersTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val standingOrderResponse = createDomesticStandingOrderApi.submitStandingOrder(consentRequest)

        // When
        val getStandingOrderResponse = getStandingOrder(standingOrderResponse)

        // Then
        assertThat(getStandingOrderResponse).isNotNull()
        assertThat(getStandingOrderResponse.data.domesticStandingOrderId).isNotEmpty()
        assertThat(getStandingOrderResponse.data.creationDateTime).isNotNull()
        assertThat(getStandingOrderResponse.data.charges).isNotNull().isEmpty()
        Assertions.assertThat(getStandingOrderResponse.data.status.toString()).`is`(Status.paymentCondition)
    }

    fun getDomesticStandingOrders_mandatoryFieldsTest() {
        // Given
        val consentRequest =
            consentFactory.createConsentWithOnlyMandatoryFieldsPopulated()
        val standingOrderResponse = createDomesticStandingOrderApi.submitStandingOrder(consentRequest)

        // When
        val getStandingOrderResponse = getStandingOrder(standingOrderResponse)

        // Then
        assertThat(getStandingOrderResponse).isNotNull()
        assertThat(getStandingOrderResponse.data.domesticStandingOrderId).isNotEmpty()
        assertThat(getStandingOrderResponse.data.creationDateTime).isNotNull()
        Assertions.assertThat(getStandingOrderResponse.data.status.toString()).`is`(Status.paymentCondition)
    }

    fun shouldGetDomesticStandingOrders_withReadRefundTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        consentRequest.data.readRefundAccount = OBReadRefundAccount.YES
        val (consent, accessTokenAuthorizationCode) = createDomesticStandingOrderConsentsApi.createDomesticStandingOrderConsentAndAuthorize(
            consentRequest
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        assertThat(consent.data.readRefundAccount).isEqualTo(OBReadRefundAccount.YES)
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        val standingOrderResponse = createDomesticStandingOrderApi.submitStandingOrder(
            consent.data.consentId, consentRequest, accessTokenAuthorizationCode
        )

        // When
        val getStandingOrderResponse = getStandingOrder(standingOrderResponse)

        // Then
        assertThat(getStandingOrderResponse).isNotNull()
        assertThat(getStandingOrderResponse.data.domesticStandingOrderId).isNotEmpty()
        assertThat(getStandingOrderResponse.data.creationDateTime).isNotNull()
        assertThat(getStandingOrderResponse.data.refund).isNotNull()
        assertThat(getStandingOrderResponse.data.refund.account).isNotNull()
        Assertions.assertThat(getStandingOrderResponse.data.status.toString()).`is`(Status.paymentCondition)
    }

    private fun getStandingOrder(standingOrderResponse: OBWriteDomesticStandingOrderResponse6): OBWriteDomesticStandingOrderResponse6 {
        val getDomesticPaymentUrl = PaymentFactory.urlWithDomesticStandingOrderId(
            paymentLinks.GetDomesticStandingOrder,
            standingOrderResponse.data.domesticStandingOrderId
        )
        return paymentApiClient.sendGetRequest(
            getDomesticPaymentUrl,
            tppResource.tpp.getClientCredentialsAccessToken(defaultPaymentScopesForAccessToken)
        )
    }
}
