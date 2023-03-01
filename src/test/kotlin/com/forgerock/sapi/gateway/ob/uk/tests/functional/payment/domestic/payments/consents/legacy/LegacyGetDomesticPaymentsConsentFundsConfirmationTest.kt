package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.payments.consents.legacy

import assertk.assertThat
import assertk.assertions.*
import com.forgerock.sapi.gateway.framework.conditions.Status
import com.forgerock.sapi.gateway.framework.configuration.psu
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.framework.http.fuel.defaultMapper
import com.forgerock.sapi.gateway.framework.signature.signPayloadSubmitPayment
import com.forgerock.sapi.gateway.ob.uk.support.discovery.payment3_1_1
import com.forgerock.sapi.gateway.ob.uk.support.discovery.payment3_1_3
import com.forgerock.sapi.gateway.ob.uk.support.discovery.payment3_1_4
import com.forgerock.sapi.gateway.ob.uk.support.payment.PaymentAS
import com.forgerock.sapi.gateway.ob.uk.support.payment.PaymentFactory
import com.forgerock.sapi.gateway.ob.uk.support.payment.PaymentRS
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.github.kittinunf.fuel.core.FuelError
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.payment.OBWriteDomesticConsentResponse2
import uk.org.openbanking.datamodel.payment.OBWriteDomesticConsentResponse3
import uk.org.openbanking.datamodel.payment.OBWriteDomesticConsentResponse4
import uk.org.openbanking.datamodel.payment.OBWriteFundsConfirmationResponse1
import uk.org.openbanking.testsupport.payment.OBWriteDomesticConsentTestDataFactory.*

