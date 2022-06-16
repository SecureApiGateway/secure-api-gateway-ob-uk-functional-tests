package com.forgerock.uk.openbanking.tests.functional.payment.domestic.scheduled.payments

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.securebanking.framework.conditions.Status
import com.forgerock.securebanking.framework.configuration.psu
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.framework.http.fuel.defaultMapper
import com.forgerock.securebanking.framework.signature.signPayloadSubmitPayment
import com.forgerock.securebanking.framework.signature.signPayloadSubmitPaymentInvalidB64ClaimTrue
import com.forgerock.securebanking.openbanking.uk.common.api.meta.OBVersion
import com.forgerock.uk.openbanking.framework.constants.INVALID_CONSENT_ID
import com.forgerock.uk.openbanking.framework.constants.INVALID_FORMAT_DETACHED_JWS
import com.forgerock.uk.openbanking.framework.constants.INVALID_SIGNING_KID
import com.forgerock.uk.openbanking.framework.errors.INVALID_FORMAT_DETACHED_JWS_ERROR
import com.forgerock.uk.openbanking.framework.errors.NO_DETACHED_JWS
import com.forgerock.uk.openbanking.framework.errors.PAYMENT_SUBMISSION_ALREADY_EXISTS
import com.forgerock.uk.openbanking.framework.errors.UNAUTHORIZED
import com.forgerock.uk.openbanking.support.discovery.payment3_1_1
import com.forgerock.uk.openbanking.support.discovery.payment3_1_3
import com.forgerock.uk.openbanking.support.discovery.payment3_1_4
import com.forgerock.uk.openbanking.support.discovery.payment3_1_8
import com.forgerock.uk.openbanking.support.payment.PaymentAS
import com.forgerock.uk.openbanking.support.payment.PaymentFactory
import com.forgerock.uk.openbanking.support.payment.PaymentFactory.Companion.copyOBWriteDomesticScheduled2DataInitiation
import com.forgerock.uk.openbanking.support.payment.PaymentFactory.Companion.mapOBDomesticScheduled2ToOBWriteDomesticScheduled2DataInitiation
import com.forgerock.uk.openbanking.support.payment.PaymentRS
import com.github.kittinunf.fuel.core.FuelError
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.payment.*
import uk.org.openbanking.testsupport.payment.OBWriteDomesticScheduledConsentTestDataFactory.*

class CreateDomesticScheduledPaymentTest(val tppResource: CreateTppCallback.TppResource) {
    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun createDomesticScheduledPayments_v3_1_8() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent4()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteDomesticScheduledConsentResponse5>(
            payment3_1_8.Links.links.CreateDomesticScheduledPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_8,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

        val patchedConsent = PaymentRS().getConsent<OBWriteDomesticScheduledConsentResponse5>(
            PaymentFactory.urlWithConsentId(
                payment3_1_8.Links.links.GetDomesticScheduledPaymentConsent,
                consent.data.consentId
            ),
            tppResource.tpp
        )

        assertThat(patchedConsent).isNotNull()
        assertThat(patchedConsent.data).isNotNull()
        assertThat(patchedConsent.data.initiation).isNotNull()
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

        // When
        val result = PaymentRS().submitPayment<OBWriteDomesticScheduledResponse5>(
            payment3_1_8.Links.links.CreateDomesticScheduledPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1_8
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.consentId).isNotEmpty()
    }

