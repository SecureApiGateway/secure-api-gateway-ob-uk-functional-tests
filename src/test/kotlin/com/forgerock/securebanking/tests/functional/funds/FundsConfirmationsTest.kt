package com.forgerock.securebanking.tests.functional.funds

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.openbanking.common.model.version.OBVersion
import com.forgerock.securebanking.framework.conditions.Status
import com.forgerock.securebanking.framework.configuration.psu
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.support.discovery.fundsConfirmations3_0
import com.forgerock.securebanking.support.discovery.fundsConfirmations3_1_2
import com.forgerock.securebanking.support.discovery.fundsConfirmations3_1_3
import com.forgerock.securebanking.support.discovery.fundsConfirmations3_1_6
import com.forgerock.securebanking.support.funds.FundsConfirmationRS
import com.forgerock.securebanking.support.funds.FundsConfirmationsAS
import org.assertj.core.api.Assertions
import org.joda.time.DateTime
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.account.OBCashAccount3
import uk.org.openbanking.datamodel.fund.*
import uk.org.openbanking.testsupport.payment.OBAmountTestDataFactory.aValidOBActiveOrHistoricCurrencyAndAmount

class FundsConfirmationsTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "funds",
        apiVersion = "v3.0",
        operations = ["CreateFundsConfirmationConsent", "GetFundsConfirmationConsent"],
        apis = ["funds-confirmation-consents"]
    )
    @Test
    fun shouldCreateFundsConfirmationConsent_v3_0() {
        // Given
        val consentRequest = getConsentRequest()

        // When
        val consent = FundsConfirmationRS().consent<OBFundsConfirmationConsentResponse1>(
            fundsConfirmations3_0.Links.links.CreateFundsConfirmationConsent,
            consentRequest,
            tppResource.tpp
        )
        val getConsentResult = FundsConfirmationRS().getConsent<OBFundsConfirmationConsentResponse1>(
            fundsConfirmations3_0.Links.links.CreateFundsConfirmationConsent + "/" + consent.data.consentId,
            tppResource.tpp
        )

        // Then
        assertThat(getConsentResult).isNotNull()
        assertThat(getConsentResult.data.consentId).isNotEmpty()
        Assertions.assertThat(getConsentResult.data.status.toString()).`is`(Status.consentCondition)
        assertThat(getConsentResult.links.self).isEqualTo(fundsConfirmations3_0.Links.links.CreateFundsConfirmationConsent + "/" + consent.data.consentId)
    }

    @EnabledIfVersion(
        type = "funds",
        apiVersion = "v3.0",
        operations = ["CreateFundsConfirmationConsent", "CreateFundsConfirmation", "GetFundsConfirmation"],
        apis = ["funds-confirmations"]
    )
    @Test
    fun shouldCreateFundsConfirmation_v3_0() {
        // Given
        val consentRequest = getConsentRequest()
        val consent = FundsConfirmationRS().consent<OBFundsConfirmationConsentResponse1>(
            fundsConfirmations3_0.Links.links.CreateFundsConfirmationConsent,
            consentRequest,
            tppResource.tpp
        )
        assertThat(consent).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        val obFundsConfirmation1Request = getFundConfirmationRequest(consent)
        // When
        // accessToken to submit fundsConfirmations use the grant type Authorization_code
        val accesstokenAuthorizationCode = FundsConfirmationsAS().headlessAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        val submisionResponse = FundsConfirmationRS().submitFundConfirmation<OBFundsConfirmationResponse1>(
            fundsConfirmations3_0.Links.links.CreateFundsConfirmation,
            obFundsConfirmation1Request,
            accesstokenAuthorizationCode,
            tppResource.tpp,
            OBVersion.v3_0
        )

        // Then
        assertThat(consent).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.links.self).isEqualTo(fundsConfirmations3_0.Links.links.CreateFundsConfirmationConsent + "/" + consent.data.consentId)
        assertThat(submisionResponse).isNotNull()
        assertThat(submisionResponse.links.self).isEqualTo(fundsConfirmations3_0.Links.links.CreateFundsConfirmation)
        assertThat(submisionResponse.data.fundsConfirmationId).isEqualTo(consent.data.consentId)
        assertThat(submisionResponse.data.reference).isEqualTo(obFundsConfirmation1Request.data.reference)
        assertThat(submisionResponse.data.consentId).isEqualTo(obFundsConfirmation1Request.data.consentId)
    }

    @EnabledIfVersion(
        type = "funds",
        apiVersion = "v3.1.2",
        operations = ["CreateFundsConfirmationConsent", "GetFundsConfirmationConsent"],
        apis = ["funds-confirmation-consents"]
    )
    @Test
    fun shouldCreateFundsConfirmationConsent_v3_1_2() {
        // Given
        val consentRequest = getConsentRequest()

        // When
        val consent = FundsConfirmationRS().consent<OBFundsConfirmationConsentResponse1>(
            fundsConfirmations3_1_2.Links.links.CreateFundsConfirmationConsent,
            consentRequest,
            tppResource.tpp
        )
        val getConsentResult = FundsConfirmationRS().getConsent<OBFundsConfirmationConsentResponse1>(
            fundsConfirmations3_1_2.Links.links.CreateFundsConfirmationConsent + "/" + consent.data.consentId,
            tppResource.tpp
        )

        // Then
        assertThat(getConsentResult).isNotNull()
        assertThat(getConsentResult.data.consentId).isNotEmpty()
        Assertions.assertThat(getConsentResult.data.status.toString()).`is`(Status.consentCondition)
        assertThat(getConsentResult.links.self).isEqualTo(fundsConfirmations3_1_2.Links.links.CreateFundsConfirmationConsent + "/" + consent.data.consentId)
    }

    @EnabledIfVersion(
        type = "funds",
        apiVersion = "v3.1.2",
        operations = ["CreateFundsConfirmationConsent", "CreateFundsConfirmation", "GetFundsConfirmation"],
        apis = ["funds-confirmations"]
    )
    @Test
    fun shouldCreateFundsConfirmation_v3_1_2() {
        // Given
        val consentRequest = getConsentRequest()
        val consent = FundsConfirmationRS().consent<OBFundsConfirmationConsentResponse1>(
            fundsConfirmations3_1_2.Links.links.CreateFundsConfirmationConsent,
            consentRequest,
            tppResource.tpp
        )
        assertThat(consent).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        val obFundsConfirmation1Request = getFundConfirmationRequest(consent)
        // When
        // accessToken to submit fundsConfirmations use the grant type Authorization_code
        val accesstokenAuthorizationCode = FundsConfirmationsAS().headlessAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        val submisionResponse = FundsConfirmationRS().submitFundConfirmation<OBFundsConfirmationResponse1>(
            fundsConfirmations3_1_2.Links.links.CreateFundsConfirmation,
            obFundsConfirmation1Request,
            accesstokenAuthorizationCode,
            tppResource.tpp,
            OBVersion.v3_1_2
        )

        // Then
        assertThat(consent).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.links.self).isEqualTo(fundsConfirmations3_1_2.Links.links.CreateFundsConfirmationConsent + "/" + consent.data.consentId)
        assertThat(submisionResponse).isNotNull()
        assertThat(submisionResponse.links.self).isEqualTo(fundsConfirmations3_1_2.Links.links.CreateFundsConfirmation)
        assertThat(submisionResponse.data.fundsConfirmationId).isEqualTo(consent.data.consentId)
        assertThat(submisionResponse.data.reference).isEqualTo(obFundsConfirmation1Request.data.reference)
        assertThat(submisionResponse.data.consentId).isEqualTo(obFundsConfirmation1Request.data.consentId)
    }

    @EnabledIfVersion(
        type = "funds",
        apiVersion = "v3.1.3",
        operations = ["CreateFundsConfirmationConsent", "GetFundsConfirmationConsent"],
        apis = ["funds-confirmation-consents"]
    )
    @Test
    fun shouldCreateFundsConfirmationConsent_v3_1_3() {
        // Given
        val consentRequest = getConsentRequest()

        // When
        val consent = FundsConfirmationRS().consent<OBFundsConfirmationConsentResponse1>(
            fundsConfirmations3_1_3.Links.links.CreateFundsConfirmationConsent,
            consentRequest,
            tppResource.tpp
        )
        val getConsentResult = FundsConfirmationRS().getConsent<OBFundsConfirmationConsentResponse1>(
            fundsConfirmations3_1_3.Links.links.CreateFundsConfirmationConsent + "/" + consent.data.consentId,
            tppResource.tpp
        )

        // Then
        assertThat(getConsentResult).isNotNull()
        assertThat(getConsentResult.data.consentId).isNotEmpty()
        Assertions.assertThat(getConsentResult.data.status.toString()).`is`(Status.consentCondition)
        assertThat(getConsentResult.links.self).isEqualTo(fundsConfirmations3_1_3.Links.links.CreateFundsConfirmationConsent + "/" + consent.data.consentId)
    }

    @EnabledIfVersion(
        type = "funds",
        apiVersion = "v3.1.3",
        operations = ["CreateFundsConfirmationConsent", "CreateFundsConfirmation", "GetFundsConfirmation"],
        apis = ["funds-confirmations"]
    )
    @Test
    fun shouldCreateFundsConfirmation_v3_1_3() {
        // Given
        val consentRequest = getConsentRequest()
        val consent = FundsConfirmationRS().consent<OBFundsConfirmationConsentResponse1>(
            fundsConfirmations3_1_3.Links.links.CreateFundsConfirmationConsent,
            consentRequest,
            tppResource.tpp
        )
        assertThat(consent).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        val obFundsConfirmation1Request = getFundConfirmationRequest(consent)
        // When
        // accessToken to submit fundsConfirmations use the grant type Authorization_code
        val accesstokenAuthorizationCode = FundsConfirmationsAS().headlessAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        val submisionResponse = FundsConfirmationRS().submitFundConfirmation<OBFundsConfirmationResponse1>(
            fundsConfirmations3_1_3.Links.links.CreateFundsConfirmation,
            obFundsConfirmation1Request,
            accesstokenAuthorizationCode,
            tppResource.tpp,
            OBVersion.v3_1_3
        )

        // Then
        assertThat(consent).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.links.self).isEqualTo(fundsConfirmations3_1_3.Links.links.CreateFundsConfirmationConsent + "/" + consent.data.consentId)
        assertThat(submisionResponse).isNotNull()
        assertThat(submisionResponse.links.self).isEqualTo(fundsConfirmations3_1_3.Links.links.CreateFundsConfirmation)
        assertThat(submisionResponse.data.fundsConfirmationId).isEqualTo(consent.data.consentId)
        assertThat(submisionResponse.data.reference).isEqualTo(obFundsConfirmation1Request.data.reference)
        assertThat(submisionResponse.data.consentId).isEqualTo(obFundsConfirmation1Request.data.consentId)
    }

    @EnabledIfVersion(
        type = "funds",
        apiVersion = "v3.1.6",
        operations = ["CreateFundsConfirmationConsent", "GetFundsConfirmationConsent"],
        apis = ["funds-confirmation-consents"]
    )
    @Test
    fun shouldCreateFundsConfirmationConsent_v3_1_6() {
        // Given
        val consentRequest = getConsentRequest()

        // When
        val consent = FundsConfirmationRS().consent<OBFundsConfirmationConsentResponse1>(
            fundsConfirmations3_1_6.Links.links.CreateFundsConfirmationConsent,
            consentRequest,
            tppResource.tpp
        )
        val getConsentResult = FundsConfirmationRS().getConsent<OBFundsConfirmationConsentResponse1>(
            fundsConfirmations3_1_6.Links.links.CreateFundsConfirmationConsent + "/" + consent.data.consentId,
            tppResource.tpp
        )

        // Then
        assertThat(getConsentResult).isNotNull()
        assertThat(getConsentResult.data.consentId).isNotEmpty()
        Assertions.assertThat(getConsentResult.data.status.toString()).`is`(Status.consentCondition)
        assertThat(getConsentResult.links.self).isEqualTo(fundsConfirmations3_1_6.Links.links.CreateFundsConfirmationConsent + "/" + consent.data.consentId)
    }

    @EnabledIfVersion(
        type = "funds",
        apiVersion = "v3.1.6",
        operations = ["CreateFundsConfirmationConsent", "CreateFundsConfirmation", "GetFundsConfirmation"],
        apis = ["funds-confirmations"]
    )
    @Test
    fun shouldCreateFundsConfirmation_v3_1_6() {
        // Given
        val consentRequest = getConsentRequest()
        val consent = FundsConfirmationRS().consent<OBFundsConfirmationConsentResponse1>(
            fundsConfirmations3_1_6.Links.links.CreateFundsConfirmationConsent,
            consentRequest,
            tppResource.tpp
        )
        assertThat(consent).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        val obFundsConfirmation1Request = getFundConfirmationRequest(consent)
        // When
        // accessToken to submit fundsConfirmations use the grant type Authorization_code
        val accesstokenAuthorizationCode = FundsConfirmationsAS().headlessAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        val submisionResponse = FundsConfirmationRS().submitFundConfirmation<OBFundsConfirmationResponse1>(
            fundsConfirmations3_1_6.Links.links.CreateFundsConfirmation,
            obFundsConfirmation1Request,
            accesstokenAuthorizationCode,
            tppResource.tpp,
            OBVersion.v3_1_6
        )

        // Then
        assertThat(consent).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.links.self).isEqualTo(fundsConfirmations3_1_6.Links.links.CreateFundsConfirmationConsent + "/" + consent.data.consentId)
        assertThat(submisionResponse).isNotNull()
        assertThat(submisionResponse.links.self).isEqualTo(fundsConfirmations3_1_6.Links.links.CreateFundsConfirmation)
        assertThat(submisionResponse.data.fundsConfirmationId).isEqualTo(consent.data.consentId)
        assertThat(submisionResponse.data.reference).isEqualTo(obFundsConfirmation1Request.data.reference)
        assertThat(submisionResponse.data.consentId).isEqualTo(obFundsConfirmation1Request.data.consentId)
    }

    private fun getConsentRequest(): OBFundsConfirmationConsent1 {
        return OBFundsConfirmationConsent1().data(
            OBFundsConfirmationConsentData1()
                .debtorAccount(
                    OBCashAccount3()
                        .identification("12345678")
                        .name("Account Name")
                        .schemeName("SortCodeAccountNumber")
                        .secondaryIdentification("2-12345678")
                )
                .expirationDateTime(DateTime.now().plusHours(1))
        )
    }

    private fun getFundConfirmationRequest(consent: OBFundsConfirmationConsentResponse1): OBFundsConfirmation1 {
        return OBFundsConfirmation1()
            .data(
                OBFundsConfirmationData1()
                    .consentId(consent.data.consentId)
                    .reference(("test-" + consent.data.consentId).substring(0, 35))
                    .instructedAmount(aValidOBActiveOrHistoricCurrencyAndAmount())
            )
    }

}
