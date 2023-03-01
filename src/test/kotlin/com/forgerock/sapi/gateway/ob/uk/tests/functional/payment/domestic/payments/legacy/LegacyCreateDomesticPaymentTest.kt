package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.payments.legacy

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.sapi.gateway.framework.conditions.Status
import com.forgerock.sapi.gateway.framework.configuration.psu
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.framework.http.fuel.defaultMapper
import com.forgerock.sapi.gateway.framework.signature.signPayloadSubmitPayment
import com.forgerock.sapi.gateway.framework.signature.signPayloadSubmitPaymentInvalidB64ClaimTrue
import com.forgerock.sapi.gateway.ob.uk.framework.constants.INVALID_SIGNING_KID
import com.forgerock.sapi.gateway.ob.uk.support.discovery.payment3_1_1
import com.forgerock.sapi.gateway.ob.uk.support.discovery.payment3_1_3
import com.forgerock.sapi.gateway.ob.uk.support.discovery.payment3_1_4
import com.forgerock.sapi.gateway.ob.uk.support.payment.PaymentAS
import com.forgerock.sapi.gateway.ob.uk.support.payment.PaymentFactory
import com.forgerock.sapi.gateway.ob.uk.support.payment.PaymentFactory.Companion.copyOBWriteDomestic2DataInitiation
import com.forgerock.sapi.gateway.ob.uk.support.payment.PaymentFactory.Companion.mapOBDomestic2ToOBWriteDomestic2DataInitiation
import com.forgerock.sapi.gateway.ob.uk.support.payment.PaymentRS
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.github.kittinunf.fuel.core.FuelError
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.payment.*
import uk.org.openbanking.testsupport.payment.OBWriteDomesticConsentTestDataFactory.*

class LegacyCreateDomesticPaymentTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"]
    )
    @Test
    fun createDomesticPayments_v3_1_4() {
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

        // When
        val result = PaymentRS().submitPayment<OBWriteDomesticResponse4>(
            payment3_1_4.Links.links.CreateDomesticPayment,
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
        if (consent.data.charges.isNullOrEmpty())
        {
            assertThat(result.data.charges).isNotEmpty()
        }
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticPayments_throwsPaymentAlreadyExists_v3_1_4() {
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

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteDomesticResponse4>(
                payment3_1_4.Links.links.CreateDomesticPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp, OBVersion.v3_1_4
            )
        }

        // Then
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.PAYMENT_SUBMISSION_ALREADY_EXISTS)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(403)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticPayments_throwsSendInvalidFormatDetachedJws_v3_1_4() {
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

        // When
        val exception = assertThrows(AssertionError::class.java) {

            PaymentRS().submitPayment<OBWriteDomesticResponse4>(
                payment3_1_4.Links.links.CreateDomesticPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                    com.forgerock.sapi.gateway.ob.uk.framework.constants.INVALID_FORMAT_DETACHED_JWS,
                tppResource.tpp,
                OBVersion.v3_1_4
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.INVALID_FORMAT_DETACHED_JWS_ERROR)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticPayments_throwsNoDetachedJws_v3_1_4() {
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

        // When
        val exception = assertThrows(AssertionError::class.java) {

            PaymentRS().submitPaymentNoDetachedJws<OBWriteDomesticResponse4>(
                payment3_1_4.Links.links.CreateDomesticPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.NO_DETACHED_JWS)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticPayments_throwsNotPermittedB64HeaderAddedInTheDetachedJws_v3_1_4() {
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

        val signedPayload =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(paymentSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {

            PaymentRS().submitPayment<OBWriteDomesticResponse4>(
                payment3_1_4.Links.links.CreateDomesticPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp, OBVersion.v3_1_4
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticPayments_throwsSendInvalidKidDetachedJws_v3_1_4() {
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

        val signedPayload =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(paymentSubmissionRequest),
                tppResource.tpp.signingKey,
                    INVALID_SIGNING_KID
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {

            PaymentRS().submitPayment<OBWriteDomesticResponse4>(
                payment3_1_4.Links.links.CreateDomesticPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp, OBVersion.v3_1_4
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticPayments_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBody_v3_1_4() {
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

        patchedConsent.data.consentId = com.forgerock.sapi.gateway.ob.uk.framework.constants.INVALID_CONSENT_ID
        val paymentSubmissionWithInvalidConsentId = OBWriteDomestic2().data(
            OBWriteDomestic2Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(copyOBWriteDomestic2DataInitiation(patchedConsent.data.initiation))
        ).risk(patchedConsent.risk)

        val signedPayload =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(paymentSubmissionWithInvalidConsentId),
                tppResource.tpp.signingKey,
                    INVALID_SIGNING_KID
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteDomesticResponse4>(
                payment3_1_4.Links.links.CreateDomesticPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp, OBVersion.v3_1_4
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticPayments_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBody_v3_1_4() {
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

        patchedConsent.data.initiation.instructedAmount.amount = "123123"
        val paymentSubmissionInvalidAmount = OBWriteDomestic2().data(
            OBWriteDomestic2Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(copyOBWriteDomestic2DataInitiation(patchedConsent.data.initiation))
        ).risk(patchedConsent.risk)

        val signedPayload =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(paymentSubmissionInvalidAmount),
                tppResource.tpp.signingKey,
                    INVALID_SIGNING_KID
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteDomesticResponse4>(
                payment3_1_4.Links.links.CreateDomesticPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp, OBVersion.v3_1_4
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"],
        compatibleVersions = ["v.3.1.2"]
    )
    @Test
    fun createDomesticPayments_v3_1_3() {
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

        // When
        val result = PaymentRS().submitPayment<OBWriteDomesticResponse3>(
            payment3_1_3.Links.links.CreateDomesticPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp, OBVersion.v3_1_3
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.consentId).isNotEmpty()
        if (consent.data.charges.isNullOrEmpty())
        {
            assertThat(result.data.charges).isNotEmpty()
        }
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"],
        compatibleVersions = ["v.3.1.2"]
    )
    @Test
    fun shouldCreateDomesticPayments_throwsPaymentAlreadyExists_v3_1_3() {
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

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteDomesticResponse3>(
                payment3_1_3.Links.links.CreateDomesticPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp, OBVersion.v3_1_3
            )
        }

        // Then
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.PAYMENT_SUBMISSION_ALREADY_EXISTS)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(403)

    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"],
        compatibleVersions = ["v.3.1.2"]
    )
    @Test
    fun shouldCreateDomesticPayments_throwsSendInvalidFormatDetachedJws_v3_1_3() {
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

        // When
        val exception = assertThrows(AssertionError::class.java) {

            PaymentRS().submitPayment<OBWriteDomesticResponse3>(
                payment3_1_3.Links.links.CreateDomesticPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                    com.forgerock.sapi.gateway.ob.uk.framework.constants.INVALID_FORMAT_DETACHED_JWS,
                tppResource.tpp, OBVersion.v3_1_3
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.INVALID_FORMAT_DETACHED_JWS_ERROR)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"],
        compatibleVersions = ["v.3.1.2"]
    )
    @Test
    fun shouldCreateDomesticPayments_throwsNoDetachedJws_v3_1_3() {
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

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPaymentNoDetachedJws<OBWriteDomesticResponse3>(
                payment3_1_3.Links.links.CreateDomesticPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.NO_DETACHED_JWS)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"],
        compatibleVersions = ["v.3.1.2"]
    )
    @Test
    fun shouldCreateDomesticPayments_throwsSendInvalidKidDetachedJws_v3_1_3() {
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
                    INVALID_SIGNING_KID,
                true
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteDomesticResponse3>(
                payment3_1_3.Links.links.CreateDomesticPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp, OBVersion.v3_1_3
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"],
        compatibleVersions = ["v.3.1.2"]
    )
    @Test
    fun shouldCreateDomesticPayments_throwsB64ClaimMissingDetachedJws_v3_1_3() {
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
                tppResource.tpp.signingKid
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteDomesticResponse3>(
                payment3_1_3.Links.links.CreateDomesticPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp, OBVersion.v3_1_3
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"],
        compatibleVersions = ["v.3.1.2"]
    )
    @Test
    fun shouldCreateDomesticPayments_throwsB64ClaimShouldBeFalseDetachedJws_v3_1_3() {
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
            signPayloadSubmitPaymentInvalidB64ClaimTrue(
                defaultMapper.writeValueAsString(paymentSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteDomesticResponse3>(
                payment3_1_3.Links.links.CreateDomesticPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp, OBVersion.v3_1_3
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"],
        compatibleVersions = ["v.3.1.2"]
    )
    @Test
    fun shouldCreateDomesticPayments_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBody_v3_1_3() {
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

        patchedConsent.data.consentId = com.forgerock.sapi.gateway.ob.uk.framework.constants.INVALID_CONSENT_ID
        val paymentSubmissionWithInvalidConsentId = OBWriteDomestic2().data(
            OBWriteDomestic2Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(copyOBWriteDomestic2DataInitiation(patchedConsent.data.initiation))
        ).risk(patchedConsent.risk)

        val signedPayload = signPayloadSubmitPayment(
            defaultMapper.writeValueAsString(paymentSubmissionWithInvalidConsentId),
            tppResource.tpp.signingKey,
            tppResource.tpp.signingKid
        )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteDomesticResponse3>(
                payment3_1_3.Links.links.CreateDomesticPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp,
                OBVersion.v3_1_3
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"],
        compatibleVersions = ["v.3.1.2"]
    )
    @Test
    fun shouldCreateDomesticPayments_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBody_v3_1_3() {
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

        patchedConsent.data.initiation.instructedAmount.amount = "123123"
        val paymentSubmissionInvalidAmount = OBWriteDomestic2().data(
            OBWriteDomestic2Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(copyOBWriteDomestic2DataInitiation(patchedConsent.data.initiation))
        ).risk(patchedConsent.risk)

        val signedPayload = signPayloadSubmitPayment(
            defaultMapper.writeValueAsString(paymentSubmissionInvalidAmount),
            tppResource.tpp.signingKey,
            tppResource.tpp.signingKid
        )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteDomesticResponse3>(
                payment3_1_3.Links.links.CreateDomesticPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp,
                OBVersion.v3_1_3
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"],
        compatibleVersions = ["v.3.1"]
    )
    @Test
    fun createDomesticPayments_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent2()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteDomesticConsentResponse2>(
            payment3_1_1.Links.links.CreateDomesticPaymentConsent,
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

        val patchedConsent = PaymentRS().getConsent<OBWriteDomesticConsentResponse2>(
            PaymentFactory.urlWithConsentId(
                payment3_1_1.Links.links.GetDomesticPaymentConsent,
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

        val paymentSubmissionRequest = OBWriteDomestic2().data(
            OBWriteDomestic2Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(mapOBDomestic2ToOBWriteDomestic2DataInitiation(patchedConsent.data.initiation))
        ).risk(patchedConsent.risk)

        val signedPayload =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(paymentSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        // When
        val result = PaymentRS().submitPayment<OBWriteDomesticResponse2>(
            payment3_1_1.Links.links.CreateDomesticPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp, OBVersion.v3_1_1
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.consentId).isNotEmpty()
        if (consent.data.charges.isNullOrEmpty())
        {
            assertThat(result.data.charges).isNotEmpty()
        }
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"],
        compatibleVersions = ["v.3.1"]
    )
    @Test
    fun shouldCreateDomesticPayments_throwsPaymentAlreadyExists_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent2()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteDomesticConsentResponse2>(
            payment3_1_1.Links.links.CreateDomesticPaymentConsent,
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

        val patchedConsent = PaymentRS().getConsent<OBWriteDomesticConsentResponse2>(
            PaymentFactory.urlWithConsentId(
                payment3_1_1.Links.links.GetDomesticPaymentConsent,
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

        val paymentSubmissionRequest = OBWriteDomestic2().data(
                OBWriteDomestic2Data()
                        .consentId(patchedConsent.data.consentId)
                        .initiation(mapOBDomestic2ToOBWriteDomestic2DataInitiation(patchedConsent.data.initiation))
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
            PaymentRS().submitPayment<OBWriteDomesticResponse2>(
                payment3_1_1.Links.links.CreateDomesticPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp, OBVersion.v3_1_1
            )
        }

        // Then
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.PAYMENT_SUBMISSION_ALREADY_EXISTS)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(403)

    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"],
        compatibleVersions = ["v.3.1"]
    )
    @Test
    fun shouldCreateDomesticPayments_throwsSendInvalidFormatDetachedJws_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent2()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteDomesticConsentResponse2>(
            payment3_1_1.Links.links.CreateDomesticPaymentConsent,
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

        val patchedConsent = PaymentRS().getConsent<OBWriteDomesticConsentResponse2>(
            PaymentFactory.urlWithConsentId(
                payment3_1_1.Links.links.GetDomesticPaymentConsent,
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

        val paymentSubmissionRequest = OBWriteDomestic2().data(
                OBWriteDomestic2Data()
                        .consentId(patchedConsent.data.consentId)
                        .initiation(mapOBDomestic2ToOBWriteDomestic2DataInitiation(patchedConsent.data.initiation))
        ).risk(patchedConsent.risk)

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteDomesticResponse2>(
                payment3_1_1.Links.links.CreateDomesticPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                    com.forgerock.sapi.gateway.ob.uk.framework.constants.INVALID_FORMAT_DETACHED_JWS,
                tppResource.tpp, OBVersion.v3_1_1
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.INVALID_FORMAT_DETACHED_JWS_ERROR)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"],
        compatibleVersions = ["v.3.1"]
    )
    @Test
    fun shouldCreateDomesticPayments_throwsNoDetachedJws_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent2()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteDomesticConsentResponse2>(
            payment3_1_1.Links.links.CreateDomesticPaymentConsent,
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

        val patchedConsent = PaymentRS().getConsent<OBWriteDomesticConsentResponse2>(
            PaymentFactory.urlWithConsentId(
                payment3_1_1.Links.links.GetDomesticPaymentConsent,
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

        val paymentSubmissionRequest = OBWriteDomestic2().data(
                OBWriteDomestic2Data()
                        .consentId(patchedConsent.data.consentId)
                        .initiation(mapOBDomestic2ToOBWriteDomestic2DataInitiation(patchedConsent.data.initiation))
        ).risk(patchedConsent.risk)

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPaymentNoDetachedJws<OBWriteDomesticResponse2>(
                payment3_1_1.Links.links.CreateDomesticPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.NO_DETACHED_JWS)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"],
        compatibleVersions = ["v.3.1"]
    )
    @Test
    fun shouldCreateDomesticPayments_throwsSendInvalidKidDetachedJws_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent2()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteDomesticConsentResponse2>(
            payment3_1_1.Links.links.CreateDomesticPaymentConsent,
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

        val patchedConsent = PaymentRS().getConsent<OBWriteDomesticConsentResponse2>(
            PaymentFactory.urlWithConsentId(
                payment3_1_1.Links.links.GetDomesticPaymentConsent,
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

        val paymentSubmissionRequest = OBWriteDomestic2().data(
                OBWriteDomestic2Data()
                        .consentId(patchedConsent.data.consentId)
                        .initiation(mapOBDomestic2ToOBWriteDomestic2DataInitiation(patchedConsent.data.initiation))
        ).risk(patchedConsent.risk)

        val signedPayload =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(paymentSubmissionRequest),
                tppResource.tpp.signingKey,
                    INVALID_SIGNING_KID
            )


        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteDomesticResponse2>(
                payment3_1_1.Links.links.CreateDomesticPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp, OBVersion.v3_1_1
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"],
        compatibleVersions = ["v.3.1"]
    )
    @Test
    fun shouldCreateDomesticPayments_throwsB64ClaimMissingDetachedJws_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent2()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteDomesticConsentResponse2>(
            payment3_1_1.Links.links.CreateDomesticPaymentConsent,
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

        val patchedConsent = PaymentRS().getConsent<OBWriteDomesticConsentResponse2>(
            PaymentFactory.urlWithConsentId(
                payment3_1_1.Links.links.GetDomesticPaymentConsent,
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

        val paymentSubmissionRequest = OBWriteDomestic2().data(
                OBWriteDomestic2Data()
                        .consentId(patchedConsent.data.consentId)
                        .initiation(mapOBDomestic2ToOBWriteDomestic2DataInitiation(patchedConsent.data.initiation))
        ).risk(patchedConsent.risk)

        val signedPayload =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(paymentSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteDomesticResponse2>(
                payment3_1_1.Links.links.CreateDomesticPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp,
                OBVersion.v3_1_1
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"],
        compatibleVersions = ["v.3.1"]
    )
    @Test
    fun shouldCreateDomesticPayments_throwsB64ClaimShouldBeFalseDetachedJws_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent2()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteDomesticConsentResponse2>(
            payment3_1_1.Links.links.CreateDomesticPaymentConsent,
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

        val patchedConsent = PaymentRS().getConsent<OBWriteDomesticConsentResponse2>(
            PaymentFactory.urlWithConsentId(
                payment3_1_1.Links.links.GetDomesticPaymentConsent,
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

        val paymentSubmissionRequest = OBWriteDomestic2().data(
                OBWriteDomestic2Data()
                        .consentId(patchedConsent.data.consentId)
                        .initiation(mapOBDomestic2ToOBWriteDomestic2DataInitiation(patchedConsent.data.initiation))
        ).risk(patchedConsent.risk)

        val signedPayload =
            signPayloadSubmitPaymentInvalidB64ClaimTrue(
                defaultMapper.writeValueAsString(paymentSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteDomesticResponse2>(
                payment3_1_1.Links.links.CreateDomesticPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp,
                OBVersion.v3_1_1
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"],
        compatibleVersions = ["v.3.1"]
    )
    @Test
    fun shouldCreateDomesticPayments_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBody_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent2()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteDomesticConsentResponse2>(
            payment3_1_1.Links.links.CreateDomesticPaymentConsent,
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

        val patchedConsent = PaymentRS().getConsent<OBWriteDomesticConsentResponse2>(
            PaymentFactory.urlWithConsentId(
                payment3_1_1.Links.links.GetDomesticPaymentConsent,
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

        val paymentSubmissionRequest = OBWriteDomestic2().data(
                OBWriteDomestic2Data()
                        .consentId(patchedConsent.data.consentId)
                        .initiation(mapOBDomestic2ToOBWriteDomestic2DataInitiation(patchedConsent.data.initiation))
        ).risk(patchedConsent.risk)

        patchedConsent.data.consentId = com.forgerock.sapi.gateway.ob.uk.framework.constants.INVALID_CONSENT_ID
        val paymentSubmissionWithInvalidConsentId = OBWriteDomestic2().data(
                OBWriteDomestic2Data()
                        .consentId(patchedConsent.data.consentId)
                        .initiation(mapOBDomestic2ToOBWriteDomestic2DataInitiation(patchedConsent.data.initiation))
        ).risk(patchedConsent.risk)

        val signedPayload = signPayloadSubmitPayment(
            defaultMapper.writeValueAsString(paymentSubmissionWithInvalidConsentId),
            tppResource.tpp.signingKey,
            tppResource.tpp.signingKid
        )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteDomesticResponse2>(
                payment3_1_1.Links.links.CreateDomesticPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp,
                OBVersion.v3_1_1
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"],
        compatibleVersions = ["v.3.1"]
    )
    @Test
    fun shouldCreateDomesticPayments_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBody_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent2()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteDomesticConsentResponse2>(
            payment3_1_1.Links.links.CreateDomesticPaymentConsent,
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

        val patchedConsent = PaymentRS().getConsent<OBWriteDomesticConsentResponse2>(
            PaymentFactory.urlWithConsentId(
                payment3_1_1.Links.links.GetDomesticPaymentConsent,
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

        val paymentSubmissionRequest = OBWriteDomestic2().data(
                OBWriteDomestic2Data()
                        .consentId(patchedConsent.data.consentId)
                        .initiation(mapOBDomestic2ToOBWriteDomestic2DataInitiation(patchedConsent.data.initiation))
        ).risk(patchedConsent.risk)

        patchedConsent.data.initiation.instructedAmount.amount = "123123"
        val paymentSubmissionInvalidAmount = OBWriteDomestic2().data(
                OBWriteDomestic2Data()
                        .consentId(patchedConsent.data.consentId)
                        .initiation(mapOBDomestic2ToOBWriteDomestic2DataInitiation(patchedConsent.data.initiation))
        ).risk(patchedConsent.risk)

        val signedPayload = signPayloadSubmitPayment(
            defaultMapper.writeValueAsString(paymentSubmissionInvalidAmount),
            tppResource.tpp.signingKey,
            tppResource.tpp.signingKid
        )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteDomesticResponse2>(
                payment3_1_1.Links.links.CreateDomesticPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp,
                OBVersion.v3_1_1
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
    }
}
