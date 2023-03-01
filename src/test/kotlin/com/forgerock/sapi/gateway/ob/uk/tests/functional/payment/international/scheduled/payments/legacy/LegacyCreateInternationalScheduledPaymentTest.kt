package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.scheduled.payments.legacy

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
import com.forgerock.sapi.gateway.ob.uk.support.discovery.*
import com.forgerock.sapi.gateway.ob.uk.support.payment.PaymentAS
import com.forgerock.sapi.gateway.ob.uk.support.payment.PaymentFactory
import com.forgerock.sapi.gateway.ob.uk.support.payment.PaymentFactory.Companion.mapOBWriteInternationalScheduled2DataInitiationToOBWriteDataInternationalScheduled2
import com.forgerock.sapi.gateway.ob.uk.support.payment.PaymentRS
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.github.kittinunf.fuel.core.FuelError
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.payment.*
import uk.org.openbanking.testsupport.payment.OBWriteInternationalScheduledConsentTestDataFactory.*

class LegacyCreateInternationalScheduledPaymentTest(val tppResource: CreateTppCallback.TppResource) {
    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun createInternationalScheduledPayment_rateType_AGREED_v3_1_4() {
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
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.data.exchangeRateInformation).isNotNull()
        assertThat(consent.data.exchangeRateInformation.rateType).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.rateType)
        assertThat(consent.data.exchangeRateInformation.unitCurrency).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.unitCurrency)
        assertThat(consent.data.exchangeRateInformation.exchangeRate).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.exchangeRate)

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().authorizeConsent(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

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

        val paymentSubmissionRequest = OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        val signedPayload = signPayloadSubmitPayment(
            defaultMapper.writeValueAsString(paymentSubmissionRequest),
            tppResource.tpp.signingKey,
            tppResource.tpp.signingKid
        )

        // When
        val result = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse5>(
            payment3_1_4.Links.links.CreateInternationalScheduledPayment,
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
        assertThat(result.data.exchangeRateInformation.exchangeRate).isNotNull()
        assertThat(result.data.exchangeRateInformation).isNotNull()
        assertThat(result.data.exchangeRateInformation.rateType).isEqualTo(consent.data.initiation.exchangeRateInformation.rateType)
        assertThat(result.data.exchangeRateInformation.unitCurrency).isEqualTo(consent.data.initiation.exchangeRateInformation.unitCurrency)
        assertThat(result.data.exchangeRateInformation.exchangeRate).isEqualTo(consent.data.initiation.exchangeRateInformation.exchangeRate)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun createInternationalScheduledPayment_rateType_ACTUAL_v3_1_4() {
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
        assertThat(consent.data.exchangeRateInformation).isNotNull()
        assertThat(consent.data.exchangeRateInformation.rateType).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.rateType)
        assertThat(consent.data.exchangeRateInformation.unitCurrency).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.unitCurrency)
        assertThat(consent.data.exchangeRateInformation.exchangeRate).isNotNull()
        assertThat(consent.data.exchangeRateInformation.expirationDateTime).isNotNull()

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().authorizeConsent(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

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

        val paymentSubmissionRequest = OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        val signedPayload = signPayloadSubmitPayment(
            defaultMapper.writeValueAsString(paymentSubmissionRequest),
            tppResource.tpp.signingKey,
            tppResource.tpp.signingKid
        )

        // When
        val result = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse5>(
            payment3_1_4.Links.links.CreateInternationalScheduledPayment,
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
        assertThat(result.data.exchangeRateInformation).isNotNull()
        assertThat(result.data.exchangeRateInformation.rateType).isEqualTo(consent.data.initiation.exchangeRateInformation.rateType)
        assertThat(result.data.exchangeRateInformation.unitCurrency).isEqualTo(consent.data.initiation.exchangeRateInformation.unitCurrency)
        assertThat(result.data.exchangeRateInformation.exchangeRate).isNotNull()
        assertThat(result.data.exchangeRateInformation.expirationDateTime).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun createInternationalScheduledPayment_rateType_INDICATIVE_v3_1_4() {
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
        assertThat(consent.data.exchangeRateInformation).isNotNull()
        assertThat(consent.data.exchangeRateInformation.rateType).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.rateType)
        assertThat(consent.data.exchangeRateInformation.unitCurrency).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.unitCurrency)
        assertThat(consent.data.exchangeRateInformation.exchangeRate).isNotNull()

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().authorizeConsent(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

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

        val paymentSubmissionRequest = OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        val signedPayload = signPayloadSubmitPayment(
            defaultMapper.writeValueAsString(paymentSubmissionRequest),
            tppResource.tpp.signingKey,
            tppResource.tpp.signingKid
        )

        // When
        val result = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse5>(
            payment3_1_4.Links.links.CreateInternationalScheduledPayment,
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
        assertThat(result.data.exchangeRateInformation).isNotNull()
        assertThat(result.data.exchangeRateInformation.rateType).isEqualTo(consent.data.initiation.exchangeRateInformation.rateType)
        assertThat(result.data.exchangeRateInformation.unitCurrency).isEqualTo(consent.data.initiation.exchangeRateInformation.unitCurrency)
        assertThat(result.data.exchangeRateInformation.exchangeRate).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun createInternationalScheduledPayment_mandatoryFields_v3_1_4() {
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
        assertThat(consent.data.exchangeRateInformation).isNotNull()
        assertThat(consent.data.exchangeRateInformation.rateType).isNotNull()
        assertThat(consent.data.exchangeRateInformation.unitCurrency).isNotNull()
        assertThat(consent.data.exchangeRateInformation.exchangeRate).isNotNull()

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().authorizeConsent(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

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

        val paymentSubmissionRequest = OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        val signedPayload = signPayloadSubmitPayment(
            defaultMapper.writeValueAsString(paymentSubmissionRequest),
            tppResource.tpp.signingKey,
            tppResource.tpp.signingKid
        )

        // When
        val result = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse5>(
            payment3_1_4.Links.links.CreateInternationalScheduledPayment,
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
        assertThat(result.data.exchangeRateInformation).isNotNull()
        assertThat(result.data.exchangeRateInformation.rateType).isNotNull()
        assertThat(result.data.exchangeRateInformation.unitCurrency).isNotNull()
        assertThat(result.data.exchangeRateInformation.exchangeRate).isNotNull()
    }

    @Disabled("Bug: https://github.com/SecureBankingAccessToolkit/SecureBankingAccessToolkit/issues/336")
    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsInternationalScheduledPaymentAlreadyExists_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent5()

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

        val paymentSubmissionRequest = OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        val signedPayload = signPayloadSubmitPayment(
            defaultMapper.writeValueAsString(paymentSubmissionRequest),
            tppResource.tpp.signingKey,
            tppResource.tpp.signingKid
        )
        val result = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse5>(
            payment3_1_4.Links.links.CreateInternationalScheduledPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp, OBVersion.v3_1_4
        )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteInternationalScheduledResponse5>(
                payment3_1_4.Links.links.CreateInternationalScheduledPayment,
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
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsSendInvalidFormatDetachedJws_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent5()

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

        val paymentSubmissionRequest = OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        // When
        val exception = assertThrows(AssertionError::class.java) {

            PaymentRS().submitPayment<OBWriteInternationalScheduledResponse5>(
                payment3_1_4.Links.links.CreateInternationalScheduledPayment,
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
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsNoDetachedJws_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent5()

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

        val paymentSubmissionRequest = OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        // When
        val exception = assertThrows(AssertionError::class.java) {

            PaymentRS().submitPaymentNoDetachedJws<OBWriteInternationalScheduledResponse5>(
                payment3_1_4.Links.links.CreateInternationalScheduledPayment,
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
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsNotPermittedB64HeaderAddedInTheDetachedJws_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent5()

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

        val paymentSubmissionRequest = OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
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

            PaymentRS().submitPayment<OBWriteInternationalScheduledResponse5>(
                payment3_1_4.Links.links.CreateInternationalScheduledPayment,
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
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsSendInvalidKidDetachedJws_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent5()

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

        val paymentSubmissionRequest = OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
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

            PaymentRS().submitPayment<OBWriteInternationalScheduledResponse5>(
                payment3_1_4.Links.links.CreateInternationalScheduledPayment,
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
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBody_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent5()

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

        val paymentSubmissionRequest = OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        patchedConsent.data.consentId = com.forgerock.sapi.gateway.ob.uk.framework.constants.INVALID_CONSENT_ID
        val paymentSubmissionWithInvalidConsentId = OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
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
            PaymentRS().submitPayment<OBWriteInternationalScheduledResponse5>(
                payment3_1_4.Links.links.CreateInternationalScheduledPayment,
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
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBody_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent5()

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

        val paymentSubmissionRequest = OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        patchedConsent.data.initiation.instructedAmount.amount = "123123"
        val paymentSubmissionInvalidAmount = OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
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
            PaymentRS().submitPayment<OBWriteInternationalScheduledResponse5>(
                payment3_1_4.Links.links.CreateInternationalScheduledPayment,
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
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun createInternationalScheduledPayment_rateType_AGREED_v3_1_3() {
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
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.data.exchangeRateInformation).isNotNull()
        assertThat(consent.data.exchangeRateInformation.rateType).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.rateType)
        assertThat(consent.data.exchangeRateInformation.unitCurrency).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.unitCurrency)
        assertThat(consent.data.exchangeRateInformation.exchangeRate).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.exchangeRate)

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().authorizeConsent(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

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

        val paymentSubmissionRequest = OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
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
        val result = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse4>(
            payment3_1_3.Links.links.CreateInternationalScheduledPayment,
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
        assertThat(result.data.exchangeRateInformation.exchangeRate).isNotNull()
        assertThat(result.data.exchangeRateInformation).isNotNull()
        assertThat(result.data.exchangeRateInformation.rateType).isEqualTo(consent.data.initiation.exchangeRateInformation.rateType)
        assertThat(result.data.exchangeRateInformation.unitCurrency).isEqualTo(consent.data.initiation.exchangeRateInformation.unitCurrency)
        assertThat(result.data.exchangeRateInformation.exchangeRate).isEqualTo(consent.data.initiation.exchangeRateInformation.exchangeRate)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun createInternationalScheduledPayment_rateType_ACTUAL_v3_1_3() {
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
        assertThat(consent.data.exchangeRateInformation).isNotNull()
        assertThat(consent.data.exchangeRateInformation.rateType).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.rateType)
        assertThat(consent.data.exchangeRateInformation.unitCurrency).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.unitCurrency)
        assertThat(consent.data.exchangeRateInformation.exchangeRate).isNotNull()
        assertThat(consent.data.exchangeRateInformation.expirationDateTime).isNotNull()

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().authorizeConsent(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

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

        val paymentSubmissionRequest = OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
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
        val result = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse4>(
            payment3_1_3.Links.links.CreateInternationalScheduledPayment,
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
        assertThat(result.data.exchangeRateInformation).isNotNull()
        assertThat(result.data.exchangeRateInformation.rateType).isEqualTo(consent.data.initiation.exchangeRateInformation.rateType)
        assertThat(result.data.exchangeRateInformation.unitCurrency).isEqualTo(consent.data.initiation.exchangeRateInformation.unitCurrency)
        assertThat(result.data.exchangeRateInformation.exchangeRate).isNotNull()
        assertThat(result.data.exchangeRateInformation.expirationDateTime).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun createInternationalScheduledPayment_rateType_INDICATIVE_v3_1_3() {
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
        assertThat(consent.data.exchangeRateInformation).isNotNull()
        assertThat(consent.data.exchangeRateInformation.rateType).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.rateType)
        assertThat(consent.data.exchangeRateInformation.unitCurrency).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.unitCurrency)
        assertThat(consent.data.exchangeRateInformation.exchangeRate).isNotNull()

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().authorizeConsent(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

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

        val paymentSubmissionRequest = OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
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
        val result = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse4>(
            payment3_1_3.Links.links.CreateInternationalScheduledPayment,
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
        assertThat(result.data.exchangeRateInformation).isNotNull()
        assertThat(result.data.exchangeRateInformation.rateType).isEqualTo(consent.data.initiation.exchangeRateInformation.rateType)
        assertThat(result.data.exchangeRateInformation.unitCurrency).isEqualTo(consent.data.initiation.exchangeRateInformation.unitCurrency)
        assertThat(result.data.exchangeRateInformation.exchangeRate).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun createInternationalScheduledPayment_mandatoryFields_v3_1_3() {
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
        assertThat(consent.data.exchangeRateInformation).isNotNull()
        assertThat(consent.data.exchangeRateInformation.rateType).isNotNull()
        assertThat(consent.data.exchangeRateInformation.unitCurrency).isNotNull()
        assertThat(consent.data.exchangeRateInformation.exchangeRate).isNotNull()

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().authorizeConsent(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

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

        val paymentSubmissionRequest = OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
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
        val result = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse4>(
            payment3_1_3.Links.links.CreateInternationalScheduledPayment,
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
        assertThat(result.data.exchangeRateInformation).isNotNull()
        assertThat(result.data.exchangeRateInformation.rateType).isNotNull()
        assertThat(result.data.exchangeRateInformation.unitCurrency).isNotNull()
        assertThat(result.data.exchangeRateInformation.exchangeRate).isNotNull()
    }

    @Disabled("Bug: https://github.com/SecureBankingAccessToolkit/SecureBankingAccessToolkit/issues/336")
    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsInternationalScheduledPaymentAlreadyExists_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent4()

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

        val paymentSubmissionRequest = OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
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
        val result = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse4>(
            payment3_1_3.Links.links.CreateInternationalScheduledPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp, OBVersion.v3_1_3
        )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteInternationalScheduledResponse4>(
                payment3_1_3.Links.links.CreateInternationalScheduledPayment,
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
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsSendInvalidFormatDetachedJws_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent4()

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

        val paymentSubmissionRequest = OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        // When
        val exception = assertThrows(AssertionError::class.java) {

            PaymentRS().submitPayment<OBWriteInternationalScheduledResponse4>(
                payment3_1_3.Links.links.CreateInternationalScheduledPayment,
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
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsNoDetachedJws_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent4()

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

        val paymentSubmissionRequest = OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPaymentNoDetachedJws<OBWriteInternationalScheduledResponse4>(
                payment3_1_3.Links.links.CreateInternationalScheduledPayment,
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
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsSendInvalidKidDetachedJws_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent4()

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

        val paymentSubmissionRequest = OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
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
            PaymentRS().submitPayment<OBWriteInternationalScheduledResponse4>(
                payment3_1_3.Links.links.CreateInternationalScheduledPayment,
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
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsB64ClaimMissingDetachedJws_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent4()

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

        val paymentSubmissionRequest = OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
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
            PaymentRS().submitPayment<OBWriteInternationalScheduledResponse4>(
                payment3_1_3.Links.links.CreateInternationalScheduledPayment,
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
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsB64ClaimShouldBeFalseDetachedJws_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent4()

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

        val paymentSubmissionRequest = OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
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
            PaymentRS().submitPayment<OBWriteInternationalScheduledResponse4>(
                payment3_1_3.Links.links.CreateInternationalScheduledPayment,
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
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBody_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent4()

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

        val paymentSubmissionRequest = OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        patchedConsent.data.consentId = com.forgerock.sapi.gateway.ob.uk.framework.constants.INVALID_CONSENT_ID
        val paymentSubmissionWithInvalidConsentId = OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
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
            PaymentRS().submitPayment<OBWriteInternationalScheduledResponse4>(
                payment3_1_3.Links.links.CreateInternationalScheduledPayment,
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
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBody_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent4()

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

        val paymentSubmissionRequest = OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        patchedConsent.data.initiation.instructedAmount.amount = "123123"
        val paymentSubmissionInvalidAmount = OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
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
            PaymentRS().submitPayment<OBWriteInternationalScheduledResponse4>(
                payment3_1_3.Links.links.CreateInternationalScheduledPayment,
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
        apiVersion = "v3.1.2",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun createInternationalScheduledPayment_rateType_AGREED_v3_1_2() {
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
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.data.exchangeRateInformation).isNotNull()
        assertThat(consent.data.exchangeRateInformation.rateType).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.rateType)
        assertThat(consent.data.exchangeRateInformation.unitCurrency).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.unitCurrency)
        assertThat(consent.data.exchangeRateInformation.exchangeRate).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.exchangeRate)

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().authorizeConsent(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

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

        val paymentSubmissionRequest = OBWriteInternationalScheduled2().data(
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
                defaultMapper.writeValueAsString(paymentSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        // When
        val result = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse3>(
            payment3_1_2.Links.links.CreateInternationalScheduledPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp, OBVersion.v3_1_2
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.consentId).isNotEmpty()
        if (consent.data.charges.isNullOrEmpty())
        {
            assertThat(result.data.charges).isNotEmpty()
        }
        assertThat(result.data.exchangeRateInformation.exchangeRate).isNotNull()
        assertThat(result.data.exchangeRateInformation).isNotNull()
        assertThat(result.data.exchangeRateInformation.rateType).isEqualTo(consent.data.initiation.exchangeRateInformation.rateType)
        assertThat(result.data.exchangeRateInformation.unitCurrency).isEqualTo(consent.data.initiation.exchangeRateInformation.unitCurrency)
        assertThat(result.data.exchangeRateInformation.exchangeRate).isEqualTo(consent.data.initiation.exchangeRateInformation.exchangeRate)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.2",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun createInternationalScheduledPayment_rateType_ACTUAL_v3_1_2() {
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
        assertThat(consent.data.exchangeRateInformation).isNotNull()
        assertThat(consent.data.exchangeRateInformation.rateType).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.rateType)
        assertThat(consent.data.exchangeRateInformation.unitCurrency).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.unitCurrency)
        assertThat(consent.data.exchangeRateInformation.exchangeRate).isNotNull()
        assertThat(consent.data.exchangeRateInformation.expirationDateTime).isNotNull()

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().authorizeConsent(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

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

        val paymentSubmissionRequest = OBWriteInternationalScheduled2().data(
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
                defaultMapper.writeValueAsString(paymentSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        // When
        val result = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse3>(
            payment3_1_2.Links.links.CreateInternationalScheduledPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp, OBVersion.v3_1_2
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.consentId).isNotEmpty()
        if (consent.data.charges.isNullOrEmpty())
        {
            assertThat(result.data.charges).isNotEmpty()
        }
        assertThat(result.data.exchangeRateInformation).isNotNull()
        assertThat(result.data.exchangeRateInformation.rateType).isEqualTo(consent.data.initiation.exchangeRateInformation.rateType)
        assertThat(result.data.exchangeRateInformation.unitCurrency).isEqualTo(consent.data.initiation.exchangeRateInformation.unitCurrency)
        assertThat(result.data.exchangeRateInformation.exchangeRate).isNotNull()
        assertThat(result.data.exchangeRateInformation.expirationDateTime).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.2",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun createInternationalScheduledPayment_rateType_INDICATIVE_v3_1_2() {
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
        assertThat(consent.data.exchangeRateInformation).isNotNull()
        assertThat(consent.data.exchangeRateInformation.rateType).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.rateType)
        assertThat(consent.data.exchangeRateInformation.unitCurrency).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.unitCurrency)
        assertThat(consent.data.exchangeRateInformation.exchangeRate).isNotNull()

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().authorizeConsent(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

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

        val paymentSubmissionRequest = OBWriteInternationalScheduled2().data(
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
                defaultMapper.writeValueAsString(paymentSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        // When
        val result = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse3>(
            payment3_1_2.Links.links.CreateInternationalScheduledPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp, OBVersion.v3_1_2
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.consentId).isNotEmpty()
        if (consent.data.charges.isNullOrEmpty())
        {
            assertThat(result.data.charges).isNotEmpty()
        }
        assertThat(result.data.exchangeRateInformation).isNotNull()
        assertThat(result.data.exchangeRateInformation.rateType).isEqualTo(consent.data.initiation.exchangeRateInformation.rateType)
        assertThat(result.data.exchangeRateInformation.unitCurrency).isEqualTo(consent.data.initiation.exchangeRateInformation.unitCurrency)
        assertThat(result.data.exchangeRateInformation.exchangeRate).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.2",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun createInternationalScheduledPayment_mandatoryFields_v3_1_2() {
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
        assertThat(consent.data.exchangeRateInformation).isNotNull()
        assertThat(consent.data.exchangeRateInformation.rateType).isNotNull()
        assertThat(consent.data.exchangeRateInformation.unitCurrency).isNotNull()
        assertThat(consent.data.exchangeRateInformation.exchangeRate).isNotNull()

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().authorizeConsent(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

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

        val paymentSubmissionRequest = OBWriteInternationalScheduled2().data(
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
                defaultMapper.writeValueAsString(paymentSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        // When
        val result = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse3>(
            payment3_1_2.Links.links.CreateInternationalScheduledPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp, OBVersion.v3_1_2
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.consentId).isNotEmpty()
        if (consent.data.charges.isNullOrEmpty())
        {
            assertThat(result.data.charges).isNotEmpty()
        }
        assertThat(result.data.exchangeRateInformation).isNotNull()
        assertThat(result.data.exchangeRateInformation.rateType).isNotNull()
        assertThat(result.data.exchangeRateInformation.unitCurrency).isNotNull()
        assertThat(result.data.exchangeRateInformation.exchangeRate).isNotNull()
    }

    @Disabled("Bug: https://github.com/SecureBankingAccessToolkit/SecureBankingAccessToolkit/issues/336")
    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.2",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsInternationalScheduledPaymentAlreadyExists_v3_1_2() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3()

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

        val paymentSubmissionRequest = OBWriteInternationalScheduled2().data(
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
                defaultMapper.writeValueAsString(paymentSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )
        val result = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse3>(
            payment3_1_2.Links.links.CreateInternationalScheduledPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp, OBVersion.v3_1_2
        )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteInternationalScheduledResponse3>(
                payment3_1_2.Links.links.CreateInternationalScheduledPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp, OBVersion.v3_1_2
            )
        }

        // Then
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.PAYMENT_SUBMISSION_ALREADY_EXISTS)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(403)

    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.2",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsSendInvalidFormatDetachedJws_v3_1_2() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3()

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

        val paymentSubmissionRequest = OBWriteInternationalScheduled2().data(
            OBWriteDataInternationalScheduled2()
                .consentId(patchedConsent.data.consentId)
                .initiation(
                    mapOBWriteInternationalScheduled2DataInitiationToOBWriteDataInternationalScheduled2(
                        patchedConsent.data.initiation
                    )
                )
        ).risk(patchedConsent.risk)

        // When
        val exception = assertThrows(AssertionError::class.java) {

            PaymentRS().submitPayment<OBWriteInternationalScheduledResponse3>(
                payment3_1_2.Links.links.CreateInternationalScheduledPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                    com.forgerock.sapi.gateway.ob.uk.framework.constants.INVALID_FORMAT_DETACHED_JWS,
                tppResource.tpp, OBVersion.v3_1_2
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.INVALID_FORMAT_DETACHED_JWS_ERROR)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.2",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsNoDetachedJws_v3_1_2() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3()

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

        val paymentSubmissionRequest = OBWriteInternationalScheduled2().data(
            OBWriteDataInternationalScheduled2()
                .consentId(patchedConsent.data.consentId)
                .initiation(
                    mapOBWriteInternationalScheduled2DataInitiationToOBWriteDataInternationalScheduled2(
                        patchedConsent.data.initiation
                    )
                )
        ).risk(patchedConsent.risk)

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPaymentNoDetachedJws<OBWriteInternationalScheduledResponse3>(
                payment3_1_2.Links.links.CreateInternationalScheduledPayment,
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
        apiVersion = "v3.1.2",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsSendInvalidKidDetachedJws_v3_1_2() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3()

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

        val paymentSubmissionRequest = OBWriteInternationalScheduled2().data(
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
                defaultMapper.writeValueAsString(paymentSubmissionRequest),
                tppResource.tpp.signingKey,
                    INVALID_SIGNING_KID,
                true
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteInternationalScheduledResponse3>(
                payment3_1_2.Links.links.CreateInternationalScheduledPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp, OBVersion.v3_1_2
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.2",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsB64ClaimMissingDetachedJws_v3_1_2() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3()

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

        val paymentSubmissionRequest = OBWriteInternationalScheduled2().data(
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
                defaultMapper.writeValueAsString(paymentSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteInternationalScheduledResponse3>(
                payment3_1_2.Links.links.CreateInternationalScheduledPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp, OBVersion.v3_1_2
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.2",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsB64ClaimShouldBeFalseDetachedJws_v3_1_2() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3()

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

        val paymentSubmissionRequest = OBWriteInternationalScheduled2().data(
            OBWriteDataInternationalScheduled2()
                .consentId(patchedConsent.data.consentId)
                .initiation(
                    mapOBWriteInternationalScheduled2DataInitiationToOBWriteDataInternationalScheduled2(
                        patchedConsent.data.initiation
                    )
                )
        ).risk(patchedConsent.risk)

        val signedPayload =
            signPayloadSubmitPaymentInvalidB64ClaimTrue(
                defaultMapper.writeValueAsString(paymentSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteInternationalScheduledResponse3>(
                payment3_1_2.Links.links.CreateInternationalScheduledPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp, OBVersion.v3_1_2
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.2",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBody_v3_1_2() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3()

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

        val paymentSubmissionRequest = OBWriteInternationalScheduled2().data(
            OBWriteDataInternationalScheduled2()
                .consentId(patchedConsent.data.consentId)
                .initiation(
                    mapOBWriteInternationalScheduled2DataInitiationToOBWriteDataInternationalScheduled2(
                        patchedConsent.data.initiation
                    )
                )
        ).risk(patchedConsent.risk)

        patchedConsent.data.consentId = com.forgerock.sapi.gateway.ob.uk.framework.constants.INVALID_CONSENT_ID
        val paymentSubmissionWithInvalidConsentId = OBWriteInternationalScheduled2().data(
            OBWriteDataInternationalScheduled2()
                .consentId(patchedConsent.data.consentId)
                .initiation(
                    mapOBWriteInternationalScheduled2DataInitiationToOBWriteDataInternationalScheduled2(
                        patchedConsent.data.initiation
                    )
                )
        ).risk(patchedConsent.risk)

        val signedPayload = signPayloadSubmitPayment(
            defaultMapper.writeValueAsString(paymentSubmissionWithInvalidConsentId),
            tppResource.tpp.signingKey,
            tppResource.tpp.signingKid
        )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteInternationalScheduledResponse3>(
                payment3_1_2.Links.links.CreateInternationalScheduledPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp,
                OBVersion.v3_1_2
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.2",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBody_v3_1_2() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3()

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

        val paymentSubmissionRequest = OBWriteInternationalScheduled2().data(
            OBWriteDataInternationalScheduled2()
                .consentId(patchedConsent.data.consentId)
                .initiation(
                    mapOBWriteInternationalScheduled2DataInitiationToOBWriteDataInternationalScheduled2(
                        patchedConsent.data.initiation
                    )
                )
        ).risk(patchedConsent.risk)

        patchedConsent.data.initiation.instructedAmount.amount = "123123"
        val paymentSubmissionInvalidAmount = OBWriteInternationalScheduled2().data(
            OBWriteDataInternationalScheduled2()
                .consentId(patchedConsent.data.consentId)
                .initiation(
                    mapOBWriteInternationalScheduled2DataInitiationToOBWriteDataInternationalScheduled2(
                        patchedConsent.data.initiation
                    )
                )
        ).risk(patchedConsent.risk)

        val signedPayload = signPayloadSubmitPayment(
            defaultMapper.writeValueAsString(paymentSubmissionInvalidAmount),
            tppResource.tpp.signingKey,
            tppResource.tpp.signingKid
        )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteInternationalScheduledResponse3>(
                payment3_1_2.Links.links.CreateInternationalScheduledPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp,
                OBVersion.v3_1_2
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun createInternationalScheduledPayment_rateType_AGREED_v3_1_1() {
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
        assertThat(consent.data.exchangeRateInformation).isNotNull()
        assertThat(consent.data.exchangeRateInformation.rateType).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.rateType)
        assertThat(consent.data.exchangeRateInformation.unitCurrency).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.unitCurrency)
        assertThat(consent.data.exchangeRateInformation.exchangeRate).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.exchangeRate)

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().authorizeConsent(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

        val patchedConsent = PaymentRS().getConsent<OBWriteInternationalScheduledConsentResponse3>(
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

        val paymentSubmissionRequest = OBWriteInternationalScheduled2().data(
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
                defaultMapper.writeValueAsString(paymentSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        // When
        val result = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse3>(
            payment3_1_1.Links.links.CreateInternationalScheduledPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1_1
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.consentId).isNotEmpty()
        if (consent.data.charges.isNullOrEmpty())
        {
            assertThat(result.data.charges).isNotEmpty()
        }
        assertThat(result.data.exchangeRateInformation.exchangeRate).isNotNull()
        assertThat(result.data.exchangeRateInformation).isNotNull()
        assertThat(result.data.exchangeRateInformation.rateType).isEqualTo(consent.data.initiation.exchangeRateInformation.rateType)
        assertThat(result.data.exchangeRateInformation.unitCurrency).isEqualTo(consent.data.initiation.exchangeRateInformation.unitCurrency)
        assertThat(result.data.exchangeRateInformation.exchangeRate).isEqualTo(consent.data.initiation.exchangeRateInformation.exchangeRate)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun createInternationalScheduledPayment_rateType_ACTUAL_v3_1_1() {
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
        assertThat(consent.data.exchangeRateInformation).isNotNull()
        assertThat(consent.data.exchangeRateInformation.rateType).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.rateType)
        assertThat(consent.data.exchangeRateInformation.unitCurrency).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.unitCurrency)
        assertThat(consent.data.exchangeRateInformation.exchangeRate).isNotNull()
        assertThat(consent.data.exchangeRateInformation.expirationDateTime).isNotNull()

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().authorizeConsent(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

        val patchedConsent = PaymentRS().getConsent<OBWriteInternationalScheduledConsentResponse3>(
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

        val paymentSubmissionRequest = OBWriteInternationalScheduled2().data(
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
                defaultMapper.writeValueAsString(paymentSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        // When
        val result = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse3>(
            payment3_1_1.Links.links.CreateInternationalScheduledPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1_1
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.consentId).isNotEmpty()
        if (consent.data.charges.isNullOrEmpty())
        {
            assertThat(result.data.charges).isNotEmpty()
        }
        assertThat(result.data.exchangeRateInformation).isNotNull()
        assertThat(result.data.exchangeRateInformation.rateType).isEqualTo(consent.data.initiation.exchangeRateInformation.rateType)
        assertThat(result.data.exchangeRateInformation.unitCurrency).isEqualTo(consent.data.initiation.exchangeRateInformation.unitCurrency)
        assertThat(result.data.exchangeRateInformation.exchangeRate).isNotNull()
        assertThat(result.data.exchangeRateInformation.expirationDateTime).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun createInternationalScheduledPayment_rateType_INDICATIVE_v3_1_1() {
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
        assertThat(consent.data.exchangeRateInformation).isNotNull()
        assertThat(consent.data.exchangeRateInformation.rateType).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.rateType)
        assertThat(consent.data.exchangeRateInformation.unitCurrency).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.unitCurrency)
        assertThat(consent.data.exchangeRateInformation.exchangeRate).isNotNull()

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().authorizeConsent(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

        val patchedConsent = PaymentRS().getConsent<OBWriteInternationalScheduledConsentResponse3>(
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

        val paymentSubmissionRequest = OBWriteInternationalScheduled2().data(
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
                defaultMapper.writeValueAsString(paymentSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        // When
        val result = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse3>(
            payment3_1_1.Links.links.CreateInternationalScheduledPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1_1
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.consentId).isNotEmpty()
        if (consent.data.charges.isNullOrEmpty())
        {
            assertThat(result.data.charges).isNotEmpty()
        }
        assertThat(result.data.exchangeRateInformation).isNotNull()
        assertThat(result.data.exchangeRateInformation.rateType).isEqualTo(consent.data.initiation.exchangeRateInformation.rateType)
        assertThat(result.data.exchangeRateInformation.unitCurrency).isEqualTo(consent.data.initiation.exchangeRateInformation.unitCurrency)
        assertThat(result.data.exchangeRateInformation.exchangeRate).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun createInternationalScheduledPayment_mandatoryFields_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3MandatoryFields()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse3>(
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
        assertThat(consent.data.exchangeRateInformation).isNotNull()
        assertThat(consent.data.exchangeRateInformation.rateType).isNotNull()
        assertThat(consent.data.exchangeRateInformation.unitCurrency).isNotNull()
        assertThat(consent.data.exchangeRateInformation.exchangeRate).isNotNull()

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().authorizeConsent(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

        val patchedConsent = PaymentRS().getConsent<OBWriteInternationalScheduledConsentResponse3>(
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

        val paymentSubmissionRequest = OBWriteInternationalScheduled2().data(
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
                defaultMapper.writeValueAsString(paymentSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        // When
        val result = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse3>(
            payment3_1_1.Links.links.CreateInternationalScheduledPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1_1
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.consentId).isNotEmpty()
        if (consent.data.charges.isNullOrEmpty())
        {
            assertThat(result.data.charges).isNotEmpty()
        }
        assertThat(result.data.exchangeRateInformation).isNotNull()
        assertThat(result.data.exchangeRateInformation.rateType).isNotNull()
        assertThat(result.data.exchangeRateInformation.unitCurrency).isNotNull()
        assertThat(result.data.exchangeRateInformation.exchangeRate).isNotNull()
    }

    @Disabled("Bug: https://github.com/SecureBankingAccessToolkit/SecureBankingAccessToolkit/issues/336")
    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsInternationalScheduledPaymentAlreadyExists_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse3>(
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

        val patchedConsent = PaymentRS().getConsent<OBWriteInternationalScheduledConsentResponse3>(
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

        val paymentSubmissionRequest = OBWriteInternationalScheduled2().data(
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
                defaultMapper.writeValueAsString(paymentSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )
        val result = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse3>(
            payment3_1_1.Links.links.CreateInternationalScheduledPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp, OBVersion.v3_1_1
        )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteInternationalScheduledResponse3>(
                payment3_1_1.Links.links.CreateInternationalScheduledPayment,
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
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsSendInvalidFormatDetachedJws_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse3>(
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

        val patchedConsent = PaymentRS().getConsent<OBWriteInternationalScheduledConsentResponse3>(
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

        val paymentSubmissionRequest = OBWriteInternationalScheduled2().data(
            OBWriteDataInternationalScheduled2()
                .consentId(patchedConsent.data.consentId)
                .initiation(
                    mapOBWriteInternationalScheduled2DataInitiationToOBWriteDataInternationalScheduled2(
                        patchedConsent.data.initiation
                    )
                )
        ).risk(patchedConsent.risk)

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteInternationalScheduledResponse3>(
                payment3_1_1.Links.links.CreateInternationalScheduledPayment,
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
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsNoDetachedJws_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse3>(
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

        val patchedConsent = PaymentRS().getConsent<OBWriteInternationalScheduledConsentResponse3>(
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

        val paymentSubmissionRequest = OBWriteInternationalScheduled2().data(
            OBWriteDataInternationalScheduled2()
                .consentId(patchedConsent.data.consentId)
                .initiation(
                    mapOBWriteInternationalScheduled2DataInitiationToOBWriteDataInternationalScheduled2(
                        patchedConsent.data.initiation
                    )
                )
        ).risk(patchedConsent.risk)

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPaymentNoDetachedJws<OBWriteInternationalScheduledResponse3>(
                payment3_1_1.Links.links.CreateInternationalScheduledPayment,
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
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsSendInvalidKidDetachedJws_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse3>(
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

        val patchedConsent = PaymentRS().getConsent<OBWriteInternationalScheduledConsentResponse3>(
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

        val paymentSubmissionRequest = OBWriteInternationalScheduled2().data(
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
                defaultMapper.writeValueAsString(paymentSubmissionRequest),
                tppResource.tpp.signingKey,
                    INVALID_SIGNING_KID
            )


        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteInternationalScheduledResponse3>(
                payment3_1_1.Links.links.CreateInternationalScheduledPayment,
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
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsB64ClaimMissingDetachedJws_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse3>(
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

        val patchedConsent = PaymentRS().getConsent<OBWriteInternationalScheduledConsentResponse3>(
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

        val paymentSubmissionRequest = OBWriteInternationalScheduled2().data(
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
                defaultMapper.writeValueAsString(paymentSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteInternationalScheduledResponse3>(
                payment3_1_1.Links.links.CreateInternationalScheduledPayment,
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
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsB64ClaimShouldBeFalseDetachedJws_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse3>(
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

        val patchedConsent = PaymentRS().getConsent<OBWriteInternationalScheduledConsentResponse3>(
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

        val paymentSubmissionRequest = OBWriteInternationalScheduled2().data(
            OBWriteDataInternationalScheduled2()
                .consentId(patchedConsent.data.consentId)
                .initiation(
                    mapOBWriteInternationalScheduled2DataInitiationToOBWriteDataInternationalScheduled2(
                        patchedConsent.data.initiation
                    )
                )
        ).risk(patchedConsent.risk)

        val signedPayload =
            signPayloadSubmitPaymentInvalidB64ClaimTrue(
                defaultMapper.writeValueAsString(paymentSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteInternationalScheduledResponse3>(
                payment3_1_1.Links.links.CreateInternationalScheduledPayment,
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
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBody_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse3>(
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

        val patchedConsent = PaymentRS().getConsent<OBWriteInternationalScheduledConsentResponse3>(
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

        val paymentSubmissionRequest = OBWriteInternationalScheduled2().data(
            OBWriteDataInternationalScheduled2()
                .consentId(patchedConsent.data.consentId)
                .initiation(
                    mapOBWriteInternationalScheduled2DataInitiationToOBWriteDataInternationalScheduled2(
                        patchedConsent.data.initiation
                    )
                )
        ).risk(patchedConsent.risk)

        patchedConsent.data.consentId = com.forgerock.sapi.gateway.ob.uk.framework.constants.INVALID_CONSENT_ID
        val paymentSubmissionWithInvalidConsentId = OBWriteInternationalScheduled2().data(
            OBWriteDataInternationalScheduled2()
                .consentId(patchedConsent.data.consentId)
                .initiation(
                    mapOBWriteInternationalScheduled2DataInitiationToOBWriteDataInternationalScheduled2(
                        patchedConsent.data.initiation
                    )
                )
        ).risk(patchedConsent.risk)

        val signedPayload = signPayloadSubmitPayment(
            defaultMapper.writeValueAsString(paymentSubmissionWithInvalidConsentId),
            tppResource.tpp.signingKey,
            tppResource.tpp.signingKid
        )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteInternationalScheduledResponse3>(
                payment3_1_1.Links.links.CreateInternationalScheduledPayment,
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
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBody_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse3>(
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

        val patchedConsent = PaymentRS().getConsent<OBWriteInternationalScheduledConsentResponse3>(
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

        val paymentSubmissionRequest = OBWriteInternationalScheduled2().data(
            OBWriteDataInternationalScheduled2()
                .consentId(patchedConsent.data.consentId)
                .initiation(
                    mapOBWriteInternationalScheduled2DataInitiationToOBWriteDataInternationalScheduled2(
                        patchedConsent.data.initiation
                    )
                )
        ).risk(patchedConsent.risk)

        patchedConsent.data.initiation.instructedAmount.amount = "123123"
        val paymentSubmissionInvalidAmount = OBWriteInternationalScheduled2().data(
            OBWriteDataInternationalScheduled2()
                .consentId(patchedConsent.data.consentId)
                .initiation(
                    mapOBWriteInternationalScheduled2DataInitiationToOBWriteDataInternationalScheduled2(
                        patchedConsent.data.initiation
                    )
                )
        ).risk(patchedConsent.risk)

        val signedPayload = signPayloadSubmitPayment(
            defaultMapper.writeValueAsString(paymentSubmissionInvalidAmount),
            tppResource.tpp.signingKey,
            tppResource.tpp.signingKid
        )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteInternationalScheduledResponse3>(
                payment3_1_1.Links.links.CreateInternationalScheduledPayment,
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
        apiVersion = "v3.1",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun createInternationalScheduledPayment_rateType_AGREED_v3_1() {
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
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.data.exchangeRateInformation).isNotNull()
        assertThat(consent.data.exchangeRateInformation.rateType).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.rateType)
        assertThat(consent.data.exchangeRateInformation.unitCurrency).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.unitCurrency)
        assertThat(consent.data.exchangeRateInformation.exchangeRate).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.exchangeRate)

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().authorizeConsent(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

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

        val paymentSubmissionRequest = OBWriteInternationalScheduled2().data(
            OBWriteDataInternationalScheduled2()
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
        val result = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse1>(
            payment3_1.Links.links.CreateInternationalScheduledPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.consentId).isNotEmpty()
        if (consent.data.charges.isNullOrEmpty())
        {
            assertThat(result.data.charges).isNotEmpty()
        }
        assertThat(result.data.exchangeRateInformation.exchangeRate).isNotNull()
        assertThat(result.data.exchangeRateInformation).isNotNull()
        assertThat(result.data.exchangeRateInformation.rateType).isEqualTo(consent.data.initiation.exchangeRateInformation.rateType)
        assertThat(result.data.exchangeRateInformation.unitCurrency).isEqualTo(consent.data.initiation.exchangeRateInformation.unitCurrency)
        assertThat(result.data.exchangeRateInformation.exchangeRate).isEqualTo(consent.data.initiation.exchangeRateInformation.exchangeRate)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun createInternationalScheduledPayment_rateType_ACTUAL_v3_1() {
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
        assertThat(consent.data.exchangeRateInformation).isNotNull()
        assertThat(consent.data.exchangeRateInformation.rateType).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.rateType)
        assertThat(consent.data.exchangeRateInformation.unitCurrency).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.unitCurrency)
        assertThat(consent.data.exchangeRateInformation.exchangeRate).isNotNull()
        assertThat(consent.data.exchangeRateInformation.expirationDateTime).isNotNull()

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().authorizeConsent(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

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

        val paymentSubmissionRequest = OBWriteInternationalScheduled2().data(
            OBWriteDataInternationalScheduled2()
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
        val result = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse1>(
            payment3_1.Links.links.CreateInternationalScheduledPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.consentId).isNotEmpty()
        if (consent.data.charges.isNullOrEmpty())
        {
            assertThat(result.data.charges).isNotEmpty()
        }
        assertThat(result.data.exchangeRateInformation).isNotNull()
        assertThat(result.data.exchangeRateInformation.rateType).isEqualTo(consent.data.initiation.exchangeRateInformation.rateType)
        assertThat(result.data.exchangeRateInformation.unitCurrency).isEqualTo(consent.data.initiation.exchangeRateInformation.unitCurrency)
        assertThat(result.data.exchangeRateInformation.exchangeRate).isNotNull()
        assertThat(result.data.exchangeRateInformation.expirationDateTime).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun createInternationalScheduledPayment_rateType_INDICATIVE_v3_1() {
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
        assertThat(consent.data.exchangeRateInformation).isNotNull()
        assertThat(consent.data.exchangeRateInformation.rateType).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.rateType)
        assertThat(consent.data.exchangeRateInformation.unitCurrency).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.unitCurrency)
        assertThat(consent.data.exchangeRateInformation.exchangeRate).isNotNull()

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().authorizeConsent(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

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

        val paymentSubmissionRequest = OBWriteInternationalScheduled2().data(
            OBWriteDataInternationalScheduled2()
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
        val result = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse1>(
            payment3_1.Links.links.CreateInternationalScheduledPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.consentId).isNotEmpty()
        if (consent.data.charges.isNullOrEmpty())
        {
            assertThat(result.data.charges).isNotEmpty()
        }
        assertThat(consent.data.exchangeRateInformation.rateType).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.rateType)
        assertThat(consent.data.exchangeRateInformation.unitCurrency).isEqualTo(consentRequest.data.initiation.exchangeRateInformation.unitCurrency)
        assertThat(consent.data.exchangeRateInformation.exchangeRate).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun createInternationalScheduledPayment_mandatoryFields_v3_1() {
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
        assertThat(consent.data.exchangeRateInformation).isNotNull()
        assertThat(consent.data.exchangeRateInformation.rateType).isNotNull()
        assertThat(consent.data.exchangeRateInformation.unitCurrency).isNotNull()
        assertThat(consent.data.exchangeRateInformation.exchangeRate).isNotNull()

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().authorizeConsent(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

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

        val paymentSubmissionRequest = OBWriteInternationalScheduled2().data(
            OBWriteDataInternationalScheduled2()
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
        val result = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse1>(
            payment3_1.Links.links.CreateInternationalScheduledPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.consentId).isNotEmpty()
        if (consent.data.charges.isNullOrEmpty())
        {
            assertThat(result.data.charges).isNotEmpty()
        }
        assertThat(result.data.exchangeRateInformation).isNotNull()
        assertThat(result.data.exchangeRateInformation.rateType).isNotNull()
        assertThat(result.data.exchangeRateInformation.unitCurrency).isNotNull()
        assertThat(result.data.exchangeRateInformation.exchangeRate).isNotNull()
    }

    @Disabled("Bug: https://github.com/SecureBankingAccessToolkit/SecureBankingAccessToolkit/issues/336")
    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsInternationalScheduledPaymentAlreadyExists_v3_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent2()

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

        val paymentSubmissionRequest = OBWriteInternationalScheduled2().data(
            OBWriteDataInternationalScheduled2()
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
        val result = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse1>(
            payment3_1.Links.links.CreateInternationalScheduledPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1
        )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteInternationalScheduledResponse1>(
                payment3_1.Links.links.CreateInternationalScheduledPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp,
                OBVersion.v3_1
            )
        }

        // Then
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.PAYMENT_SUBMISSION_ALREADY_EXISTS)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(403)

    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsSendInvalidFormatDetachedJws_v3_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent2()

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

        val paymentSubmissionRequest = OBWriteInternationalScheduled2().data(
            OBWriteDataInternationalScheduled2()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteInternationalScheduledResponse1>(
                payment3_1.Links.links.CreateInternationalScheduledPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                    com.forgerock.sapi.gateway.ob.uk.framework.constants.INVALID_FORMAT_DETACHED_JWS,
                tppResource.tpp,
                OBVersion.v3_1
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.INVALID_FORMAT_DETACHED_JWS_ERROR)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsNoDetachedJws_v3_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent2()

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

        val paymentSubmissionRequest = OBWriteInternationalScheduled2().data(
            OBWriteDataInternationalScheduled2()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().submitPaymentNoDetachedJws<OBWriteInternationalScheduledResponse1>(
                payment3_1.Links.links.CreateInternationalScheduledPayment,
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
        apiVersion = "v3.1",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsSendInvalidKidDetachedJws_v3_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent2()

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

        val paymentSubmissionRequest = OBWriteInternationalScheduled2().data(
            OBWriteDataInternationalScheduled2()
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
            PaymentRS().submitPayment<OBWriteInternationalScheduledResponse1>(
                payment3_1.Links.links.CreateInternationalScheduledPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp,
                OBVersion.v3_1
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsB64ClaimMissingDetachedJws_v3_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent2()

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

        val paymentSubmissionRequest = OBWriteInternationalScheduled2().data(
            OBWriteDataInternationalScheduled2()
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
            PaymentRS().submitPayment<OBWriteInternationalScheduledResponse1>(
                payment3_1.Links.links.CreateInternationalScheduledPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp,
                OBVersion.v3_1
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsB64ClaimShouldBeFalseDetachedJws_v3_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent2()

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

        val paymentSubmissionRequest = OBWriteInternationalScheduled2().data(
            OBWriteDataInternationalScheduled2()
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
            PaymentRS().submitPayment<OBWriteInternationalScheduledResponse1>(
                payment3_1.Links.links.CreateInternationalScheduledPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp,
                OBVersion.v3_1
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBody_v3_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent2()

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

        val paymentSubmissionRequest = OBWriteInternationalScheduled2().data(
            OBWriteDataInternationalScheduled2()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        patchedConsent.data.consentId = com.forgerock.sapi.gateway.ob.uk.framework.constants.INVALID_CONSENT_ID
        val paymentSubmissionWithInvalidConsentId = OBWriteInternationalScheduled2().data(
            OBWriteDataInternationalScheduled2()
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
            PaymentRS().submitPayment<OBWriteInternationalScheduledResponse1>(
                payment3_1.Links.links.CreateInternationalScheduledPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp,
                OBVersion.v3_1
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBody_v3_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent2()

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

        val paymentSubmissionRequest = OBWriteInternationalScheduled2().data(
            OBWriteDataInternationalScheduled2()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        patchedConsent.data.initiation.instructedAmount.amount = "123123"
        val paymentSubmissionInvalidAmount = OBWriteInternationalScheduled2().data(
            OBWriteDataInternationalScheduled2()
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
            PaymentRS().submitPayment<OBWriteInternationalScheduledResponse1>(
                payment3_1.Links.links.CreateInternationalScheduledPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp,
                OBVersion.v3_1
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
    }
}