class LegacyGetDomesticPaymentsConsentFundsConfirmationTest(val tppResource: CreateTppCallback.TppResource) {
    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateDomesticPaymentConsent", "GetDomesticPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["domestic-payment-consents"],
    )
    @Test
    fun shouldGetDomesticPaymentConsentsFundsConfirmation_true_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent4()
        consentRequest.data.initiation.instructedAmount.amount("3")

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteDomesticConsentResponse4>(
            payment3_1_4.Links.links.CreateDomesticPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_4,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().authorizeConsent(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

        // When
        val result = PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
            PaymentFactory.urlWithConsentId(
                payment3_1_4.Links.links.GetDomesticPaymentConsentsConsentIdFundsConfirmation,
                consent.data.consentId
            ),
            accessTokenAuthorizationCode
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isTrue()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateDomesticPaymentConsent", "GetDomesticPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["domestic-payment-consents"],
    )
    @Test
    fun shouldGetDomesticPaymentConsentsFundsConfirmation_false_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent4()
        consentRequest.data.initiation.instructedAmount.amount("1000000")

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteDomesticConsentResponse4>(
            payment3_1_4.Links.links.CreateDomesticPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_4,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().authorizeConsent(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

        // When
        val result = PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
            PaymentFactory.urlWithConsentId(
                payment3_1_4.Links.links.GetDomesticPaymentConsentsConsentIdFundsConfirmation,
                consent.data.consentId
            ),
            accessTokenAuthorizationCode
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isFalse()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    @Disabled("Enhancement: https://github.com/SecureBankingAccessToolkit/SecureBankingAccessToolkit/issues/337")
    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateDomesticPaymentConsent", "GetDomesticPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["domestic-payment-consents"],
    )
    @Test
    fun shouldGetDomesticPaymentConsentsFundsConfirmation_throwsWrongGrantType_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent4()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteDomesticConsentResponse4>(
            payment3_1_4.Links.links.CreateDomesticPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_4,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentRS().getClientCredentialsAccessToken(tppResource.tpp)

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
                PaymentFactory.urlWithConsentId(
                    payment3_1_4.Links.links.GetDomesticPaymentConsentsConsentIdFundsConfirmation,
                    consent.data.consentId
                ),
                accessTokenClientCredentials
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateDomesticPaymentConsent", "GetDomesticPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["domestic-payment-consents"],
        compatibleVersions = ["v.3.1.2"]
    )
    @Test
    fun shouldGetDomesticPaymentConsentsFundsConfirmation_true_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent3()
        consentRequest.data.initiation.instructedAmount.amount("3")

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteDomesticConsentResponse3>(
            payment3_1_3.Links.links.CreateDomesticPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_3,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().authorizeConsent(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

        // When
        val result = PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
            PaymentFactory.urlWithConsentId(
                payment3_1_3.Links.links.GetDomesticPaymentConsentsConsentIdFundsConfirmation,
                consent.data.consentId
            ),
            accessTokenAuthorizationCode
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isTrue()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateDomesticPaymentConsent", "GetDomesticPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["domestic-payment-consents"],
        compatibleVersions = ["v.3.1.2"]
    )
    @Test
    fun shouldGetDomesticPaymentConsentsFundsConfirmation_false_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent3()
        consentRequest.data.initiation.instructedAmount.amount("1000000")

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteDomesticConsentResponse3>(
            payment3_1_3.Links.links.CreateDomesticPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_3,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().authorizeConsent(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

        // When
        val result = PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
            PaymentFactory.urlWithConsentId(
                payment3_1_3.Links.links.GetDomesticPaymentConsentsConsentIdFundsConfirmation,
                consent.data.consentId
            ),
            accessTokenAuthorizationCode
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isFalse()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    @Disabled("Enhancement: https://github.com/SecureBankingAccessToolkit/SecureBankingAccessToolkit/issues/337")
    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateDomesticPaymentConsent", "GetDomesticPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["domestic-payment-consents"],
        compatibleVersions = ["v.3.1.2"]
    )
    @Test
    fun shouldGetDomesticPaymentConsentsFundsConfirmation_throwsWrongGrantType_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent3()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteDomesticConsentResponse3>(
            payment3_1_3.Links.links.CreateDomesticPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_3,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentRS().getClientCredentialsAccessToken(tppResource.tpp)

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
                PaymentFactory.urlWithConsentId(
                    payment3_1_3.Links.links.GetDomesticPaymentConsentsConsentIdFundsConfirmation,
                    consent.data.consentId
                ),
                accessTokenClientCredentials
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateDomesticPaymentConsent", "GetDomesticPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["domestic-payment-consents"],
        compatibleVersions = ["v.3.1"]
    )
    @Test
    fun shouldGetDomesticPaymentConsentsFundsConfirmation_true_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent2()
        consentRequest.data.initiation.instructedAmount.amount("3")

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteDomesticConsentResponse2>(
            payment3_1_1.Links.links.CreateDomesticPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_1,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().authorizeConsent(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

        // When
        val result = PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
            PaymentFactory.urlWithConsentId(
                payment3_1_1.Links.links.GetDomesticPaymentConsentsConsentIdFundsConfirmation,
                consent.data.consentId
            ),
            accessTokenAuthorizationCode
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isTrue()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateDomesticPaymentConsent", "GetDomesticPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["domestic-payment-consents"],
        compatibleVersions = ["v.3.1"]
    )
    @Test
    fun shouldGetDomesticPaymentConsentsFundsConfirmation_false_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent2()
        consentRequest.data.initiation.instructedAmount.amount("1000000")

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteDomesticConsentResponse2>(
            payment3_1_1.Links.links.CreateDomesticPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_1,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().authorizeConsent(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

        // When
        val result = PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
            PaymentFactory.urlWithConsentId(
                payment3_1_1.Links.links.GetDomesticPaymentConsentsConsentIdFundsConfirmation,
                consent.data.consentId
            ),
            accessTokenAuthorizationCode
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.fundsAvailableResult).isNotNull()
        assertThat(result.data.fundsAvailableResult.fundsAvailable).isFalse()
        assertThat(result.data.fundsAvailableResult.fundsAvailableDateTime).isNotNull()
    }

    @Disabled("Enhancement: https://github.com/SecureBankingAccessToolkit/SecureBankingAccessToolkit/issues/337")
    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateDomesticPaymentConsent", "GetDomesticPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["domestic-payment-consents"],
        compatibleVersions = ["v.3.1"]
    )
    @Test
    fun shouldGetDomesticPaymentConsentsFundsConfirmation_throwsWrongGrantType_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent2()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteDomesticConsentResponse2>(
            payment3_1_1.Links.links.CreateDomesticPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_1,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentRS().getClientCredentialsAccessToken(tppResource.tpp)

        // When
        val exception = assertThrows(AssertionError::class.java) {
            PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
                PaymentFactory.urlWithConsentId(
                    payment3_1_1.Links.links.GetDomesticPaymentConsentsConsentIdFundsConfirmation,
                    consent.data.consentId
                ),
                accessTokenClientCredentials
            )
        }

        // Then
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
    }
}
