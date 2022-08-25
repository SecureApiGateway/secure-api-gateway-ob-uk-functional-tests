package com.forgerock.uk.openbanking.tests.functional.payment.domestic.payments.api.v3_1_8

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.securebanking.framework.conditions.Status
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.support.discovery.getPaymentsApiLinks
import com.forgerock.uk.openbanking.support.payment.PaymentFactory
import com.forgerock.uk.openbanking.support.payment.defaultPaymentScopesForAccessToken
import com.forgerock.uk.openbanking.tests.functional.payment.domestic.payments.consents.api.v3_1_8.CreateDomesticPaymentsConsents
import org.assertj.core.api.Assertions
import uk.org.openbanking.datamodel.payment.OBReadRefundAccountEnum
import uk.org.openbanking.datamodel.payment.OBWriteDomesticResponse5
import uk.org.openbanking.testsupport.payment.OBWriteDomesticConsentTestDataFactory

class GetDomesticPayment(
    val version: OBVersion,
    val tppResource: CreateTppCallback.TppResource
) {

    private val createDomesticPaymentsConsentsApi = CreateDomesticPaymentsConsents(version, tppResource)
    private val createDomesticPaymentApi = CreateDomesticPayment(version, tppResource)
    private val paymentLinks = getPaymentsApiLinks(version)
    private val paymentApiClient = tppResource.tpp.paymentApiClient

    fun getDomesticPaymentsTest() {
        // Given
        val consentRequest = OBWriteDomesticConsentTestDataFactory.aValidOBWriteDomesticConsent4()
        val paymentResponse = createDomesticPaymentApi.submitPayment(consentRequest)

        // When
        val getPaymentResponse = getDomesticPayment(paymentResponse)

        // Then
        assertThat(getPaymentResponse).isNotNull()
        assertThat(getPaymentResponse.data.domesticPaymentId).isNotEmpty()
        assertThat(getPaymentResponse.data.creationDateTime).isNotNull()
        Assertions.assertThat(getPaymentResponse.data.status.toString()).`is`(Status.paymentCondition)
    }

    fun shouldGetDomesticPayments_withReadRefundTest() {
        // Given
        val consentRequest = OBWriteDomesticConsentTestDataFactory.aValidOBWriteDomesticConsent4()
        consentRequest.data.readRefundAccount = OBReadRefundAccountEnum.YES

        val (consent, accessTokenAuthorizationCode) = createDomesticPaymentsConsentsApi.createDomesticPaymentsConsentAndAuthorize(
            consentRequest
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        assertThat(consent.data.readRefundAccount).isEqualTo(OBReadRefundAccountEnum.YES)
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        val paymentResponse = createDomesticPaymentApi.submitPayment(consent, accessTokenAuthorizationCode)

        // When
        val getPaymentResponse = getDomesticPayment(paymentResponse)

        // Then
        assertThat(getPaymentResponse).isNotNull()
        assertThat(getPaymentResponse.data.domesticPaymentId).isNotEmpty()
        assertThat(getPaymentResponse.data.creationDateTime).isNotNull()
        //TODO: Waiting for the fix from the issue: https://github.com/SecureBankingAccessToolkit/SecureBankingAccessToolkit/issues/241
        assertThat(getPaymentResponse.data.refund.account.identification).isEqualTo(consent.data.initiation.debtorAccount.identification)
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