package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.payments.consents.legacy

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
import com.forgerock.sapi.gateway.ob.uk.support.discovery.payment3_1_1
import com.forgerock.sapi.gateway.ob.uk.support.discovery.payment3_1_3
import com.forgerock.sapi.gateway.ob.uk.support.discovery.payment3_1_4
import com.forgerock.sapi.gateway.ob.uk.support.payment.PaymentRS
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.github.kittinunf.fuel.core.FuelError
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.payment.OBWriteDomesticConsentResponse2
import uk.org.openbanking.datamodel.payment.OBWriteDomesticConsentResponse3
import uk.org.openbanking.datamodel.payment.OBWriteDomesticConsentResponse4
import uk.org.openbanking.testsupport.payment.OBWriteDomesticConsentTestDataFactory.*

class LegacyCreateDomesticPaymentsConsentsTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"]
    )
    @Test
    fun createDomesticPaymentsConsents_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent4()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        // When
        val consent = PaymentRS().consent<OBWriteDomesticConsentResponse4>(
            payment3_1_4.Links.links.CreateDomesticPaymentConsent,
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
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticPaymentsConsents_throwsSendInvalidFormatDetachedJws_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent4()

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteDomesticConsentResponse4>(
                payment3_1_4.Links.links.CreateDomesticPaymentConsent,
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
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticPaymentsConsents_throwsNoDetachedJws_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent4()

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consentNoDetachedJwt<OBWriteDomesticConsentResponse4>(
                payment3_1_4.Links.links.CreateDomesticPaymentConsent,
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
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticPaymentsConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJws_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent4()
        val signedPayload =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteDomesticConsentResponse4>(
                payment3_1_4.Links.links.CreateDomesticPaymentConsent,
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
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticPaymentsConsents_throwsSendInvalidKidDetachedJws_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent4()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                    INVALID_SIGNING_KID
            )


        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteDomesticConsentResponse4>(
                payment3_1_4.Links.links.CreateDomesticPaymentConsent,
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
        apiVersion = "v3.1.3",
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"],
        compatibleVersions = ["v.3.1.2"]
    )
    @Test
    fun createDomesticPaymentsConsents_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent3()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        // When
        val consent = PaymentRS().consent<OBWriteDomesticConsentResponse3>(
            payment3_1_3.Links.links.CreateDomesticPaymentConsent,
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
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"],
        compatibleVersions = ["v.3.1.2"]
    )
    @Test
    fun shouldCreateDomesticPaymentsConsents_throwsSendInvalidFormatDetachedJws_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent3()

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteDomesticConsentResponse3>(
                payment3_1_3.Links.links.CreateDomesticPaymentConsent,
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
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"],
        compatibleVersions = ["v.3.1.2"]
    )
    @Test
    fun shouldCreateDomesticPaymentsConsents_throwsNoDetachedJws_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent3()

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consentNoDetachedJwt<OBWriteDomesticConsentResponse3>(
                payment3_1_3.Links.links.CreateDomesticPaymentConsent,
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
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"],
        compatibleVersions = ["v.3.1.2"]
    )
    @Test
    fun shouldCreateDomesticPaymentsConsents_throwsSendInvalidKidDetachedJws_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent3()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                    INVALID_SIGNING_KID,
                true
            )


        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteDomesticConsentResponse3>(
                payment3_1_3.Links.links.CreateDomesticPaymentConsent,
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
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"],
        compatibleVersions = ["v.3.1.2"]
    )
    @Test
    fun shouldCreateDomesticPaymentsConsents_throwsB64ClaimMissingDetachedJws_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent3()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteDomesticConsentResponse3>(
                payment3_1_3.Links.links.CreateDomesticPaymentConsent,
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
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"],
        compatibleVersions = ["v.3.1.2"]
    )
    @Test
    fun shouldCreateDomesticPaymentsConsents_throwsB64ClaimShouldBeFalseDetachedJws_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent3()

        val signedPayloadConsent =
            signPayloadSubmitPaymentInvalidB64ClaimTrue(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteDomesticConsentResponse3>(
                payment3_1_3.Links.links.CreateDomesticPaymentConsent,
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
        apiVersion = "v3.1.1",
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"],
        compatibleVersions = ["v.3.1"]
    )
    @Test
    fun createDomesticPaymentsConsents_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent2()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        // When
        val consent = PaymentRS().consent<OBWriteDomesticConsentResponse2>(
            payment3_1_1.Links.links.CreateDomesticPaymentConsent,
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
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"],
        compatibleVersions = ["v.3.1"]
    )
    @Test
    fun shouldCreateDomesticPaymentsConsents_throwsSendInvalidFormatDetachedJws_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent2()

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteDomesticConsentResponse2>(
                payment3_1_1.Links.links.CreateDomesticPaymentConsent,
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
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"],
        compatibleVersions = ["v.3.1"]
    )
    @Test
    fun shouldCreateDomesticPaymentsConsents_throwsNoDetachedJws_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent2()

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consentNoDetachedJwt<OBWriteDomesticConsentResponse2>(
                payment3_1_1.Links.links.CreateDomesticPaymentConsent,
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
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"],
        compatibleVersions = ["v.3.1"]
    )
    @Test
    fun shouldCreateDomesticPaymentsConsents_throwsSendInvalidKidDetachedJws_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent2()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                    INVALID_SIGNING_KID,
                true
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteDomesticConsentResponse2>(
                payment3_1_1.Links.links.CreateDomesticPaymentConsent,
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
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"],
        compatibleVersions = ["v.3.1"]
    )
    @Test
    fun shouldCreateDomesticPaymentsConsents_throwsB64ClaimMissingDetachedJws_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent2()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteDomesticConsentResponse2>(
                payment3_1_1.Links.links.CreateDomesticPaymentConsent,
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
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"],
        compatibleVersions = ["v.3.1"]
    )
    @Test
    fun shouldCreateDomesticPaymentsConsents_throwsB64ClaimShouldBeFalseDetachedJws_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent2()

        val signedPayloadConsent =
            signPayloadSubmitPaymentInvalidB64ClaimTrue(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteDomesticConsentResponse2>(
                payment3_1_1.Links.links.CreateDomesticPaymentConsent,
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
}
