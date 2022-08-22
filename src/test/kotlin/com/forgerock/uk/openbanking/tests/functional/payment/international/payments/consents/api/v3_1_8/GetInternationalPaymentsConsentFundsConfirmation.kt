package com.forgerock.uk.openbanking.tests.functional.payment.international.payments.consents.api.v3_1_8

import assertk.assertThat
import assertk.assertions.*
import com.forgerock.securebanking.framework.conditions.Status
import com.forgerock.securebanking.framework.configuration.psu
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.http.fuel.defaultMapper
import com.forgerock.securebanking.framework.signature.signPayloadSubmitPayment
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.framework.errors.INVALID_CONSENT_STATUS
import com.forgerock.uk.openbanking.framework.errors.UNAUTHORIZED
import com.forgerock.uk.openbanking.support.payment.PaymentAS
import com.forgerock.uk.openbanking.support.payment.PaymentFactory
import com.forgerock.uk.openbanking.support.payment.PaymentRS
import com.github.kittinunf.fuel.core.FuelError
import org.assertj.core.api.Assertions
import uk.org.openbanking.datamodel.payment.OBExchangeRateType2Code
import uk.org.openbanking.datamodel.payment.OBWriteFundsConfirmationResponse1
import uk.org.openbanking.datamodel.payment.OBWriteInternationalConsentResponse6
import uk.org.openbanking.testsupport.payment.OBWriteInternationalConsentTestDataFactory.aValidOBWriteInternationalConsent5

