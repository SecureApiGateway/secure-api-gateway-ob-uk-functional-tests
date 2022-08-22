package com.forgerock.uk.openbanking.tests.functional.payment.international.payments.api.v3_1_8

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.securebanking.framework.conditions.Status
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.http.fuel.defaultMapper
import com.forgerock.securebanking.framework.signature.signPayloadSubmitPayment
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.framework.constants.INVALID_CONSENT_ID
import com.forgerock.uk.openbanking.framework.constants.INVALID_FORMAT_DETACHED_JWS
import com.forgerock.uk.openbanking.framework.constants.INVALID_SIGNING_KID
import com.forgerock.uk.openbanking.framework.errors.INVALID_FORMAT_DETACHED_JWS_ERROR
import com.forgerock.uk.openbanking.framework.errors.NO_DETACHED_JWS
import com.forgerock.uk.openbanking.framework.errors.PAYMENT_SUBMISSION_ALREADY_EXISTS
import com.forgerock.uk.openbanking.framework.errors.UNAUTHORIZED
import com.forgerock.uk.openbanking.support.payment.PaymentFactory
import com.forgerock.uk.openbanking.support.payment.PaymentRS
import com.forgerock.uk.openbanking.tests.functional.payment.international.payments.consents.api.v3_1_8.CreateInternationalPaymentsConsents
import com.github.kittinunf.fuel.core.FuelError
import org.assertj.core.api.Assertions
import uk.org.openbanking.datamodel.payment.*
import uk.org.openbanking.testsupport.payment.OBWriteInternationalConsentTestDataFactory