    @Disabled("Bug: https://github.com/SecureBankingAccessToolkit/SecureBankingAccessToolkit/issues/336")
    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsPaymentAlreadyExists_v3_1_8() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent4()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteDomesticScheduledConsentResponse5>(
            payment3_1_8.Links.links.CreateDomesticScheduledPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_8,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

        val patchedConsent = PaymentRS().getConsent<OBWriteDomesticScheduledConsentResponse5>(
            PaymentFactory.urlWithConsentId(
                payment3_1_8.Links.links.GetDomesticScheduledPaymentConsent,
                consent.data.consentId
            ),
            tppResource.tpp
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
        val result = PaymentRS().submitPayment<OBWriteDomesticScheduledResponse5>(
            payment3_1_8.Links.links.CreateDomesticScheduledPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1_8
        )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteDomesticScheduledResponse5>(
                payment3_1_8.Links.links.CreateDomesticScheduledPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp,
                OBVersion.v3_1_8
            )
        }

        // Then
        assertThat(exception.message.toString()).contains(PAYMENT_SUBMISSION_ALREADY_EXISTS)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(403)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsSendInvalidFormatDetachedJws_v3_1_8() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent4()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteDomesticScheduledConsentResponse5>(
            payment3_1_8.Links.links.CreateDomesticScheduledPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_8,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

        val patchedConsent = PaymentRS().getConsent<OBWriteDomesticScheduledConsentResponse5>(
            PaymentFactory.urlWithConsentId(
                payment3_1_8.Links.links.GetDomesticScheduledPaymentConsent,
                consent.data.consentId
            ),
            tppResource.tpp
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

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteDomesticScheduledResponse5>(
                payment3_1_8.Links.links.CreateDomesticScheduledPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                INVALID_FORMAT_DETACHED_JWS,
                tppResource.tpp,
                OBVersion.v3_1_8
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(INVALID_FORMAT_DETACHED_JWS_ERROR)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsNoDetachedJws_v3_1_8() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent4()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteDomesticScheduledConsentResponse5>(
            payment3_1_8.Links.links.CreateDomesticScheduledPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_8,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

        val patchedConsent = PaymentRS().getConsent<OBWriteDomesticScheduledConsentResponse5>(
            PaymentFactory.urlWithConsentId(
                payment3_1_8.Links.links.GetDomesticScheduledPaymentConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            OBVersion.v3_1_8
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

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPaymentNoDetachedJws<OBWriteDomesticScheduledResponse5>(
                payment3_1_8.Links.links.CreateDomesticScheduledPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(NO_DETACHED_JWS)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsNotPermittedB64HeaderAddedInTheDetachedJws_v3_1_8() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent4()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteDomesticScheduledConsentResponse5>(
            payment3_1_8.Links.links.CreateDomesticScheduledPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_8,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

        val patchedConsent = PaymentRS().getConsent<OBWriteDomesticScheduledConsentResponse5>(
            PaymentFactory.urlWithConsentId(
                payment3_1_8.Links.links.GetDomesticScheduledPaymentConsent,
                consent.data.consentId
            ),
            tppResource.tpp
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

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteDomesticScheduledResponse5>(
                payment3_1_8.Links.links.CreateDomesticScheduledPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp,
                OBVersion.v3_1_8
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsSendInvalidKidDetachedJws_v3_1_8() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent4()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteDomesticScheduledConsentResponse5>(
            payment3_1_8.Links.links.CreateDomesticScheduledPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_8,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

        val patchedConsent = PaymentRS().getConsent<OBWriteDomesticScheduledConsentResponse5>(
            PaymentFactory.urlWithConsentId(
                payment3_1_8.Links.links.GetDomesticScheduledPaymentConsent,
                consent.data.consentId
            ),
            tppResource.tpp
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
                INVALID_SIGNING_KID
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteDomesticScheduledResponse5>(
                payment3_1_8.Links.links.CreateDomesticScheduledPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp,
                OBVersion.v3_1_8
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBody_v3_1_8() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent4()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteDomesticScheduledConsentResponse5>(
            payment3_1_8.Links.links.CreateDomesticScheduledPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_8,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

        val patchedConsent = PaymentRS().getConsent<OBWriteDomesticScheduledConsentResponse5>(
            PaymentFactory.urlWithConsentId(
                payment3_1_8.Links.links.GetDomesticScheduledPaymentConsent,
                consent.data.consentId
            ),
            tppResource.tpp
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

        patchedConsent.data.consentId = INVALID_CONSENT_ID
        val paymentSubmissionWithInvalidConsentId = OBWriteDomesticScheduled2().data(
            OBWriteDomesticScheduled2Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        val signedPayload = signPayloadSubmitPayment(
            defaultMapper.writeValueAsString(paymentSubmissionWithInvalidConsentId),
            tppResource.tpp.signingKey,
            tppResource.tpp.signingKid
        )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteDomesticScheduledResponse5>(
                payment3_1_8.Links.links.CreateDomesticScheduledPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp,
                OBVersion.v3_1_8
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBody_v3_1_8() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent4()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteDomesticScheduledConsentResponse5>(
            payment3_1_8.Links.links.CreateDomesticScheduledPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_8,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

        val patchedConsent = PaymentRS().getConsent<OBWriteDomesticScheduledConsentResponse5>(
            PaymentFactory.urlWithConsentId(
                payment3_1_8.Links.links.GetDomesticScheduledPaymentConsent,
                consent.data.consentId
            ),
            tppResource.tpp
        )

        assertThat(patchedConsent).isNotNull()
        assertThat(patchedConsent.data).isNotNull()
        assertThat(patchedConsent.risk).isNotNull()
        assertThat(patchedConsent.data.consentId).isNotEmpty()
        Assertions.assertThat(patchedConsent.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = OBWriteDomesticScheduled2().data(
            OBWriteDomesticScheduled2Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(copyOBWriteDomesticScheduled2DataInitiation(patchedConsent.data.initiation))
        ).risk(patchedConsent.risk)

        patchedConsent.data.initiation.instructedAmount.amount = "123123"
        val paymentSubmissionInvalidAmount = OBWriteDomesticScheduled2().data(
            OBWriteDomesticScheduled2Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        val signedPayload = signPayloadSubmitPayment(
            defaultMapper.writeValueAsString(paymentSubmissionInvalidAmount),
            tppResource.tpp.signingKey,
            tppResource.tpp.signingKid
        )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteDomesticScheduledResponse5>(
                payment3_1_8.Links.links.CreateDomesticScheduledPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp,
                OBVersion.v3_1_8
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"]
    )
    @Test
    fun createDomesticScheduledPayments_v3_1_4() {
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
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

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

        // When
        val result = PaymentRS().submitPayment<OBWriteDomesticScheduledResponse4>(
            payment3_1_4.Links.links.CreateDomesticScheduledPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1_4
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.consentId).isNotEmpty()
    }

    @Disabled("Bug: https://github.com/SecureBankingAccessToolkit/SecureBankingAccessToolkit/issues/336")
    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsPaymentAlreadyExists_v3_1_4() {
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
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

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
        val result = PaymentRS().submitPayment<OBWriteDomesticScheduledResponse4>(
            payment3_1_4.Links.links.CreateDomesticScheduledPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp, OBVersion.v3_1_4
        )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteDomesticScheduledResponse4>(
                payment3_1_4.Links.links.CreateDomesticScheduledPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp, OBVersion.v3_1_4
            )
        }

        // Then
        assertThat(exception.message.toString()).contains(PAYMENT_SUBMISSION_ALREADY_EXISTS)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(403)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsSendInvalidFormatDetachedJws_v3_1_4() {
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
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

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

        // When
        val exception = assertThrows(AssertionError::class.java) {

            PaymentRS().submitPayment<OBWriteDomesticScheduledResponse4>(
                payment3_1_4.Links.links.CreateDomesticScheduledPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                INVALID_FORMAT_DETACHED_JWS,
                tppResource.tpp,
                OBVersion.v3_1_4
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(INVALID_FORMAT_DETACHED_JWS_ERROR)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsNoDetachedJws_v3_1_4() {
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
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

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

        // When
        val exception = assertThrows(AssertionError::class.java) {

            PaymentRS().submitPaymentNoDetachedJws<OBWriteDomesticScheduledResponse4>(
                payment3_1_4.Links.links.CreateDomesticScheduledPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(NO_DETACHED_JWS)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsNotPermittedB64HeaderAddedInTheDetachedJws_v3_1_4() {
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
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

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

        val signedPayload =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(paymentSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {

            PaymentRS().submitPayment<OBWriteDomesticScheduledResponse4>(
                payment3_1_4.Links.links.CreateDomesticScheduledPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp, OBVersion.v3_1_4
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsSendInvalidKidDetachedJws_v3_1_4() {
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
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

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

        val signedPayload =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(paymentSubmissionRequest),
                tppResource.tpp.signingKey,
                INVALID_SIGNING_KID
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {

            PaymentRS().submitPayment<OBWriteDomesticScheduledResponse4>(
                payment3_1_4.Links.links.CreateDomesticScheduledPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp, OBVersion.v3_1_4
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBody_v3_1_4() {
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
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

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

        patchedConsent.data.consentId = INVALID_CONSENT_ID
        val paymentSubmissionWithInvalidConsentId = OBWriteDomesticScheduled2().data(
            OBWriteDomesticScheduled2Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        val signedPayload =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(paymentSubmissionWithInvalidConsentId),
                tppResource.tpp.signingKey,
                INVALID_SIGNING_KID
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteDomesticScheduledResponse4>(
                payment3_1_4.Links.links.CreateDomesticScheduledPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp, OBVersion.v3_1_4
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBody_v3_1_4() {
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
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

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

        patchedConsent.data.initiation.instructedAmount.amount = "123123"
        val paymentSubmissionInvalidAmount = OBWriteDomesticScheduled2().data(
            OBWriteDomesticScheduled2Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        val signedPayload =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(paymentSubmissionInvalidAmount),
                tppResource.tpp.signingKey,
                INVALID_SIGNING_KID
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteDomesticScheduledResponse4>(
                payment3_1_4.Links.links.CreateDomesticScheduledPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp, OBVersion.v3_1_4
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1.2"]
    )
    @Test
    fun createDomesticScheduledPayments_v3_1_3() {
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
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

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

        // When
        val result = PaymentRS().submitPayment<OBWriteDomesticScheduledResponse3>(
            payment3_1_3.Links.links.CreateDomesticScheduledPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp, OBVersion.v3_1_3
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.consentId).isNotEmpty()
    }

    @Disabled("Bug: https://github.com/SecureBankingAccessToolkit/SecureBankingAccessToolkit/issues/336")
    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1.2"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsPaymentAlreadyExists_v3_1_3() {
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
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

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
        val result = PaymentRS().submitPayment<OBWriteDomesticScheduledResponse3>(
            payment3_1_3.Links.links.CreateDomesticScheduledPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp, OBVersion.v3_1_3
        )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteDomesticScheduledResponse3>(
                payment3_1_3.Links.links.CreateDomesticScheduledPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp, OBVersion.v3_1_3
            )
        }

        // Then
        assertThat(exception.message.toString()).contains(PAYMENT_SUBMISSION_ALREADY_EXISTS)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(403)

    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1.2"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsSendInvalidFormatDetachedJws_v3_1_3() {
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
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

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

        // When
        val exception = assertThrows(AssertionError::class.java) {

            PaymentRS().submitPayment<OBWriteDomesticScheduledResponse3>(
                payment3_1_3.Links.links.CreateDomesticScheduledPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                INVALID_FORMAT_DETACHED_JWS,
                tppResource.tpp, OBVersion.v3_1_3
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(INVALID_FORMAT_DETACHED_JWS_ERROR)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1.2"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsNoDetachedJws_v3_1_3() {
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
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

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

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPaymentNoDetachedJws<OBWriteDomesticScheduledResponse3>(
                payment3_1_3.Links.links.CreateDomesticScheduledPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(NO_DETACHED_JWS)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1.2"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsSendInvalidKidDetachedJws_v3_1_3() {
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
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

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
                INVALID_SIGNING_KID,
                true
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteDomesticScheduledResponse3>(
                payment3_1_3.Links.links.CreateDomesticScheduledPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp, OBVersion.v3_1_3
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1.2"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsB64ClaimMissingDetachedJws_v3_1_3() {
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
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

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
                tppResource.tpp.signingKid
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteDomesticScheduledResponse3>(
                payment3_1_3.Links.links.CreateDomesticScheduledPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp, OBVersion.v3_1_3
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1.2"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsB64ClaimShouldBeFalseDetachedJws_v3_1_3() {
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
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

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
            signPayloadSubmitPaymentInvalidB64ClaimTrue(
                defaultMapper.writeValueAsString(paymentSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteDomesticScheduledResponse3>(
                payment3_1_3.Links.links.CreateDomesticScheduledPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp, OBVersion.v3_1_3
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1.2"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBody_v3_1_3() {
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
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

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

        patchedConsent.data.consentId = INVALID_CONSENT_ID
        val paymentSubmissionWithInvalidConsentId = OBWriteDomesticScheduled2().data(
            OBWriteDomesticScheduled2Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        val signedPayload = signPayloadSubmitPayment(
            defaultMapper.writeValueAsString(paymentSubmissionWithInvalidConsentId),
            tppResource.tpp.signingKey,
            tppResource.tpp.signingKid
        )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteDomesticScheduledResponse3>(
                payment3_1_3.Links.links.CreateDomesticScheduledPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp,
                OBVersion.v3_1_3
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1.2"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBody_v3_1_3() {
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
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

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

        patchedConsent.data.initiation.instructedAmount.amount = "123123"
        val paymentSubmissionInvalidAmount = OBWriteDomesticScheduled2().data(
            OBWriteDomesticScheduled2Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        val signedPayload = signPayloadSubmitPayment(
            defaultMapper.writeValueAsString(paymentSubmissionInvalidAmount),
            tppResource.tpp.signingKey,
            tppResource.tpp.signingKid
        )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteDomesticScheduledResponse3>(
                payment3_1_3.Links.links.CreateDomesticScheduledPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp,
                OBVersion.v3_1_3
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1"]
    )
    @Test
    fun createDomesticScheduledPayments_v3_1_1() {
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
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

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
                .initiation(mapOBDomesticScheduled2ToOBWriteDomesticScheduled2DataInitiation(patchedConsent.data.initiation))
        ).risk(patchedConsent.risk)

        val signedPayload =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(paymentSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        // When
        val result = PaymentRS().submitPayment<OBWriteDomesticScheduledResponse2>(
            payment3_1_1.Links.links.CreateDomesticScheduledPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp, OBVersion.v3_1_1
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.consentId).isNotEmpty()
    }

    @Disabled("Bug: https://github.com/SecureBankingAccessToolkit/SecureBankingAccessToolkit/issues/336")
    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsPaymentAlreadyExists_v3_1_1() {
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
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

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
                .initiation(mapOBDomesticScheduled2ToOBWriteDomesticScheduled2DataInitiation(patchedConsent.data.initiation))
        ).risk(patchedConsent.risk)

        val signedPayload =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(paymentSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )
        val result = PaymentRS().submitPayment<OBWriteDomesticScheduledResponse2>(
            payment3_1_1.Links.links.CreateDomesticScheduledPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp, OBVersion.v3_1_1
        )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteDomesticScheduledResponse2>(
                payment3_1_1.Links.links.CreateDomesticScheduledPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp, OBVersion.v3_1_1
            )
        }

        // Then
        assertThat(exception.message.toString()).contains(PAYMENT_SUBMISSION_ALREADY_EXISTS)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(403)

    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsSendInvalidFormatDetachedJws_v3_1_1() {
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
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

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
                .initiation(mapOBDomesticScheduled2ToOBWriteDomesticScheduled2DataInitiation(patchedConsent.data.initiation))
        ).risk(patchedConsent.risk)

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteDomesticScheduledResponse2>(
                payment3_1_1.Links.links.CreateDomesticScheduledPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                INVALID_FORMAT_DETACHED_JWS,
                tppResource.tpp, OBVersion.v3_1_1
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(INVALID_FORMAT_DETACHED_JWS_ERROR)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsNoDetachedJws_v3_1_1() {
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
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

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
                .initiation(mapOBDomesticScheduled2ToOBWriteDomesticScheduled2DataInitiation(patchedConsent.data.initiation))
        ).risk(patchedConsent.risk)

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPaymentNoDetachedJws<OBWriteDomesticScheduledResponse2>(
                payment3_1_1.Links.links.CreateDomesticScheduledPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(NO_DETACHED_JWS)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsSendInvalidKidDetachedJws_v3_1_1() {
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
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

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
                .initiation(mapOBDomesticScheduled2ToOBWriteDomesticScheduled2DataInitiation(patchedConsent.data.initiation))
        ).risk(patchedConsent.risk)

        val signedPayload =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(paymentSubmissionRequest),
                tppResource.tpp.signingKey,
                INVALID_SIGNING_KID
            )


        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteDomesticScheduledResponse2>(
                payment3_1_1.Links.links.CreateDomesticScheduledPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp, OBVersion.v3_1_1
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsB64ClaimMissingDetachedJws_v3_1_1() {
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
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

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
                .initiation(mapOBDomesticScheduled2ToOBWriteDomesticScheduled2DataInitiation(patchedConsent.data.initiation))
        ).risk(patchedConsent.risk)

        val signedPayload =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(paymentSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteDomesticScheduledResponse2>(
                payment3_1_1.Links.links.CreateDomesticScheduledPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp,
                OBVersion.v3_1_1
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsB64ClaimShouldBeFalseDetachedJws_v3_1_1() {
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
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

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
                .initiation(mapOBDomesticScheduled2ToOBWriteDomesticScheduled2DataInitiation(patchedConsent.data.initiation))
        ).risk(patchedConsent.risk)

        val signedPayload =
            signPayloadSubmitPaymentInvalidB64ClaimTrue(
                defaultMapper.writeValueAsString(paymentSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteDomesticScheduledResponse2>(
                payment3_1_1.Links.links.CreateDomesticScheduledPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp,
                OBVersion.v3_1_1
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBody_v3_1_1() {
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
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

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
                .initiation(mapOBDomesticScheduled2ToOBWriteDomesticScheduled2DataInitiation(patchedConsent.data.initiation))
        ).risk(patchedConsent.risk)

        patchedConsent.data.consentId = INVALID_CONSENT_ID
        val paymentSubmissionWithInvalidConsentId = OBWriteDomesticScheduled2().data(
            OBWriteDomesticScheduled2Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(mapOBDomesticScheduled2ToOBWriteDomesticScheduled2DataInitiation(patchedConsent.data.initiation))
        ).risk(patchedConsent.risk)

        val signedPayload = signPayloadSubmitPayment(
            defaultMapper.writeValueAsString(paymentSubmissionWithInvalidConsentId),
            tppResource.tpp.signingKey,
            tppResource.tpp.signingKid
        )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteDomesticScheduledResponse2>(
                payment3_1_1.Links.links.CreateDomesticScheduledPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp,
                OBVersion.v3_1_1
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBody_v3_1_1() {
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
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

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
                .initiation(mapOBDomesticScheduled2ToOBWriteDomesticScheduled2DataInitiation(patchedConsent.data.initiation))
        ).risk(patchedConsent.risk)

        patchedConsent.data.initiation.instructedAmount.amount = "123123"
        val paymentSubmissionInvalidAmount = OBWriteDomesticScheduled2().data(
            OBWriteDomesticScheduled2Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(mapOBDomesticScheduled2ToOBWriteDomesticScheduled2DataInitiation(patchedConsent.data.initiation))
        ).risk(patchedConsent.risk)

        val signedPayload = signPayloadSubmitPayment(
            defaultMapper.writeValueAsString(paymentSubmissionInvalidAmount),
            tppResource.tpp.signingKey,
            tppResource.tpp.signingKid
        )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteDomesticScheduledResponse2>(
                payment3_1_1.Links.links.CreateDomesticScheduledPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp,
                OBVersion.v3_1_1
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
    }
}