class GetInternationalPaymentsConsentFundsConfirmation(
    val version: OBVersion,
    val tppResource: CreateTppCallback.TppResource
) {

    private val createInternationalPaymentsConsents = CreateInternationalPaymentsConsents(version, tppResource)

    fun shouldGetInternationalPaymentConsentsFundsConfirmation_true_rateType_AGREED_Test() {
        // Given
        val consentRequest = aValidOBWriteInternationalConsent5()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.AGREED
        consentRequest.data.initiation.instructedAmount.amount("3")

        val (consent, accessTokenAuthorizationCode) =
            createInternationalPaymentsConsents.createInternationalPaymentConsentAndGetAccessToken(consentRequest)

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()
        assertThat(consent.data.initiation.exchangeRateInformation.exchangeRate).isNotNull()

        // When
        val result = PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
            PaymentFactory.urlWithConsentId(
                createInternationalPaymentsConsents.paymentLinks.GetInternationalPaymentConsentsConsentIdFundsConfirmation,
                consent.data.consentId
            ),
            accessTokenAuthorizationCode
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.isFundsAvailable).isTrue()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    fun shouldGetInternationalPaymentConsentsFundsConfirmation_true_rateType_ACTUAL_Test() {
        // Given
        val consentRequest = aValidOBWriteInternationalConsent5()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.ACTUAL
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null
        consentRequest.data.initiation.instructedAmount.amount("3")

        val (consent, accessTokenAuthorizationCode) =
            createInternationalPaymentsConsents.createInternationalPaymentConsentAndGetAccessToken(consentRequest)

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()

        // When
        val result = PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
            PaymentFactory.urlWithConsentId(
                createInternationalPaymentsConsents.paymentLinks.GetInternationalPaymentConsentsConsentIdFundsConfirmation,
                consent.data.consentId
            ),
            accessTokenAuthorizationCode
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.isFundsAvailable).isTrue()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    fun shouldGetInternationalPaymentConsentsFundsConfirmation_true_rateType_INDICATIVE_Test() {
        // Given
        val consentRequest = aValidOBWriteInternationalConsent5()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.INDICATIVE
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null
        consentRequest.data.initiation.instructedAmount.amount("3")

        val (consent, accessTokenAuthorizationCode) =
            createInternationalPaymentsConsents.createInternationalPaymentConsentAndGetAccessToken(consentRequest)

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()

        // When
        val result = PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
            PaymentFactory.urlWithConsentId(
                createInternationalPaymentsConsents.paymentLinks.GetInternationalPaymentConsentsConsentIdFundsConfirmation,
                consent.data.consentId
            ),
            accessTokenAuthorizationCode
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.isFundsAvailable).isTrue()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    fun shouldGetInternationalPaymentConsentsFundsConfirmation_false_rateType_AGREED_Test() {
        // Given
        val consentRequest = aValidOBWriteInternationalConsent5()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.AGREED
        consentRequest.data.initiation.instructedAmount.amount("1000000")

        val (consent, accessTokenAuthorizationCode) =
            createInternationalPaymentsConsents.createInternationalPaymentConsentAndGetAccessToken(consentRequest)

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()
        assertThat(consent.data.initiation.exchangeRateInformation.exchangeRate).isNotNull()

        // When
        val result = PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
            PaymentFactory.urlWithConsentId(
                createInternationalPaymentsConsents.paymentLinks.GetInternationalPaymentConsentsConsentIdFundsConfirmation,
                consent.data.consentId
            ),
            accessTokenAuthorizationCode
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.isFundsAvailable).isFalse()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    fun shouldGetInternationalPaymentConsentsFundsConfirmation_false_rateType_ACTUAL_Test() {
        // Given
        val consentRequest = aValidOBWriteInternationalConsent5()
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

        val consent = PaymentRS().consent<OBWriteInternationalConsentResponse6>(
            createInternationalPaymentsConsents.paymentLinks.CreateInternationalPaymentConsent,
            consentRequest,
            tppResource.tpp,
            version,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

        // When
        val result = PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
            PaymentFactory.urlWithConsentId(
                createInternationalPaymentsConsents.paymentLinks.GetInternationalPaymentConsentsConsentIdFundsConfirmation,
                consent.data.consentId
            ),
            accessTokenAuthorizationCode
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.isFundsAvailable).isFalse()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    fun shouldGetInternationalPaymentConsentsFundsConfirmation_false_rateType_INDICATIVE_Test() {
        // Given
        val consentRequest = aValidOBWriteInternationalConsent5()
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

        val consent = PaymentRS().consent<OBWriteInternationalConsentResponse6>(
            createInternationalPaymentsConsents.paymentLinks.CreateInternationalPaymentConsent,
            consentRequest,
            tppResource.tpp,
            version,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

        // When
        val result = PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
            PaymentFactory.urlWithConsentId(
                createInternationalPaymentsConsents.paymentLinks.GetInternationalPaymentConsentsConsentIdFundsConfirmation,
                consent.data.consentId
            ),
            accessTokenAuthorizationCode
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.isFundsAvailable).isFalse()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    fun shouldGetInternationalPaymentConsentsFundsConfirmation_throwsWrongGrantType_Test() {
        // Given
        val consentRequest = aValidOBWriteInternationalConsent5()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteInternationalConsentResponse6>(
            createInternationalPaymentsConsents.paymentLinks.CreateInternationalPaymentConsent,
            consentRequest,
            tppResource.tpp,
            version,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()

        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentRS().getAccessToken(tppResource.tpp)

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
                PaymentFactory.urlWithConsentId(
                    createInternationalPaymentsConsents.paymentLinks.GetInternationalPaymentConsentsConsentIdFundsConfirmation,
                    consent.data.consentId
                ),
                accessTokenClientCredentials
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
    }

    fun shouldGetInternationalPaymentConsentsFundsConfirmation_throwsInvalidConsentStatus_Test() {
        // Given
        val consentRequest = aValidOBWriteInternationalConsent5()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteInternationalConsentResponse6>(
            createInternationalPaymentsConsents.paymentLinks.CreateInternationalPaymentConsent,
            consentRequest,
            tppResource.tpp,
            version,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
                PaymentFactory.urlWithConsentId(
                    createInternationalPaymentsConsents.paymentLinks.GetInternationalPaymentConsentsConsentIdFundsConfirmation,
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