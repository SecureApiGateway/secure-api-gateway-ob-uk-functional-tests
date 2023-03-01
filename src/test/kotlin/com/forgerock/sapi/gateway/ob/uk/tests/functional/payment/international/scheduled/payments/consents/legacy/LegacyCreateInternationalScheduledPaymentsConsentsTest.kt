package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.scheduled.payments.consents.legacy

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.sapi.gateway.framework.conditions.Status
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.framework.http.fuel.defaultMapper
import com.forgerock.sapi.gateway.framework.signature.signPayloadSubmitPayment
import com.forgerock.sapi.gateway.framework.signature.signPayloadSubmitPaymentInvalidB64ClaimTrue
import com.forgerock.sapi.gateway.ob.uk.framework.constants.INVALID_SIGNING_KID
import com.forgerock.sapi.gateway.ob.uk.support.discovery.*
import com.forgerock.sapi.gateway.ob.uk.support.payment.PaymentRS
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.github.kittinunf.fuel.core.FuelError
import org.assertj.core.api.Assertions
import org.joda.time.DateTime
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.payment.OBWriteInternationalScheduledConsentResponse2
import uk.org.openbanking.datamodel.payment.OBWriteInternationalScheduledConsentResponse3
import uk.org.openbanking.datamodel.payment.OBWriteInternationalScheduledConsentResponse4
import uk.org.openbanking.datamodel.payment.OBWriteInternationalScheduledConsentResponse5
import uk.org.openbanking.testsupport.payment.OBWriteInternationalScheduledConsentTestDataFactory.*

