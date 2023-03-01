package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.payments.consents.legacy

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.sapi.gateway.framework.conditions.Status
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.framework.http.fuel.defaultMapper
import com.forgerock.sapi.gateway.framework.signature.signPayloadSubmitPayment
import com.forgerock.sapi.gateway.ob.uk.support.discovery.*
import com.forgerock.sapi.gateway.ob.uk.support.payment.PaymentFactory
import com.forgerock.sapi.gateway.ob.uk.support.payment.PaymentRS
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.payment.*
import uk.org.openbanking.testsupport.payment.OBWriteInternationalConsentTestDataFactory.*

class LegacyGetInternationalPaymentsConsentsTest(val tppResource: CreateTppCallback.TppResource) {
    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldGetInternationalPaymentsConsents_rateType_AGREED_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteInternationalConsent5()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.AGREED

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )


        val consent = PaymentRS().consent<OBWriteInternationalConsentResponse5>(
            payment3_1_4.Links.links.CreateInternationalPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_4,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()
        assertThat(consent.data.initiation.exchangeRateInformation.exchangeRate).isNotNull()

        // When
        val result = PaymentRS().getConsent<OBWriteInternationalConsentResponse5>(
            PaymentFactory.urlWithConsentId(
                payment3_1_4.Links.links.GetInternationalPaymentConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            OBVersion.v3_1_4
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.risk).isNotNull()
        assertThat(result.data).isEqualTo(consent.data)
        assertThat(result.risk).isEqualTo(consent.risk)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldGetInternationalPaymentsConsents_rateType_ACTUAL_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteInternationalConsent5()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.ACTUAL
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteInternationalConsentResponse5>(
            payment3_1_4.Links.links.CreateInternationalPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_4,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()

        // When
        val result = PaymentRS().getConsent<OBWriteInternationalConsentResponse5>(
            PaymentFactory.urlWithConsentId(
                payment3_1_4.Links.links.GetInternationalPaymentConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            OBVersion.v3_1_4
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.risk).isNotNull()
        assertThat(result.data).isEqualTo(consent.data)
        assertThat(result.risk).isEqualTo(consent.risk)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldGetInternationalPaymentsConsents_rateType_INDICATIVE_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteInternationalConsent5()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.INDICATIVE
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )


        val consent = PaymentRS().consent<OBWriteInternationalConsentResponse5>(
            payment3_1_4.Links.links.CreateInternationalPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_4,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()

        // When
        val result = PaymentRS().getConsent<OBWriteInternationalConsentResponse5>(
            PaymentFactory.urlWithConsentId(
                payment3_1_4.Links.links.GetInternationalPaymentConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            OBVersion.v3_1_4
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.risk).isNotNull()
        assertThat(result.data).isEqualTo(consent.data)
        assertThat(result.risk).isEqualTo(consent.risk)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldGetInternationalPaymentsConsents_rateType_AGREED_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteInternationalConsent4()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.AGREED

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        val consent = PaymentRS().consent<OBWriteInternationalConsentResponse4>(
            payment3_1_3.Links.links.CreateInternationalPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_3,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()
        assertThat(consent.data.initiation.exchangeRateInformation.exchangeRate).isNotNull()

        // When
        val result = PaymentRS().getConsent<OBWriteInternationalConsentResponse4>(
            PaymentFactory.urlWithConsentId(
                payment3_1_3.Links.links.GetInternationalPaymentConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            OBVersion.v3_1_3
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.risk).isNotNull()
        assertThat(result.data).isEqualTo(consent.data)
        assertThat(result.risk).isEqualTo(consent.risk)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldGetInternationalPaymentsConsents_rateType_ACTUAL_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteInternationalConsent4()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.ACTUAL
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null


        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        val consent = PaymentRS().consent<OBWriteInternationalConsentResponse4>(
            payment3_1_3.Links.links.CreateInternationalPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_3,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()

        // When
        val result = PaymentRS().getConsent<OBWriteInternationalConsentResponse4>(
            PaymentFactory.urlWithConsentId(
                payment3_1_3.Links.links.GetInternationalPaymentConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            OBVersion.v3_1_3
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.risk).isNotNull()
        assertThat(result.data).isEqualTo(consent.data)
        assertThat(result.risk).isEqualTo(consent.risk)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldGetInternationalPaymentsConsents_rateType_INDICATIVE_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteInternationalConsent4()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.INDICATIVE
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        val consent = PaymentRS().consent<OBWriteInternationalConsentResponse4>(
            payment3_1_3.Links.links.CreateInternationalPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_3,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()

        // When
        val result = PaymentRS().getConsent<OBWriteInternationalConsentResponse4>(
            PaymentFactory.urlWithConsentId(
                payment3_1_3.Links.links.GetInternationalPaymentConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            OBVersion.v3_1_3
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.risk).isNotNull()
        assertThat(result.data).isEqualTo(consent.data)
        assertThat(result.risk).isEqualTo(consent.risk)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.2",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldGetInternationalPaymentsConsents_rateType_AGREED_v3_1_2() {
        // Given
        val consentRequest = aValidOBWriteInternationalConsent3()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.AGREED

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        val consent = PaymentRS().consent<OBWriteInternationalConsentResponse3>(
            payment3_1_2.Links.links.CreateInternationalPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_2,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()
        assertThat(consent.data.initiation.exchangeRateInformation.exchangeRate).isNotNull()

        // When
        val result = PaymentRS().getConsent<OBWriteInternationalConsentResponse3>(
            PaymentFactory.urlWithConsentId(
                payment3_1_2.Links.links.GetInternationalPaymentConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            OBVersion.v3_1_2
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.risk).isNotNull()
        assertThat(result.data).isEqualTo(consent.data)
        assertThat(result.risk).isEqualTo(consent.risk)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.2",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldGetInternationalPaymentsConsents_rateType_ACTUAL_v3_1_2() {
        // Given
        val consentRequest = aValidOBWriteInternationalConsent3()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.ACTUAL
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        val consent = PaymentRS().consent<OBWriteInternationalConsentResponse3>(
            payment3_1_2.Links.links.CreateInternationalPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_2,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()

        // When
        val result = PaymentRS().getConsent<OBWriteInternationalConsentResponse3>(
            PaymentFactory.urlWithConsentId(
                payment3_1_2.Links.links.GetInternationalPaymentConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            OBVersion.v3_1_2
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.risk).isNotNull()
        assertThat(result.data).isEqualTo(consent.data)
        assertThat(result.risk).isEqualTo(consent.risk)
    }


    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.2",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldGetInternationalPaymentsConsents_rateType_INDICATIVE_v3_1_2() {
        // Given
        val consentRequest = aValidOBWriteInternationalConsent3()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.INDICATIVE
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        val consent = PaymentRS().consent<OBWriteInternationalConsentResponse3>(
            payment3_1_2.Links.links.CreateInternationalPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_2,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()

        // When
        val result = PaymentRS().getConsent<OBWriteInternationalConsentResponse3>(
            PaymentFactory.urlWithConsentId(
                payment3_1_2.Links.links.GetInternationalPaymentConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            OBVersion.v3_1_2
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.risk).isNotNull()
        assertThat(result.data).isEqualTo(consent.data)
        assertThat(result.risk).isEqualTo(consent.risk)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldGetInternationalPaymentsConsents_rateType_AGREED_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalConsent3()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.AGREED

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        val consent = PaymentRS().consent<OBWriteInternationalConsentResponse3>(
            payment3_1_1.Links.links.CreateInternationalPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_1,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()
        assertThat(consent.data.initiation.exchangeRateInformation.exchangeRate).isNotNull()

        // When
        val result = PaymentRS().getConsent<OBWriteInternationalConsentResponse3>(
            PaymentFactory.urlWithConsentId(
                payment3_1_1.Links.links.GetInternationalPaymentConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            OBVersion.v3_1_1
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.risk).isNotNull()
        assertThat(result.data).isEqualTo(consent.data)
        assertThat(result.risk).isEqualTo(consent.risk)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldGetInternationalPaymentsConsents_rateType_ACTUAL_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalConsent3()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.ACTUAL
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        val consent = PaymentRS().consent<OBWriteInternationalConsentResponse3>(
            payment3_1_1.Links.links.CreateInternationalPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_1,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()

        // When
        val result = PaymentRS().getConsent<OBWriteInternationalConsentResponse3>(
            PaymentFactory.urlWithConsentId(
                payment3_1_1.Links.links.GetInternationalPaymentConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            OBVersion.v3_1_1
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.risk).isNotNull()
        assertThat(result.data).isEqualTo(consent.data)
        assertThat(result.risk).isEqualTo(consent.risk)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldGetInternationalPaymentsConsents_rateType_INDICATIVE_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalConsent3()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        val consent = PaymentRS().consent<OBWriteInternationalConsentResponse3>(
            payment3_1_1.Links.links.CreateInternationalPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_1,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()

        // When
        val result = PaymentRS().getConsent<OBWriteInternationalConsentResponse3>(
            PaymentFactory.urlWithConsentId(
                payment3_1_1.Links.links.GetInternationalPaymentConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            OBVersion.v3_1_1
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.risk).isNotNull()
        assertThat(result.data).isEqualTo(consent.data)
        assertThat(result.risk).isEqualTo(consent.risk)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldGetInternationalPaymentsConsents_rateType_AGREED_v3_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalConsent2()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        val consent = PaymentRS().consent<OBWriteInternationalConsentResponse2>(
            payment3_1.Links.links.CreateInternationalPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()
        assertThat(consent.data.initiation.exchangeRateInformation.exchangeRate).isNotNull()

        // When
        val result = PaymentRS().getConsent<OBWriteInternationalConsentResponse2>(
            PaymentFactory.urlWithConsentId(
                payment3_1.Links.links.GetInternationalPaymentConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            OBVersion.v3_1
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.risk).isNotNull()
        assertThat(result.data).isEqualTo(consent.data)
        assertThat(result.risk).isEqualTo(consent.risk)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldGetInternationalPaymentsConsents_rateType_ACTUAL_v3_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalConsent2()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.ACTUAL
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        val consent = PaymentRS().consent<OBWriteInternationalConsentResponse2>(
            payment3_1.Links.links.CreateInternationalPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()

        // When
        val result = PaymentRS().getConsent<OBWriteInternationalConsentResponse2>(
            PaymentFactory.urlWithConsentId(
                payment3_1.Links.links.GetInternationalPaymentConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            OBVersion.v3_1
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.risk).isNotNull()
        assertThat(result.data).isEqualTo(consent.data)
        assertThat(result.risk).isEqualTo(consent.risk)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldGetInternationalPaymentsConsents_rateType_INDICATIVE_v3_1() {
        // Given
        val consentRequest = aValidOBWriteInternationalConsent2()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.INDICATIVE
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        val consent = PaymentRS().consent<OBWriteInternationalConsentResponse2>(
            payment3_1.Links.links.CreateInternationalPaymentConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()

        // When
        val result = PaymentRS().getConsent<OBWriteInternationalConsentResponse2>(
            PaymentFactory.urlWithConsentId(
                payment3_1.Links.links.GetInternationalPaymentConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            OBVersion.v3_1
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.risk).isNotNull()
        assertThat(result.data).isEqualTo(consent.data)
        assertThat(result.risk).isEqualTo(consent.risk)
    }
}