class CreateInternationalPayment(
    val version: OBVersion,
    val tppResource: CreateTppCallback.TppResource
) {

    private val createInternationalPaymentsConsents = CreateInternationalPaymentsConsents(version, tppResource)

    fun createInternationalPayment_rateType_AGREED_Test() {
        // Given
        val consentRequest = OBWriteInternationalConsentTestDataFactory.aValidOBWriteInternationalConsent5()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.AGREED
        val (consent, accessTokenAuthorizationCode) =
            createInternationalPaymentsConsents.createInternationalPaymentConsentAndGetAccessToken(consentRequest)

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        val patchedConsent = PaymentRS().getConsent<OBWriteInternationalConsentResponse6>(
            PaymentFactory.urlWithConsentId(
                createInternationalPaymentsConsents.paymentLinks.GetInternationalPaymentConsent,
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

        val paymentSubmissionRequest = OBWriteInternational3().data(
            OBWriteInternational3Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        val signedPayload = signPayloadSubmitPayment(
            defaultMapper.writeValueAsString(paymentSubmissionRequest),
            tppResource.tpp.signingKey,
            tppResource.tpp.signingKid
        )

        // When
        val result = PaymentRS().submitPayment<OBWriteInternationalResponse5>(
            createInternationalPaymentsConsents.paymentLinks.CreateInternationalPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            version
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.consentId).isNotEmpty()
        assertThat(result.data.exchangeRateInformation.exchangeRate).isNotNull()
    }

    fun createInternationalPayment_rateType_ACTUAL_Test() {
        // Given
        val consentRequest = OBWriteInternationalConsentTestDataFactory.aValidOBWriteInternationalConsent5()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.ACTUAL
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null

        val (consent, accessTokenAuthorizationCode) =
            createInternationalPaymentsConsents.createInternationalPaymentConsentAndGetAccessToken(consentRequest)

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        val patchedConsent = PaymentRS().getConsent<OBWriteInternationalConsentResponse6>(
            PaymentFactory.urlWithConsentId(
                createInternationalPaymentsConsents.paymentLinks.GetInternationalPaymentConsent,
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

        val paymentSubmissionRequest = OBWriteInternational3().data(
            OBWriteInternational3Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        val signedPayload = signPayloadSubmitPayment(
            defaultMapper.writeValueAsString(paymentSubmissionRequest),
            tppResource.tpp.signingKey,
            tppResource.tpp.signingKid
        )

        // When
        val result = PaymentRS().submitPayment<OBWriteInternationalResponse5>(
            createInternationalPaymentsConsents.paymentLinks.CreateInternationalPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            version
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.consentId).isNotEmpty()
    }

    fun createInternationalPayment_rateType_INDICATIVE_Test() {
        // Given
        val consentRequest = OBWriteInternationalConsentTestDataFactory.aValidOBWriteInternationalConsent5()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.INDICATIVE
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null

        val (consent, accessTokenAuthorizationCode) =
            createInternationalPaymentsConsents.createInternationalPaymentConsentAndGetAccessToken(consentRequest)

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        val patchedConsent = PaymentRS().getConsent<OBWriteInternationalConsentResponse6>(
            PaymentFactory.urlWithConsentId(
                createInternationalPaymentsConsents.paymentLinks.GetInternationalPaymentConsent,
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

        val paymentSubmissionRequest = OBWriteInternational3().data(
            OBWriteInternational3Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        val signedPayload = signPayloadSubmitPayment(
            defaultMapper.writeValueAsString(paymentSubmissionRequest),
            tppResource.tpp.signingKey,
            tppResource.tpp.signingKid
        )

        // When
        val result = PaymentRS().submitPayment<OBWriteInternationalResponse5>(
            createInternationalPaymentsConsents.paymentLinks.CreateInternationalPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            version
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.consentId).isNotEmpty()
    }

    fun createInternationalPayment_mandatoryFields_Test() {
        // Given
        val consentRequest =
            OBWriteInternationalConsentTestDataFactory.aValidOBWriteInternationalConsent5MandatoryFields()

        val (consent, accessTokenAuthorizationCode) =
            createInternationalPaymentsConsents.createInternationalPaymentConsentAndGetAccessToken(consentRequest)

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        val patchedConsent = PaymentRS().getConsent<OBWriteInternationalConsentResponse6>(
            PaymentFactory.urlWithConsentId(
                createInternationalPaymentsConsents.paymentLinks.GetInternationalPaymentConsent,
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

        val paymentSubmissionRequest = OBWriteInternational3().data(
            OBWriteInternational3Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        val signedPayload = signPayloadSubmitPayment(
            defaultMapper.writeValueAsString(paymentSubmissionRequest),
            tppResource.tpp.signingKey,
            tppResource.tpp.signingKid
        )

        // When
        val result = PaymentRS().submitPayment<OBWriteInternationalResponse5>(
            createInternationalPaymentsConsents.paymentLinks.CreateInternationalPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            version
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.consentId).isNotEmpty()
    }

    fun shouldCreateInternationalPayment_throwsInternationalPaymentAlreadyExists_Test() {
        // Given
        val consentRequest = OBWriteInternationalConsentTestDataFactory.aValidOBWriteInternationalConsent5()

        val (consent, accessTokenAuthorizationCode) =
            createInternationalPaymentsConsents.createInternationalPaymentConsentAndGetAccessToken(consentRequest)

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        val patchedConsent = PaymentRS().getConsent<OBWriteInternationalConsentResponse6>(
            PaymentFactory.urlWithConsentId(
                createInternationalPaymentsConsents.paymentLinks.GetInternationalPaymentConsent,
                consent.data.consentId
            ),
            tppResource.tpp
        )

        assertThat(patchedConsent).isNotNull()
        assertThat(patchedConsent.data).isNotNull()
        assertThat(patchedConsent.risk).isNotNull()
        assertThat(patchedConsent.data.consentId).isNotEmpty()
        Assertions.assertThat(patchedConsent.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = OBWriteInternational3().data(
            OBWriteInternational3Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        val signedPayload = signPayloadSubmitPayment(
            defaultMapper.writeValueAsString(paymentSubmissionRequest),
            tppResource.tpp.signingKey,
            tppResource.tpp.signingKid
        )
        val result = PaymentRS().submitPayment<OBWriteInternationalResponse5>(
            createInternationalPaymentsConsents.paymentLinks.CreateInternationalPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            version
        )

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteInternationalResponse5>(
                createInternationalPaymentsConsents.paymentLinks.CreateInternationalPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp,
                version
            )
        }

        // Then
        assertThat(exception.message.toString()).contains(PAYMENT_SUBMISSION_ALREADY_EXISTS)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(403)
    }

    fun shouldCreateInternationalPayment_throwsSendInvalidFormatDetachedJws_Test() {
        // Given
        val consentRequest = OBWriteInternationalConsentTestDataFactory.aValidOBWriteInternationalConsent5()
        val (consent, accessTokenAuthorizationCode) =
            createInternationalPaymentsConsents.createInternationalPaymentConsentAndGetAccessToken(consentRequest)

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        val patchedConsent = PaymentRS().getConsent<OBWriteInternationalConsentResponse6>(
            PaymentFactory.urlWithConsentId(
                createInternationalPaymentsConsents.paymentLinks.GetInternationalPaymentConsent,
                consent.data.consentId
            ),
            tppResource.tpp
        )

        assertThat(patchedConsent).isNotNull()
        assertThat(patchedConsent.data).isNotNull()
        assertThat(patchedConsent.risk).isNotNull()
        assertThat(patchedConsent.data.consentId).isNotEmpty()
        Assertions.assertThat(patchedConsent.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = OBWriteInternational3().data(
            OBWriteInternational3Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteInternationalResponse5>(
                createInternationalPaymentsConsents.paymentLinks.CreateInternationalPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                INVALID_FORMAT_DETACHED_JWS,
                tppResource.tpp,
                version
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(INVALID_FORMAT_DETACHED_JWS_ERROR)
    }

    fun shouldCreateInternationalPayment_throwsNoDetachedJws_Test() {
        // Given
        val consentRequest = OBWriteInternationalConsentTestDataFactory.aValidOBWriteInternationalConsent5()
        val (consent, accessTokenAuthorizationCode) =
            createInternationalPaymentsConsents.createInternationalPaymentConsentAndGetAccessToken(consentRequest)

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        val patchedConsent = PaymentRS().getConsent<OBWriteInternationalConsentResponse6>(
            PaymentFactory.urlWithConsentId(
                createInternationalPaymentsConsents.paymentLinks.GetInternationalPaymentConsent,
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

        val paymentSubmissionRequest = OBWriteInternational3().data(
            OBWriteInternational3Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            PaymentRS().submitPaymentNoDetachedJws<OBWriteInternationalResponse5>(
                createInternationalPaymentsConsents.paymentLinks.CreateInternationalPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(NO_DETACHED_JWS)
    }

    fun shouldCreateInternationalPayment_throwsNotPermittedB64HeaderAddedInTheDetachedJws_Test() {
        // Given
        val consentRequest = OBWriteInternationalConsentTestDataFactory.aValidOBWriteInternationalConsent5()
        val (consent, accessTokenAuthorizationCode) =
            createInternationalPaymentsConsents.createInternationalPaymentConsentAndGetAccessToken(consentRequest)

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        val patchedConsent = PaymentRS().getConsent<OBWriteInternationalConsentResponse6>(
            PaymentFactory.urlWithConsentId(
                createInternationalPaymentsConsents.paymentLinks.GetInternationalPaymentConsent,
                consent.data.consentId
            ),
            tppResource.tpp
        )

        assertThat(patchedConsent).isNotNull()
        assertThat(patchedConsent.data).isNotNull()
        assertThat(patchedConsent.risk).isNotNull()
        assertThat(patchedConsent.data.consentId).isNotEmpty()
        Assertions.assertThat(patchedConsent.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = OBWriteInternational3().data(
            OBWriteInternational3Data()
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
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteInternationalResponse5>(
                createInternationalPaymentsConsents.paymentLinks.CreateInternationalPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp,
                version
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
    }

    fun shouldCreateInternationalPayment_throwsSendInvalidKidDetachedJws_Test() {
        // Given
        val consentRequest = OBWriteInternationalConsentTestDataFactory.aValidOBWriteInternationalConsent5()
        val (consent, accessTokenAuthorizationCode) =
            createInternationalPaymentsConsents.createInternationalPaymentConsentAndGetAccessToken(consentRequest)

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        val patchedConsent = PaymentRS().getConsent<OBWriteInternationalConsentResponse6>(
            PaymentFactory.urlWithConsentId(
                createInternationalPaymentsConsents.paymentLinks.GetInternationalPaymentConsent,
                consent.data.consentId
            ),
            tppResource.tpp
        )

        assertThat(patchedConsent).isNotNull()
        assertThat(patchedConsent.data).isNotNull()
        assertThat(patchedConsent.risk).isNotNull()
        assertThat(patchedConsent.data.consentId).isNotEmpty()
        Assertions.assertThat(patchedConsent.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = OBWriteInternational3().data(
            OBWriteInternational3Data()
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
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteInternationalResponse5>(
                createInternationalPaymentsConsents.paymentLinks.CreateInternationalPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp,
                version
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
    }

    fun shouldCreateInternationalPayment_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBody_Test() {
        // Given
        val consentRequest = OBWriteInternationalConsentTestDataFactory.aValidOBWriteInternationalConsent5()
        val (consent, accessTokenAuthorizationCode) =
            createInternationalPaymentsConsents.createInternationalPaymentConsentAndGetAccessToken(consentRequest)

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        val patchedConsent = PaymentRS().getConsent<OBWriteInternationalConsentResponse6>(
            PaymentFactory.urlWithConsentId(
                createInternationalPaymentsConsents.paymentLinks.GetInternationalPaymentConsent,
                consent.data.consentId
            ),
            tppResource.tpp
        )

        assertThat(patchedConsent).isNotNull()
        assertThat(patchedConsent.data).isNotNull()
        assertThat(patchedConsent.risk).isNotNull()
        assertThat(patchedConsent.data.consentId).isNotEmpty()
        Assertions.assertThat(patchedConsent.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = OBWriteInternational3().data(
            OBWriteInternational3Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        patchedConsent.data.consentId = INVALID_CONSENT_ID
        val paymentSubmissionWithInvalidConsentId = OBWriteInternational3().data(
            OBWriteInternational3Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        val signedPayload = signPayloadSubmitPayment(
            defaultMapper.writeValueAsString(paymentSubmissionWithInvalidConsentId),
            tppResource.tpp.signingKey,
            tppResource.tpp.signingKid
        )

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteInternationalResponse5>(
                createInternationalPaymentsConsents.paymentLinks.CreateInternationalPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp,
                version
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
    }

    fun shouldCreateInternationalPayment_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBody_Test() {
        // Given
        val consentRequest = OBWriteInternationalConsentTestDataFactory.aValidOBWriteInternationalConsent5()
        val (consent, accessTokenAuthorizationCode) =
            createInternationalPaymentsConsents.createInternationalPaymentConsentAndGetAccessToken(consentRequest)

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        val patchedConsent = PaymentRS().getConsent<OBWriteInternationalConsentResponse6>(
            PaymentFactory.urlWithConsentId(
                createInternationalPaymentsConsents.paymentLinks.GetInternationalPaymentConsent,
                consent.data.consentId
            ),
            tppResource.tpp
        )

        assertThat(patchedConsent).isNotNull()
        assertThat(patchedConsent.data).isNotNull()
        assertThat(patchedConsent.risk).isNotNull()
        assertThat(patchedConsent.data.consentId).isNotEmpty()
        Assertions.assertThat(patchedConsent.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = OBWriteInternational3().data(
            OBWriteInternational3Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(PaymentFactory.copyOBWriteInternational3DataInitiation(patchedConsent.data.initiation))
        ).risk(patchedConsent.risk)

        patchedConsent.data.initiation.instructedAmount.amount = "123123"
        val paymentSubmissionInvalidAmount = OBWriteInternational3().data(
            OBWriteInternational3Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        val signedPayload = signPayloadSubmitPayment(
            defaultMapper.writeValueAsString(paymentSubmissionInvalidAmount),
            tppResource.tpp.signingKey,
            tppResource.tpp.signingKid
        )

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            PaymentRS().submitPayment<OBWriteInternationalResponse5>(
                createInternationalPaymentsConsents.paymentLinks.CreateInternationalPayment,
                paymentSubmissionRequest,
                accessTokenAuthorizationCode,
                signedPayload,
                tppResource.tpp,
                version
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
    }
}