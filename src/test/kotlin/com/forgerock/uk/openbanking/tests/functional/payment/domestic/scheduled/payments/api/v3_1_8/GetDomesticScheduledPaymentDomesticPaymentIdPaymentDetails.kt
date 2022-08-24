package com.forgerock.uk.openbanking.tests.functional.payment.domestic.scheduled.payments.api.v3_1_8

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.securebanking.framework.conditions.Status
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.http.fuel.defaultMapper
import com.forgerock.securebanking.framework.signature.signPayloadSubmitPayment
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.support.payment.PaymentFactory
import com.forgerock.uk.openbanking.support.payment.PaymentRS
import com.forgerock.uk.openbanking.support.payment.defaultPaymentScopesForAccessToken
import com.forgerock.uk.openbanking.tests.functional.payment.domestic.payments.api.v3_1_8.CreateDomesticPayment
import com.forgerock.uk.openbanking.tests.functional.payment.domestic.scheduled.payments.consents.api.v3_1_8.CreateDomesticScheduledPaymentsConsents
import org.assertj.core.api.Assertions
import uk.org.openbanking.datamodel.payment.*
import uk.org.openbanking.testsupport.payment.OBWriteDomesticScheduledConsentTestDataFactory

class GetDomesticScheduledPaymentDomesticPaymentIdPaymentDetails(
    val version: OBVersion,
    val tppResource: CreateTppCallback.TppResource
) {

    private val createDomesticScheduledPaymentsConsents = CreateDomesticScheduledPaymentsConsents(version, tppResource)
    private val createDomesticScheduledPayments = CreateDomesticScheduledPayment(version, tppResource)
    private val paymentApiClient = tppResource.tpp.paymentApiClient

    fun getDomesticScheduledPaymentDomesticPaymentIdPaymentDetailsTest() {
        // Given
        val consentRequest = OBWriteDomesticScheduledConsentTestDataFactory.aValidOBWriteDomesticScheduledConsent4()
        val paymentResponse = createDomesticScheduledPayments.submitPayment(consentRequest)

        // When
        val getDomesticPaymentDetailsUrl = PaymentFactory.urlWithDomesticScheduledPaymentId(
            createDomesticScheduledPaymentsConsents.paymentLinks.GetDomesticScheduledPaymentDomesticPaymentIdPaymentDetails,
            paymentResponse.data.domesticScheduledPaymentId
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