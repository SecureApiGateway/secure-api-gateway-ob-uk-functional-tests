package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.standing.orders.api.v4_0_0

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.sapi.gateway.framework.conditions.Status
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.framework.consent.ConsentFactoryRegistryHolder
import com.forgerock.sapi.gateway.ob.uk.framework.consent.payment.OBWriteInternationalStandingOrderConsent6FactoryV4
import com.forgerock.sapi.gateway.ob.uk.support.discovery.getPaymentsApiLinks
import com.forgerock.sapi.gateway.ob.uk.support.payment.PaymentFactoryV4
import com.forgerock.sapi.gateway.ob.uk.support.payment.defaultPaymentScopesForAccessToken
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.standing.orders.consents.api.v4_0_0.CreateInternationalStandingOrderConsents
import org.assertj.core.api.Assertions
import uk.org.openbanking.datamodel.v4.common.OBReadRefundAccount
import uk.org.openbanking.datamodel.v4.payment.OBWriteInternationalConsent5DataReadRefundAccount
import uk.org.openbanking.datamodel.v4.payment.OBWriteInternationalStandingOrderResponse7

class GetInternationalStandingOrder(val version: OBVersion, val tppResource: CreateTppCallback.TppResource) {

    private val createInternationalStandingOrderConsentsApi = CreateInternationalStandingOrderConsents(version, tppResource)
    private val createInternationalStandingOrderApi = CreateInternationalStandingOrder(version, tppResource)
    private val paymentLinks = getPaymentsApiLinks(version)
    private val paymentApiClient = tppResource.tpp.paymentApiClient
    private val consentFactory = ConsentFactoryRegistryHolder.consentFactoryRegistry.getConsentFactory(
        OBWriteInternationalStandingOrderConsent6FactoryV4::class.java
    )

    fun getInternationalStandingOrdersTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val standingOrderResponse = createInternationalStandingOrderApi.submitStandingOrder(consentRequest)

        // When
        val getStandingOrderResponse = getStandingOrder(standingOrderResponse)

        // Then
        assertThat(getStandingOrderResponse).isNotNull()
        assertThat(getStandingOrderResponse.data.internationalStandingOrderId).isNotEmpty()
        assertThat(getStandingOrderResponse.data.creationDateTime).isNotNull()
        assertThat(getStandingOrderResponse.data.charges).isNotNull().isEmpty()
        Assertions.assertThat(getStandingOrderResponse.data.status.toString()).`is`(Status.paymentCondition)
    }

    fun getInternationalStandingOrders_mandatoryFieldsTest() {
        // Given
        val consentRequest = consentFactory.createConsentWithOnlyMandatoryFieldsPopulated()
        val standingOrderResponse = createInternationalStandingOrderApi.submitStandingOrder(consentRequest)

        // When
        val getStandingOrderResponse = getStandingOrder(standingOrderResponse)

        // Then
        assertThat(getStandingOrderResponse).isNotNull()
        assertThat(getStandingOrderResponse.data.internationalStandingOrderId).isNotEmpty()
        assertThat(getStandingOrderResponse.data.creationDateTime).isNotNull()
        Assertions.assertThat(getStandingOrderResponse.data.status.toString()).`is`(Status.paymentCondition)
    }

    fun shouldGetInternationalStandingOrders_withReadRefundTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        consentRequest.data.readRefundAccount = OBWriteInternationalConsent5DataReadRefundAccount.YES
        val (consent, accessTokenAuthorizationCode) = createInternationalStandingOrderConsentsApi.createInternationalStandingOrderConsentAndAuthorize(
            consentRequest
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        assertThat(consent.data.readRefundAccount).isEqualTo(OBReadRefundAccount.YES)
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        val standingOrderResponse = createInternationalStandingOrderApi.submitStandingOrder(
                consent.data.consentId, consentRequest, accessTokenAuthorizationCode
        )

        // When
        val getStandingOrderResponse = getStandingOrder(standingOrderResponse)

        // Then
        assertThat(getStandingOrderResponse).isNotNull()
        assertThat(getStandingOrderResponse.data.internationalStandingOrderId).isNotEmpty()
        assertThat(getStandingOrderResponse.data.creationDateTime).isNotNull()
        assertThat(getStandingOrderResponse.data.refund).isNotNull()
        assertThat(getStandingOrderResponse.data.refund.account).isNotNull()
        Assertions.assertThat(getStandingOrderResponse.data.status.toString()).`is`(Status.paymentCondition)
    }

    private fun getStandingOrder(standingOrderResponse: OBWriteInternationalStandingOrderResponse7): OBWriteInternationalStandingOrderResponse7 {
        val getInternationalStandingOrderURL= PaymentFactoryV4.urlWithInternationalStandingOrderPaymentId(
            paymentLinks.GetInternationalStandingOrder,
            standingOrderResponse.data.consentId
        )
        return paymentApiClient.sendGetRequest(
            getInternationalStandingOrderURL,
            tppResource.tpp.getClientCredentialsAccessToken(defaultPaymentScopesForAccessToken)
        )
    }
}
