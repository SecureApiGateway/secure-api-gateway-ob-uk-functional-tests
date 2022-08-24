package com.forgerock.uk.openbanking.tests.functional.payment.domestic.standing.order.consents.api.v3_1_8

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.securebanking.framework.conditions.Status
import com.forgerock.securebanking.framework.configuration.psu
import com.forgerock.securebanking.framework.data.AccessToken
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.framework.constants.INVALID_FREQUENCY
import com.forgerock.uk.openbanking.framework.errors.INVALID_FORMAT_DETACHED_JWS_ERROR
import com.forgerock.uk.openbanking.framework.errors.INVALID_FREQUENCY_VALUE
import com.forgerock.uk.openbanking.framework.errors.NO_DETACHED_JWS
import com.forgerock.uk.openbanking.framework.errors.UNAUTHORIZED
import com.forgerock.uk.openbanking.support.discovery.getPaymentsApiLinks
import com.forgerock.uk.openbanking.support.payment.BadJwsSignatureProducer
import com.forgerock.uk.openbanking.support.payment.DefaultJwsSignatureProducer
import com.forgerock.uk.openbanking.support.payment.InvalidKidJwsSignatureProducer
import com.forgerock.uk.openbanking.support.payment.PaymentAS
import com.forgerock.uk.openbanking.support.payment.defaultPaymentScopesForAccessToken
import com.github.kittinunf.fuel.core.FuelError
import org.assertj.core.api.Assertions
import uk.org.openbanking.datamodel.payment.OBWriteDomesticStandingOrderConsent5
import uk.org.openbanking.datamodel.payment.OBWriteDomesticStandingOrderConsentResponse6
import uk.org.openbanking.testsupport.payment.OBWriteDomesticStandingOrderConsentTestDataFactory

class CreateDomesticStandingOrderConsents(val version: OBVersion, val tppResource: CreateTppCallback.TppResource) {

    val paymentLinks = getPaymentsApiLinks(version)
    private val paymentApiClient = tppResource.tpp.paymentApiClient

    fun createDomesticStandingOrdersConsentsTest() {
        // Given
        val consentRequest =
            OBWriteDomesticStandingOrderConsentTestDataFactory.aValidOBWriteDomesticStandingOrderConsent5()
        val consent = createDomesticStandingOrderConsent(consentRequest)

        // Then
        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()
    }

    fun createDomesticStandingOrdersConsents_mandatoryFields() {
        // Given
        val consentRequest =
            OBWriteDomesticStandingOrderConsentTestDataFactory.aValidOBWriteDomesticStandingOrderConsent5MandatoryFields()
        val consent = createDomesticStandingOrderConsent(consentRequest)

        // Then
        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()
    }

    fun shouldCreateDomesticStandingOrdersConsents_throwsInvalidFrequencyValue() {
        // Given
        val consentRequest =
            OBWriteDomesticStandingOrderConsentTestDataFactory.aValidOBWriteDomesticStandingOrderConsent5()
        consentRequest.data.initiation.frequency = INVALID_FREQUENCY

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            createDomesticStandingOrderConsent(consentRequest)
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(INVALID_FREQUENCY_VALUE)
    }

    fun shouldCreateDomesticStandingOrdersConsents_throwsSendInvalidFormatDetachedJwsTest() {
        // Given
        val consentRequest =
            OBWriteDomesticStandingOrderConsentTestDataFactory.aValidOBWriteDomesticStandingOrderConsent5()

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            buildCreateConsentRequest(consentRequest).configureJwsSignatureProducer(BadJwsSignatureProducer()).sendRequest()
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(INVALID_FORMAT_DETACHED_JWS_ERROR)
    }

    fun shouldCreateDomesticStandingOrdersConsents_throwsNoDetachedJws() {
        // Given
        val consentRequest =
            OBWriteDomesticStandingOrderConsentTestDataFactory.aValidOBWriteDomesticStandingOrderConsent5()

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            buildCreateConsentRequest(consentRequest).configureJwsSignatureProducer(null).sendRequest()
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(NO_DETACHED_JWS)
    }

    fun shouldCreateDomesticStandingOrdersConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJwsTest() {
        // Given
        val consentRequest =
            OBWriteDomesticStandingOrderConsentTestDataFactory.aValidOBWriteDomesticStandingOrderConsent5()

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            buildCreateConsentRequest(consentRequest).configureJwsSignatureProducer(DefaultJwsSignatureProducer(tppResource.tpp, false)).sendRequest()
        }

        // Then
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
    }

    fun shouldCreateDomesticStandingOrdersConsents_throwsSendInvalidKidDetachedJwsTest() {
        // Given
        val consentRequest =
            OBWriteDomesticStandingOrderConsentTestDataFactory.aValidOBWriteDomesticStandingOrderConsent5()

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            buildCreateConsentRequest(consentRequest).configureJwsSignatureProducer(InvalidKidJwsSignatureProducer(tppResource.tpp)).sendRequest()
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
    }

    fun createDomesticStandingOrderConsent(consentRequest: OBWriteDomesticStandingOrderConsent5): OBWriteDomesticStandingOrderConsentResponse6 {
        return buildCreateConsentRequest(consentRequest).sendRequest()
    }

    private fun buildCreateConsentRequest(
        consent: OBWriteDomesticStandingOrderConsent5
    ) = paymentApiClient.createDefaultPostRequest(
        paymentLinks.CreateDomesticStandingOrderConsent,
        tppResource.tpp.getClientCredentialsAccessToken(defaultPaymentScopesForAccessToken),
        consent
    )

    fun createDomesticStandingOrderConsentAndGetAccessToken(consentRequest: OBWriteDomesticStandingOrderConsent5): Pair<OBWriteDomesticStandingOrderConsentResponse6, AccessToken> {
        val consent = createDomesticStandingOrderConsent(consentRequest)
        val accessTokenAuthorizationCode = PaymentAS().authorizeConsent(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        return consent to accessTokenAuthorizationCode
    }
}