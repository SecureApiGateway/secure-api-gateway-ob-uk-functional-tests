package com.forgerock.uk.openbanking.tests.functional.payment.international.payments.consents.api.v3_1_8

import assertk.assertThat
import assertk.assertions.*
import com.forgerock.securebanking.framework.data.AccessToken
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.framework.errors.INVALID_CONSENT_STATUS
import com.forgerock.uk.openbanking.framework.errors.UNAUTHORIZED
import com.forgerock.uk.openbanking.support.discovery.getPaymentsApiLinks
import com.forgerock.uk.openbanking.support.payment.PaymentFactory
import com.github.kittinunf.fuel.core.FuelError
import uk.org.openbanking.datamodel.payment.*
import uk.org.openbanking.testsupport.payment.OBWriteInternationalConsentTestDataFactory.aValidOBWriteInternationalConsent5

class GetInternationalPaymentsConsentFundsConfirmation(
    val version: OBVersion,
    val tppResource: CreateTppCallback.TppResource
) {
    private val createInternationalPaymentsConsents = CreateInternationalPaymentsConsents(version, tppResource)
    private val paymentLinks = getPaymentsApiLinks(version)
    private val paymentApiClient = tppResource.tpp.paymentApiClient

    fun shouldGetInternationalPaymentConsentsFundsConfirmation_true_rateType_AGREED_Test() {
        // Given
        val consentRequest = aValidOBWriteInternationalConsent5()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.AGREED
        consentRequest.data.initiation.instructedAmount.amount("3")

        // When
        val result = createConsentAndGetFundsConfirmation(consentRequest)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isTrue()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    fun shouldGetInternationalPaymentConsentsFundsConfirmation_true_rateType_ACTUAL_Test() {
        // Given
        val consentRequest = aValidOBWriteInternationalConsent5()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.ACTUAL
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null
        consentRequest.data.initiation.instructedAmount.amount("3")

        // When
        val result = createConsentAndGetFundsConfirmation(consentRequest)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isTrue()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    fun shouldGetInternationalPaymentConsentsFundsConfirmation_true_rateType_INDICATIVE_Test() {
        // Given
        val consentRequest = aValidOBWriteInternationalConsent5()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.INDICATIVE
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null
        consentRequest.data.initiation.instructedAmount.amount("3")

        // When
        val result = createConsentAndGetFundsConfirmation(consentRequest)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isTrue()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    fun shouldGetInternationalPaymentConsentsFundsConfirmation_false_rateType_AGREED_Test() {
        // Given
        val consentRequest = aValidOBWriteInternationalConsent5()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.AGREED
        consentRequest.data.initiation.instructedAmount.amount("1000000")

        // When
        val result = createConsentAndGetFundsConfirmation(consentRequest)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isFalse()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    fun shouldGetInternationalPaymentConsentsFundsConfirmation_false_rateType_ACTUAL_Test() {
        // Given
        val consentRequest = aValidOBWriteInternationalConsent5()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.ACTUAL
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null
        consentRequest.data.initiation.instructedAmount.amount("1000000")

        // When
        val result = createConsentAndGetFundsConfirmation(consentRequest)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isFalse()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    fun shouldGetInternationalPaymentConsentsFundsConfirmation_false_rateType_INDICATIVE_Test() {
        // Given
        val consentRequest = aValidOBWriteInternationalConsent5()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.INDICATIVE
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null
        consentRequest.data.initiation.instructedAmount.amount("1000000")

        // When
        val result = createConsentAndGetFundsConfirmation(consentRequest)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isFalse()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    fun shouldGetInternationalPaymentConsentsFundsConfirmation_throwsWrongGrantType_Test() {
        // Given
        val consentRequest = aValidOBWriteInternationalConsent5()
        val (consent, _) = createInternationalPaymentsConsents.createInternationalPaymentConsentAndAuthorize(
            consentRequest
        )

        // client_credentials access token must not be allowed to get the funds confirmation
        val accessTokenClientCredentials = paymentApiClient.getClientCredentialsAccessToken(tppResource.tpp)

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            getFundsConfirmation(consent, accessTokenClientCredentials)
        }

        // Then
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
    }

    
    fun shouldGetInternationalPaymentConsentsFundsConfirmation_throwsInvalidConsentStatus_Test() {
        // Given
        val consentRequest = aValidOBWriteInternationalConsent5()
        val (consent, accessTokenAuthorizationCode) = createInternationalPaymentsConsents.createInternationalPaymentConsentAndAuthorize(
            consentRequest
        )
        val patchedConsent = createInternationalPaymentsConsents.getPatchedConsent(consent)
        val paymentSubmissionRequest = createPaymentRequest(patchedConsent)

        val payment: OBWriteInternationalResponse5 = paymentApiClient.submitPayment(
            paymentLinks.CreateInternationalPayment,
            accessTokenAuthorizationCode,
            paymentSubmissionRequest
        )

        //An ASPSP can only respond to a funds confirmation request if the international-payment-consent resource has an Authorised status.
        // If the status is not Authorised, an ASPSP must respond with a 400 (Bad Request) and a UK.OBIE.Resource.InvalidConsentStatus error code

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            getFundsConfirmation(consent, accessTokenAuthorizationCode)
        }

        // Then
        assertThat(exception.message.toString()).contains(INVALID_CONSENT_STATUS)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
    }

    private fun createConsentAndGetFundsConfirmation(consentRequest: OBWriteInternationalConsent5): OBWriteFundsConfirmationResponse1 {
        val (consent, accessTokenAuthorizationCode) = createInternationalPaymentsConsents.createInternationalPaymentConsentAndAuthorize(
            consentRequest
        )
        return getFundsConfirmation(consent, accessTokenAuthorizationCode)
    }

    private fun getFundsConfirmation(
        consent: OBWriteInternationalConsentResponse6,
        accessTokenAuthorizationCode: AccessToken
    ): OBWriteFundsConfirmationResponse1 {
        return paymentApiClient.sendGetRequest(
            PaymentFactory.urlWithConsentId(
                paymentLinks.GetInternationalPaymentConsentsConsentIdFundsConfirmation,
                consent.data.consentId
            ), accessTokenAuthorizationCode
        )
    }

    private fun createPaymentRequest(patchedConsent: OBWriteInternationalConsentResponse6): OBWriteInternational3 {
        return OBWriteInternational3().data(
            OBWriteInternational3Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)
    }
}