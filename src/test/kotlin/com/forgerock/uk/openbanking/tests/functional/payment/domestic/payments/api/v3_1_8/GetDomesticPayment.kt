package com.forgerock.uk.openbanking.tests.functional.payment.domestic.payments.api.v3_1_8

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
import com.forgerock.uk.openbanking.tests.functional.payment.domestic.payments.consents.api.v3_1_8.CreateDomesticPaymentsConsents
import org.assertj.core.api.Assertions
import uk.org.openbanking.datamodel.payment.*
import uk.org.openbanking.testsupport.payment.OBWriteDomesticConsentTestDataFactory

class GetDomesticPayment(
    val version: OBVersion,
    val tppResource: CreateTppCallback.TppResource
) {

    private val createDomesticPaymentsConsentsApi = CreateDomesticPaymentsConsents(version, tppResource)

    fun getDomesticPaymentsTest() {
        // Given
        val consentRequest = OBWriteDomesticConsentTestDataFactory.aValidOBWriteDomesticConsent4()
        val (consent, accessTokenAuthorizationCode) = createDomesticPaymentsConsentsApi.createDomesticPaymentsConsentAndGetAccessToken(
            consentRequest
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentRS().getAccessToken(tppResource.tpp)

        val patchedConsent = PaymentRS().getConsent<OBWriteDomesticConsentResponse5>(
            PaymentFactory.urlWithConsentId(
                createDomesticPaymentsConsentsApi.paymentLinks.GetDomesticPaymentConsent,
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

        val paymentSubmissionRequest = OBWriteDomestic2().data(
            OBWriteDataDomestic2()
                .consentId(patchedConsent.data.consentId)
                .initiation(PaymentFactory.mapOBWriteDomestic2DataInitiationToOBDomestic2(patchedConsent.data.initiation))
        ).risk(patchedConsent.risk)

        val signedPayload = signPayloadSubmitPayment(
            defaultMapper.writeValueAsString(paymentSubmissionRequest),
            tppResource.tpp.signingKey,
            tppResource.tpp.signingKid
        )

        val submissionResponse = PaymentRS().submitPayment<OBWriteDomesticResponse5>(
            createDomesticPaymentsConsentsApi.paymentLinks.CreateDomesticPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            version
        )

        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.domesticPaymentId).isNotEmpty()

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteDomesticResponse5>(
            PaymentFactory.urlWithDomesticPaymentId(
                createDomesticPaymentsConsentsApi.paymentLinks.GetDomesticPayment,
                submissionResponse.data.domesticPaymentId
            ),
            accessTokenClientCredentials,
            tppResource.tpp
        )

        // Then
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.domesticPaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(Status.paymentCondition)
    }

    fun shouldGetDomesticPayments_withReadRefundTest() {
        // Given
        val consentRequest = OBWriteDomesticConsentTestDataFactory.aValidOBWriteDomesticConsent4()
        consentRequest.data.readRefundAccount = OBReadRefundAccountEnum.YES

        val (consent, accessTokenAuthorizationCode) = createDomesticPaymentsConsentsApi.createDomesticPaymentsConsentAndGetAccessToken(
            consentRequest
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        assertThat(consent.data.readRefundAccount).isEqualTo(OBReadRefundAccountEnum.YES)
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentRS().getAccessToken(tppResource.tpp)

        val patchedConsent = PaymentRS().getConsent<OBWriteDomesticConsentResponse5>(
            PaymentFactory.urlWithConsentId(
                createDomesticPaymentsConsentsApi.paymentLinks.GetDomesticPaymentConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            version
        )

        assertThat(patchedConsent).isNotNull()
        assertThat(patchedConsent.data).isNotNull()
        assertThat(patchedConsent.risk).isNotNull()
        assertThat(patchedConsent.data.consentId).isNotEmpty()
        assertThat(patchedConsent.data.readRefundAccount).isEqualTo(OBReadRefundAccountEnum.YES)
        Assertions.assertThat(patchedConsent.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = OBWriteDomestic2().data(
            OBWriteDataDomestic2()
                .consentId(patchedConsent.data.consentId)
                .initiation(PaymentFactory.mapOBWriteDomestic2DataInitiationToOBDomestic2(patchedConsent.data.initiation))
        ).risk(patchedConsent.risk)

        val signedPayload = signPayloadSubmitPayment(
            defaultMapper.writeValueAsString(paymentSubmissionRequest),
            tppResource.tpp.signingKey,
            tppResource.tpp.signingKid
        )

        val submissionResponse = PaymentRS().submitPayment<OBWriteDomesticResponse5>(
            createDomesticPaymentsConsentsApi.paymentLinks.CreateDomesticPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            version
        )

        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.domesticPaymentId).isNotEmpty()

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteDomesticResponse5>(
            PaymentFactory.urlWithDomesticPaymentId(
                createDomesticPaymentsConsentsApi.paymentLinks.GetDomesticPayment,
                submissionResponse.data.domesticPaymentId
            ),
            accessTokenClientCredentials,
            tppResource.tpp
        )

        // Then
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.domesticPaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        //TODO: Waiting for the fix from the issue: https://github.com/SecureBankingAccessToolkit/SecureBankingAccessToolkit/issues/241
        assertThat(paymentResult.data.refund.account.identification).isEqualTo(consent.data.initiation.debtorAccount.identification)
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(Status.paymentCondition)
    }
}