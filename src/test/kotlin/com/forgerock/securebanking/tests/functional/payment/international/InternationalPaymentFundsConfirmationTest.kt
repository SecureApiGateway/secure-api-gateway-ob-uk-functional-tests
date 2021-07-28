package com.forgerock.securebanking.tests.functional.payment.international

import assertk.assertThat
import assertk.assertions.*
import com.forgerock.openbanking.common.model.version.OBVersion.v3_1_6
import com.forgerock.securebanking.framework.conditions.Status
import com.forgerock.securebanking.framework.configuration.psu
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.support.funds.FundsConfirmationsAS
import com.forgerock.securebanking.support.payment.PaymentAS
import com.forgerock.securebanking.support.payment.PaymentRS
import com.forgerock.securebanking.support.discovery.payment3_1_2
import com.forgerock.securebanking.support.discovery.payment3_1_6
import com.github.kittinunf.fuel.core.FuelError
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.payment.OBWriteFundsConfirmationResponse1
import uk.org.openbanking.datamodel.payment.OBWriteInternationalConsentResponse2
import uk.org.openbanking.datamodel.payment.OBWriteInternationalConsentResponse5
import uk.org.openbanking.testsupport.payment.OBWriteInternationalConsentTestDataFactory.aValidOBWriteInternationalConsent2

class InternationalPaymentFundsConfirmationTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.2",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-payment-consents"]
    )
    @Test
    @DisplayName("shouldGet_SingleInternationalPayment_FundsConfirmation_v3_1_2() ")
    fun shouldGet_FundsConfirmation_v3_1_2() {
        // Given
        val consentRequest = aValidOBWriteInternationalConsent2()

        // When
        val consent = PaymentRS().consent<OBWriteInternationalConsentResponse2>(
            payment3_1_2.Links.links.CreateInternationalPaymentConsent,
            consentRequest,
            tppResource.tpp
        )
        val getConsentResult = PaymentRS().getConsent<OBWriteInternationalConsentResponse2>(
            payment3_1_2.Links.links.CreateInternationalPaymentConsent + "/" + consent.data.consentId,
            tppResource.tpp
        )
        val fundsConfirmationUrl = payment3_1_2.Links.links.GetInternationalPaymentConsentsConsentIdFundsConfirmation
            .replace("{ConsentId}", consent.data.consentId)
        val accessTokenAuthorizationCode = FundsConfirmationsAS().headlessAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        val fundsConfirmationResult = PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
            fundsConfirmationUrl,
            accessTokenAuthorizationCode
        )

        // Then
        assertThat(getConsentResult).isNotNull()
        assertThat(getConsentResult.data.consentId).isNotEmpty()
        Assertions.assertThat(getConsentResult.data.status.toString()).`is`(Status.consentCondition)
        assertThat(getConsentResult.links.self).isEqualTo(payment3_1_2.Links.links.CreateInternationalPaymentConsent + "/" + consent.data.consentId)
        assertThat(getConsentResult.meta).isNotNull()
        // then fundsConfirmation asserts
        assertThat(fundsConfirmationResult).isNotNull()
        assertThat(fundsConfirmationResult.data.fundsAvailableResult.isFundsAvailable).isTrue()
        assertThat(fundsConfirmationResult.links.self).isEqualTo(fundsConfirmationUrl)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.2",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["domestic-payment-consents"]
    )
    @Test
    @DisplayName("shouldGet_Wrong_grant_type_SingleInternationalPayment_FundsConfirmation_v3_1_2() ")
    fun shouldGet_WrongGrantType_fundsConfirmation_v3_1_2() {
        // Given
        val consentRequest = aValidOBWriteInternationalConsent2()

        // When
        val consent = PaymentRS().consent<OBWriteInternationalConsentResponse2>(
            payment3_1_2.Links.links.CreateInternationalPaymentConsent,
            consentRequest,
            tppResource.tpp
        )
        val fundsConfirmationUrl = payment3_1_2.Links.links.GetInternationalPaymentConsentsConsentIdFundsConfirmation
            .replace("{ConsentId}", consent.data.consentId)
        val accessTokenClientCredentials = PaymentAS().clientCredentialsAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            tppResource.tpp
        )

        // then
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
                fundsConfirmationUrl,
                accessTokenClientCredentials
            )
        }
        assertThat(exception.message.toString()).contains("The access token grant type CLIENT_CREDENTIAL doesn't match one of the expected grant types")
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(403)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.6",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-payment-consents"]
    )
    @Test
    @DisplayName("shouldGet_SingleInternationalPayment_FundsConfirmation_v3_1_6() ")
    fun shouldGet_FundsConfirmation_v3_1_6() {
        // Given
        val consentRequest = aValidOBWriteInternationalConsent2()

        // When
        val consent = PaymentRS().consent<OBWriteInternationalConsentResponse5>(
            payment3_1_6.Links.links.CreateInternationalPaymentConsent,
            consentRequest,
            tppResource.tpp,
            v3_1_6
        )
        val getConsentResult = PaymentRS().getConsent<OBWriteInternationalConsentResponse5>(
            payment3_1_6.Links.links.CreateInternationalPaymentConsent + "/" + consent.data.consentId,
            tppResource.tpp
        )
        val fundsConfirmationUrl = payment3_1_6.Links.links.GetInternationalPaymentConsentsConsentIdFundsConfirmation
            .replace("{ConsentId}", consent.data.consentId)
        val accessTokenAuthorizationCode = FundsConfirmationsAS().headlessAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        val fundsConfirmationResult = PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
            fundsConfirmationUrl,
            accessTokenAuthorizationCode
        )

        // Then
        assertThat(getConsentResult).isNotNull()
        assertThat(getConsentResult.data.consentId).isNotEmpty()
        Assertions.assertThat(getConsentResult.data.status.toString()).`is`(Status.consentCondition)
        assertThat(getConsentResult.links.self).isEqualTo(payment3_1_6.Links.links.CreateInternationalPaymentConsent + "/" + consent.data.consentId)
        assertThat(getConsentResult.meta).isNotNull()
        // then fundsConfirmation asserts
        assertThat(fundsConfirmationResult).isNotNull()
        assertThat(fundsConfirmationResult.data.fundsAvailableResult.isFundsAvailable).isTrue()
        assertThat(fundsConfirmationResult.links.self).isEqualTo(fundsConfirmationUrl)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.6",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["domestic-payment-consents"]
    )
    @Test
    @DisplayName("shouldGet_Wrong_grant_type_SingleInternationalPayment_FundsConfirmation_v3_1_2() ")
    fun shouldGet_WrongGrantType_FundsConfirmation_v3_1_6() {
        // Given
        val consentRequest = aValidOBWriteInternationalConsent2()

        // When
        val consent = PaymentRS().consent<OBWriteInternationalConsentResponse5>(
            payment3_1_6.Links.links.CreateInternationalPaymentConsent,
            consentRequest,
            tppResource.tpp,
            v3_1_6
        )
        val fundsConfirmationUrl = payment3_1_6.Links.links.GetInternationalPaymentConsentsConsentIdFundsConfirmation
            .replace("{ConsentId}", consent.data.consentId)
        val accessTokenClientCredentials = PaymentAS().clientCredentialsAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            tppResource.tpp
        )

        // then
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            PaymentRS().getFundsConfirmation<OBWriteFundsConfirmationResponse1>(
                fundsConfirmationUrl,
                accessTokenClientCredentials
            )
        }
        assertThat(exception.message.toString()).contains("The access token grant type CLIENT_CREDENTIAL doesn't match one of the expected grant types")
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(403)
    }
}
