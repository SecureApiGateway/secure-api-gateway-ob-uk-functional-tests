package com.forgerock.uk.openbanking.tests.functional.payment.international.scheduled.payments.consents.legacy

import assertk.assertThat
import assertk.assertions.*
import com.forgerock.securebanking.framework.conditions.Status
import com.forgerock.securebanking.framework.configuration.psu
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.framework.http.fuel.defaultMapper
import com.forgerock.securebanking.framework.signature.signPayloadSubmitPayment
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.framework.errors.INVALID_CONSENT_STATUS
import com.forgerock.uk.openbanking.framework.errors.UNAUTHORIZED
import com.forgerock.uk.openbanking.support.discovery.payment3_1_1
import com.forgerock.uk.openbanking.support.discovery.payment3_1_2
import com.forgerock.uk.openbanking.support.discovery.payment3_1_3
import com.forgerock.uk.openbanking.support.discovery.payment3_1_4
import com.forgerock.uk.openbanking.support.payment.PaymentAS
import com.forgerock.uk.openbanking.support.payment.PaymentFactory
import com.forgerock.uk.openbanking.support.payment.PaymentRS
import com.github.kittinunf.fuel.core.FuelError
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.payment.*
import uk.org.openbanking.testsupport.payment.OBWriteInternationalScheduledConsentTestDataFactory.*