class LegacyCreateInternationalScheduledPaymentsConsentsTest(val tppResource: CreateTppCallback.TppResource) {
    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun createInternationalScheduledPaymentsConsents_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent5()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        // When
        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse5>(
            payment3_1_4.Links.links.CreateInternationalScheduledPaymentConsent,
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
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun createInternationalScheduledPaymentsConsents_mandatoryFields_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent5MandatoryFields()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        // When
        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse5>(
            payment3_1_4.Links.links.CreateInternationalScheduledPaymentConsent,
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
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPaymentConsents_throwsSendInvalidFormatDetachedJws_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent5()

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteInternationalScheduledConsentResponse5>(
                payment3_1_4.Links.links.CreateInternationalScheduledPaymentConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1_4,
                    com.forgerock.sapi.gateway.ob.uk.framework.constants.INVALID_FORMAT_DETACHED_JWS
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.INVALID_FORMAT_DETACHED_JWS_ERROR)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPaymentConsents_throwsNoDetachedJws_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent5()

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consentNoDetachedJwt<OBWriteInternationalScheduledConsentResponse5>(
                payment3_1_4.Links.links.CreateInternationalScheduledPaymentConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1_4
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.NO_DETACHED_JWS)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPaymentConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJws_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent5()
        val signedPayload =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteInternationalScheduledConsentResponse5>(
                payment3_1_4.Links.links.CreateInternationalScheduledPaymentConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1_4,
                signedPayload
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPaymentConsents_throwsSendInvalidKidDetachedJws_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent5()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                    INVALID_SIGNING_KID
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteInternationalScheduledConsentResponse5>(
                payment3_1_4.Links.links.CreateInternationalScheduledPaymentConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1_4,
                signedPayloadConsent
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.SIGNATURE_VALIDATION_FAILED)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPaymentConsents_throwsRequestExecutionTimeInThePast_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent5()
        consentRequest.data.initiation.requestedExecutionDateTime = DateTime.now().minusDays(1)

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteInternationalScheduledConsentResponse5>(
                payment3_1_4.Links.links.CreateInternationalScheduledPaymentConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1_4,
                signedPayloadConsent
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.REQUEST_EXECUTION_TIME_IN_THE_PAST)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun createInternationalScheduledPaymentsConsents_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent4()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        // When
        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse4>(
            payment3_1_3.Links.links.CreateInternationalScheduledPaymentConsent,
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
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun createInternationalScheduledPaymentsConsents_mandatoryFields_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent4MandatoryFields()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        // When
        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse4>(
            payment3_1_3.Links.links.CreateInternationalScheduledPaymentConsent,
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
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPaymentConsents_throwsSendInvalidFormatDetachedJws_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent4()

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteInternationalScheduledConsentResponse4>(
                payment3_1_3.Links.links.CreateInternationalScheduledPaymentConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1_3,
                    com.forgerock.sapi.gateway.ob.uk.framework.constants.INVALID_FORMAT_DETACHED_JWS
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.INVALID_FORMAT_DETACHED_JWS_ERROR)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPaymentConsents_throwsNoDetachedJws_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent4()

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consentNoDetachedJwt<OBWriteInternationalScheduledConsentResponse4>(
                payment3_1_3.Links.links.CreateInternationalScheduledPaymentConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1_3
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.NO_DETACHED_JWS)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPaymentConsents_throwsSendInvalidKidDetachedJws_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent4()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                    INVALID_SIGNING_KID,
                true
            )


        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteInternationalScheduledConsentResponse4>(
                payment3_1_3.Links.links.CreateInternationalScheduledPaymentConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1_3,
                signedPayloadConsent
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.SIGNATURE_VALIDATION_FAILED)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPaymentConsents_throwsB64ClaimMissingDetachedJws_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent4()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteInternationalScheduledConsentResponse4>(
                payment3_1_3.Links.links.CreateInternationalScheduledPaymentConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1_3,
                signedPayloadConsent
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPaymentConsents_throwsB64ClaimShouldBeFalseDetachedJws_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent4()

        val signedPayloadConsent =
            signPayloadSubmitPaymentInvalidB64ClaimTrue(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteInternationalScheduledConsentResponse4>(
                payment3_1_3.Links.links.CreateInternationalScheduledPaymentConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1_3,
                signedPayloadConsent
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPaymentConsents_throwsRequestExecutionTimeInThePast_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent4()
        consentRequest.data.initiation.requestedExecutionDateTime = DateTime.now().minusDays(1)

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteInternationalScheduledConsentResponse4>(
                payment3_1_3.Links.links.CreateInternationalScheduledPaymentConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1_3,
                signedPayloadConsent
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.REQUEST_EXECUTION_TIME_IN_THE_PAST)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.2",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun createInternationalScheduledPaymentsConsents_v3_1_2() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        // When
        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse3>(
            payment3_1_2.Links.links.CreateInternationalScheduledPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_2,
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
        apiVersion = "v3.1.2",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun createInternationalScheduledPaymentsConsents_mandatoryFields_v3_1_2() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3MandatoryFields()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        // When
        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse3>(
            payment3_1_2.Links.links.CreateInternationalScheduledPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_2,
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
        apiVersion = "v3.1.2",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPaymentConsents_throwsSendInvalidFormatDetachedJws_v3_1_2() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3()

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteInternationalScheduledConsentResponse3>(
                payment3_1_2.Links.links.CreateInternationalScheduledPaymentConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1_2,
                    com.forgerock.sapi.gateway.ob.uk.framework.constants.INVALID_FORMAT_DETACHED_JWS
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.INVALID_FORMAT_DETACHED_JWS_ERROR)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.2",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPaymentConsents_throwsNoDetachedJws_v3_1_2() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3()

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consentNoDetachedJwt<OBWriteInternationalScheduledConsentResponse3>(
                payment3_1_2.Links.links.CreateInternationalScheduledPaymentConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1_2
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.NO_DETACHED_JWS)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.2",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPaymentConsents_throwsSendInvalidKidDetachedJws_v3_1_2() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                    INVALID_SIGNING_KID,
                true
            )


        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteInternationalScheduledConsentResponse3>(
                payment3_1_2.Links.links.CreateInternationalScheduledPaymentConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1_2,
                signedPayloadConsent
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.SIGNATURE_VALIDATION_FAILED)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.2",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPaymentConsents_throwsB64ClaimMissingDetachedJws_v3_1_2() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteInternationalScheduledConsentResponse3>(
                payment3_1_2.Links.links.CreateInternationalScheduledPaymentConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1_2,
                signedPayloadConsent
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.2",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPaymentConsents_throwsB64ClaimShouldBeFalseDetachedJws_v3_1_2() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3()

        val signedPayloadConsent =
            signPayloadSubmitPaymentInvalidB64ClaimTrue(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteInternationalScheduledConsentResponse3>(
                payment3_1_2.Links.links.CreateInternationalScheduledPaymentConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1_2,
                signedPayloadConsent
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.2",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPaymentConsents_throwsRequestExecutionTimeInThePast_v3_1_2() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3()
        consentRequest.data.initiation.requestedExecutionDateTime = DateTime.now().minusDays(1)

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteInternationalScheduledConsentResponse3>(
                payment3_1_2.Links.links.CreateInternationalScheduledPaymentConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1_2,
                signedPayloadConsent
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.REQUEST_EXECUTION_TIME_IN_THE_PAST)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun createInternationalScheduledPaymentsConsents_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        // When
        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse3>(
            payment3_1_1.Links.links.CreateInternationalScheduledPaymentConsent,
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
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun createInternationalScheduledPaymentsConsents_mandatoryFields_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3MandatoryFields()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        // When
        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse3>(
            payment3_1_1.Links.links.CreateInternationalScheduledPaymentConsent,
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
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPaymentConsents_throwsSendInvalidFormatDetachedJws_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3()

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteInternationalScheduledConsentResponse3>(
                payment3_1_1.Links.links.CreateInternationalScheduledPaymentConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1_1,
                    com.forgerock.sapi.gateway.ob.uk.framework.constants.INVALID_FORMAT_DETACHED_JWS
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.INVALID_FORMAT_DETACHED_JWS_ERROR)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPaymentConsents_throwsNoDetachedJws_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3()

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consentNoDetachedJwt<OBWriteInternationalScheduledConsentResponse3>(
                payment3_1_1.Links.links.CreateInternationalScheduledPaymentConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1_1
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.NO_DETACHED_JWS)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPaymentConsents_throwsSendInvalidKidDetachedJws_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                    INVALID_SIGNING_KID,
                true
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteInternationalScheduledConsentResponse3>(
                payment3_1_1.Links.links.CreateInternationalScheduledPaymentConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1_1,
                signedPayloadConsent
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.SIGNATURE_VALIDATION_FAILED)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPaymentConsents_throwsB64ClaimMissingDetachedJws_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteInternationalScheduledConsentResponse3>(
                payment3_1_1.Links.links.CreateInternationalScheduledPaymentConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1_1,
                signedPayloadConsent
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPaymentConsents_throwsB64ClaimShouldBeFalseDetachedJws_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3()

        val signedPayloadConsent =
            signPayloadSubmitPaymentInvalidB64ClaimTrue(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteInternationalScheduledConsentResponse3>(
                payment3_1_1.Links.links.CreateInternationalScheduledPaymentConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1_1,
                signedPayloadConsent
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPaymentConsents_throwsRequestExecutionTimeInThePast_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3()
        consentRequest.data.initiation.requestedExecutionDateTime = DateTime.now().minusDays(1)

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteInternationalScheduledConsentResponse3>(
                payment3_1_1.Links.links.CreateInternationalScheduledPaymentConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1_1,
                signedPayloadConsent
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.REQUEST_EXECUTION_TIME_IN_THE_PAST)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun createInternationalScheduledPaymentsConsents_v3_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent2()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        // When
        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse2>(
            payment3_1.Links.links.CreateInternationalScheduledPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1,
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
        apiVersion = "v3.1",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun createInternationalScheduledPaymentsConsents_mandatoryFields_v3_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent2MandatoryFields()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        // When
        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse2>(
            payment3_1.Links.links.CreateInternationalScheduledPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1,
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
        apiVersion = "v3.1",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPaymentConsents_throwsSendInvalidFormatDetachedJws_v3_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent2()

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteInternationalScheduledConsentResponse2>(
                payment3_1.Links.links.CreateInternationalScheduledPaymentConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1,
                    com.forgerock.sapi.gateway.ob.uk.framework.constants.INVALID_FORMAT_DETACHED_JWS
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.INVALID_FORMAT_DETACHED_JWS_ERROR)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPaymentConsents_throwsNoDetachedJws_v3_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent2()

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consentNoDetachedJwt<OBWriteInternationalScheduledConsentResponse2>(
                payment3_1.Links.links.CreateInternationalScheduledPaymentConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.NO_DETACHED_JWS)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPaymentConsents_throwsSendInvalidKidDetachedJws_v3_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent2()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                    INVALID_SIGNING_KID,
                true
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteInternationalScheduledConsentResponse2>(
                payment3_1.Links.links.CreateInternationalScheduledPaymentConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1,
                signedPayloadConsent
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.SIGNATURE_VALIDATION_FAILED)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPaymentConsents_throwsB64ClaimMissingDetachedJws_v3_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent2()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteInternationalScheduledConsentResponse2>(
                payment3_1.Links.links.CreateInternationalScheduledPaymentConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1,
                signedPayloadConsent
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPaymentConsents_throwsB64ClaimShouldBeFalseDetachedJws_v3_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent2()

        val signedPayloadConsent =
            signPayloadSubmitPaymentInvalidB64ClaimTrue(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteInternationalScheduledConsentResponse2>(
                payment3_1.Links.links.CreateInternationalScheduledPaymentConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1,
                signedPayloadConsent
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPaymentConsents_throwsRequestExecutionTimeInThePast_v3_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent2()
        consentRequest.data.initiation.requestedExecutionDateTime = DateTime.now().minusDays(1)

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteInternationalScheduledConsentResponse2>(
                payment3_1.Links.links.CreateInternationalScheduledPaymentConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1,
                signedPayloadConsent
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.REQUEST_EXECUTION_TIME_IN_THE_PAST)
    }
}
