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
import uk.org.openbanking.testsupport.payment.OBWriteInternationalStandingOrderConsentTestDataFactory.aValidOBWriteInternationalStandingOrderConsent4
import uk.org.openbanking.testsupport.payment.OBWriteInternationalStandingOrderConsentTestDataFactory.aValidOBWriteInternationalStandingOrderConsent6

class InternationalStandingOrderPaymentTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.2",
        operations = ["CreateInternationalStandingOrderConsent"],
        apis = ["international-standing-order-consents"]
    )
    @Test
    fun shouldCreateInternationalStandingOrderPaymentConsent_v3_1_2() {
        // Given
        val consentRequest = aValidOBWriteInternationalStandingOrderConsent4()

        // When
        val consent = PaymentRS().consent<OBWriteInternationalStandingOrderConsentResponse4>(
            payment3_1_2.Links.links.CreateInternationalStandingOrderConsent,
            consentRequest,
            tppResource.tpp
        )
        val getConsentResult = PaymentRS().getConsent<OBWriteInternationalStandingOrderConsentResponse4>(
            payment3_1_2.Links.links.CreateInternationalStandingOrderConsent + "/" + consent.data.consentId,
            tppResource.tpp
        )

        // Then
        assertThat(getConsentResult).isNotNull()
        assertThat(getConsentResult.data.consentId).isNotEmpty()
        Assertions.assertThat(getConsentResult.data.status.toString()).`is`(consentCondition)
        assertThat(getConsentResult.links.self).isEqualTo(payment3_1_2.Links.links.CreateInternationalStandingOrderConsent + "/" + consent.data.consentId)
        assertThat(getConsentResult.meta).isNotNull()

    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.2",
        operations = ["CreateInternationalStandingOrderConsent", "CreateInternationalStandingOrder"],
        apis = ["international-standing-orders"]
    )
    @Test
    fun shouldCreateInternationalStandingOrderPayment_v3_1_2() {
        // Given
        val consentRequest = aValidOBWriteInternationalStandingOrderConsent4()
        val consent = PaymentRS().consent<OBWriteInternationalStandingOrderConsentResponse2>(
            payment3_1_2.Links.links.CreateInternationalStandingOrderConsent,
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

        val paymentSubmissionRequest = OBWriteInternationalStandingOrder2().data(
            OBWriteDataInternationalStandingOrder2()
                .consentId(consent.data.consentId)
                .initiation(consent.data.initiation)
        ).risk(consent.risk)

        val submissionResp = PaymentRS().submitPayment<OBWriteInternationalStandingOrderResponse2>(
            payment3_1_2.Links.links.CreateInternationalStandingOrder,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            tppResource.tpp
        )

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteInternationalStandingOrderResponse2>(
            payment3_1_2.Links.links.CreateInternationalStandingOrder,
            submissionResp.data.internationalStandingOrderId,
            accessTokenClientCredentials
        )

        // Then
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.internationalStandingOrderId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_2.Links.links.CreateInternationalStandingOrder + "/" + submissionResp.data.internationalStandingOrderId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateInternationalStandingOrderConsent", "CreateInternationalStandingOrder"],
        apis = ["international-standing-orders"]
    )
    @Test
    fun shouldCreateInternationalStandingOrderPayment_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteInternationalStandingOrderConsent6()
        val consent = PaymentRS().consent<OBWriteInternationalStandingOrderConsentResponse7>(
            payment3_1_4.Links.links.CreateInternationalStandingOrderConsent,
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

        val paymentSubmissionRequest = OBWriteInternationalStandingOrder4().data(
            OBWriteInternationalStandingOrder4Data()
                .consentId(consent.data.consentId)
                .initiation(toOBWriteInternationalStandingOrder4DataInitiation(consent.data.initiation))
        ).risk(consent.risk)

        val submissionResp = PaymentRS().submitPayment<OBWriteInternationalStandingOrderResponse7>(
            payment3_1_4.Links.links.CreateInternationalStandingOrder,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            tppResource.tpp,
            v3_1_4
        )

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteInternationalStandingOrderResponse7>(
            payment3_1_4.Links.links.CreateInternationalStandingOrder,
            submissionResp.data.internationalStandingOrderId,
            accessTokenClientCredentials,
            v3_1_4
        )

        // Then
        assertThat(consent.data.readRefundAccount).isEqualTo(OBReadRefundAccountEnum.NO)
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.refund).isNull()
        assertThat(paymentResult.data.internationalStandingOrderId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_4.Links.links.CreateInternationalStandingOrder + "/" + submissionResp.data.internationalStandingOrderId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateInternationalStandingOrderConsent", "CreateInternationalStandingOrder"],
        apis = ["international-standing-orders"]
    )
    @Test
    fun shouldCreateInternationalStandingOrderPayment_v3_1_4_readRefund() {
        // Given
        val consentRequest = aValidOBWriteInternationalStandingOrderConsent6()
        consentRequest.data.readRefundAccount(OBReadRefundAccountEnum.YES)
        val consent = PaymentRS().consent<OBWriteInternationalStandingOrderConsentResponse7>(
            payment3_1_4.Links.links.CreateInternationalStandingOrderConsent,
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

        val paymentSubmissionRequest = OBWriteInternationalStandingOrder4().data(
            OBWriteInternationalStandingOrder4Data()
                .consentId(consent.data.consentId)
                .initiation(toOBWriteInternationalStandingOrder4DataInitiation(consent.data.initiation))
        ).risk(consent.risk)

        val submissionResp = PaymentRS().submitPayment<OBWriteInternationalStandingOrderResponse6>(
            payment3_1_4.Links.links.CreateInternationalStandingOrder,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            tppResource.tpp,
            v3_1_4
        )

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteInternationalStandingOrderResponse6>(
            payment3_1_4.Links.links.CreateInternationalStandingOrder,
            submissionResp.data.internationalStandingOrderId,
            accessTokenClientCredentials,
            v3_1_4
        )

        // Then
        assertThat(consent.data.readRefundAccount).isEqualTo(OBReadRefundAccountEnum.YES)
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.refund.account.identification).isEqualTo(consent.data.initiation.debtorAccount.identification)
        assertThat(paymentResult.data.refund.creditor.name).isEqualTo(consent.data.initiation.creditor.name)
        assertThat(paymentResult.data.refund.agent.identification).isEqualTo(consent.data.initiation.creditorAgent.identification)
        assertThat(paymentResult.data.internationalStandingOrderId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_4.Links.links.CreateInternationalStandingOrder + "/" + submissionResp.data.internationalStandingOrderId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateInternationalStandingOrderConsent", "CreateInternationalStandingOrder"],
        apis = ["international-standing-orders"]
    )
    @Test
    @DisplayName("shouldCreateIntnlStandingOrderPayment_v3_1_4_readRefund_debtorAccountNotPresent()")
    fun shouldCreate_readRefund_v314_debtorAccountNotPresent() {
        // Given
        val consentRequest = aValidOBWriteInternationalStandingOrderConsent6()
        consentRequest.data.readRefundAccount(OBReadRefundAccountEnum.YES)
        consentRequest.data.initiation.debtorAccount(null)
        val consent = PaymentRS().consent<OBWriteInternationalStandingOrderConsentResponse7>(
            payment3_1_4.Links.links.CreateInternationalStandingOrderConsent,
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

        val paymentSubmissionRequest = OBWriteInternationalStandingOrder4().data(
            OBWriteInternationalStandingOrder4Data()
                .consentId(consent.data.consentId)
                .initiation(toOBWriteInternationalStandingOrder4DataInitiation(consent.data.initiation))
        ).risk(consent.risk)

        val submissionResp = PaymentRS().submitPayment<OBWriteInternationalStandingOrderResponse6>(
            payment3_1_4.Links.links.CreateInternationalStandingOrder,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            tppResource.tpp,
            v3_1_4
        )

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteInternationalStandingOrderResponse6>(
            payment3_1_4.Links.links.CreateInternationalStandingOrder,
            submissionResp.data.internationalStandingOrderId,
            accessTokenClientCredentials,
            v3_1_4
        )

        // Then
        assertThat(consent.data.readRefundAccount).isEqualTo(OBReadRefundAccountEnum.YES)
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.refund).isNull()
        assertThat(paymentResult.data.internationalStandingOrderId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_4.Links.links.CreateInternationalStandingOrder + "/" + submissionResp.data.internationalStandingOrderId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateInternationalStandingOrderConsent", "CreateInternationalStandingOrder"],
        apis = ["international-standing-orders"]
    )
    @Test
    fun shouldCreateInternationalStandingOrderPayment_v3_1_4_readRefund_null() {
        // Given
        val consentRequest = aValidOBWriteInternationalStandingOrderConsent6()
        consentRequest.data.readRefundAccount(null)
        val consent = PaymentRS().consent<OBWriteInternationalStandingOrderConsentResponse7>(
            payment3_1_4.Links.links.CreateInternationalStandingOrderConsent,
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

        val paymentSubmissionRequest = OBWriteInternationalStandingOrder4().data(
            OBWriteInternationalStandingOrder4Data()
                .consentId(consent.data.consentId)
                .initiation(toOBWriteInternationalStandingOrder4DataInitiation(consent.data.initiation))
        ).risk(consent.risk)

        val submissionResp = PaymentRS().submitPayment<OBWriteInternationalStandingOrderResponse6>(
            payment3_1_4.Links.links.CreateInternationalStandingOrder,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            tppResource.tpp,
            v3_1_4
        )

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteInternationalStandingOrderResponse6>(
            payment3_1_4.Links.links.CreateInternationalStandingOrder,
            submissionResp.data.internationalStandingOrderId,
            accessTokenClientCredentials,
            v3_1_4
        )

        // Then
        assertThat(consent.data.readRefundAccount).isNull()
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.refund).isNull()
        assertThat(paymentResult.data.internationalStandingOrderId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_4.Links.links.CreateInternationalStandingOrder + "/" + submissionResp.data.internationalStandingOrderId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.6",
        operations = ["CreateInternationalStandingOrderConsent", "CreateInternationalStandingOrder"],
        apis = ["international-standing-orders"]
    )
    @Test
    fun shouldCreateInternationalStandingOrderPayment_v3_1_6() {
        // Given
        val consentRequest = aValidOBWriteInternationalStandingOrderConsent6()
        val consent = PaymentRS().consent<OBWriteInternationalStandingOrderConsentResponse7>(
            payment3_1_6.Links.links.CreateInternationalStandingOrderConsent,
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

        val paymentSubmissionRequest = OBWriteInternationalStandingOrder4().data(
            OBWriteInternationalStandingOrder4Data()
                .consentId(consent.data.consentId)
                .initiation(toOBWriteInternationalStandingOrder4DataInitiation(consent.data.initiation))
        ).risk(consent.risk)

        val submissionResp = PaymentRS().submitPayment<OBWriteInternationalStandingOrderResponse7>(
            payment3_1_6.Links.links.CreateInternationalStandingOrder,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            tppResource.tpp,
            v3_1_6
        )

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteInternationalStandingOrderResponse7>(
            payment3_1_6.Links.links.CreateInternationalStandingOrder,
            submissionResp.data.internationalStandingOrderId,
            accessTokenClientCredentials,
            v3_1_6
        )

        // Then
        assertThat(consent.data.readRefundAccount).isEqualTo(OBReadRefundAccountEnum.NO)
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.refund).isNull()
        assertThat(paymentResult.data.internationalStandingOrderId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_6.Links.links.CreateInternationalStandingOrder + "/" + submissionResp.data.internationalStandingOrderId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.6",
        operations = ["CreateInternationalStandingOrderConsent", "CreateInternationalStandingOrder"],
        apis = ["international-standing-orders"]
    )
    @Test
    fun shouldCreateInternationalStandingOrderPayment_v3_1_6_readRefund() {
        // Given
        val consentRequest = aValidOBWriteInternationalStandingOrderConsent6()
        consentRequest.data.readRefundAccount(OBReadRefundAccountEnum.YES)
        val consent = PaymentRS().consent<OBWriteInternationalStandingOrderConsentResponse7>(
            payment3_1_6.Links.links.CreateInternationalStandingOrderConsent,
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

        val paymentSubmissionRequest = OBWriteInternationalStandingOrder4().data(
            OBWriteInternationalStandingOrder4Data()
                .consentId(consent.data.consentId)
                .initiation(toOBWriteInternationalStandingOrder4DataInitiation(consent.data.initiation))
        ).risk(consent.risk)

        val submissionResp = PaymentRS().submitPayment<OBWriteInternationalStandingOrderResponse7>(
            payment3_1_6.Links.links.CreateInternationalStandingOrder,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            tppResource.tpp,
            v3_1_6
        )

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteInternationalStandingOrderResponse7>(
            payment3_1_6.Links.links.CreateInternationalStandingOrder,
            submissionResp.data.internationalStandingOrderId,
            accessTokenClientCredentials,
            v3_1_6
        )

        // Then
        assertThat(consent.data.readRefundAccount).isEqualTo(OBReadRefundAccountEnum.YES)
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.refund.account.identification).isEqualTo(consent.data.initiation.debtorAccount.identification)
        assertThat(paymentResult.data.refund.creditor.name).isEqualTo(consent.data.initiation.creditor.name)
        assertThat(paymentResult.data.refund.agent.identification).isEqualTo(consent.data.initiation.creditorAgent.identification)
        assertThat(paymentResult.data.internationalStandingOrderId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_6.Links.links.CreateInternationalStandingOrder + "/" + submissionResp.data.internationalStandingOrderId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.6",
        operations = ["CreateInternationalStandingOrderConsent", "CreateInternationalStandingOrder"],
        apis = ["international-standing-orders"]
    )
    @Test
    @DisplayName("shouldCreateInternationalStandingOrderPayment_v3_1_6_readRefund_debtorAccountNotPresent")
    fun shouldCreateIntnlStdngOrderPayment_readRefund_debtorAccountNotPresent() {
        // Given
        val consentRequest = aValidOBWriteInternationalStandingOrderConsent6()
        consentRequest.data.readRefundAccount(OBReadRefundAccountEnum.YES)
        consentRequest.data.initiation.debtorAccount(null)
        val consent = PaymentRS().consent<OBWriteInternationalStandingOrderConsentResponse7>(
            payment3_1_6.Links.links.CreateInternationalStandingOrderConsent,
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

        val paymentSubmissionRequest = OBWriteInternationalStandingOrder4().data(
            OBWriteInternationalStandingOrder4Data()
                .consentId(consent.data.consentId)
                .initiation(toOBWriteInternationalStandingOrder4DataInitiation(consent.data.initiation))
        ).risk(consent.risk)

        val submissionResp = PaymentRS().submitPayment<OBWriteInternationalStandingOrderResponse7>(
            payment3_1_6.Links.links.CreateInternationalStandingOrder,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            tppResource.tpp,
            v3_1_6
        )

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteInternationalStandingOrderResponse7>(
            payment3_1_6.Links.links.CreateInternationalStandingOrder,
            submissionResp.data.internationalStandingOrderId,
            accessTokenClientCredentials,
            v3_1_6
        )

        // Then
        assertThat(consent.data.readRefundAccount).isEqualTo(OBReadRefundAccountEnum.YES)
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.refund).isNull()
        assertThat(paymentResult.data.internationalStandingOrderId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_6.Links.links.CreateInternationalStandingOrder + "/" + submissionResp.data.internationalStandingOrderId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.6",
        operations = ["CreateInternationalStandingOrderConsent", "CreateInternationalStandingOrder"],
        apis = ["international-standing-orders"]
    )
    @Test
    fun shouldCreateInternationalStandingOrderPayment_v3_1_6_readRefund_null() {
        // Given
        val consentRequest = aValidOBWriteInternationalStandingOrderConsent6()
        consentRequest.data.readRefundAccount(null)
        val consent = PaymentRS().consent<OBWriteInternationalStandingOrderConsentResponse7>(
            payment3_1_6.Links.links.CreateInternationalStandingOrderConsent,
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

        val paymentSubmissionRequest = OBWriteInternationalStandingOrder4().data(
            OBWriteInternationalStandingOrder4Data()
                .consentId(consent.data.consentId)
                .initiation(toOBWriteInternationalStandingOrder4DataInitiation(consent.data.initiation))
        ).risk(consent.risk)

        val submissionResp = PaymentRS().submitPayment<OBWriteInternationalStandingOrderResponse7>(
            payment3_1_6.Links.links.CreateInternationalStandingOrder,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            tppResource.tpp,
            v3_1_6
        )

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteInternationalStandingOrderResponse7>(
            payment3_1_6.Links.links.CreateInternationalStandingOrder,
            submissionResp.data.internationalStandingOrderId,
            accessTokenClientCredentials,
            v3_1_6
        )

        // Then
        assertThat(consent.data.readRefundAccount).isNull()
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.refund).isNull()
        assertThat(paymentResult.data.internationalStandingOrderId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_6.Links.links.CreateInternationalStandingOrder + "/" + submissionResp.data.internationalStandingOrderId)
        assertThat(paymentResult.meta).isNotNull()
    }

    private fun toOBWriteInternationalStandingOrder4DataInitiation(initiation: OBWriteInternationalStandingOrderConsentResponse7DataInitiation?) =
        if (initiation == null) null else OBWriteInternationalStandingOrder4DataInitiation()
            .frequency(initiation.frequency)
            .reference(initiation.reference)
            .numberOfPayments(initiation.numberOfPayments)
            .firstPaymentDateTime(initiation.firstPaymentDateTime)
            .finalPaymentDateTime(initiation.finalPaymentDateTime)
            .purpose(initiation.purpose)
            .extendedPurpose(initiation.extendedPurpose)
            .chargeBearer(initiation.chargeBearer)
            .currencyOfTransfer(initiation.currencyOfTransfer)
            .destinationCountryCode(initiation.destinationCountryCode)
            .instructedAmount(initiation.instructedAmount)
            .debtorAccount(toOBWriteDomesticStandingOrder3DataInitiationDebtorAccount(initiation.debtorAccount))
            .creditor(initiation.creditor)
            .creditorAgent(initiation.creditorAgent)
            .creditorAccount(initiation.creditorAccount)
            .supplementaryData(initiation.supplementaryData)

    private fun toOBWriteDomesticStandingOrder3DataInitiationDebtorAccount(debtorAccount: OBWriteDomestic2DataInitiationDebtorAccount?) =
        if (debtorAccount == null) null else OBWriteDomesticStandingOrder3DataInitiationDebtorAccount()
            .schemeName(debtorAccount.schemeName)
            .identification(debtorAccount.identification)
            .name(debtorAccount.name)
            .secondaryIdentification(debtorAccount.secondaryIdentification)
}
