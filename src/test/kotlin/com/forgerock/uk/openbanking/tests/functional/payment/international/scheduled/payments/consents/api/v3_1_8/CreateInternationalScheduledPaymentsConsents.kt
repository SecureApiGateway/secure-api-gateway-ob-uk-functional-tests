package com.forgerock.uk.openbanking.tests.functional.payment.international.scheduled.payments.consents.api.v3_1_8

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.securebanking.framework.conditions.Status
import com.forgerock.securebanking.framework.configuration.psu
import com.forgerock.securebanking.framework.data.AccessToken
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.http.fuel.defaultMapper
import com.forgerock.securebanking.framework.signature.signPayloadSubmitPayment
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.framework.constants.INVALID_FORMAT_DETACHED_JWS
import com.forgerock.uk.openbanking.framework.constants.INVALID_SIGNING_KID
import com.forgerock.uk.openbanking.framework.errors.INVALID_FORMAT_DETACHED_JWS_ERROR
import com.forgerock.uk.openbanking.framework.errors.NO_DETACHED_JWS
import com.forgerock.uk.openbanking.framework.errors.REQUEST_EXECUTION_TIME_IN_THE_PAST
import com.forgerock.uk.openbanking.framework.errors.UNAUTHORIZED
import com.forgerock.uk.openbanking.support.discovery.getPaymentsApiLinks
import com.forgerock.uk.openbanking.support.payment.BadJwsSignatureProducer
import com.forgerock.uk.openbanking.support.payment.DefaultJwsSignatureProducer
import com.forgerock.uk.openbanking.support.payment.InvalidKidJwsSignatureProducer
import com.forgerock.uk.openbanking.support.payment.PaymentAS
import com.forgerock.uk.openbanking.support.payment.PaymentRS
import com.forgerock.uk.openbanking.support.payment.defaultPaymentScopesForAccessToken
import com.github.kittinunf.fuel.core.FuelError
import org.assertj.core.api.Assertions
import org.joda.time.DateTime
import uk.org.openbanking.datamodel.payment.OBWriteInternationalConsent5
import uk.org.openbanking.datamodel.payment.OBWriteInternationalScheduledConsent5
import uk.org.openbanking.datamodel.payment.OBWriteInternationalScheduledConsentResponse6
import uk.org.openbanking.testsupport.payment.OBWriteInternationalScheduledConsentTestDataFactory

class CreateInternationalScheduledPaymentsConsents(
    val version: OBVersion,
    val tppResource: CreateTppCallback.TppResource
) {

    val paymentLinks = getPaymentsApiLinks(version)
    private val paymentApiClient = tppResource.tpp.paymentApiClient

    fun createInternationalScheduledPaymentsConsentsTest() {
        // Given
        val consentRequest =
            OBWriteInternationalScheduledConsentTestDataFactory.aValidOBWriteInternationalScheduledConsent5()
        val consent = createInternationalScheduledPaymentConsent(consentRequest)

        // Then
        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()
    }

    fun createInternationalScheduledPaymentsConsents_mandatoryFields_Test() {
        // Given
        val consentRequest =
            OBWriteInternationalScheduledConsentTestDataFactory.aValidOBWriteInternationalScheduledConsent5MandatoryFields()
        val consent = createInternationalScheduledPaymentConsent(consentRequest)

        // Then
        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()
    }

    fun shouldCreateInternationalScheduledPaymentConsents_throwsSendInvalidFormatDetachedJws_Test() {
        // Given
        val consentRequest =
            OBWriteInternationalScheduledConsentTestDataFactory.aValidOBWriteInternationalScheduledConsent5()

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            buildCreateConsentRequest(consentRequest).configureJwsSignatureProducer(BadJwsSignatureProducer()).sendRequest()
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(INVALID_FORMAT_DETACHED_JWS_ERROR)
    }

    fun shouldCreateInternationalScheduledPaymentConsents_throwsNoDetachedJws_Test() {
        // Given
        val consentRequest =
            OBWriteInternationalScheduledConsentTestDataFactory.aValidOBWriteInternationalScheduledConsent5()

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            buildCreateConsentRequest(consentRequest).configureJwsSignatureProducer(null).sendRequest()
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(NO_DETACHED_JWS)
    }

    fun shouldCreateInternationalScheduledPaymentConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJws_Test() {
        // Given
        val consentRequest =
            OBWriteInternationalScheduledConsentTestDataFactory.aValidOBWriteInternationalScheduledConsent5()

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            buildCreateConsentRequest(consentRequest).configureJwsSignatureProducer(DefaultJwsSignatureProducer(tppResource.tpp, false)).sendRequest()
        }

        // Then
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
    }

    fun shouldCreateInternationalScheduledPaymentConsents_throwsSendInvalidKidDetachedJws_Test() {
        // Given
        val consentRequest =
            OBWriteInternationalScheduledConsentTestDataFactory.aValidOBWriteInternationalScheduledConsent5()

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            buildCreateConsentRequest(consentRequest).configureJwsSignatureProducer(InvalidKidJwsSignatureProducer(tppResource.tpp)).sendRequest()
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
    }

    fun shouldCreateInternationalScheduledPaymentConsents_throwsRequestExecutionTimeInThePast_Test() {
        // Given
        val consentRequest =
            OBWriteInternationalScheduledConsentTestDataFactory.aValidOBWriteInternationalScheduledConsent5()
        consentRequest.data.initiation.requestedExecutionDateTime = DateTime.now().minusDays(1)
        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            createInternationalScheduledPaymentConsent(consentRequest)
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(REQUEST_EXECUTION_TIME_IN_THE_PAST)
    }

    fun createInternationalScheduledPaymentConsent(consentRequest: OBWriteInternationalScheduledConsent5): OBWriteInternationalScheduledConsentResponse6 {
        return buildCreateConsentRequest(consentRequest).sendRequest()
    }

    private fun buildCreateConsentRequest(
        consent: OBWriteInternationalScheduledConsent5
    ) = paymentApiClient.createDefaultPostRequest(
        paymentLinks.CreateInternationalScheduledPaymentConsent,
        tppResource.tpp.getClientCredentialsAccessToken(defaultPaymentScopesForAccessToken),
        consent
    )

    fun createInternationalScheduledPaymentConsentAndGetAccessToken(consentRequest: OBWriteInternationalScheduledConsent5): Pair<OBWriteInternationalScheduledConsentResponse6, AccessToken> {
        val consent = createInternationalScheduledPaymentConsent(consentRequest)
        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().authorizeConsent(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        return consent to accessTokenAuthorizationCode
    }
}