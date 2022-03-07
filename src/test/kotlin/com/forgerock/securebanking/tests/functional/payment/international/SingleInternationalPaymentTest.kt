package com.forgerock.securebanking.tests.functional.payment.international

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import com.forgerock.openbanking.common.model.version.OBVersion.v3_1_4
import com.forgerock.openbanking.common.model.version.OBVersion.v3_1_6
import com.forgerock.securebanking.framework.conditions.Status.consentCondition
import com.forgerock.securebanking.framework.conditions.Status.paymentCondition
import com.forgerock.securebanking.framework.configuration.psu
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.support.discovery.payment3_1_2
import com.forgerock.securebanking.support.discovery.payment3_1_4
import com.forgerock.securebanking.support.discovery.payment3_1_6
import com.forgerock.securebanking.support.payment.PaymentAS
import com.forgerock.securebanking.support.payment.PaymentRS
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.payment.*
import uk.org.openbanking.testsupport.payment.OBWriteInternationalConsentTestDataFactory.aValidOBWriteInternationalConsent2
import uk.org.openbanking.testsupport.payment.OBWriteInternationalConsentTestDataFactory.aValidOBWriteInternationalConsent5

class SingleInternationalPaymentTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.2",
        operations = ["CreateInternationalPaymentConsent"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldCreateSingleInternationalPaymentConsent_v3_1_2() {
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

        // Then
        assertThat(getConsentResult).isNotNull()
        assertThat(getConsentResult.data.consentId).isNotEmpty()
        Assertions.assertThat(getConsentResult.data.status.toString()).`is`(consentCondition)
        assertThat(getConsentResult.links.self).isEqualTo(payment3_1_2.Links.links.CreateInternationalPaymentConsent + "/" + consent.data.consentId)
        assertThat(getConsentResult.meta).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.2",
        operations = ["CreateInternationalPaymentConsent", "CreateInternationalPayment"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun shouldCreateSingleInternationalPayment_v3_1_2() {
        // Given
        val consentRequest = aValidOBWriteInternationalConsent2()
        val consent = PaymentRS().consent<OBWriteInternationalConsentResponse2>(
            payment3_1_2.Links.links.CreateInternationalPaymentConsent,
            consentRequest,
            tppResource.tpp
        )
        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().headlessAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentAS().clientCredentialsAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            tppResource.tpp
        )
        val paymentSubmissionRequest = OBWriteInternational2().data(
            OBWriteDataInternational2()
                .consentId(consent.data.consentId)
                .initiation(consent.data.initiation)
        ).risk(consent.risk)

        val submissionResp = PaymentRS().submitPayment<OBWriteInternationalResponse2>(
            payment3_1_2.Links.links.CreateInternationalPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            tppResource.tpp
        )
        // When
        val paymentResult = PaymentRS().getPayment<OBWriteInternationalResponse2>(
            payment3_1_2.Links.links.CreateInternationalPayment,
            submissionResp.data.internationalPaymentId,
            accessTokenClientCredentials
        )

        // Then
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.internationalPaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_2.Links.links.CreateInternationalPayment + "/" + submissionResp.data.internationalPaymentId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateInternationalPaymentConsent", "CreateInternationalPayment"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun shouldCreateSingleInternationalPayment_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteInternationalConsent5()
        val consent = PaymentRS().consent<OBWriteInternationalConsentResponse5>(
            payment3_1_4.Links.links.CreateInternationalPaymentConsent,
            consentRequest,
            tppResource.tpp,
            v3_1_4
        )
        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().headlessAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentAS().clientCredentialsAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            tppResource.tpp
        )
        val paymentSubmissionRequest = OBWriteInternational3().data(
            OBWriteInternational3Data()
                .consentId(consent.data.consentId)
                .initiation(consent.data.initiation)
        ).risk(consent.risk)

        val submissionResp = PaymentRS().submitPayment<OBWriteInternationalResponse5>(
            payment3_1_4.Links.links.CreateInternationalPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            tppResource.tpp,
            v3_1_4
        )
        // When
        val paymentResult = PaymentRS().getPayment<OBWriteInternationalResponse4>(
            payment3_1_4.Links.links.CreateInternationalPayment,
            submissionResp.data.internationalPaymentId,
            accessTokenClientCredentials,
            v3_1_4
        )

        // Then
        assertThat(consent.data.readRefundAccount).isEqualTo(OBReadRefundAccountEnum.NO)
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.refund).isNull()
        assertThat(paymentResult.data.internationalPaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_4.Links.links.CreateInternationalPayment + "/" + submissionResp.data.internationalPaymentId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateInternationalPaymentConsent", "CreateInternationalPayment"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun shouldCreateSingleInternationalPayment_v3_1_4_readRefund() {
        // Given
        val consentRequest = aValidOBWriteInternationalConsent5()
        consentRequest.data.readRefundAccount(OBReadRefundAccountEnum.YES)
        val consent = PaymentRS().consent<OBWriteInternationalConsentResponse5>(
            payment3_1_4.Links.links.CreateInternationalPaymentConsent,
            consentRequest,
            tppResource.tpp,
            v3_1_4
        )
        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().headlessAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentAS().clientCredentialsAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            tppResource.tpp
        )
        val paymentSubmissionRequest = OBWriteInternational3().data(
            OBWriteInternational3Data()
                .consentId(consent.data.consentId)
                .initiation(consent.data.initiation)
        ).risk(consent.risk)

        val submissionResp = PaymentRS().submitPayment<OBWriteInternationalResponse5>(
            payment3_1_4.Links.links.CreateInternationalPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            tppResource.tpp,
            v3_1_4
        )
        // When
        val paymentResult = PaymentRS().getPayment<OBWriteInternationalResponse5>(
            payment3_1_4.Links.links.CreateInternationalPayment,
            submissionResp.data.internationalPaymentId,
            accessTokenClientCredentials,
            v3_1_4
        )

        // Then
        assertThat(consent.data.readRefundAccount).isEqualTo(OBReadRefundAccountEnum.YES)
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.refund.account.identification).isEqualTo(consent.data.initiation.debtorAccount.identification)
        assertThat(paymentResult.data.refund.creditor.name).isEqualTo(consent.data.initiation.creditor.name)
        assertThat(paymentResult.data.refund.agent.identification).isEqualTo(consent.data.initiation.creditorAgent.identification)
        assertThat(paymentResult.data.internationalPaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_4.Links.links.CreateInternationalPayment + "/" + submissionResp.data.internationalPaymentId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateInternationalPaymentConsent", "CreateInternationalPayment"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    @DisplayName("shouldCreateSingleInternationalPayment_v3_1_4_readRefund_debtorAccountNotPresent() ")
    fun shouldCreate_v314_readRefund_debtorAccountNotPresent() {
        // Given
        val consentRequest = aValidOBWriteInternationalConsent5()
        consentRequest.data.readRefundAccount(OBReadRefundAccountEnum.YES)
        consentRequest.data.initiation.debtorAccount(null)
        val consent = PaymentRS().consent<OBWriteInternationalConsentResponse5>(
            payment3_1_4.Links.links.CreateInternationalPaymentConsent,
            consentRequest,
            tppResource.tpp,
            v3_1_4
        )
        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().headlessAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentAS().clientCredentialsAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            tppResource.tpp
        )
        val paymentSubmissionRequest = OBWriteInternational3().data(
            OBWriteInternational3Data()
                .consentId(consent.data.consentId)
                .initiation(consent.data.initiation)
        ).risk(consent.risk)

        val submissionResp = PaymentRS().submitPayment<OBWriteInternationalResponse5>(
            payment3_1_4.Links.links.CreateInternationalPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            tppResource.tpp,
            v3_1_4
        )
        // When
        val paymentResult = PaymentRS().getPayment<OBWriteInternationalResponse5>(
            payment3_1_4.Links.links.CreateInternationalPayment,
            submissionResp.data.internationalPaymentId,
            accessTokenClientCredentials,
            v3_1_4
        )

        // Then
        assertThat(consent.data.readRefundAccount).isEqualTo(OBReadRefundAccountEnum.YES)
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.refund).isNull()
        assertThat(paymentResult.data.internationalPaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_4.Links.links.CreateInternationalPayment + "/" + submissionResp.data.internationalPaymentId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateInternationalPaymentConsent", "CreateInternationalPayment"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun shouldCreateSingleInternationalPayment_v3_1_4_readRefund_null() {
        // Given
        val consentRequest = aValidOBWriteInternationalConsent5()
        consentRequest.data.readRefundAccount(null)
        val consent = PaymentRS().consent<OBWriteInternationalConsentResponse5>(
            payment3_1_4.Links.links.CreateInternationalPaymentConsent,
            consentRequest,
            tppResource.tpp,
            v3_1_4
        )
        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().headlessAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentAS().clientCredentialsAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            tppResource.tpp
        )
        val paymentSubmissionRequest = OBWriteInternational3().data(
            OBWriteInternational3Data()
                .consentId(consent.data.consentId)
                .initiation(consent.data.initiation)
        ).risk(consent.risk)

        val submissionResp = PaymentRS().submitPayment<OBWriteInternationalResponse5>(
            payment3_1_4.Links.links.CreateInternationalPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            tppResource.tpp,
            v3_1_4
        )
        // When
        val paymentResult = PaymentRS().getPayment<OBWriteInternationalResponse5>(
            payment3_1_4.Links.links.CreateInternationalPayment,
            submissionResp.data.internationalPaymentId,
            accessTokenClientCredentials,
            v3_1_4
        )

        // Then
        assertThat(consent.data.readRefundAccount).isNull()
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.refund).isNull()
        assertThat(paymentResult.data.internationalPaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_4.Links.links.CreateInternationalPayment + "/" + submissionResp.data.internationalPaymentId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.6",
        operations = ["CreateInternationalPaymentConsent", "CreateInternationalPayment"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun shouldCreateSingleInternationalPayment_v3_1_6() {
        // Given
        val consentRequest = aValidOBWriteInternationalConsent5()
        val consent = PaymentRS().consent<OBWriteInternationalConsentResponse6>(
            payment3_1_6.Links.links.CreateInternationalPaymentConsent,
            consentRequest,
            tppResource.tpp,
            v3_1_6
        )
        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().headlessAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentAS().clientCredentialsAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            tppResource.tpp
        )
        val paymentSubmissionRequest = OBWriteInternational3().data(
            OBWriteInternational3Data()
                .consentId(consent.data.consentId)
                .initiation(consent.data.initiation)
        ).risk(consent.risk)

        val submissionResp = PaymentRS().submitPayment<OBWriteInternationalResponse5>(
            payment3_1_6.Links.links.CreateInternationalPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            tppResource.tpp,
            v3_1_6
        )
        // When
        val paymentResult = PaymentRS().getPayment<OBWriteInternationalResponse5>(
            payment3_1_6.Links.links.CreateInternationalPayment,
            submissionResp.data.internationalPaymentId,
            accessTokenClientCredentials,
            v3_1_6
        )

        // Then
        assertThat(consent.data.readRefundAccount).isEqualTo(OBReadRefundAccountEnum.NO)
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.refund).isNull()
        assertThat(paymentResult.data.internationalPaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        assertThat(paymentResult.data.debtor.name).isEqualTo(consentRequest.data.initiation.debtorAccount.name)
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_6.Links.links.CreateInternationalPayment + "/" + submissionResp.data.internationalPaymentId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.6",
        operations = ["CreateInternationalPaymentConsent", "CreateInternationalPayment"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun shouldCreateSingleInternationalPayment_v3_1_6_readRefund() {
        // Given
        val consentRequest = aValidOBWriteInternationalConsent5()
        consentRequest.data.readRefundAccount(OBReadRefundAccountEnum.YES)
        val consent = PaymentRS().consent<OBWriteInternationalConsentResponse6>(
            payment3_1_6.Links.links.CreateInternationalPaymentConsent,
            consentRequest,
            tppResource.tpp,
            v3_1_6
        )
        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().headlessAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentAS().clientCredentialsAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            tppResource.tpp
        )
        val paymentSubmissionRequest = OBWriteInternational3().data(
            OBWriteInternational3Data()
                .consentId(consent.data.consentId)
                .initiation(consent.data.initiation)
        ).risk(consent.risk)

        val submissionResp = PaymentRS().submitPayment<OBWriteInternationalResponse5>(
            payment3_1_6.Links.links.CreateInternationalPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            tppResource.tpp,
            v3_1_6
        )
        // When
        val paymentResult = PaymentRS().getPayment<OBWriteInternationalResponse5>(
            payment3_1_6.Links.links.CreateInternationalPayment,
            submissionResp.data.internationalPaymentId,
            accessTokenClientCredentials,
            v3_1_6
        )

        // Then
        assertThat(consent.data.readRefundAccount).isEqualTo(OBReadRefundAccountEnum.YES)
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.refund.account.identification).isEqualTo(consent.data.initiation.debtorAccount.identification)
        assertThat(paymentResult.data.refund.creditor.name).isEqualTo(consent.data.initiation.creditor.name)
        assertThat(paymentResult.data.refund.agent.identification).isEqualTo(consent.data.initiation.creditorAgent.identification)
        assertThat(paymentResult.data.internationalPaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        assertThat(paymentResult.data.debtor.name).isEqualTo(consentRequest.data.initiation.debtorAccount.name)
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_6.Links.links.CreateInternationalPayment + "/" + submissionResp.data.internationalPaymentId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.6",
        operations = ["CreateInternationalPaymentConsent", "CreateInternationalPayment"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    @DisplayName("shouldCreateSingleInternationalPayment_v3_1_6_readRefund_debtorAccountNotPresent() ")
    fun shouldCreate_v316_readRefund_debtorAccountNotPresent() {
        // Given
        val consentRequest = aValidOBWriteInternationalConsent5()
        consentRequest.data.readRefundAccount(OBReadRefundAccountEnum.YES)
        consentRequest.data.initiation.debtorAccount(null)
        val consent = PaymentRS().consent<OBWriteInternationalConsentResponse6>(
            payment3_1_6.Links.links.CreateInternationalPaymentConsent,
            consentRequest,
            tppResource.tpp,
            v3_1_6
        )
        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().headlessAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentAS().clientCredentialsAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            tppResource.tpp
        )
        val paymentSubmissionRequest = OBWriteInternational3().data(
            OBWriteInternational3Data()
                .consentId(consent.data.consentId)
                .initiation(consent.data.initiation)
        ).risk(consent.risk)

        val submissionResp = PaymentRS().submitPayment<OBWriteInternationalResponse5>(
            payment3_1_6.Links.links.CreateInternationalPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            tppResource.tpp,
            v3_1_6
        )
        // When
        val paymentResult = PaymentRS().getPayment<OBWriteInternationalResponse5>(
            payment3_1_6.Links.links.CreateInternationalPayment,
            submissionResp.data.internationalPaymentId,
            accessTokenClientCredentials,
            v3_1_6
        )

        // Then
        assertThat(consent.data.readRefundAccount).isEqualTo(OBReadRefundAccountEnum.YES)
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.refund).isNull()
        assertThat(paymentResult.data.internationalPaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_6.Links.links.CreateInternationalPayment + "/" + submissionResp.data.internationalPaymentId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.6",
        operations = ["CreateInternationalPaymentConsent", "CreateInternationalPayment"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun shouldCreateSingleInternationalPayment_v3_1_6_readRefund_null() {
        // Given
        val consentRequest = aValidOBWriteInternationalConsent5()
        consentRequest.data.readRefundAccount(null)
        val consent = PaymentRS().consent<OBWriteInternationalConsentResponse6>(
            payment3_1_6.Links.links.CreateInternationalPaymentConsent,
            consentRequest,
            tppResource.tpp,
            v3_1_6
        )
        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().headlessAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentAS().clientCredentialsAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            tppResource.tpp
        )
        val paymentSubmissionRequest = OBWriteInternational3().data(
            OBWriteInternational3Data()
                .consentId(consent.data.consentId)
                .initiation(consent.data.initiation)
        ).risk(consent.risk)

        val submissionResp = PaymentRS().submitPayment<OBWriteInternationalResponse5>(
            payment3_1_6.Links.links.CreateInternationalPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            tppResource.tpp,
            v3_1_6
        )
        // When
        val paymentResult = PaymentRS().getPayment<OBWriteInternationalResponse5>(
            payment3_1_6.Links.links.CreateInternationalPayment,
            submissionResp.data.internationalPaymentId,
            accessTokenClientCredentials,
            v3_1_6
        )

        // Then
        assertThat(consent.data.readRefundAccount).isNull()
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.refund).isNull()
        assertThat(paymentResult.data.internationalPaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_6.Links.links.CreateInternationalPayment + "/" + submissionResp.data.internationalPaymentId)
        assertThat(paymentResult.meta).isNotNull()
    }
}