@Disabled("Not implemented")
class LegacyGetInternationalScheduledPaymentsConsentFundsConfirmationTest(val tppResource: CreateTppCallback.TppResource) {
    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-scheduled-payment-consents"],
    )
    @Test
    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_true_rateType_AGREED_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent5()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.AGREED
        consentRequest.data.initiation.instructedAmount.amount("3")

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
        assertThat(consent.data.initiation.exchangeRateInformation.exchangeRate).isNotNull()

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().authorizeConsent(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

        // When
        val result = PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
            PaymentFactory.urlWithConsentId(
                payment3_1_4.Links.links.GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation,
                consent.data.consentId
            ),
            accessTokenAuthorizationCode
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isTrue()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-scheduled-payment-consents"],
    )
    @Test
    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_true_rateType_ACTUAL_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent5()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.ACTUAL
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null
        consentRequest.data.initiation.instructedAmount.amount("3")

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

        // When
        val result = PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
            PaymentFactory.urlWithConsentId(
                payment3_1_4.Links.links.GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation,
                consent.data.consentId
            ),
            accessTokenAuthorizationCode
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isTrue()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-scheduled-payment-consents"],
    )
    @Test
    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_true_rateType_INDICATIVE_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent5()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.INDICATIVE
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null
        consentRequest.data.initiation.instructedAmount.amount("3")

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

        // When
        val result = PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
            PaymentFactory.urlWithConsentId(
                payment3_1_4.Links.links.GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation,
                consent.data.consentId
            ),
            accessTokenAuthorizationCode
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isTrue()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-scheduled-payment-consents"],
    )
    @Test
    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_false_rateType_AGREED_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent5()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.AGREED
        consentRequest.data.initiation.instructedAmount.amount("1000000")

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
        assertThat(consent.data.initiation.exchangeRateInformation.exchangeRate).isNotNull()

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().authorizeConsent(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

        // When
        val result = PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
            PaymentFactory.urlWithConsentId(
                payment3_1_4.Links.links.GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation,
                consent.data.consentId
            ),
            accessTokenAuthorizationCode
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isFalse()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-scheduled-payment-consents"],
    )
    @Test
    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_false_rateType_ACTUAL_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent5()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.ACTUAL
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null
        consentRequest.data.initiation.instructedAmount.amount("1000000")

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

        // When
        val result = PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
            PaymentFactory.urlWithConsentId(
                payment3_1_4.Links.links.GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation,
                consent.data.consentId
            ),
            accessTokenAuthorizationCode
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isFalse()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-scheduled-payment-consents"],
    )
    @Test
    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_false_rateType_INDICATIVE_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent5()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.INDICATIVE
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null
        consentRequest.data.initiation.instructedAmount.amount("1000000")

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

        // When
        val result = PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
            PaymentFactory.urlWithConsentId(
                payment3_1_4.Links.links.GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation,
                consent.data.consentId
            ),
            accessTokenAuthorizationCode
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isFalse()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    @Disabled("Enhancement: https://github.com/SecureBankingAccessToolkit/SecureBankingAccessToolkit/issues/337")
    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-scheduled-payment-consents"],
    )
    @Test
    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_throwsWrongGrantType_v3_1_4() {
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

        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentRS().getClientCredentialsAccessToken(tppResource.tpp)

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
                PaymentFactory.urlWithConsentId(
                    payment3_1_4.Links.links.GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation,
                    consent.data.consentId
                ),
                accessTokenClientCredentials
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
    }

    
    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-scheduled-payment-consents"],
    )
    @Test
    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_throwsInvalidConsentStatus_v3_1_4() {
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

        val payment = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse5>(
            payment3_1_4.Links.links.CreateInternationalScheduledPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1_4
        )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
                PaymentFactory.urlWithConsentId(
                    payment3_1_4.Links.links.GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation,
                    consent.data.consentId
                ),
                accessTokenAuthorizationCode
            )
        }

        // Then
        assertThat(exception.message.toString()).contains(INVALID_CONSENT_STATUS)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_true_rateType_AGREED_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent4()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.AGREED
        consentRequest.data.initiation.instructedAmount.amount("3")

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
        assertThat(consent.data.initiation.exchangeRateInformation.exchangeRate).isNotNull()

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().authorizeConsent(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

        // When
        val result = PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
            PaymentFactory.urlWithConsentId(
                payment3_1_3.Links.links.GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation,
                consent.data.consentId
            ),
            accessTokenAuthorizationCode
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isTrue()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_true_rateType_ACTUAL_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent4()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.ACTUAL
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null
        consentRequest.data.initiation.instructedAmount.amount("3")

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

        // When
        val result = PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
            PaymentFactory.urlWithConsentId(
                payment3_1_3.Links.links.GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation,
                consent.data.consentId
            ),
            accessTokenAuthorizationCode
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isTrue()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_true_rateType_INDICATIVE_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent4()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.INDICATIVE
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null
        consentRequest.data.initiation.instructedAmount.amount("3")

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

        // When
        val result = PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
            PaymentFactory.urlWithConsentId(
                payment3_1_3.Links.links.GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation,
                consent.data.consentId
            ),
            accessTokenAuthorizationCode
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isTrue()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_false_rateType_AGREED_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent4()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.AGREED
        consentRequest.data.initiation.instructedAmount.amount("1000000")

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
        assertThat(consent.data.initiation.exchangeRateInformation.exchangeRate).isNotNull()

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().authorizeConsent(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

        // When
        val result = PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
            PaymentFactory.urlWithConsentId(
                payment3_1_3.Links.links.GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation,
                consent.data.consentId
            ),
            accessTokenAuthorizationCode
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isFalse()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_false_rateType_ACTUAL_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent4()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.ACTUAL
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null
        consentRequest.data.initiation.instructedAmount.amount("1000000")

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

        // When
        val result = PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
            PaymentFactory.urlWithConsentId(
                payment3_1_3.Links.links.GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation,
                consent.data.consentId
            ),
            accessTokenAuthorizationCode
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isFalse()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_false_rateType_INDICATIVE_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent4()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.INDICATIVE
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null
        consentRequest.data.initiation.instructedAmount.amount("1000000")

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

        // When
        val result = PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
            PaymentFactory.urlWithConsentId(
                payment3_1_3.Links.links.GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation,
                consent.data.consentId
            ),
            accessTokenAuthorizationCode
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isFalse()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    @Disabled("Enhancement: https://github.com/SecureBankingAccessToolkit/SecureBankingAccessToolkit/issues/337")
    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_throwsWrongGrantType_v3_1_3() {
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

        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentRS().getClientCredentialsAccessToken(tppResource.tpp)

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
                PaymentFactory.urlWithConsentId(
                    payment3_1_3.Links.links.GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation,
                    consent.data.consentId
                ),
                accessTokenClientCredentials
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
    }

    
    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-scheduled-payment-consents"],
    )
    @Test
    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_throwsInvalidConsentStatus_v3_1_3() {
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

        val payment = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse4>(
            payment3_1_3.Links.links.CreateInternationalScheduledPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp, OBVersion.v3_1_3
        )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
                PaymentFactory.urlWithConsentId(
                    payment3_1_3.Links.links.GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation,
                    consent.data.consentId
                ),
                accessTokenAuthorizationCode
            )
        }

        // Then
        assertThat(exception.message.toString()).contains(INVALID_CONSENT_STATUS)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
    }


    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.2",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_true_rateType_AGREED_v3_1_2() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.AGREED
        consentRequest.data.initiation.instructedAmount.amount("3")

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
        assertThat(consent.data.initiation.exchangeRateInformation.exchangeRate).isNotNull()

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().authorizeConsent(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

        // When
        val result = PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
            PaymentFactory.urlWithConsentId(
                payment3_1_2.Links.links.GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation,
                consent.data.consentId
            ),
            accessTokenAuthorizationCode
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isTrue()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.2",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_true_rateType_ACTUAL_v3_1_2() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.ACTUAL
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null
        consentRequest.data.initiation.instructedAmount.amount("3")

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

        // When
        val result = PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
            PaymentFactory.urlWithConsentId(
                payment3_1_2.Links.links.GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation,
                consent.data.consentId
            ),
            accessTokenAuthorizationCode
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isTrue()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.2",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_true_rateType_INDICATIVE_v3_1_2() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.INDICATIVE
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null
        consentRequest.data.initiation.instructedAmount.amount("3")

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

        // When
        val result = PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
            PaymentFactory.urlWithConsentId(
                payment3_1_2.Links.links.GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation,
                consent.data.consentId
            ),
            accessTokenAuthorizationCode
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isTrue()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.2",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_false_rateType_AGREED_v3_1_2() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.AGREED
        consentRequest.data.initiation.instructedAmount.amount("1000000")

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
        assertThat(consent.data.initiation.exchangeRateInformation.exchangeRate).isNotNull()

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().authorizeConsent(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

        // When
        val result = PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
            PaymentFactory.urlWithConsentId(
                payment3_1_2.Links.links.GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation,
                consent.data.consentId
            ),
            accessTokenAuthorizationCode
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isFalse()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.2",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_false_rateType_ACTUAL_v3_1_2() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.ACTUAL
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null
        consentRequest.data.initiation.instructedAmount.amount("1000000")

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

        // When
        val result = PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
            PaymentFactory.urlWithConsentId(
                payment3_1_2.Links.links.GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation,
                consent.data.consentId
            ),
            accessTokenAuthorizationCode
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isFalse()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.2",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_false_rateType_INDICATIVE_v3_1_2() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.INDICATIVE
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null
        consentRequest.data.initiation.instructedAmount.amount("1000000")

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

        // When
        val result = PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
            PaymentFactory.urlWithConsentId(
                payment3_1_2.Links.links.GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation,
                consent.data.consentId
            ),
            accessTokenAuthorizationCode
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isFalse()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    @Disabled("Enhancement: https://github.com/SecureBankingAccessToolkit/SecureBankingAccessToolkit/issues/337")
    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.2",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_throwsWrongGrantType_v3_1_2() {
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

        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentRS().getClientCredentialsAccessToken(tppResource.tpp)

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
                PaymentFactory.urlWithConsentId(
                    payment3_1_2.Links.links.GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation,
                    consent.data.consentId
                ),
                accessTokenClientCredentials
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
    }

    
    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.2",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-scheduled-payment-consents"],
    )
    @Test
    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_throwsInvalidConsentStatus_v3_1_2() {
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
                .initiation(PaymentFactory.mapOBWriteInternationalScheduled2DataInitiationToOBWriteDataInternationalScheduled2(patchedConsent.data.initiation))
        ).risk(patchedConsent.risk)

        val signedPayload =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(paymentSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        val payment = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse3>(
            payment3_1_2.Links.links.CreateInternationalScheduledPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp, OBVersion.v3_1_2
        )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
                PaymentFactory.urlWithConsentId(
                    payment3_1_2.Links.links.GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation,
                    consent.data.consentId
                ),
                accessTokenAuthorizationCode
            )
        }

        // Then
        assertThat(exception.message.toString()).contains(INVALID_CONSENT_STATUS)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1"]
    )
    @Test
    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_true_rateType_AGREED_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent2()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.AGREED
        consentRequest.data.initiation.instructedAmount.amount("3")

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
        assertThat(consent.data.initiation.exchangeRateInformation.exchangeRate).isNotNull()

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().authorizeConsent(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

        // When
        val result = PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
            PaymentFactory.urlWithConsentId(
                payment3_1_1.Links.links.GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation,
                consent.data.consentId
            ),
            accessTokenAuthorizationCode
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isTrue()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1"]
    )
    @Test
    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_true_rateType_ACTUAL_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent2()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.ACTUAL
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null
        consentRequest.data.initiation.instructedAmount.amount("3")

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

        // When
        val result = PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
            PaymentFactory.urlWithConsentId(
                payment3_1_1.Links.links.GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation,
                consent.data.consentId
            ),
            accessTokenAuthorizationCode
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isTrue()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1"]
    )
    @Test
    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_true_rateType_INDICATIVE_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent2()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.INDICATIVE
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null
        consentRequest.data.initiation.instructedAmount.amount("3")

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

        // When
        val result = PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
            PaymentFactory.urlWithConsentId(
                payment3_1_1.Links.links.GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation,
                consent.data.consentId
            ),
            accessTokenAuthorizationCode
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isTrue()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1"]
    )
    @Test
    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_false_rateType_AGREED_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent2()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.AGREED
        consentRequest.data.initiation.instructedAmount.amount("1000000")

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
        assertThat(consent.data.initiation.exchangeRateInformation.exchangeRate).isNotNull()

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().authorizeConsent(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

        // When
        val result = PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
            PaymentFactory.urlWithConsentId(
                payment3_1_1.Links.links.GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation,
                consent.data.consentId
            ),
            accessTokenAuthorizationCode
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isFalse()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1"]
    )
    @Test
    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_false_rateType_ACTUAL_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent2()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.ACTUAL
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null
        consentRequest.data.initiation.instructedAmount.amount("1000000")

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

        // When
        val result = PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
            PaymentFactory.urlWithConsentId(
                payment3_1_1.Links.links.GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation,
                consent.data.consentId
            ),
            accessTokenAuthorizationCode
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isFalse()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1"]
    )
    @Test
    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_false_rateType_INDICATIVE_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent2()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.INDICATIVE
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null
        consentRequest.data.initiation.instructedAmount.amount("1000000")

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

        // When
        val result = PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
            PaymentFactory.urlWithConsentId(
                payment3_1_1.Links.links.GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation,
                consent.data.consentId
            ),
            accessTokenAuthorizationCode
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isFalse()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    @Disabled("Enhancement: https://github.com/SecureBankingAccessToolkit/SecureBankingAccessToolkit/issues/337")
    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1"]
    )
    @Test
    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_throwsWrongGrantType_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent2()

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

        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentRS().getClientCredentialsAccessToken(tppResource.tpp)

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
                PaymentFactory.urlWithConsentId(
                    payment3_1_1.Links.links.GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation,
                    consent.data.consentId
                ),
                accessTokenClientCredentials
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
    }

    
    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1"]
    )
    @Test
    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_throwsInvalidConsentStatus_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent2()

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
                .initiation(PaymentFactory.mapOBWriteInternationalScheduled2DataInitiationToOBWriteDataInternationalScheduled2(patchedConsent.data.initiation))
        ).risk(patchedConsent.risk)

        val signedPayload =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(paymentSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        val payment = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse3>(
            payment3_1_1.Links.links.CreateInternationalScheduledPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1_1
        )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
                PaymentFactory.urlWithConsentId(
                    payment3_1_1.Links.links.GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation,
                    consent.data.consentId
                ),
                accessTokenAuthorizationCode
            )
        }

        // Then
        assertThat(exception.message.toString()).contains(INVALID_CONSENT_STATUS)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
    }

}
