package com.forgerock.securebanking.payment.international

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import com.forgerock.openbanking.common.model.version.OBVersion.v3_1_4
import com.forgerock.openbanking.common.model.version.OBVersion.v3_1_6
import com.forgerock.securebanking.Status.consentCondition
import com.forgerock.securebanking.Status.paymentCondition
import com.forgerock.securebanking.discovery.payment3_1_2
import com.forgerock.securebanking.discovery.payment3_1_4
import com.forgerock.securebanking.discovery.payment3_1_6
import com.forgerock.securebanking.junit.CreateTppCallback
import com.forgerock.securebanking.junit.EnabledIfVersion
import com.forgerock.securebanking.payment.PaymentAS
import com.forgerock.securebanking.payment.PaymentRS
import com.forgerock.securebanking.psu
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.payment.*
import uk.org.openbanking.testsupport.payment.OBWriteInternationalScheduledConsentTestDataFactory.aValidOBWriteInternationalScheduledConsent3
import uk.org.openbanking.testsupport.payment.OBWriteInternationalScheduledConsentTestDataFactory.aValidOBWriteInternationalScheduledConsent5

class InternationalScheduledPaymentTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.2",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPaymentConsent_v3_1_2() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3()

        // When
        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse3>(
            payment3_1_2.Links.links.CreateInternationalScheduledPaymentConsent,
            consentRequest,
            tppResource.tpp
        )
        val getConsentResult = PaymentRS().getConsent<OBWriteInternationalScheduledConsentResponse3>(
            payment3_1_2.Links.links.CreateInternationalScheduledPaymentConsent + "/" + consent.data.consentId,
            tppResource.tpp
        )

        // Then
        assertThat(getConsentResult).isNotNull()
        assertThat(getConsentResult.data.consentId).isNotEmpty()
        Assertions.assertThat(getConsentResult.data.status.toString()).`is`(consentCondition)
        assertThat(getConsentResult.links.self).isEqualTo(payment3_1_2.Links.links.CreateInternationalScheduledPaymentConsent + "/" + consent.data.consentId)
        assertThat(getConsentResult.meta).isNotNull()

    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.2",
        operations = ["CreateInternationalScheduledPaymentConsent", "CreateInternationalScheduledPayment"],
        apis = ["international-scheduled-payments"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_v3_1_2() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent3()
        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse2>(
            payment3_1_2.Links.links.CreateInternationalScheduledPaymentConsent,
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

        val paymentSubmissionRequest = OBWriteInternationalScheduled2().data(
            OBWriteDataInternationalScheduled2()
                .consentId(consent.data.consentId)
                .initiation(consent.data.initiation)
        ).risk(consent.risk)


        val submissionResp = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse2>(
            payment3_1_2.Links.links.CreateInternationalScheduledPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            tppResource.tpp
        )
        // When
        val paymentResult = PaymentRS().getPayment<OBWriteInternationalScheduledResponse2>(
            payment3_1_2.Links.links.CreateInternationalScheduledPayment,
            submissionResp.data.internationalScheduledPaymentId,
            accessTokenClientCredentials
        )

        // Then
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.internationalScheduledPaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_2.Links.links.CreateInternationalScheduledPayment + "/" + submissionResp.data.internationalScheduledPaymentId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateInternationalScheduledPaymentConsent", "CreateInternationalScheduledPayment"],
        apis = ["international-scheduled-payments"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent5()
        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse5>(
            payment3_1_4.Links.links.CreateInternationalScheduledPaymentConsent,
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

        val paymentSubmissionRequest = OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
                .consentId(consent.data.consentId)
                .initiation(consent.data.initiation)
        ).risk(consent.risk)


        val submissionResp = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse5>(
            payment3_1_4.Links.links.CreateInternationalScheduledPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            tppResource.tpp,
            v3_1_4
        )
        // When
        val paymentResult = PaymentRS().getPayment<OBWriteInternationalScheduledResponse5>(
            payment3_1_4.Links.links.CreateInternationalScheduledPayment,
            submissionResp.data.internationalScheduledPaymentId,
            accessTokenClientCredentials,
            v3_1_4
        )

        // Then
        assertThat(consent.data.readRefundAccount).isEqualTo(OBReadRefundAccountEnum.NO)
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.refund).isNull()
        assertThat(paymentResult.data.internationalScheduledPaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_4.Links.links.CreateInternationalScheduledPayment + "/" + submissionResp.data.internationalScheduledPaymentId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateInternationalScheduledPaymentConsent", "CreateInternationalScheduledPayment"],
        apis = ["international-scheduled-payments"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_v3_1_4_readRefund() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent5()
        consentRequest.data.readRefundAccount(OBReadRefundAccountEnum.YES)
        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse5>(
            payment3_1_4.Links.links.CreateInternationalScheduledPaymentConsent,
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

        val paymentSubmissionRequest = OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
                .consentId(consent.data.consentId)
                .initiation(consent.data.initiation)
        ).risk(consent.risk)


        val submissionResp = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse5>(
            payment3_1_4.Links.links.CreateInternationalScheduledPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            tppResource.tpp,
            v3_1_4
        )
        // When
        val paymentResult = PaymentRS().getPayment<OBWriteInternationalScheduledResponse5>(
            payment3_1_4.Links.links.CreateInternationalScheduledPayment,
            submissionResp.data.internationalScheduledPaymentId,
            accessTokenClientCredentials,
            v3_1_4
        )

        // Then
        assertThat(consent.data.readRefundAccount).isEqualTo(OBReadRefundAccountEnum.YES)
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.refund.account.identification).isEqualTo(consent.data.initiation.debtorAccount.identification)
        assertThat(paymentResult.data.refund.creditor.name).isEqualTo(consent.data.initiation.creditor.name)
        assertThat(paymentResult.data.refund.agent.identification).isEqualTo(consent.data.initiation.creditorAgent.identification)
        assertThat(paymentResult.data.internationalScheduledPaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_4.Links.links.CreateInternationalScheduledPayment + "/" + submissionResp.data.internationalScheduledPaymentId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateInternationalScheduledPaymentConsent", "CreateInternationalScheduledPayment"],
        apis = ["international-scheduled-payments"]
    )
    @Test
    fun shouldCreateIntnlScheduledPayment_v3_1_4_readRefund_debtorAccountNotPresent() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent5()
        consentRequest.data.readRefundAccount(OBReadRefundAccountEnum.YES)
        consentRequest.data.initiation.debtorAccount(null)
        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse5>(
            payment3_1_4.Links.links.CreateInternationalScheduledPaymentConsent,
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

        val paymentSubmissionRequest = OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
                .consentId(consent.data.consentId)
                .initiation(consent.data.initiation)
        ).risk(consent.risk)


        val submissionResp = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse5>(
            payment3_1_4.Links.links.CreateInternationalScheduledPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            tppResource.tpp,
            v3_1_4
        )
        // When
        val paymentResult = PaymentRS().getPayment<OBWriteInternationalScheduledResponse5>(
            payment3_1_4.Links.links.CreateInternationalScheduledPayment,
            submissionResp.data.internationalScheduledPaymentId,
            accessTokenClientCredentials,
            v3_1_4
        )

        // Then
        assertThat(consent.data.readRefundAccount).isEqualTo(OBReadRefundAccountEnum.YES)
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.refund).isNull()
        assertThat(paymentResult.data.internationalScheduledPaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_4.Links.links.CreateInternationalScheduledPayment + "/" + submissionResp.data.internationalScheduledPaymentId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateInternationalScheduledPaymentConsent", "CreateInternationalScheduledPayment"],
        apis = ["international-scheduled-payments"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_v3_1_4_readRefund_null() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent5()
        consentRequest.data.readRefundAccount(null)
        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse5>(
            payment3_1_4.Links.links.CreateInternationalScheduledPaymentConsent,
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

        val paymentSubmissionRequest = OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
                .consentId(consent.data.consentId)
                .initiation(consent.data.initiation)
        ).risk(consent.risk)


        val submissionResp = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse5>(
            payment3_1_4.Links.links.CreateInternationalScheduledPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            tppResource.tpp,
            v3_1_4
        )
        // When
        val paymentResult = PaymentRS().getPayment<OBWriteInternationalScheduledResponse5>(
            payment3_1_4.Links.links.CreateInternationalScheduledPayment,
            submissionResp.data.internationalScheduledPaymentId,
            accessTokenClientCredentials,
            v3_1_4
        )

        // Then
        assertThat(consent.data.readRefundAccount).isNull()
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.refund).isNull()
        assertThat(paymentResult.data.internationalScheduledPaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_4.Links.links.CreateInternationalScheduledPayment + "/" + submissionResp.data.internationalScheduledPaymentId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.6",
        operations = ["CreateInternationalScheduledPaymentConsent", "CreateInternationalScheduledPayment"],
        apis = ["international-scheduled-payments"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_v3_1_6() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent5()
        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse6>(
            payment3_1_6.Links.links.CreateInternationalScheduledPaymentConsent,
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

        val paymentSubmissionRequest = OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
                .consentId(consent.data.consentId)
                .initiation(toOBWriteInternationalScheduled3DataInitiation(consent.data.initiation))
        ).risk(consent.risk)


        val submissionResp = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse6>(
            payment3_1_6.Links.links.CreateInternationalScheduledPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            tppResource.tpp,
            v3_1_6
        )
        // When
        val paymentResult = PaymentRS().getPayment<OBWriteInternationalScheduledResponse6>(
            payment3_1_6.Links.links.CreateInternationalScheduledPayment,
            submissionResp.data.internationalScheduledPaymentId,
            accessTokenClientCredentials,
            v3_1_6
        )

        // Then
        assertThat(consent.data.readRefundAccount).isEqualTo(OBReadRefundAccountEnum.NO)
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.refund).isNull()
        assertThat(paymentResult.data.internationalScheduledPaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        assertThat(paymentResult.data.debtor.name).isEqualTo(consentRequest.data.initiation.debtorAccount.name)
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_6.Links.links.CreateInternationalScheduledPayment + "/" + submissionResp.data.internationalScheduledPaymentId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.6",
        operations = ["CreateInternationalScheduledPaymentConsent", "CreateInternationalScheduledPayment"],
        apis = ["international-scheduled-payments"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_v3_1_6_readRefund() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent5()
        consentRequest.data.readRefundAccount(OBReadRefundAccountEnum.YES)
        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse6>(
            payment3_1_6.Links.links.CreateInternationalScheduledPaymentConsent,
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

        val paymentSubmissionRequest = OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
                .consentId(consent.data.consentId)
                .initiation(toOBWriteInternationalScheduled3DataInitiation(consent.data.initiation))
        ).risk(consent.risk)


        val submissionResp = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse6>(
            payment3_1_6.Links.links.CreateInternationalScheduledPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            tppResource.tpp,
            v3_1_6
        )
        // When
        val paymentResult = PaymentRS().getPayment<OBWriteInternationalScheduledResponse6>(
            payment3_1_6.Links.links.CreateInternationalScheduledPayment,
            submissionResp.data.internationalScheduledPaymentId,
            accessTokenClientCredentials,
            v3_1_6
        )

        // Then
        assertThat(consent.data.readRefundAccount).isEqualTo(OBReadRefundAccountEnum.YES)
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.refund.account.identification).isEqualTo(consent.data.initiation.debtorAccount.identification)
        assertThat(paymentResult.data.refund.creditor.name).isEqualTo(consent.data.initiation.creditor.name)
        assertThat(paymentResult.data.refund.agent.identification).isEqualTo(consent.data.initiation.creditorAgent.identification)
        assertThat(paymentResult.data.internationalScheduledPaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        assertThat(paymentResult.data.debtor.name).isEqualTo(consentRequest.data.initiation.debtorAccount.name)
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_6.Links.links.CreateInternationalScheduledPayment + "/" + submissionResp.data.internationalScheduledPaymentId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.6",
        operations = ["CreateInternationalScheduledPaymentConsent", "CreateInternationalScheduledPayment"],
        apis = ["international-scheduled-payments"]
    )
    @Test
    fun shouldCreateIntnlScheduledPayment_v3_1_6_readRefund_debtorAccountNotPresent() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent5()
        consentRequest.data.readRefundAccount(OBReadRefundAccountEnum.YES)
        consentRequest.data.initiation.debtorAccount(null)
        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse6>(
            payment3_1_6.Links.links.CreateInternationalScheduledPaymentConsent,
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

        val paymentSubmissionRequest = OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
                .consentId(consent.data.consentId)
                .initiation(toOBWriteInternationalScheduled3DataInitiation(consent.data.initiation))
        ).risk(consent.risk)


        val submissionResp = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse6>(
            payment3_1_6.Links.links.CreateInternationalScheduledPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            tppResource.tpp,
            v3_1_6
        )
        // When
        val paymentResult = PaymentRS().getPayment<OBWriteInternationalScheduledResponse6>(
            payment3_1_6.Links.links.CreateInternationalScheduledPayment,
            submissionResp.data.internationalScheduledPaymentId,
            accessTokenClientCredentials,
            v3_1_6
        )

        // Then
        assertThat(consent.data.readRefundAccount).isEqualTo(OBReadRefundAccountEnum.YES)
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.refund).isNull()
        assertThat(paymentResult.data.internationalScheduledPaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_6.Links.links.CreateInternationalScheduledPayment + "/" + submissionResp.data.internationalScheduledPaymentId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.6",
        operations = ["CreateInternationalScheduledPaymentConsent", "CreateInternationalScheduledPayment"],
        apis = ["international-scheduled-payments"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_v3_1_6_readRefund_null() {
        // Given
        val consentRequest = aValidOBWriteInternationalScheduledConsent5()
        consentRequest.data.readRefundAccount(null)
        val consent = PaymentRS().consent<OBWriteInternationalScheduledConsentResponse6>(
            payment3_1_6.Links.links.CreateInternationalScheduledPaymentConsent,
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

        val paymentSubmissionRequest = OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
                .consentId(consent.data.consentId)
                .initiation(toOBWriteInternationalScheduled3DataInitiation(consent.data.initiation))
        ).risk(consent.risk)


        val submissionResp = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse6>(
            payment3_1_6.Links.links.CreateInternationalScheduledPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            tppResource.tpp,
            v3_1_6
        )
        // When
        val paymentResult = PaymentRS().getPayment<OBWriteInternationalScheduledResponse6>(
            payment3_1_6.Links.links.CreateInternationalScheduledPayment,
            submissionResp.data.internationalScheduledPaymentId,
            accessTokenClientCredentials,
            v3_1_6
        )

        // Then
        assertThat(consent.data.readRefundAccount).isNull()
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.refund).isNull()
        assertThat(paymentResult.data.internationalScheduledPaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_6.Links.links.CreateInternationalScheduledPayment + "/" + submissionResp.data.internationalScheduledPaymentId)
        assertThat(paymentResult.meta).isNotNull()
    }

    private fun toOBWriteInternationalScheduled3DataInitiation(initiation: OBWriteInternationalScheduledConsentResponse6DataInitiation?) =
        if (initiation == null) null else {
            OBWriteInternationalScheduled3DataInitiation()
                .instructionIdentification(initiation.instructionIdentification)
                .endToEndIdentification(initiation.endToEndIdentification)
                .localInstrument(initiation.localInstrument)
                .instructionPriority(initiation.instructionPriority)
                .purpose(initiation.purpose)
                .extendedPurpose(initiation.extendedPurpose)
                .chargeBearer(initiation.chargeBearer)
                .requestedExecutionDateTime(initiation.requestedExecutionDateTime)
                .currencyOfTransfer(initiation.currencyOfTransfer)
                .destinationCountryCode(initiation.destinationCountryCode)
                .instructedAmount(initiation.instructedAmount)
                .exchangeRateInformation(initiation.exchangeRateInformation)
                .debtorAccount(initiation.debtorAccount)
                .creditor(toOBWriteInternational3DataInitiationCreditor(initiation.creditor))
                .creditorAgent(initiation.creditorAgent)
                .creditorAccount(initiation.creditorAccount)
                .remittanceInformation(initiation.remittanceInformation)
                .supplementaryData(initiation.supplementaryData)
        }

    private fun toOBWriteInternational3DataInitiationCreditor(creditor: OBWriteInternationalScheduledConsentResponse6DataInitiationCreditor?) =
        if (creditor == null) null else OBWriteInternational3DataInitiationCreditor()
            .name(creditor.name)
            .postalAddress(creditor.postalAddress)
}
