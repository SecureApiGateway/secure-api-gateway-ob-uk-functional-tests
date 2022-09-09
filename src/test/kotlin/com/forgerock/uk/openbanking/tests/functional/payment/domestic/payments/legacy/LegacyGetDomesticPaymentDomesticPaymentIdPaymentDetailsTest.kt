package com.forgerock.uk.openbanking.tests.functional.payment.domestic.payments.legacy

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.securebanking.framework.conditions.Status
import com.forgerock.securebanking.framework.configuration.psu
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.framework.http.fuel.defaultMapper
import com.forgerock.securebanking.framework.signature.signPayloadSubmitPayment
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.support.discovery.payment3_1_3
import com.forgerock.uk.openbanking.support.discovery.payment3_1_4
import com.forgerock.uk.openbanking.support.payment.PaymentAS
import com.forgerock.uk.openbanking.support.payment.PaymentFactory
import com.forgerock.uk.openbanking.support.payment.PaymentFactory.Companion.copyOBWriteDomestic2DataInitiation
import com.forgerock.uk.openbanking.support.payment.PaymentFactory.Companion.urlWithDomesticPaymentId
import com.forgerock.uk.openbanking.support.payment.PaymentRS
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.payment.*
import uk.org.openbanking.testsupport.payment.OBWriteDomesticConsentTestDataFactory.aValidOBWriteDomesticConsent3
import uk.org.openbanking.testsupport.payment.OBWriteDomesticConsentTestDataFactory.aValidOBWriteDomesticConsent4

class LegacyGetDomesticPaymentDomesticPaymentIdPaymentDetailsTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["GetDomesticPayment", "CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent", "GetDomesticPaymentDomesticPaymentIdPaymentDetails"],
        apis = ["domestic-payments", "domestic-payment-consents"]
    )
    @Test
    fun getDomesticPaymentDomesticPaymentIdPaymentDetails_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent4()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteDomesticConsentResponse4>(
            payment3_1_4.Links.links.CreateDomesticPaymentConsent,
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

        val patchedConsent = PaymentRS().getConsent<OBWriteDomesticConsentResponse4>(
            PaymentFactory.urlWithConsentId(
                payment3_1_4.Links.links.GetDomesticPaymentConsent,
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

        val paymentSubmissionRequest = OBWriteDomestic2().data(
            OBWriteDomestic2Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(copyOBWriteDomestic2DataInitiation(patchedConsent.data.initiation))
        ).risk(patchedConsent.risk)

        val signedPayload = signPayloadSubmitPayment(
            defaultMapper.writeValueAsString(paymentSubmissionRequest),
            tppResource.tpp.signingKey,
            tppResource.tpp.signingKid
        )

        val submissionResponse = PaymentRS().submitPayment<OBWriteDomesticResponse4>(
            payment3_1_4.Links.links.CreateDomesticPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1_4
        )

        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.domesticPaymentId).isNotEmpty()

        // When
        val paymentResult = PaymentRS().getPayment<OBWritePaymentDetailsResponse1>(
            urlWithDomesticPaymentId(
                payment3_1_4.Links.links.GetDomesticPaymentDomesticPaymentIdPaymentDetails,
                submissionResponse.data.domesticPaymentId
            ),
            accessTokenClientCredentials,
            tppResource.tpp
        )

        // Then
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data).isNotNull()
        assertThat(paymentResult.data.paymentStatus).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["GetDomesticPayment", "CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent", "GetDomesticPaymentDomesticPaymentIdPaymentDetails"],
        apis = ["domestic-payments", "domestic-payment-consents"]
    )
    @Test
    fun getDomesticPaymentDomesticPaymentIdPaymentDetails_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent3()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteDomesticConsentResponse3>(
            payment3_1_3.Links.links.CreateDomesticPaymentConsent,
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

        val patchedConsent = PaymentRS().getConsent<OBWriteDomesticConsentResponse3>(
            PaymentFactory.urlWithConsentId(
                payment3_1_3.Links.links.GetDomesticPaymentConsent,
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

        val paymentSubmissionRequest = OBWriteDomestic2().data(
            OBWriteDomestic2Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(copyOBWriteDomestic2DataInitiation(patchedConsent.data.initiation))
        ).risk(patchedConsent.risk)

        val signedPayload =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(paymentSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        val submissionResponse = PaymentRS().submitPayment<OBWriteDomesticResponse3>(
            payment3_1_3.Links.links.CreateDomesticPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1_3
        )

        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.domesticPaymentId).isNotEmpty()

        // When
        val paymentResult = PaymentRS().getPayment<OBWritePaymentDetailsResponse1>(
            urlWithDomesticPaymentId(
                payment3_1_3.Links.links.GetDomesticPaymentDomesticPaymentIdPaymentDetails,
                submissionResponse.data.domesticPaymentId
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
