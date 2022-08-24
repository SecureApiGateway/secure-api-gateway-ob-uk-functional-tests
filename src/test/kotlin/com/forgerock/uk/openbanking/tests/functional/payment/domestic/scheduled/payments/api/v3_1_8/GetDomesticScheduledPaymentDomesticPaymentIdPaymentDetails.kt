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
import com.forgerock.uk.openbanking.tests.functional.payment.domestic.scheduled.payments.consents.api.v3_1_8.CreateDomesticScheduledPaymentsConsents
import org.assertj.core.api.Assertions
import uk.org.openbanking.datamodel.payment.*
import uk.org.openbanking.testsupport.payment.OBWriteDomesticScheduledConsentTestDataFactory

class GetDomesticScheduledPaymentDomesticPaymentIdPaymentDetails(
    val version: OBVersion,
    val tppResource: CreateTppCallback.TppResource
) {

    private val createDomesticScheduledPaymentsConsents = CreateDomesticScheduledPaymentsConsents(version, tppResource)

    fun getDomesticScheduledPaymentDomesticPaymentIdPaymentDetailsTest() {
        // Given
        val consentRequest = OBWriteDomesticScheduledConsentTestDataFactory.aValidOBWriteDomesticScheduledConsent4()
        val (consent, accessTokenAuthorizationCode) = createDomesticScheduledPaymentsConsents.createDomesticScheduledPaymentConsentAndAuthorize(
            consentRequest
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentRS().getClientCredentialsAccessToken(tppResource.tpp)

        val patchedConsent = PaymentRS().getConsent<OBWriteDomesticScheduledConsentResponse5>(
            PaymentFactory.urlWithConsentId(
                createDomesticScheduledPaymentsConsents.paymentLinks.GetDomesticScheduledPaymentConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            version
        )

        assertThat(patchedConsent).isNotNull()
        assertThat(patchedConsent.data).isNotNull()
        assertThat(patchedConsent.risk).isNotNull()
        assertThat(patchedConsent.data.consentId).isNotEmpty()
        Assertions.assertThat(patchedConsent.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = OBWriteDomesticScheduled2().data(
            OBWriteDomesticScheduled2Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        val signedPayload = signPayloadSubmitPayment(
            defaultMapper.writeValueAsString(paymentSubmissionRequest),
            tppResource.tpp.signingKey,
            tppResource.tpp.signingKid
        )

        val submissionResponse = PaymentRS().submitPayment<OBWriteDomesticScheduledResponse5>(
            createDomesticScheduledPaymentsConsents.paymentLinks.CreateDomesticScheduledPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            version
        )

        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.domesticScheduledPaymentId).isNotEmpty()

        // When
        val paymentResult = PaymentRS().getPayment<OBWritePaymentDetailsResponse1>(
            PaymentFactory.urlWithDomesticScheduledPaymentId(
                createDomesticScheduledPaymentsConsents.paymentLinks.GetDomesticScheduledPaymentDomesticPaymentIdPaymentDetails,
                submissionResponse.data.domesticScheduledPaymentId
            ),
            accessTokenClientCredentials,
            tppResource.tpp
        )

        // Then
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data).isNotNull()
        assertThat(paymentResult.data.paymentStatus).isNotNull()
    }
}