package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.standing.order.consents.legacy

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
import com.forgerock.sapi.gateway.ob.uk.support.discovery.payment3_1
import com.forgerock.sapi.gateway.ob.uk.support.discovery.payment3_1_1
import com.forgerock.sapi.gateway.ob.uk.support.discovery.payment3_1_3
import com.forgerock.sapi.gateway.ob.uk.support.discovery.payment3_1_4
import com.forgerock.sapi.gateway.ob.uk.support.payment.PaymentRS
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.github.kittinunf.fuel.core.FuelError
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.payment.OBWriteDomesticStandingOrderConsentResponse2
import uk.org.openbanking.datamodel.payment.OBWriteDomesticStandingOrderConsentResponse3
import uk.org.openbanking.datamodel.payment.OBWriteDomesticStandingOrderConsentResponse4
import uk.org.openbanking.datamodel.payment.OBWriteDomesticStandingOrderConsentResponse5
import uk.org.openbanking.testsupport.payment.OBWriteDomesticStandingOrderConsentTestDataFactory.*

class LegacyCreateDomesticStandingOrderConsentsTest(val tppResource: CreateTppCallback.TppResource) {
    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateDomesticStandingOrderConsent"],
        apis = ["domestic-standing-order-consents"]
    )
    @Test
    fun createDomesticStandingOrdersConsents_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent5()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        // When
        val consent = PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse5>(
            payment3_1_4.Links.links.CreateDomesticStandingOrderConsent,
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
        operations = ["CreateDomesticStandingOrderConsent"],
        apis = ["domestic-standing-order-consents"]
    )
    @Test
    fun createDomesticStandingOrdersConsents_mandatoryFields_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent5MandatoryFields()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        // When
        val consent = PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse5>(
            payment3_1_4.Links.links.CreateDomesticStandingOrderConsent,
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
        operations = ["CreateDomesticStandingOrderConsent"],
        apis = ["domestic-standing-order-consents"]
    )
    @Test
    fun shouldCreateDomesticStandingOrdersConsents_throwsInvalidFrequencyValue_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent5()
        consentRequest.data.initiation.frequency = com.forgerock.sapi.gateway.ob.uk.framework.constants.INVALID_FREQUENCY
        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse5>(
                payment3_1_4.Links.links.CreateDomesticStandingOrderConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1_4,
                signedPayloadConsent
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.INVALID_FREQUENCY_VALUE)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateDomesticStandingOrderConsent"],
        apis = ["domestic-standing-order-consents"]
    )
    @Test
    fun shouldCreateDomesticStandingOrdersConsents_throwsSendInvalidFormatDetachedJws_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent5()

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse5>(
                payment3_1_4.Links.links.CreateDomesticStandingOrderConsent,
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
        operations = ["CreateDomesticStandingOrderConsent"],
        apis = ["domestic-standing-order-consents"]
    )
    @Test
    fun shouldCreateDomesticStandingOrdersConsents_throwsNoDetachedJws_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent5()

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consentNoDetachedJwt<OBWriteDomesticStandingOrderConsentResponse5>(
                payment3_1_4.Links.links.CreateDomesticStandingOrderConsent,
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
        operations = ["CreateDomesticStandingOrderConsent"],
        apis = ["domestic-standing-order-consents"]
    )
    @Test
    fun shouldCreateDomesticStandingOrdersConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJws_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent5()
        val signedPayload =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse5>(
                payment3_1_4.Links.links.CreateDomesticStandingOrderConsent,
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
        operations = ["CreateDomesticStandingOrderConsent"],
        apis = ["domestic-standing-order-consents"]
    )
    @Test
    fun shouldCreateDomesticStandingOrdersConsents_throwsSendInvalidKidDetachedJws_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent5()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                    INVALID_SIGNING_KID
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse5>(
                payment3_1_4.Links.links.CreateDomesticStandingOrderConsent,
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
        operations = ["CreateDomesticStandingOrderConsent"],
        apis = ["domestic-standing-order-consents"],
        compatibleVersions = ["v.3.1.2"]
    )
    @Test
    fun createDomesticStandingOrdersConsents_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent4()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        // When
        val consent = PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse4>(
            payment3_1_3.Links.links.CreateDomesticStandingOrderConsent,
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
        operations = ["CreateDomesticStandingOrderConsent"],
        apis = ["domestic-standing-order-consents"],
        compatibleVersions = ["v.3.1.2"]
    )
    @Test
    fun createDomesticStandingOrdersConsents_mandatoryFields_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent4MandatoryFields()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        // When
        val consent = PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse4>(
            payment3_1_3.Links.links.CreateDomesticStandingOrderConsent,
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
        operations = ["CreateDomesticStandingOrderConsent"],
        apis = ["domestic-standing-order-consents"],
        compatibleVersions = ["v.3.1.2"]
    )
    @Test
    fun shouldCreateDomesticStandingOrdersConsents_throwsInvalidFrequencyValue_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent4()
        consentRequest.data.initiation.frequency = com.forgerock.sapi.gateway.ob.uk.framework.constants.INVALID_FREQUENCY
        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse4>(
                payment3_1_3.Links.links.CreateDomesticStandingOrderConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1_3,
                signedPayloadConsent
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.INVALID_FREQUENCY_VALUE)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateDomesticStandingOrderConsent"],
        apis = ["domestic-standing-order-consents"],
        compatibleVersions = ["v.3.1.2"]
    )
    @Test
    fun shouldCreateDomesticStandingOrdersConsents_throwsSendInvalidFormatDetachedJws_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent4()

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse4>(
                payment3_1_3.Links.links.CreateDomesticStandingOrderConsent,
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
        operations = ["CreateDomesticStandingOrderConsent"],
        apis = ["domestic-standing-order-consents"],
        compatibleVersions = ["v.3.1.2"]
    )
    @Test
    fun shouldCreateDomesticStandingOrdersConsents_throwsNoDetachedJws_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent4()

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consentNoDetachedJwt<OBWriteDomesticStandingOrderConsentResponse4>(
                payment3_1_3.Links.links.CreateDomesticStandingOrderConsent,
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
        operations = ["CreateDomesticStandingOrderConsent"],
        apis = ["domestic-standing-order-consents"],
        compatibleVersions = ["v.3.1.2"]
    )
    @Test
    fun shouldCreateDomesticStandingOrdersConsents_throwsSendInvalidKidDetachedJws_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent4()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                    INVALID_SIGNING_KID,
                true
            )


        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse4>(
                payment3_1_3.Links.links.CreateDomesticStandingOrderConsent,
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
        operations = ["CreateDomesticStandingOrderConsent"],
        apis = ["domestic-standing-order-consents"],
        compatibleVersions = ["v.3.1.2"]
    )
    @Test
    fun shouldCreateDomesticStandingOrdersConsents_throwsB64ClaimMissingDetachedJws_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent4()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse4>(
                payment3_1_3.Links.links.CreateDomesticStandingOrderConsent,
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
        operations = ["CreateDomesticStandingOrderConsent"],
        apis = ["domestic-standing-order-consents"],
        compatibleVersions = ["v.3.1.2"]
    )
    @Test
    fun shouldCreateDomesticStandingOrdersConsents_throwsB64ClaimShouldBeFalseDetachedJws_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent4()

        val signedPayloadConsent =
            signPayloadSubmitPaymentInvalidB64ClaimTrue(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse4>(
                payment3_1_3.Links.links.CreateDomesticStandingOrderConsent,
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
        operations = ["CreateDomesticStandingOrderConsent"],
        apis = ["domestic-standing-order-consents"]
    )
    @Test
    fun createDomesticStandingOrdersConsents_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent3()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        // When
        val consent = PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse3>(
            payment3_1_1.Links.links.CreateDomesticStandingOrderConsent,
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
        operations = ["CreateDomesticStandingOrderConsent"],
        apis = ["domestic-standing-order-consents"]
    )
    @Test
    fun createDomesticStandingOrdersConsents_mandatoryFields_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent3MandatoryFields()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        // When
        val consent = PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse3>(
            payment3_1_1.Links.links.CreateDomesticStandingOrderConsent,
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
        operations = ["CreateDomesticStandingOrderConsent"],
        apis = ["domestic-standing-order-consents"]
    )
    @Test
    fun shouldCreateDomesticStandingOrdersConsents_throwsInvalidFrequencyValue_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent3()
        consentRequest.data.initiation.frequency = com.forgerock.sapi.gateway.ob.uk.framework.constants.INVALID_FREQUENCY
        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse3>(
                payment3_1_1.Links.links.CreateDomesticStandingOrderConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1_1,
                signedPayloadConsent
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.INVALID_FREQUENCY_VALUE)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateDomesticStandingOrderConsent"],
        apis = ["domestic-standing-order-consents"]
    )
    @Test
    fun shouldCreateDomesticStandingOrdersConsents_throwsSendInvalidFormatDetachedJws_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent3()

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse3>(
                payment3_1_1.Links.links.CreateDomesticStandingOrderConsent,
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
        operations = ["CreateDomesticStandingOrderConsent"],
        apis = ["domestic-standing-order-consents"]
    )
    @Test
    fun shouldCreateDomesticStandingOrdersConsents_throwsNoDetachedJws_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent3()

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consentNoDetachedJwt<OBWriteDomesticStandingOrderConsentResponse3>(
                payment3_1_1.Links.links.CreateDomesticStandingOrderConsent,
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
        operations = ["CreateDomesticStandingOrderConsent"],
        apis = ["domestic-standing-order-consents"]
    )
    @Test
    fun shouldCreateDomesticStandingOrdersConsents_throwsSendInvalidKidDetachedJws_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent3()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                    INVALID_SIGNING_KID,
                true
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse3>(
                payment3_1_1.Links.links.CreateDomesticStandingOrderConsent,
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
        operations = ["CreateDomesticStandingOrderConsent"],
        apis = ["domestic-standing-order-consents"]
    )
    @Test
    fun shouldCreateDomesticStandingOrdersConsents_throwsB64ClaimMissingDetachedJws_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent3()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse3>(
                payment3_1_1.Links.links.CreateDomesticStandingOrderConsent,
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
        operations = ["CreateDomesticStandingOrderConsent"],
        apis = ["domestic-standing-order-consents"]
    )
    @Test
    fun shouldCreateDomesticStandingOrdersConsents_throwsB64ClaimShouldBeFalseDetachedJws_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent3()

        val signedPayloadConsent =
            signPayloadSubmitPaymentInvalidB64ClaimTrue(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse3>(
                payment3_1_1.Links.links.CreateDomesticStandingOrderConsent,
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
        apiVersion = "v3.1",
        operations = ["CreateDomesticStandingOrderConsent"],
        apis = ["domestic-standing-order-consents"]
    )
    @Test
    fun createDomesticStandingOrdersConsents_v3_1() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent2()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        // When
        val consent = PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse2>(
            payment3_1.Links.links.CreateDomesticStandingOrderConsent,
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
        operations = ["CreateDomesticStandingOrderConsent"],
        apis = ["domestic-standing-order-consents"]
    )
    @Test
    fun createDomesticStandingOrdersConsents_mandatoryFields_v3_1() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent2MandatoryFields()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        // When
        val consent = PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse2>(
            payment3_1.Links.links.CreateDomesticStandingOrderConsent,
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
        operations = ["CreateDomesticStandingOrderConsent"],
        apis = ["domestic-standing-order-consents"]
    )
    @Test
    fun shouldCreateDomesticStandingOrdersConsents_throwsInvalidFrequencyValue_v3_1() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent2()
        consentRequest.data.initiation.frequency = com.forgerock.sapi.gateway.ob.uk.framework.constants.INVALID_FREQUENCY
        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse2>(
                payment3_1.Links.links.CreateDomesticStandingOrderConsent,
                consentRequest,
                tppResource.tpp,
                OBVersion.v3_1,
                signedPayloadConsent
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.INVALID_FREQUENCY_VALUE)
    }


    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1",
        operations = ["CreateDomesticStandingOrderConsent"],
        apis = ["domestic-standing-order-consents"]
    )
    @Test
    fun shouldCreateDomesticStandingOrdersConsents_throwsSendInvalidFormatDetachedJws_v3_1() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent2()

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse2>(
                payment3_1.Links.links.CreateDomesticStandingOrderConsent,
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
        operations = ["CreateDomesticStandingOrderConsent"],
        apis = ["domestic-standing-order-consents"]
    )
    @Test
    fun shouldCreateDomesticStandingOrdersConsents_throwsNoDetachedJws_v3_1() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent2()

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consentNoDetachedJwt<OBWriteDomesticStandingOrderConsentResponse2>(
                payment3_1.Links.links.CreateDomesticStandingOrderConsent,
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
        operations = ["CreateDomesticStandingOrderConsent"],
        apis = ["domestic-standing-order-consents"]
    )
    @Test
    fun shouldCreateDomesticStandingOrdersConsents_throwsSendInvalidKidDetachedJws_v3_1() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent2()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                    INVALID_SIGNING_KID,
                true
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse2>(
                payment3_1.Links.links.CreateDomesticStandingOrderConsent,
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
        operations = ["CreateDomesticStandingOrderConsent"],
        apis = ["domestic-standing-order-consents"]
    )
    @Test
    fun shouldCreateDomesticStandingOrdersConsents_throwsB64ClaimMissingDetachedJws_v3_1() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent2()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse2>(
                payment3_1.Links.links.CreateDomesticStandingOrderConsent,
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
        operations = ["CreateDomesticStandingOrderConsent"],
        apis = ["domestic-standing-order-consents"]
    )
    @Test
    fun shouldCreateDomesticStandingOrdersConsents_throwsB64ClaimShouldBeFalseDetachedJws_v3_1() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent2()

        val signedPayloadConsent =
            signPayloadSubmitPaymentInvalidB64ClaimTrue(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse2>(
                payment3_1.Links.links.CreateDomesticStandingOrderConsent,
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
}
