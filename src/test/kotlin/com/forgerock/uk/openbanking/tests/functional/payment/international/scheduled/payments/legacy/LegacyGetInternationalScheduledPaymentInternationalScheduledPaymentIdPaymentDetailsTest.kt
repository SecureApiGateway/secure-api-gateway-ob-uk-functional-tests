package com.forgerock.uk.openbanking.tests.functional.payment.international.scheduled.payments.legacy

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
import com.forgerock.uk.openbanking.support.discovery.*
import com.forgerock.uk.openbanking.support.payment.PaymentAS
import com.forgerock.uk.openbanking.support.payment.PaymentFactory
import com.forgerock.uk.openbanking.support.payment.PaymentFactory.Companion.mapOBWriteInternationalScheduled2DataInitiationToOBWriteDataInternationalScheduled2
import com.forgerock.uk.openbanking.support.payment.PaymentFactory.Companion.urlWithInternationalScheduledPaymentId
import com.forgerock.uk.openbanking.support.payment.PaymentRS
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.payment.*
import uk.org.openbanking.testsupport.payment.OBWriteInternationalScheduledConsentTestDataFactory.*

class LegacyGetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetailsTest(val tppResource: CreateTppCallback.TppResource) {
    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["GetInternationalScheduledPayment", "CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails_rateType_AGREED_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent5()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.AGREED

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse5>(
            payment3_1_4.Links.links.CreateInternationalScheduledPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_4,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        assertThat(consent.data.initiation.exchangeRateInformation.exchangeRate).isNotNull()
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

        val patchedConsent = PaymentRS().getConsent<OBWriteInternationalScheduledConsentResponse5>(
            PaymentFactory.urlWithConsentId(
                payment3_1_4.Links.links.GetInternationalScheduledPaymentConsent,
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

        val standingOrderSubmissionRequest = OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        val signedPayload = signPayloadSubmitPayment(
            defaultMapper.writeValueAsString(standingOrderSubmissionRequest),
            tppResource.tpp.signingKey,
            tppResource.tpp.signingKid
        )

        val submissionResponse = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse5>(
            payment3_1_4.Links.links.CreateInternationalScheduledPayment,
            standingOrderSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1_4
        )

        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.internationalScheduledPaymentId).isNotEmpty()

        // When
        val result = PaymentRS().getPayment<OBWritePaymentDetailsResponse1>(
            urlWithInternationalScheduledPaymentId(
                payment3_1_4.Links.links.GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails,
                submissionResponse.data.internationalScheduledPaymentId
            ),
            accessTokenClientCredentials,
            tppResource.tpp
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.paymentStatus).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["GetInternationalScheduledPayment", "CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails_rateType_ACTUAL_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent5()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.ACTUAL
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse5>(
            payment3_1_4.Links.links.CreateInternationalScheduledPaymentConsent,
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

        val patchedConsent = PaymentRS().getConsent<OBWriteInternationalScheduledConsentResponse5>(
            PaymentFactory.urlWithConsentId(
                payment3_1_4.Links.links.GetInternationalScheduledPaymentConsent,
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

        val standingOrderSubmissionRequest = OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        val signedPayload = signPayloadSubmitPayment(
            defaultMapper.writeValueAsString(standingOrderSubmissionRequest),
            tppResource.tpp.signingKey,
            tppResource.tpp.signingKid
        )

        val submissionResponse = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse5>(
            payment3_1_4.Links.links.CreateInternationalScheduledPayment,
            standingOrderSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1_4
        )

        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.internationalScheduledPaymentId).isNotEmpty()

        // When
        val result = PaymentRS().getPayment<OBWritePaymentDetailsResponse1>(
            urlWithInternationalScheduledPaymentId(
                payment3_1_4.Links.links.GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails,
                submissionResponse.data.internationalScheduledPaymentId
            ),
            accessTokenClientCredentials,
            tppResource.tpp
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.paymentStatus).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["GetInternationalScheduledPayment", "CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails_rateType_INDICATIVE_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent5()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.INDICATIVE
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse5>(
            payment3_1_4.Links.links.CreateInternationalScheduledPaymentConsent,
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

        val patchedConsent = PaymentRS().getConsent<OBWriteInternationalScheduledConsentResponse5>(
            PaymentFactory.urlWithConsentId(
                payment3_1_4.Links.links.GetInternationalScheduledPaymentConsent,
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

        val standingOrderSubmissionRequest = OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        val signedPayload = signPayloadSubmitPayment(
            defaultMapper.writeValueAsString(standingOrderSubmissionRequest),
            tppResource.tpp.signingKey,
            tppResource.tpp.signingKid
        )

        val submissionResponse = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse5>(
            payment3_1_4.Links.links.CreateInternationalScheduledPayment,
            standingOrderSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1_4
        )

        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.internationalScheduledPaymentId).isNotEmpty()

        // When
        val result = PaymentRS().getPayment<OBWritePaymentDetailsResponse1>(
            urlWithInternationalScheduledPaymentId(
                payment3_1_4.Links.links.GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails,
                submissionResponse.data.internationalScheduledPaymentId
            ),
            accessTokenClientCredentials,
            tppResource.tpp
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.paymentStatus).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["GetInternationalScheduledPayment", "CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails_v3_1_4_mandatoryFields() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent5MandatoryFields()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse5>(
            payment3_1_4.Links.links.CreateInternationalScheduledPaymentConsent,
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

        val patchedConsent = PaymentRS().getConsent<OBWriteInternationalScheduledConsentResponse5>(
            PaymentFactory.urlWithConsentId(
                payment3_1_4.Links.links.GetInternationalScheduledPaymentConsent,
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

        val standingOrderSubmissionRequest = OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        val signedPayload = signPayloadSubmitPayment(
            defaultMapper.writeValueAsString(standingOrderSubmissionRequest),
            tppResource.tpp.signingKey,
            tppResource.tpp.signingKid
        )

        val submissionResponse = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse5>(
            payment3_1_4.Links.links.CreateInternationalScheduledPayment,
            standingOrderSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1_4
        )

        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.internationalScheduledPaymentId).isNotEmpty()

        // When
        val result = PaymentRS().getPayment<OBWritePaymentDetailsResponse1>(
            urlWithInternationalScheduledPaymentId(
                payment3_1_4.Links.links.GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails,
                submissionResponse.data.internationalScheduledPaymentId
            ),
            accessTokenClientCredentials,
            tppResource.tpp
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.paymentStatus).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["GetInternationalScheduledPayment", "CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails_rateType_AGREED_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent4()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.AGREED

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse4>(
            payment3_1_3.Links.links.CreateInternationalScheduledPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_3,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        assertThat(consent.data.initiation.exchangeRateInformation.exchangeRate).isNotNull()
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

        val patchedConsent = PaymentRS().getConsent<OBWriteInternationalScheduledConsentResponse4>(
            PaymentFactory.urlWithConsentId(
                payment3_1_3.Links.links.GetInternationalScheduledPaymentConsent,
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

        val standingOrderSubmissionRequest = OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        val signedPayload =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(standingOrderSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        val submissionResponse = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse4>(
            payment3_1_3.Links.links.CreateInternationalScheduledPayment,
            standingOrderSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1_3
        )

        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.internationalScheduledPaymentId).isNotEmpty()

        // When
        val result = PaymentRS().getPayment<OBWritePaymentDetailsResponse1>(
            urlWithInternationalScheduledPaymentId(
                payment3_1_3.Links.links.GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails,
                submissionResponse.data.internationalScheduledPaymentId
            ),
            accessTokenClientCredentials,
            tppResource.tpp
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.paymentStatus).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["GetInternationalScheduledPayment", "CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails_rateType_ACTUAL_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent4()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.ACTUAL
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse4>(
            payment3_1_3.Links.links.CreateInternationalScheduledPaymentConsent,
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

        val patchedConsent = PaymentRS().getConsent<OBWriteInternationalScheduledConsentResponse4>(
            PaymentFactory.urlWithConsentId(
                payment3_1_3.Links.links.GetInternationalScheduledPaymentConsent,
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

        val standingOrderSubmissionRequest = OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        val signedPayload =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(standingOrderSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        val submissionResponse = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse4>(
            payment3_1_3.Links.links.CreateInternationalScheduledPayment,
            standingOrderSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1_3
        )

        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.internationalScheduledPaymentId).isNotEmpty()

        // When
        val result = PaymentRS().getPayment<OBWritePaymentDetailsResponse1>(
            urlWithInternationalScheduledPaymentId(
                payment3_1_3.Links.links.GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails,
                submissionResponse.data.internationalScheduledPaymentId
            ),
            accessTokenClientCredentials,
            tppResource.tpp
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.paymentStatus).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["GetInternationalScheduledPayment", "CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails_rateType_INDICATIVE_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent4()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.INDICATIVE
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse4>(
            payment3_1_3.Links.links.CreateInternationalScheduledPaymentConsent,
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

        val patchedConsent = PaymentRS().getConsent<OBWriteInternationalScheduledConsentResponse4>(
            PaymentFactory.urlWithConsentId(
                payment3_1_3.Links.links.GetInternationalScheduledPaymentConsent,
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

        val standingOrderSubmissionRequest = OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        val signedPayload =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(standingOrderSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        val submissionResponse = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse4>(
            payment3_1_3.Links.links.CreateInternationalScheduledPayment,
            standingOrderSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1_3
        )

        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.internationalScheduledPaymentId).isNotEmpty()

        // When
        val result = PaymentRS().getPayment<OBWritePaymentDetailsResponse1>(
            urlWithInternationalScheduledPaymentId(
                payment3_1_3.Links.links.GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails,
                submissionResponse.data.internationalScheduledPaymentId
            ),
            accessTokenClientCredentials,
            tppResource.tpp
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.paymentStatus).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["GetInternationalScheduledPayment", "CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails_v3_1_3_mandatoryFields() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent4MandatoryFields()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse4>(
            payment3_1_3.Links.links.CreateInternationalScheduledPaymentConsent,
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

        val patchedConsent = PaymentRS().getConsent<OBWriteInternationalScheduledConsentResponse4>(
            PaymentFactory.urlWithConsentId(
                payment3_1_3.Links.links.GetInternationalScheduledPaymentConsent,
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

        val standingOrderSubmissionRequest = OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        val signedPayload =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(standingOrderSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        val submissionResponse = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse4>(
            payment3_1_3.Links.links.CreateInternationalScheduledPayment,
            standingOrderSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1_3
        )

        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.internationalScheduledPaymentId).isNotEmpty()

        // When
        val result = PaymentRS().getPayment<OBWritePaymentDetailsResponse1>(
            urlWithInternationalScheduledPaymentId(
                payment3_1_3.Links.links.GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails,
                submissionResponse.data.internationalScheduledPaymentId
            ),
            accessTokenClientCredentials,
            tppResource.tpp
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.paymentStatus).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.2",
        operations = ["GetInternationalScheduledPayment", "CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails_rateType_AGREED_v3_1_2() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.AGREED

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse3>(
            payment3_1_2.Links.links.CreateInternationalScheduledPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_2,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        assertThat(consent.data.initiation.exchangeRateInformation.exchangeRate).isNotNull()
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

        val patchedConsent = PaymentRS().getConsent<OBWriteInternationalScheduledConsentResponse3>(
            PaymentFactory.urlWithConsentId(
                payment3_1_2.Links.links.GetInternationalScheduledPaymentConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            OBVersion.v3_1_2
        )

        assertThat(patchedConsent).isNotNull()
        assertThat(patchedConsent.data).isNotNull()
        assertThat(patchedConsent.risk).isNotNull()
        assertThat(patchedConsent.data.consentId).isNotEmpty()
        Assertions.assertThat(patchedConsent.data.status.toString()).`is`(Status.consentCondition)

        val standingOrderSubmissionRequest = OBWriteInternationalScheduled2().data(
            OBWriteDataInternationalScheduled2()
                .consentId(patchedConsent.data.consentId)
                .initiation(
                    mapOBWriteInternationalScheduled2DataInitiationToOBWriteDataInternationalScheduled2(
                        patchedConsent.data.initiation
                    )
                )
        ).risk(patchedConsent.risk)

        val signedPayload =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(standingOrderSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        val submissionResponse = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse4>(
            payment3_1_2.Links.links.CreateInternationalScheduledPayment,
            standingOrderSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1_2
        )

        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.internationalScheduledPaymentId).isNotEmpty()

        // When
        val result = PaymentRS().getPayment<OBWritePaymentDetailsResponse1>(
            urlWithInternationalScheduledPaymentId(
                payment3_1_2.Links.links.GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails,
                submissionResponse.data.internationalScheduledPaymentId
            ),
            accessTokenClientCredentials,
            tppResource.tpp
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.paymentStatus).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.2",
        operations = ["GetInternationalScheduledPayment", "CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails_rateType_ACTUAL_v3_1_2() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.ACTUAL
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse3>(
            payment3_1_2.Links.links.CreateInternationalScheduledPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_2,
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

        val patchedConsent = PaymentRS().getConsent<OBWriteInternationalScheduledConsentResponse3>(
            PaymentFactory.urlWithConsentId(
                payment3_1_2.Links.links.GetInternationalScheduledPaymentConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            OBVersion.v3_1_2
        )

        assertThat(patchedConsent).isNotNull()
        assertThat(patchedConsent.data).isNotNull()
        assertThat(patchedConsent.risk).isNotNull()
        assertThat(patchedConsent.data.consentId).isNotEmpty()
        Assertions.assertThat(patchedConsent.data.status.toString()).`is`(Status.consentCondition)

        val standingOrderSubmissionRequest = OBWriteInternationalScheduled2().data(
            OBWriteDataInternationalScheduled2()
                .consentId(patchedConsent.data.consentId)
                .initiation(
                    mapOBWriteInternationalScheduled2DataInitiationToOBWriteDataInternationalScheduled2(
                        patchedConsent.data.initiation
                    )
                )
        ).risk(patchedConsent.risk)

        val signedPayload =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(standingOrderSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        val submissionResponse = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse4>(
            payment3_1_2.Links.links.CreateInternationalScheduledPayment,
            standingOrderSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1_2
        )

        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.internationalScheduledPaymentId).isNotEmpty()

        // When
        val result = PaymentRS().getPayment<OBWritePaymentDetailsResponse1>(
            urlWithInternationalScheduledPaymentId(
                payment3_1_2.Links.links.GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails,
                submissionResponse.data.internationalScheduledPaymentId
            ),
            accessTokenClientCredentials,
            tppResource.tpp
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.paymentStatus).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.2",
        operations = ["GetInternationalScheduledPayment", "CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails_rateType_INDICATIVE_v3_1_2() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.INDICATIVE
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse3>(
            payment3_1_2.Links.links.CreateInternationalScheduledPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_2,
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

        val patchedConsent = PaymentRS().getConsent<OBWriteInternationalScheduledConsentResponse3>(
            PaymentFactory.urlWithConsentId(
                payment3_1_2.Links.links.GetInternationalScheduledPaymentConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            OBVersion.v3_1_2
        )

        assertThat(patchedConsent).isNotNull()
        assertThat(patchedConsent.data).isNotNull()
        assertThat(patchedConsent.risk).isNotNull()
        assertThat(patchedConsent.data.consentId).isNotEmpty()
        Assertions.assertThat(patchedConsent.data.status.toString()).`is`(Status.consentCondition)

        val standingOrderSubmissionRequest = OBWriteInternationalScheduled2().data(
            OBWriteDataInternationalScheduled2()
                .consentId(patchedConsent.data.consentId)
                .initiation(
                    mapOBWriteInternationalScheduled2DataInitiationToOBWriteDataInternationalScheduled2(
                        patchedConsent.data.initiation
                    )
                )
        ).risk(patchedConsent.risk)

        val signedPayload =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(standingOrderSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        val submissionResponse = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse4>(
            payment3_1_2.Links.links.CreateInternationalScheduledPayment,
            standingOrderSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1_2
        )

        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.internationalScheduledPaymentId).isNotEmpty()

        // When
        val result = PaymentRS().getPayment<OBWritePaymentDetailsResponse1>(
            urlWithInternationalScheduledPaymentId(
                payment3_1_2.Links.links.GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails,
                submissionResponse.data.internationalScheduledPaymentId
            ),
            accessTokenClientCredentials,
            tppResource.tpp
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.paymentStatus).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.2",
        operations = ["GetInternationalScheduledPayment", "CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails_v3_1_2_mandatoryFields() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3MandatoryFields()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse3>(
            payment3_1_2.Links.links.CreateInternationalScheduledPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_2,
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

        val patchedConsent = PaymentRS().getConsent<OBWriteInternationalScheduledConsentResponse3>(
            PaymentFactory.urlWithConsentId(
                payment3_1_2.Links.links.GetInternationalScheduledPaymentConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            OBVersion.v3_1_2
        )

        assertThat(patchedConsent).isNotNull()
        assertThat(patchedConsent.data).isNotNull()
        assertThat(patchedConsent.risk).isNotNull()
        assertThat(patchedConsent.data.consentId).isNotEmpty()
        Assertions.assertThat(patchedConsent.data.status.toString()).`is`(Status.consentCondition)

        val standingOrderSubmissionRequest = OBWriteInternationalScheduled2().data(
            OBWriteDataInternationalScheduled2()
                .consentId(patchedConsent.data.consentId)
                .initiation(
                    mapOBWriteInternationalScheduled2DataInitiationToOBWriteDataInternationalScheduled2(
                        patchedConsent.data.initiation
                    )
                )
        ).risk(patchedConsent.risk)

        val signedPayload =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(standingOrderSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        val submissionResponse = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse4>(
            payment3_1_2.Links.links.CreateInternationalScheduledPayment,
            standingOrderSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1_2
        )

        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.internationalScheduledPaymentId).isNotEmpty()

        // When
        val result = PaymentRS().getPayment<OBWritePaymentDetailsResponse1>(
            urlWithInternationalScheduledPaymentId(
                payment3_1_2.Links.links.GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails,
                submissionResponse.data.internationalScheduledPaymentId
            ),
            accessTokenClientCredentials,
            tppResource.tpp
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.paymentStatus).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["GetInternationalScheduledPayment", "CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails_rateType_AGREED_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.AGREED

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse2>(
            payment3_1_1.Links.links.CreateInternationalScheduledPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_1,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        assertThat(consent.data.initiation.exchangeRateInformation.exchangeRate).isNotNull()
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

        val patchedConsent = PaymentRS().getConsent<OBWriteInternationalScheduledConsentResponse2>(
            PaymentFactory.urlWithConsentId(
                payment3_1_1.Links.links.GetInternationalScheduledPaymentConsent,
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

        val standingOrderSubmissionRequest = OBWriteInternationalScheduled2().data(
            OBWriteDataInternationalScheduled2()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        val signedPayload =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(standingOrderSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        val submissionResponse = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse4>(
            payment3_1_1.Links.links.CreateInternationalScheduledPayment,
            standingOrderSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1_1
        )

        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.internationalScheduledPaymentId).isNotEmpty()

        // When
        val result = PaymentRS().getPayment<OBWritePaymentDetailsResponse1>(
            urlWithInternationalScheduledPaymentId(
                payment3_1_1.Links.links.GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails,
                submissionResponse.data.internationalScheduledPaymentId
            ),
            accessTokenClientCredentials,
            tppResource.tpp
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.paymentStatus).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["GetInternationalScheduledPayment", "CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails_rateType_ACTUAL_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.ACTUAL
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse2>(
            payment3_1_1.Links.links.CreateInternationalScheduledPaymentConsent,
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

        val patchedConsent = PaymentRS().getConsent<OBWriteInternationalScheduledConsentResponse2>(
            PaymentFactory.urlWithConsentId(
                payment3_1_1.Links.links.GetInternationalScheduledPaymentConsent,
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

        val standingOrderSubmissionRequest = OBWriteInternationalScheduled2().data(
            OBWriteDataInternationalScheduled2()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        val signedPayload =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(standingOrderSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        val submissionResponse = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse4>(
            payment3_1_1.Links.links.CreateInternationalScheduledPayment,
            standingOrderSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1_1
        )

        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.internationalScheduledPaymentId).isNotEmpty()

        // When
        val result = PaymentRS().getPayment<OBWritePaymentDetailsResponse1>(
            urlWithInternationalScheduledPaymentId(
                payment3_1_1.Links.links.GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails,
                submissionResponse.data.internationalScheduledPaymentId
            ),
            accessTokenClientCredentials,
            tppResource.tpp
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.paymentStatus).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["GetInternationalScheduledPayment", "CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails_rateType_INDICATIVE_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.INDICATIVE
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse2>(
            payment3_1_1.Links.links.CreateInternationalScheduledPaymentConsent,
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

        val patchedConsent = PaymentRS().getConsent<OBWriteInternationalScheduledConsentResponse2>(
            PaymentFactory.urlWithConsentId(
                payment3_1_1.Links.links.GetInternationalScheduledPaymentConsent,
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

        val standingOrderSubmissionRequest = OBWriteInternationalScheduled2().data(
            OBWriteDataInternationalScheduled2()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        val signedPayload =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(standingOrderSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        val submissionResponse = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse4>(
            payment3_1_1.Links.links.CreateInternationalScheduledPayment,
            standingOrderSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1_1
        )

        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.internationalScheduledPaymentId).isNotEmpty()

        // When
        val result = PaymentRS().getPayment<OBWritePaymentDetailsResponse1>(
            urlWithInternationalScheduledPaymentId(
                payment3_1_1.Links.links.GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails,
                submissionResponse.data.internationalScheduledPaymentId
            ),
            accessTokenClientCredentials,
            tppResource.tpp
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.paymentStatus).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["GetInternationalScheduledPayment", "CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails_v3_1_1_mandatoryFields() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3MandatoryFields()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse2>(
            payment3_1_1.Links.links.CreateInternationalScheduledPaymentConsent,
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

        val patchedConsent = PaymentRS().getConsent<OBWriteInternationalScheduledConsentResponse2>(
            PaymentFactory.urlWithConsentId(
                payment3_1_1.Links.links.GetInternationalScheduledPaymentConsent,
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

        val standingOrderSubmissionRequest = OBWriteInternationalScheduled2().data(
            OBWriteDataInternationalScheduled2()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        val signedPayload =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(standingOrderSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        val submissionResponse = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse4>(
            payment3_1_1.Links.links.CreateInternationalScheduledPayment,
            standingOrderSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1_1
        )

        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.internationalScheduledPaymentId).isNotEmpty()

        // When
        val result = PaymentRS().getPayment<OBWritePaymentDetailsResponse1>(
            urlWithInternationalScheduledPaymentId(
                payment3_1_1.Links.links.GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails,
                submissionResponse.data.internationalScheduledPaymentId
            ),
            accessTokenClientCredentials,
            tppResource.tpp
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.paymentStatus).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1",
        operations = ["GetInternationalScheduledPayment", "CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails_rateType_AGREED_v3_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent2()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.AGREED

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse2>(
            payment3_1.Links.links.CreateInternationalScheduledPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        assertThat(consent.data.initiation.exchangeRateInformation.exchangeRate).isNotNull()
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

        val patchedConsent = PaymentRS().getConsent<OBWriteInternationalScheduledConsentResponse2>(
            PaymentFactory.urlWithConsentId(
                payment3_1.Links.links.GetInternationalScheduledPaymentConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            OBVersion.v3_1
        )

        assertThat(patchedConsent).isNotNull()
        assertThat(patchedConsent.data).isNotNull()
        assertThat(patchedConsent.risk).isNotNull()
        assertThat(patchedConsent.data.consentId).isNotEmpty()
        Assertions.assertThat(patchedConsent.data.status.toString()).`is`(Status.consentCondition)

        val standingOrderSubmissionRequest = OBWriteInternationalScheduled2().data(
            OBWriteDataInternationalScheduled2()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        val signedPayload =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(standingOrderSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        val submissionResponse = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse4>(
            payment3_1.Links.links.CreateInternationalScheduledPayment,
            standingOrderSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1
        )

        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.internationalScheduledPaymentId).isNotEmpty()

        // When
        val result = PaymentRS().getPayment<OBWritePaymentDetailsResponse1>(
            urlWithInternationalScheduledPaymentId(
                payment3_1.Links.links.GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails,
                submissionResponse.data.internationalScheduledPaymentId
            ),
            accessTokenClientCredentials,
            tppResource.tpp
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.paymentStatus).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1",
        operations = ["GetInternationalScheduledPayment", "CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails_rateType_ACTUAL_v3_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent2()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.ACTUAL
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse2>(
            payment3_1.Links.links.CreateInternationalScheduledPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1,
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

        val patchedConsent = PaymentRS().getConsent<OBWriteInternationalScheduledConsentResponse2>(
            PaymentFactory.urlWithConsentId(
                payment3_1.Links.links.GetInternationalScheduledPaymentConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            OBVersion.v3_1
        )

        assertThat(patchedConsent).isNotNull()
        assertThat(patchedConsent.data).isNotNull()
        assertThat(patchedConsent.risk).isNotNull()
        assertThat(patchedConsent.data.consentId).isNotEmpty()
        Assertions.assertThat(patchedConsent.data.status.toString()).`is`(Status.consentCondition)

        val standingOrderSubmissionRequest = OBWriteInternationalScheduled2().data(
            OBWriteDataInternationalScheduled2()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        val signedPayload =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(standingOrderSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        val submissionResponse = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse4>(
            payment3_1.Links.links.CreateInternationalScheduledPayment,
            standingOrderSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1
        )

        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.internationalScheduledPaymentId).isNotEmpty()

        // When
        val result = PaymentRS().getPayment<OBWritePaymentDetailsResponse1>(
            urlWithInternationalScheduledPaymentId(
                payment3_1.Links.links.GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails,
                submissionResponse.data.internationalScheduledPaymentId
            ),
            accessTokenClientCredentials,
            tppResource.tpp
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.paymentStatus).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1",
        operations = ["GetInternationalScheduledPayment", "CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails_rateType_INDICATIVE_v3_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent2()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.INDICATIVE
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse2>(
            payment3_1.Links.links.CreateInternationalScheduledPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1,
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

        val patchedConsent = PaymentRS().getConsent<OBWriteInternationalScheduledConsentResponse2>(
            PaymentFactory.urlWithConsentId(
                payment3_1.Links.links.GetInternationalScheduledPaymentConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            OBVersion.v3_1
        )

        assertThat(patchedConsent).isNotNull()
        assertThat(patchedConsent.data).isNotNull()
        assertThat(patchedConsent.risk).isNotNull()
        assertThat(patchedConsent.data.consentId).isNotEmpty()
        Assertions.assertThat(patchedConsent.data.status.toString()).`is`(Status.consentCondition)

        val standingOrderSubmissionRequest = OBWriteInternationalScheduled2().data(
            OBWriteDataInternationalScheduled2()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        val signedPayload =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(standingOrderSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        val submissionResponse = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse4>(
            payment3_1.Links.links.CreateInternationalScheduledPayment,
            standingOrderSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1
        )

        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.internationalScheduledPaymentId).isNotEmpty()

        // When
        val result = PaymentRS().getPayment<OBWritePaymentDetailsResponse1>(
            urlWithInternationalScheduledPaymentId(
                payment3_1.Links.links.GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails,
                submissionResponse.data.internationalScheduledPaymentId
            ),
            accessTokenClientCredentials,
            tppResource.tpp
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.paymentStatus).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1",
        operations = ["GetInternationalScheduledPayment", "CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails_v3_1_mandatoryFields() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent2MandatoryFields()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse2>(
            payment3_1.Links.links.CreateInternationalScheduledPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1,
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

        val patchedConsent = PaymentRS().getConsent<OBWriteInternationalScheduledConsentResponse2>(
            PaymentFactory.urlWithConsentId(
                payment3_1.Links.links.GetInternationalScheduledPaymentConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            OBVersion.v3_1
        )

        assertThat(patchedConsent).isNotNull()
        assertThat(patchedConsent.data).isNotNull()
        assertThat(patchedConsent.risk).isNotNull()
        assertThat(patchedConsent.data.consentId).isNotEmpty()
        Assertions.assertThat(patchedConsent.data.status.toString()).`is`(Status.consentCondition)

        val standingOrderSubmissionRequest = OBWriteInternationalScheduled2().data(
            OBWriteDataInternationalScheduled2()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        val signedPayload =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(standingOrderSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        val submissionResponse = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse4>(
            payment3_1.Links.links.CreateInternationalScheduledPayment,
            standingOrderSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1
        )

        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.internationalScheduledPaymentId).isNotEmpty()

        // When
        val result = PaymentRS().getPayment<OBWritePaymentDetailsResponse1>(
            urlWithInternationalScheduledPaymentId(
                payment3_1.Links.links.GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails,
                submissionResponse.data.internationalScheduledPaymentId
            ),
            accessTokenClientCredentials,
            tppResource.tpp
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.paymentStatus).isNotNull()
    }
}
