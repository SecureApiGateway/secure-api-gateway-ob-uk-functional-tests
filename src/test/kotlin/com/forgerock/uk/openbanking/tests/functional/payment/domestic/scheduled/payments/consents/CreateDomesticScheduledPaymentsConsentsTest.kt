package com.forgerock.uk.openbanking.tests.functional.payment.domestic.scheduled.payments.consents

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.securebanking.framework.conditions.Status
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.framework.http.fuel.defaultMapper
import com.forgerock.securebanking.framework.signature.signPayloadSubmitPayment
import com.forgerock.securebanking.framework.signature.signPayloadSubmitPaymentInvalidB64ClaimTrue
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.framework.constants.INVALID_FORMAT_DETACHED_JWS
import com.forgerock.uk.openbanking.framework.constants.INVALID_SIGNING_KID
import com.forgerock.uk.openbanking.framework.errors.INVALID_FORMAT_DETACHED_JWS_ERROR
import com.forgerock.uk.openbanking.framework.errors.NO_DETACHED_JWS
import com.forgerock.uk.openbanking.framework.errors.SIGNATURE_VALIDATION_FAILED
import com.forgerock.uk.openbanking.framework.errors.UNAUTHORIZED
import com.forgerock.uk.openbanking.support.discovery.payment3_1_1
import com.forgerock.uk.openbanking.support.discovery.payment3_1_3
import com.forgerock.uk.openbanking.support.discovery.payment3_1_4
import com.forgerock.uk.openbanking.support.discovery.payment3_1_8
import com.forgerock.uk.openbanking.support.payment.PaymentRS
import com.github.kittinunf.fuel.core.FuelError
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.payment.OBWriteDomesticScheduledConsentResponse2
import uk.org.openbanking.datamodel.payment.OBWriteDomesticScheduledConsentResponse3
import uk.org.openbanking.datamodel.payment.OBWriteDomesticScheduledConsentResponse4
import uk.org.openbanking.datamodel.payment.OBWriteDomesticScheduledConsentResponse5
import uk.org.openbanking.testsupport.payment.OBWriteDomesticScheduledConsentTestDataFactory.*

class CreateDomesticScheduledPaymentsConsentsTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun createDomesticScheduledPaymentsConsents_v3_1_8() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent4()
        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        // When
        val consent = PaymentRS().consent<OBWriteDomesticScheduledConsentResponse5>(
            payment3_1_8.Links.links.CreateDomesticScheduledPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_8,
            signedPayloadConsent
        )

        // Then
        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateDomesticScheduledPaymentsConsents_throwsSendInvalidFormatDetachedJws_v3_1_8() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent4()

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteDomesticScheduledConsentResponse5>(
                payment3_1_8.Links.links.CreateDomesticScheduledPaymentConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1_8,
                INVALID_FORMAT_DETACHED_JWS
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(INVALID_FORMAT_DETACHED_JWS_ERROR)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateDomesticScheduledPaymentsConsents_throwsNoDetachedJws_v3_1_8() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent4()

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consentNoDetachedJwt<OBWriteDomesticScheduledConsentResponse5>(
                payment3_1_8.Links.links.CreateDomesticScheduledPaymentConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1_8
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(NO_DETACHED_JWS)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateDomesticScheduledPaymentsConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJws_v3_1_8() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent4()

        val signedPayload =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteDomesticScheduledConsentResponse5>(
                payment3_1_8.Links.links.CreateDomesticScheduledPaymentConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1_8,
                signedPayload
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateDomesticScheduledPaymentsConsents_throwsSendInvalidKidDetachedJws_v3_1_8() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent4()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                INVALID_SIGNING_KID
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteDomesticScheduledConsentResponse5>(
                payment3_1_8.Links.links.CreateDomesticScheduledPaymentConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1_8,
                signedPayloadConsent
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payment-consents"]
    )
    @Test
    fun createDomesticScheduledPaymentsConsents_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent4()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        // When
        val consent = PaymentRS().consent<OBWriteDomesticScheduledConsentResponse4>(
            payment3_1_4.Links.links.CreateDomesticScheduledPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_4,
            signedPayloadConsent
        )

        // Then
        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticScheduledPaymentsConsents_throwsSendInvalidFormatDetachedJws_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent4()

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteDomesticScheduledConsentResponse4>(
                payment3_1_4.Links.links.CreateDomesticScheduledPaymentConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1_4,
                INVALID_FORMAT_DETACHED_JWS
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(INVALID_FORMAT_DETACHED_JWS_ERROR)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticScheduledPaymentsConsents_throwsNoDetachedJws_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent4()

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consentNoDetachedJwt<OBWriteDomesticScheduledConsentResponse4>(
                payment3_1_4.Links.links.CreateDomesticScheduledPaymentConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1_4
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(NO_DETACHED_JWS)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticScheduledPaymentsConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJws_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent4()
        val signedPayload =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteDomesticScheduledConsentResponse4>(
                payment3_1_4.Links.links.CreateDomesticScheduledPaymentConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1_4,
                signedPayload
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticScheduledPaymentsConsents_throwsSendInvalidKidDetachedJws_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent4()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                INVALID_SIGNING_KID
            )


        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteDomesticScheduledConsentResponse4>(
                payment3_1_4.Links.links.CreateDomesticScheduledPaymentConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1_4,
                signedPayloadConsent
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(SIGNATURE_VALIDATION_FAILED)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1.2"]
    )
    @Test
    fun createDomesticScheduledPaymentsConsents_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent3()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        // When
        val consent = PaymentRS().consent<OBWriteDomesticScheduledConsentResponse3>(
            payment3_1_3.Links.links.CreateDomesticScheduledPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_3,
            signedPayloadConsent
        )

        // Then
        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1.2"]
    )
    @Test
    fun shouldCreateDomesticScheduledPaymentsConsents_throwsSendInvalidFormatDetachedJws_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent3()

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteDomesticScheduledConsentResponse3>(
                payment3_1_3.Links.links.CreateDomesticScheduledPaymentConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1_3,
                INVALID_FORMAT_DETACHED_JWS
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(INVALID_FORMAT_DETACHED_JWS_ERROR)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1.2"]
    )
    @Test
    fun shouldCreateDomesticScheduledPaymentsConsents_throwsNoDetachedJws_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent3()

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consentNoDetachedJwt<OBWriteDomesticScheduledConsentResponse3>(
                payment3_1_3.Links.links.CreateDomesticScheduledPaymentConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1_3
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(NO_DETACHED_JWS)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1.2"]
    )
    @Test
    fun shouldCreateDomesticScheduledPaymentsConsents_throwsSendInvalidKidDetachedJws_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent3()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                INVALID_SIGNING_KID,
                true
            )


        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteDomesticScheduledConsentResponse3>(
                payment3_1_3.Links.links.CreateDomesticScheduledPaymentConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1_3,
                signedPayloadConsent
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(SIGNATURE_VALIDATION_FAILED)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1.2"]
    )
    @Test
    fun shouldCreateDomesticScheduledPaymentsConsents_throwsB64ClaimMissingDetachedJws_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent3()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteDomesticScheduledConsentResponse3>(
                payment3_1_3.Links.links.CreateDomesticScheduledPaymentConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1_3,
                signedPayloadConsent
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1.2"]
    )
    @Test
    fun shouldCreateDomesticScheduledPaymentsConsents_throwsB64ClaimShouldBeFalseDetachedJws_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent3()

        val signedPayloadConsent =
            signPayloadSubmitPaymentInvalidB64ClaimTrue(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteDomesticScheduledConsentResponse3>(
                payment3_1_3.Links.links.CreateDomesticScheduledPaymentConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1_3,
                signedPayloadConsent
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1"]
    )
    @Test
    fun createDomesticScheduledPaymentsConsents_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent2()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        // When
        val consent = PaymentRS().consent<OBWriteDomesticScheduledConsentResponse2>(
            payment3_1_1.Links.links.CreateDomesticScheduledPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_1,
            signedPayloadConsent
        )

        // Then
        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1"]
    )
    @Test
    fun shouldCreateDomesticScheduledPaymentsConsents_throwsSendInvalidFormatDetachedJws_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent2()

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteDomesticScheduledConsentResponse2>(
                payment3_1_1.Links.links.CreateDomesticScheduledPaymentConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1_1,
                INVALID_FORMAT_DETACHED_JWS
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(INVALID_FORMAT_DETACHED_JWS_ERROR)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1"]
    )
    @Test
    fun shouldCreateDomesticScheduledPaymentsConsents_throwsNoDetachedJws_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent2()

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consentNoDetachedJwt<OBWriteDomesticScheduledConsentResponse2>(
                payment3_1_1.Links.links.CreateDomesticScheduledPaymentConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1_1
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(NO_DETACHED_JWS)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1"]
    )
    @Test
    fun shouldCreateDomesticScheduledPaymentsConsents_throwsSendInvalidKidDetachedJws_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent2()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                INVALID_SIGNING_KID,
                true
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteDomesticScheduledConsentResponse2>(
                payment3_1_1.Links.links.CreateDomesticScheduledPaymentConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1_1,
                signedPayloadConsent
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(SIGNATURE_VALIDATION_FAILED)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1"]
    )
    @Test
    fun shouldCreateDomesticScheduledPaymentsConsents_throwsB64ClaimMissingDetachedJws_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent2()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteDomesticScheduledConsentResponse2>(
                payment3_1_1.Links.links.CreateDomesticScheduledPaymentConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1_1,
                signedPayloadConsent
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1"]
    )
    @Test
    fun shouldCreateDomesticScheduledPaymentsConsents_throwsB64ClaimShouldBeFalseDetachedJws_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent2()

        val signedPayloadConsent =
            signPayloadSubmitPaymentInvalidB64ClaimTrue(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteDomesticScheduledConsentResponse2>(
                payment3_1_1.Links.links.CreateDomesticScheduledPaymentConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1_1,
                signedPayloadConsent
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(UNAUTHORIZED)
    }
}
