package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.scheduled.payments.legacy

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.sapi.gateway.framework.conditions.Status
import com.forgerock.sapi.gateway.framework.configuration.psu
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.framework.http.fuel.defaultMapper
import com.forgerock.sapi.gateway.framework.signature.signPayloadSubmitPayment
import com.forgerock.sapi.gateway.ob.uk.support.discovery.payment3_1_1
import com.forgerock.sapi.gateway.ob.uk.support.discovery.payment3_1_3
import com.forgerock.sapi.gateway.ob.uk.support.discovery.payment3_1_4
import com.forgerock.sapi.gateway.ob.uk.support.payment.PaymentAS
import com.forgerock.sapi.gateway.ob.uk.support.payment.PaymentFactory
import com.forgerock.sapi.gateway.ob.uk.support.payment.PaymentFactory.Companion.urlWithDomesticScheduledPaymentId
import com.forgerock.sapi.gateway.ob.uk.support.payment.PaymentRS
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.payment.*
import uk.org.openbanking.testsupport.payment.OBWriteDomesticScheduledConsentTestDataFactory.*

class LegacyGetDomesticScheduledPaymentTest(val tppResource: CreateTppCallback.TppResource) {
    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["GetDomesticScheduledPayment", "CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"]
    )
    @Test
    fun getDomesticScheduledPayments_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent4()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteDomesticScheduledConsentResponse4>(
            payment3_1_4.Links.links.CreateDomesticScheduledPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_4,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().authorizeConsent(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentRS().getClientCredentialsAccessToken(tppResource.tpp)

        val patchedConsent = PaymentRS().getConsent<OBWriteDomesticScheduledConsentResponse4>(
            PaymentFactory.urlWithConsentId(
                payment3_1_4.Links.links.GetDomesticScheduledPaymentConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            OBVersion.v3_1_4
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

        val submissionResponse = PaymentRS().submitPayment<OBWriteDomesticScheduledResponse4>(
            payment3_1_4.Links.links.CreateDomesticScheduledPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1_4
        )

        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.domesticScheduledPaymentId).isNotEmpty()


        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.domesticScheduledPaymentId).isNotEmpty()

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteDomesticScheduledResponse4>(
            urlWithDomesticScheduledPaymentId(
                payment3_1_4.Links.links.GetDomesticScheduledPayment,
                submissionResponse.data.domesticScheduledPaymentId
            ),
            accessTokenClientCredentials,
            tppResource.tpp
        )

        // Then
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.domesticScheduledPaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(Status.paymentCondition)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["GetDomesticScheduledPayment", "CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"]
    )
    @Disabled
    @Test
    fun shouldGetDomesticScheduledPayments_withReadRefund_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent4()
        consentRequest.data.readRefundAccount = OBReadRefundAccountEnum.YES

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteDomesticScheduledConsentResponse4>(
            payment3_1_4.Links.links.CreateDomesticScheduledPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_4,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        assertThat(consent.data.readRefundAccount).isEqualTo(OBReadRefundAccountEnum.YES)
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().authorizeConsent(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentRS().getClientCredentialsAccessToken(tppResource.tpp)

        val patchedConsent = PaymentRS().getConsent<OBWriteDomesticScheduledConsentResponse4>(
            PaymentFactory.urlWithConsentId(
                payment3_1_4.Links.links.GetDomesticScheduledPaymentConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            OBVersion.v3_1_4
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

        val submissionResponse = PaymentRS().submitPayment<OBWriteDomesticScheduledResponse4>(
            payment3_1_4.Links.links.CreateDomesticScheduledPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1_4
        )

        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.domesticScheduledPaymentId).isNotEmpty()


        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.domesticScheduledPaymentId).isNotEmpty()

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteDomesticScheduledResponse4>(
            urlWithDomesticScheduledPaymentId(
                payment3_1_4.Links.links.GetDomesticScheduledPayment,
                submissionResponse.data.domesticScheduledPaymentId
            ),
            accessTokenClientCredentials,
            tppResource.tpp
        )

        // Then
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.domesticScheduledPaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        //TODO: Waiting for the fix from the issue: https://github.com/SecureBankingAccessToolkit/SecureBankingAccessToolkit/issues/241
//        assertThat(paymentResult.data.refund.account.identification).isEqualTo(consent.data.initiation.debtorAccount.identification)
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(Status.paymentCondition)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["GetDomesticScheduledPayment", "CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1.2"]
    )
    @Test
    fun getDomesticScheduledPayments_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent3()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteDomesticScheduledConsentResponse3>(
            payment3_1_3.Links.links.CreateDomesticScheduledPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_3,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().authorizeConsent(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentRS().getClientCredentialsAccessToken(tppResource.tpp)

        val patchedConsent = PaymentRS().getConsent<OBWriteDomesticScheduledConsentResponse3>(
            PaymentFactory.urlWithConsentId(
                payment3_1_3.Links.links.GetDomesticScheduledPaymentConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            OBVersion.v3_1_3
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

        val signedPayload =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(paymentSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        val submissionResponse = PaymentRS().submitPayment<OBWriteDomesticScheduledResponse3>(
            payment3_1_3.Links.links.CreateDomesticScheduledPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1_3
        )

        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.domesticScheduledPaymentId).isNotEmpty()

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteDomesticScheduledResponse3>(
            urlWithDomesticScheduledPaymentId(
                payment3_1_3.Links.links.GetDomesticScheduledPayment,
                submissionResponse.data.domesticScheduledPaymentId
            ),
            accessTokenClientCredentials,
            tppResource.tpp
        )

        // Then
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.domesticScheduledPaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(Status.paymentCondition)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["GetDomesticScheduledPayment", "CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1"]
    )
    @Test
    fun getDomesticScheduledPayments_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent2()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteDomesticScheduledConsentResponse2>(
            payment3_1_1.Links.links.CreateDomesticScheduledPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_1,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().authorizeConsent(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentRS().getClientCredentialsAccessToken(tppResource.tpp)

        val patchedConsent = PaymentRS().getConsent<OBWriteDomesticScheduledConsentResponse2>(
            PaymentFactory.urlWithConsentId(
                payment3_1_1.Links.links.GetDomesticScheduledPaymentConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            OBVersion.v3_1_1
        )

        assertThat(patchedConsent).isNotNull()
        assertThat(patchedConsent.data).isNotNull()
        assertThat(patchedConsent.risk).isNotNull()
        assertThat(patchedConsent.data.consentId).isNotEmpty()
        Assertions.assertThat(patchedConsent.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = OBWriteDomesticScheduled2().data(
            OBWriteDomesticScheduled2Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(
                    PaymentFactory.mapOBDomesticScheduled2ToOBWriteDomesticScheduled2DataInitiation(
                        patchedConsent.data.initiation
                    )
                )
        ).risk(patchedConsent.risk)

        val signedPayload =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(paymentSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        val submissionResponse = PaymentRS().submitPayment<OBWriteDomesticScheduledResponse2>(
            payment3_1_1.Links.links.CreateDomesticScheduledPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1_1
        )

        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.domesticScheduledPaymentId).isNotEmpty()

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteDomesticScheduledResponse2>(
            urlWithDomesticScheduledPaymentId(
                payment3_1_1.Links.links.GetDomesticScheduledPayment,
                submissionResponse.data.domesticScheduledPaymentId
            ),
            accessTokenClientCredentials,
            tppResource.tpp
        )

        // Then
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.domesticScheduledPaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(Status.paymentCondition)
    }
}
