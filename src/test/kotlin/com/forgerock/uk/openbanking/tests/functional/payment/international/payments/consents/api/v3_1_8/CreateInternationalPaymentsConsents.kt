package com.forgerock.uk.openbanking.tests.functional.payment.international.payments.consents.api.v3_1_8

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
import com.forgerock.uk.openbanking.framework.errors.UNAUTHORIZED
import com.forgerock.uk.openbanking.support.discovery.getPaymentsApiLinks
import com.forgerock.uk.openbanking.support.payment.PaymentAS
import com.forgerock.uk.openbanking.support.payment.PaymentRS
import com.github.kittinunf.fuel.core.FuelError
import org.assertj.core.api.Assertions
import uk.org.openbanking.datamodel.payment.OBWriteInternationalConsent5
import uk.org.openbanking.datamodel.payment.OBWriteInternationalConsentResponse6
import uk.org.openbanking.testsupport.payment.OBWriteInternationalConsentTestDataFactory

class CreateInternationalPaymentsConsents(val version: OBVersion, val tppResource: CreateTppCallback.TppResource) {

    val paymentLinks = getPaymentsApiLinks(version)

    fun createInternationalPaymentsConsents() {
        // Given
        val consentRequest = OBWriteInternationalConsentTestDataFactory.aValidOBWriteInternationalConsent5()
        val consent = createInternationalPaymentConsent(consentRequest)

        // Then
        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()
    }

    fun createInternationalPaymentsConsents_mandatoryFieldsTest() {
        // Given
        val consentRequest =
            OBWriteInternationalConsentTestDataFactory.aValidOBWriteInternationalConsent5MandatoryFields()
        val consent = createInternationalPaymentConsent(consentRequest)

        // Then
        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()
    }

    fun shouldCreateInternationalPaymentConsents_throwsSendInvalidFormatDetachedJwsTest() {
        // Given
        val consentRequest = OBWriteInternationalConsentTestDataFactory.aValidOBWriteInternationalConsent5()

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteInternationalConsentResponse6>(
                paymentLinks.CreateInternationalPaymentConsent,
                consentRequest,
                tppResource.tpp,
                version,
                INVALID_FORMAT_DETACHED_JWS
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(INVALID_FORMAT_DETACHED_JWS_ERROR)
    }

    fun shouldCreateInternationalPaymentConsents_throwsNoDetachedJwsTest() {
        // Given
        val consentRequest = OBWriteInternationalConsentTestDataFactory.aValidOBWriteInternationalConsent5()

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            PaymentRS().consentNoDetachedJwt<OBWriteInternationalConsentResponse6>(
                paymentLinks.CreateInternationalPaymentConsent,
                consentRequest,
                tppResource.tpp,
                version
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(NO_DETACHED_JWS)
    }

    fun shouldCreateInternationalPaymentConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJwsTest() {
        // Given
        val consentRequest = OBWriteInternationalConsentTestDataFactory.aValidOBWriteInternationalConsent5()

        val signedPayload =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteInternationalConsentResponse6>(
                paymentLinks.CreateInternationalPaymentConsent,
                consentRequest,
                tppResource.tpp,
                version,
                signedPayload
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
    }

    fun shouldCreateInternationalPaymentConsents_throwsSendInvalidKidDetachedJwsTest() {
        // Given
        val consentRequest = OBWriteInternationalConsentTestDataFactory.aValidOBWriteInternationalConsent5()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                INVALID_SIGNING_KID
            )

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteInternationalConsentResponse6>(
                paymentLinks.CreateInternationalPaymentConsent,
                consentRequest,
                tppResource.tpp,
                version,
                signedPayloadConsent
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
    }

    fun createInternationalPaymentConsent(consentRequest: OBWriteInternationalConsent5): OBWriteInternationalConsentResponse6 {
        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        // When
        val consent = PaymentRS().consent<OBWriteInternationalConsentResponse6>(
            paymentLinks.CreateInternationalPaymentConsent,
            consentRequest,
            tppResource.tpp,
            version,
            signedPayloadConsent
        )
        return consent
    }

    fun createInternationalPaymentConsentAndGetAccessToken(consentRequest: OBWriteInternationalConsent5): Pair<OBWriteInternationalConsentResponse6, AccessToken> {
        val consent = createInternationalPaymentConsent(consentRequest)
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        return consent to accessTokenAuthorizationCode
    }
}