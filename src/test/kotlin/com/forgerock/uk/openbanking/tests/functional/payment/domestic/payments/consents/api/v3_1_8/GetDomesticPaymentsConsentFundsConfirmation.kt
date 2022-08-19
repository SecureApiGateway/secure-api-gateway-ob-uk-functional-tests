package com.forgerock.uk.openbanking.tests.functional.payment.domestic.payments.consents.api.v3_1_8

import assertk.assertThat
import assertk.assertions.*
import com.forgerock.securebanking.framework.conditions.Status
import com.forgerock.securebanking.framework.configuration.psu
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.framework.errors.UNAUTHORIZED
import com.forgerock.uk.openbanking.support.payment.PaymentAS
import com.forgerock.uk.openbanking.support.payment.PaymentFactory
import com.forgerock.uk.openbanking.support.payment.PaymentRS
import com.github.kittinunf.fuel.core.FuelError
import org.assertj.core.api.Assertions
import uk.org.openbanking.datamodel.payment.OBWriteFundsConfirmationResponse1
import uk.org.openbanking.testsupport.payment.OBWriteDomesticConsentTestDataFactory.aValidOBWriteDomesticConsent4

class GetDomesticPaymentsConsentFundsConfirmation(
    val version: OBVersion,
    val tppResource: CreateTppCallback.TppResource
) {

    private val createDomesticPaymentsConsentsApi = CreateDomesticPaymentsConsents(version, tppResource)

    fun shouldGetDomesticPaymentConsentsFundsConfirmation_false() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent4()
        consentRequest.data.initiation.instructedAmount.amount("1000000")
        // Given
        val consent = createDomesticPaymentsConsentsApi.createDomesticPaymentsConsent(consentRequest)

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
                createDomesticPaymentsConsentsApi.paymentLinks.GetDomesticPaymentConsentsConsentIdFundsConfirmation,
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

    fun shouldGetDomesticPaymentConsentsFundsConfirmation_throwsWrongGrantType() {
        // Given
        val consent = createDomesticPaymentsConsentsApi.createDomesticPaymentsConsent(aValidOBWriteDomesticConsent4())

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
                    createDomesticPaymentsConsentsApi.paymentLinks.GetDomesticPaymentConsentsConsentIdFundsConfirmation,
                    consent.data.consentId
                ),
                accessTokenClientCredentials
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
    }

    fun shouldGetDomesticPaymentConsentsFundsConfirmation_true() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent4()
        consentRequest.data.initiation.instructedAmount.amount("3")
        val consent = createDomesticPaymentsConsentsApi.createDomesticPaymentsConsent(consentRequest)

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
                createDomesticPaymentsConsentsApi.paymentLinks.GetDomesticPaymentConsentsConsentIdFundsConfirmation,
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
}