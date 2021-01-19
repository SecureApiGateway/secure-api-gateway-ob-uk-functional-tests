package com.forgerock.openbanking.payment.domestic

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import com.forgerock.openbanking.Status.consentCondition
import com.forgerock.openbanking.Status.paymentCondition
import com.forgerock.openbanking.common.model.version.OBVersion.v3_1_4
import com.forgerock.openbanking.common.model.version.OBVersion.v3_1_6
import com.forgerock.openbanking.discovery.payment3_1_1
import com.forgerock.openbanking.discovery.payment3_1_2
import com.forgerock.openbanking.discovery.payment3_1_4
import com.forgerock.openbanking.discovery.payment3_1_6
import com.forgerock.openbanking.junit.CreateTppCallback
import com.forgerock.openbanking.junit.EnabledIfOpenBankingVersion
import com.forgerock.openbanking.payment.PaymentAS
import com.forgerock.openbanking.payment.PaymentRS
import com.forgerock.openbanking.psu
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.payment.*
import uk.org.openbanking.datamodel.service.converter.payment.OBDomesticStandingOrderConverter.toOBDomesticStandingOrder3
import uk.org.openbanking.datamodel.service.converter.payment.OBDomesticStandingOrderConverter.toOBWriteDomesticStandingOrder3DataInitiation
import uk.org.openbanking.testsupport.payment.OBWriteDomesticStandingOrderConsentTestDataFactory.aValidOBWriteDomesticStandingOrderConsent4
import uk.org.openbanking.testsupport.payment.OBWriteDomesticStandingOrderConsentTestDataFactory.aValidOBWriteDomesticStandingOrderConsent5

class DomesticStandingOrdersTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfOpenBankingVersion(type = "payments", version = "v3.1.2", apis = ["domestic-standing-order-consents"])
    @Test
    fun shouldCreateDomesticStandingOrderPaymentConsent_v3_1_2() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent4()

        // When
        val consentResponse = PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse3>(payment3_1_2.Links.links.CreateDomesticStandingOrderConsent, consentRequest, tppResource.tpp)
        val getConsentResult = PaymentRS().getConsent<OBWriteDomesticStandingOrderConsentResponse3>(payment3_1_2.Links.links.CreateDomesticStandingOrderConsent + "/" + consentResponse.data.consentId, tppResource.tpp)

        // Then
        assertThat(getConsentResult).isNotNull()
        assertThat(getConsentResult.data.consentId).isNotEmpty()
        Assertions.assertThat(getConsentResult.data.status.toString()).`is`(consentCondition)
        assertThat(getConsentResult.links.self).isEqualTo(payment3_1_1.Links.links.CreateDomesticStandingOrderConsent + "/" + consentResponse.data.consentId)
        assertThat(getConsentResult.meta).isNotNull()
    }

    @EnabledIfOpenBankingVersion(type = "payments", version = "v3.1.2", apis = ["domestic-standing-orders"])
    @Test
    fun shouldCreateDomesticStandingOrderPayment_v3_1_2() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent4()
        val consentResponse = PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse3>(payment3_1_2.Links.links.CreateDomesticStandingOrderConsent, consentRequest, tppResource.tpp)

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().headlessAuthentication(consentResponse.data.consentId, tppResource.tpp.registrationResponse, psu, tppResource.tpp)
        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentAS().clientCredentialsAuthentication(consentResponse.data.consentId, tppResource.tpp.registrationResponse, tppResource.tpp)
        val paymentSubmissionRequest = OBWriteDomesticStandingOrder3().data(
                OBWriteDomesticStandingOrder3Data()
                        .consentId(consentResponse.data.consentId)
                        .initiation(toOBWriteDomesticStandingOrder3DataInitiation(consentResponse.data.initiation))
        ).risk(consentResponse.risk)
        val submissionResp = PaymentRS().submitPayment<OBWriteDomesticStandingOrderResponse4>(payment3_1_2.Links.links.CreateDomesticStandingOrder, paymentSubmissionRequest, accessTokenAuthorizationCode, tppResource.tpp)

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteDomesticStandingOrderResponse4>(payment3_1_2.Links.links.CreateDomesticStandingOrder, submissionResp.data.domesticStandingOrderId, accessTokenClientCredentials)

        // Then
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.domesticStandingOrderId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_1.Links.links.CreateDomesticStandingOrder + "/" + submissionResp.data.domesticStandingOrderId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfOpenBankingVersion(type = "payments", version = "v3.1.4", apis = ["domestic-standing-orders"])
    @Test
    fun shouldCreateDomesticStandingOrderPayment_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent5()
        val consentResponse = PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse5>(payment3_1_4.Links.links.CreateDomesticStandingOrderConsent, consentRequest, tppResource.tpp, v3_1_4)

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().headlessAuthentication(consentResponse.data.consentId, tppResource.tpp.registrationResponse, psu, tppResource.tpp)
        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentAS().clientCredentialsAuthentication(consentResponse.data.consentId, tppResource.tpp.registrationResponse, tppResource.tpp)
        val paymentSubmissionRequest = OBWriteDomesticStandingOrder3().data(
                OBWriteDomesticStandingOrder3Data()
                        .consentId(consentResponse.data.consentId)
                        .initiation(consentResponse.data.initiation)
        ).risk(consentResponse.risk)
        val submissionResp = PaymentRS().submitPayment<OBWriteDomesticStandingOrderResponse5>(payment3_1_4.Links.links.CreateDomesticStandingOrder, paymentSubmissionRequest, accessTokenAuthorizationCode, tppResource.tpp, v3_1_4)

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteDomesticStandingOrderResponse5>(payment3_1_4.Links.links.CreateDomesticStandingOrder, submissionResp.data.domesticStandingOrderId, accessTokenClientCredentials, v3_1_4)

        // Then
        assertThat(consentResponse.data.readRefundAccount).isEqualTo(OBWriteDomesticStandingOrderConsentResponse5Data.ReadRefundAccountEnum.NO)
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.refund).isNull()
        assertThat(paymentResult.data.domesticStandingOrderId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_4.Links.links.CreateDomesticStandingOrder + "/" + submissionResp.data.domesticStandingOrderId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfOpenBankingVersion(type = "payments", version = "v3.1.4", apis = ["domestic-standing-orders"])
    @Test
    fun shouldCreateDomesticStandingOrderPayment_v3_1_4_readRefund() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent5()
        consentRequest.data.readRefundAccount(OBWriteDomesticStandingOrderConsent5Data.ReadRefundAccountEnum.YES)
        val consentResponse = PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse5>(payment3_1_4.Links.links.CreateDomesticStandingOrderConsent, consentRequest, tppResource.tpp, v3_1_4)

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().headlessAuthentication(consentResponse.data.consentId, tppResource.tpp.registrationResponse, psu, tppResource.tpp)
        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentAS().clientCredentialsAuthentication(consentResponse.data.consentId, tppResource.tpp.registrationResponse, tppResource.tpp)
        val paymentSubmissionRequest = OBWriteDomesticStandingOrder3().data(
                OBWriteDomesticStandingOrder3Data()
                        .consentId(consentResponse.data.consentId)
                        .initiation(consentResponse.data.initiation)
        ).risk(consentResponse.risk)
        val submissionResp = PaymentRS().submitPayment<OBWriteDomesticStandingOrderResponse5>(payment3_1_4.Links.links.CreateDomesticStandingOrder, paymentSubmissionRequest, accessTokenAuthorizationCode, tppResource.tpp, v3_1_4)

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteDomesticStandingOrderResponse5>(payment3_1_4.Links.links.CreateDomesticStandingOrder, submissionResp.data.domesticStandingOrderId, accessTokenClientCredentials, v3_1_4)

        // Then
        assertThat(consentResponse.data.readRefundAccount).isEqualTo(OBWriteDomesticStandingOrderConsentResponse5Data.ReadRefundAccountEnum.YES)
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.refund.account.identification).isEqualTo(consentRequest.data.initiation.debtorAccount.identification)
        assertThat(paymentResult.data.domesticStandingOrderId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_4.Links.links.CreateDomesticStandingOrder + "/" + submissionResp.data.domesticStandingOrderId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfOpenBankingVersion(type = "payments", version = "v3.1.4", apis = ["domestic-standing-orders"])
    @Test
    fun shouldCreateDomesticStandingOrderPayment_v3_1_4_readRefund_debtorAccountNotPresent() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent5()
        consentRequest.data.readRefundAccount(OBWriteDomesticStandingOrderConsent5Data.ReadRefundAccountEnum.YES)
        consentRequest.data.initiation.debtorAccount(null)
        val consentResponse = PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse5>(payment3_1_4.Links.links.CreateDomesticStandingOrderConsent, consentRequest, tppResource.tpp, v3_1_4)

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().headlessAuthentication(consentResponse.data.consentId, tppResource.tpp.registrationResponse, psu, tppResource.tpp)
        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentAS().clientCredentialsAuthentication(consentResponse.data.consentId, tppResource.tpp.registrationResponse, tppResource.tpp)
        val paymentSubmissionRequest = OBWriteDomesticStandingOrder3().data(
                OBWriteDomesticStandingOrder3Data()
                        .consentId(consentResponse.data.consentId)
                        .initiation(consentResponse.data.initiation)
        ).risk(consentResponse.risk)
        val submissionResp = PaymentRS().submitPayment<OBWriteDomesticStandingOrderResponse5>(payment3_1_4.Links.links.CreateDomesticStandingOrder, paymentSubmissionRequest, accessTokenAuthorizationCode, tppResource.tpp, v3_1_4)

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteDomesticStandingOrderResponse5>(payment3_1_4.Links.links.CreateDomesticStandingOrder, submissionResp.data.domesticStandingOrderId, accessTokenClientCredentials, v3_1_4)

        // Then
        assertThat(consentResponse.data.readRefundAccount).isEqualTo(OBWriteDomesticStandingOrderConsentResponse5Data.ReadRefundAccountEnum.YES)
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.refund).isNull()
        assertThat(paymentResult.data.domesticStandingOrderId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_4.Links.links.CreateDomesticStandingOrder + "/" + submissionResp.data.domesticStandingOrderId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfOpenBankingVersion(type = "payments", version = "v3.1.4", apis = ["domestic-standing-orders"])
    @Test
    fun shouldCreateDomesticStandingOrderPayment_v3_1_4_readRefund_Null() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent5()
        consentRequest.data.readRefundAccount(null)
        val consentResponse = PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse5>(payment3_1_4.Links.links.CreateDomesticStandingOrderConsent, consentRequest, tppResource.tpp, v3_1_4)

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().headlessAuthentication(consentResponse.data.consentId, tppResource.tpp.registrationResponse, psu, tppResource.tpp)
        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentAS().clientCredentialsAuthentication(consentResponse.data.consentId, tppResource.tpp.registrationResponse, tppResource.tpp)
        val paymentSubmissionRequest = OBWriteDomesticStandingOrder3().data(
                OBWriteDomesticStandingOrder3Data()
                        .consentId(consentResponse.data.consentId)
                        .initiation(consentResponse.data.initiation)
        ).risk(consentResponse.risk)
        val submissionResp = PaymentRS().submitPayment<OBWriteDomesticStandingOrderResponse5>(payment3_1_4.Links.links.CreateDomesticStandingOrder, paymentSubmissionRequest, accessTokenAuthorizationCode, tppResource.tpp, v3_1_4)

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteDomesticStandingOrderResponse5>(payment3_1_4.Links.links.CreateDomesticStandingOrder, submissionResp.data.domesticStandingOrderId, accessTokenClientCredentials, v3_1_4)

        // Then
        assertThat(consentResponse.data.readRefundAccount).isNull()
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.refund).isNull()
        assertThat(paymentResult.data.domesticStandingOrderId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_4.Links.links.CreateDomesticStandingOrder + "/" + submissionResp.data.domesticStandingOrderId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfOpenBankingVersion(type = "payments", version = "v3.1.6", apis = ["domestic-standing-orders"])
    @Test
    fun shouldCreateDomesticStandingOrderPayment_v3_1_6() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent5()
        val consentResponse = PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse5>(payment3_1_6.Links.links.CreateDomesticStandingOrderConsent, consentRequest, tppResource.tpp, v3_1_6)

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().headlessAuthentication(consentResponse.data.consentId, tppResource.tpp.registrationResponse, psu, tppResource.tpp)
        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentAS().clientCredentialsAuthentication(consentResponse.data.consentId, tppResource.tpp.registrationResponse, tppResource.tpp)
        val paymentSubmissionRequest = OBWriteDomesticStandingOrder3().data(
                OBWriteDomesticStandingOrder3Data()
                        .consentId(consentResponse.data.consentId)
                        .initiation(consentResponse.data.initiation)
        ).risk(consentResponse.risk)
        val submissionResp = PaymentRS().submitPayment<OBWriteDomesticStandingOrderResponse6>(payment3_1_6.Links.links.CreateDomesticStandingOrder, paymentSubmissionRequest, accessTokenAuthorizationCode, tppResource.tpp, v3_1_6)

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteDomesticStandingOrderResponse6>(payment3_1_6.Links.links.CreateDomesticStandingOrder, submissionResp.data.domesticStandingOrderId, accessTokenClientCredentials, v3_1_6)

        // Then
        assertThat(consentResponse.data.readRefundAccount).isEqualTo(OBWriteDomesticStandingOrderConsentResponse5Data.ReadRefundAccountEnum.NO)
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.refund).isNull()
        assertThat(paymentResult.data.domesticStandingOrderId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_6.Links.links.CreateDomesticStandingOrder + "/" + submissionResp.data.domesticStandingOrderId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfOpenBankingVersion(type = "payments", version = "v3.1.6", apis = ["domestic-standing-orders"])
    @Test
    fun shouldCreateDomesticStandingOrderPayment_v3_1_6_readRefund() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent5()
        consentRequest.data.readRefundAccount(OBWriteDomesticStandingOrderConsent5Data.ReadRefundAccountEnum.YES)
        val consentResponse = PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse5>(payment3_1_6.Links.links.CreateDomesticStandingOrderConsent, consentRequest, tppResource.tpp, v3_1_6)

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().headlessAuthentication(consentResponse.data.consentId, tppResource.tpp.registrationResponse, psu, tppResource.tpp)
        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentAS().clientCredentialsAuthentication(consentResponse.data.consentId, tppResource.tpp.registrationResponse, tppResource.tpp)
        val paymentSubmissionRequest = OBWriteDomesticStandingOrder3().data(
                OBWriteDomesticStandingOrder3Data()
                        .consentId(consentResponse.data.consentId)
                        .initiation(consentResponse.data.initiation)
        ).risk(consentResponse.risk)
        val submissionResp = PaymentRS().submitPayment<OBWriteDomesticStandingOrderResponse6>(payment3_1_6.Links.links.CreateDomesticStandingOrder, paymentSubmissionRequest, accessTokenAuthorizationCode, tppResource.tpp, v3_1_6)

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteDomesticStandingOrderResponse6>(payment3_1_6.Links.links.CreateDomesticStandingOrder, submissionResp.data.domesticStandingOrderId, accessTokenClientCredentials, v3_1_6)

        // Then
        assertThat(consentResponse.data.readRefundAccount).isEqualTo(OBWriteDomesticStandingOrderConsentResponse5Data.ReadRefundAccountEnum.YES)
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.refund.account.identification).isEqualTo(consentRequest.data.initiation.debtorAccount.identification)
        assertThat(paymentResult.data.domesticStandingOrderId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_6.Links.links.CreateDomesticStandingOrder + "/" + submissionResp.data.domesticStandingOrderId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfOpenBankingVersion(type = "payments", version = "v3.1.6", apis = ["domestic-standing-orders"])
    @Test
    fun shouldCreateDomesticStandingOrderPayment_v3_1_6_readRefund_debtorAccountNotPresent() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent5()
        consentRequest.data.readRefundAccount(OBWriteDomesticStandingOrderConsent5Data.ReadRefundAccountEnum.YES)
        consentRequest.data.initiation.debtorAccount(null)
        val consentResponse = PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse5>(payment3_1_6.Links.links.CreateDomesticStandingOrderConsent, consentRequest, tppResource.tpp, v3_1_6)

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().headlessAuthentication(consentResponse.data.consentId, tppResource.tpp.registrationResponse, psu, tppResource.tpp)
        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentAS().clientCredentialsAuthentication(consentResponse.data.consentId, tppResource.tpp.registrationResponse, tppResource.tpp)
        val paymentSubmissionRequest = OBWriteDomesticStandingOrder3().data(
                OBWriteDomesticStandingOrder3Data()
                        .consentId(consentResponse.data.consentId)
                        .initiation(consentResponse.data.initiation)
        ).risk(consentResponse.risk)
        val submissionResp = PaymentRS().submitPayment<OBWriteDomesticStandingOrderResponse6>(payment3_1_6.Links.links.CreateDomesticStandingOrder, paymentSubmissionRequest, accessTokenAuthorizationCode, tppResource.tpp, v3_1_6)

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteDomesticStandingOrderResponse6>(payment3_1_6.Links.links.CreateDomesticStandingOrder, submissionResp.data.domesticStandingOrderId, accessTokenClientCredentials, v3_1_6)

        // Then
        assertThat(consentResponse.data.readRefundAccount).isEqualTo(OBWriteDomesticStandingOrderConsentResponse5Data.ReadRefundAccountEnum.YES)
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.refund).isNull()
        assertThat(paymentResult.data.domesticStandingOrderId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_6.Links.links.CreateDomesticStandingOrder + "/" + submissionResp.data.domesticStandingOrderId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfOpenBankingVersion(type = "payments", version = "v3.1.6", apis = ["domestic-standing-orders"])
    @Test
    fun shouldCreateDomesticStandingOrderPayment_v3_1_6_readRefund_Null() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent5()
        consentRequest.data.readRefundAccount(null)
        val consentResponse = PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse5>(payment3_1_6.Links.links.CreateDomesticStandingOrderConsent, consentRequest, tppResource.tpp, v3_1_6)

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().headlessAuthentication(consentResponse.data.consentId, tppResource.tpp.registrationResponse, psu, tppResource.tpp)
        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentAS().clientCredentialsAuthentication(consentResponse.data.consentId, tppResource.tpp.registrationResponse, tppResource.tpp)
        val paymentSubmissionRequest = OBWriteDomesticStandingOrder3().data(
                OBWriteDomesticStandingOrder3Data()
                        .consentId(consentResponse.data.consentId)
                        .initiation(consentResponse.data.initiation)
        ).risk(consentResponse.risk)
        val submissionResp = PaymentRS().submitPayment<OBWriteDomesticStandingOrderResponse6>(payment3_1_6.Links.links.CreateDomesticStandingOrder, paymentSubmissionRequest, accessTokenAuthorizationCode, tppResource.tpp, v3_1_6)

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteDomesticStandingOrderResponse6>(payment3_1_6.Links.links.CreateDomesticStandingOrder, submissionResp.data.domesticStandingOrderId, accessTokenClientCredentials, v3_1_6)

        // Then
        assertThat(consentResponse.data.readRefundAccount).isNull()
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.refund).isNull()
        assertThat(paymentResult.data.domesticStandingOrderId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_6.Links.links.CreateDomesticStandingOrder + "/" + submissionResp.data.domesticStandingOrderId)
        assertThat(paymentResult.meta).isNotNull()
    }
}