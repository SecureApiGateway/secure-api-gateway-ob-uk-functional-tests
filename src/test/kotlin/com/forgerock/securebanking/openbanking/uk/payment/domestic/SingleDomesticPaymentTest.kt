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
import com.forgerock.openbanking.discovery.payment3_1
import com.forgerock.openbanking.discovery.payment3_1_2
import com.forgerock.openbanking.discovery.payment3_1_4
import com.forgerock.openbanking.discovery.payment3_1_6
import com.forgerock.openbanking.junit.CreateTppCallback
import com.forgerock.openbanking.junit.EnabledIfOpenBankingVersion
import com.forgerock.openbanking.payment.PaymentAS
import com.forgerock.openbanking.payment.PaymentRS
import com.forgerock.openbanking.psu
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.payment.*
import uk.org.openbanking.datamodel.service.converter.payment.OBDomesticConverter.toOBDomestic2
import uk.org.openbanking.testsupport.payment.OBWriteDomesticConsentTestDataFactory.*

@Tags(Tag("paymentTest"))
class SingleDomesticPaymentTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfOpenBankingVersion(type = "payments", version = "v3.1.2", apis = ["domestic-payment-consents"])
    @Test
    fun shouldCreateSingleDomesticPaymentConsent_v3_1_2() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent2()

        // When
        val consent = PaymentRS().consent<OBWriteDomesticConsentResponse2>(payment3_1_2.Links.links.CreateDomesticPaymentConsent, consentRequest, tppResource.tpp)
        val getConsentResult = PaymentRS().getConsent<OBWriteDomesticConsentResponse2>(payment3_1_2.Links.links.CreateDomesticPaymentConsent+"/"+consent.data.consentId, tppResource.tpp)

        // Then
        assertThat(getConsentResult).isNotNull()
        assertThat(getConsentResult.data.consentId).isNotEmpty()
        Assertions.assertThat(getConsentResult.data.status.toString()).`is`(consentCondition)
        assertThat(getConsentResult.links.self).isEqualTo(payment3_1.Links.links.CreateDomesticPaymentConsent+"/"+consent.data.consentId)
        assertThat(getConsentResult.meta).isNotNull()
    }

    @EnabledIfOpenBankingVersion(type = "payments", version = "v3.1.2", apis = ["domestic-payment-consents"])
    @Test
    fun shouldCreateSingleDomesticPaymentConsent_noOptionalDebtorAccount_v3_1_2() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent2()
        consentRequest.data.initiation.debtorAccount(null)

        // When
        val consent = PaymentRS().consent<OBWriteDomesticConsentResponse2>(payment3_1_2.Links.links.CreateDomesticPaymentConsent, consentRequest, tppResource.tpp)
        val getConsentResult = PaymentRS().getConsent<OBWriteDomesticConsentResponse2>(payment3_1_2.Links.links.CreateDomesticPaymentConsent+"/"+consent.data.consentId, tppResource.tpp)

        // Then
        assertThat(getConsentResult).isNotNull()
        assertThat(getConsentResult.data.consentId).isNotEmpty()
        Assertions.assertThat(getConsentResult.data.status.toString()).`is`(consentCondition)
        assertThat(getConsentResult.links.self).isEqualTo(payment3_1.Links.links.CreateDomesticPaymentConsent+"/"+consent.data.consentId)
        assertThat(getConsentResult.meta).isNotNull()
        assertThat(getConsentResult.data.initiation.debtorAccount).isNull()
    }

    @EnabledIfOpenBankingVersion(type = "payments", version = "v3.1.2", apis = ["domestic-payments", "domestic-payment-consents"])
    @Test
    fun shouldCreateSingleDomesticPayment_v3_1_2() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent2()
        val consent = PaymentRS().consent<OBWriteDomesticConsentResponse2>(payment3_1_2.Links.links.CreateDomesticPaymentConsent, consentRequest, tppResource.tpp)
        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().headlessAuthentication(consent.data.consentId, tppResource.tpp.registrationResponse, psu, tppResource.tpp)
        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentAS().clientCredentialsAuthentication(consent.data.consentId, tppResource.tpp.registrationResponse, tppResource.tpp)
        val paymentSubmissionRequest = OBWriteDomestic2().data(
                OBWriteDataDomestic2()
                        .consentId(consent.data.consentId)
                        .initiation(consent.data.initiation)
        ).risk(consent.risk)
        val submissionResp = PaymentRS().submitPayment<OBWriteDomesticResponse2>(payment3_1_2.Links.links.CreateDomesticPayment, paymentSubmissionRequest, accessTokenAuthorizationCode, tppResource.tpp)

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteDomesticResponse2>(payment3_1_2.Links.links.CreateDomesticPayment, submissionResp.data.domesticPaymentId, accessTokenClientCredentials)

        // Then
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.domesticPaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1.Links.links.CreateDomesticPayment+"/"+submissionResp.data.domesticPaymentId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfOpenBankingVersion(type = "payments", version = "v3.1.4", apis = ["domestic-payment-consents"])
    @Test
    fun shouldCreateSingleDomesticPaymentConsent_noOptionalDebtorAccount_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent4()
        consentRequest.data.initiation.debtorAccount(null)

        // When
        val consent = PaymentRS().consent<OBWriteDomesticConsentResponse4>(payment3_1_4.Links.links.CreateDomesticPaymentConsent, consentRequest, tppResource.tpp, v3_1_4)
        val getConsentResult = PaymentRS().getConsent<OBWriteDomesticConsentResponse4>(payment3_1_4.Links.links.CreateDomesticPaymentConsent+"/"+consent.data.consentId, tppResource.tpp)

        // Then
        assertThat(getConsentResult).isNotNull()
        assertThat(getConsentResult.data.consentId).isNotEmpty()
        Assertions.assertThat(getConsentResult.data.status.toString()).`is`(consentCondition)
        assertThat(getConsentResult.links.self).isEqualTo(payment3_1_4.Links.links.CreateDomesticPaymentConsent+"/"+consent.data.consentId)
        assertThat(getConsentResult.meta).isNotNull()
        assertThat(getConsentResult.data.initiation.debtorAccount).isNull()
    }

    @EnabledIfOpenBankingVersion(type = "payments", version = "v3.1.4", apis = ["domestic-payment-consents"])
    @Test
    fun shouldCreateSingleDomesticPaymentConsent_v3_1_4_readRefund() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent4()
        consentRequest.data.readRefundAccount(OBWriteDomesticConsent4Data.ReadRefundAccountEnum.YES)
        consentRequest.data.initiation.debtorAccount(null)

        // When
        val consent = PaymentRS().consent<OBWriteDomesticConsentResponse4>(payment3_1_4.Links.links.CreateDomesticPaymentConsent, consentRequest, tppResource.tpp, v3_1_4)
        val getConsentResult = PaymentRS().getConsent<OBWriteDomesticConsentResponse4>(payment3_1_4.Links.links.CreateDomesticPaymentConsent+"/"+consent.data.consentId, tppResource.tpp)

        // Then
        assertThat(getConsentResult).isNotNull()
        assertThat(getConsentResult.data.readRefundAccount).isEqualTo(OBWriteDomesticConsentResponse4Data.ReadRefundAccountEnum.YES)
        assertThat(getConsentResult.data.consentId).isNotEmpty()
        Assertions.assertThat(getConsentResult.data.status.toString()).`is`(consentCondition)
        assertThat(getConsentResult.links.self).isEqualTo(payment3_1_4.Links.links.CreateDomesticPaymentConsent+"/"+consent.data.consentId)
        assertThat(getConsentResult.meta).isNotNull()
        assertThat(getConsentResult.data.initiation.debtorAccount).isNull()
    }

    @EnabledIfOpenBankingVersion(type = "payments", version = "v3.1.4", apis = ["domestic-payments", "domestic-payment-consents"])
    @Test
    fun shouldCreateSingleDomesticPayment_v3_1_4_readRefund() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent4()
        consentRequest.data.readRefundAccount(OBWriteDomesticConsent4Data.ReadRefundAccountEnum.YES)
        val consent = PaymentRS().consent<OBWriteDomesticConsentResponse4>(payment3_1_4.Links.links.CreateDomesticPaymentConsent, consentRequest, tppResource.tpp, v3_1_4)
        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().headlessAuthentication(consent.data.consentId, tppResource.tpp.registrationResponse, psu, tppResource.tpp)
        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentAS().clientCredentialsAuthentication(consent.data.consentId, tppResource.tpp.registrationResponse, tppResource.tpp)
        val paymentSubmissionRequest = OBWriteDomestic2().data(
                OBWriteDataDomestic2()
                        .consentId(consent.data.consentId)
                        .initiation(toOBDomestic2(consent.data.initiation))
        ).risk(consent.risk)
        val submissionResp = PaymentRS().submitPayment<OBWriteDomesticResponse4>(payment3_1_4.Links.links.CreateDomesticPayment, paymentSubmissionRequest, accessTokenAuthorizationCode, tppResource.tpp, v3_1_4)

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteDomesticResponse4>(payment3_1_4.Links.links.CreateDomesticPayment, submissionResp.data.domesticPaymentId, accessTokenClientCredentials, v3_1_4)

        // Then
        assertThat(consent.data.readRefundAccount).isEqualTo(OBWriteDomesticConsentResponse4Data.ReadRefundAccountEnum.YES)
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.refund.account.identification).isEqualTo(consent.data.initiation.debtorAccount.identification)
        assertThat(paymentResult.data.domesticPaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        assertThat(paymentResult.data.refund).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_4.Links.links.CreateDomesticPayment+"/"+submissionResp.data.domesticPaymentId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfOpenBankingVersion(type = "payments", version = "v3.1.4", apis = ["domestic-payments", "domestic-payment-consents"])
    @Test
    fun shouldCreateSingleDomesticPayment_v3_1_4_readRefund_Null() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent4()
        consentRequest.data.readRefundAccount(null)
        val consent = PaymentRS().consent<OBWriteDomesticConsentResponse4>(payment3_1_4.Links.links.CreateDomesticPaymentConsent, consentRequest, tppResource.tpp, v3_1_4)
        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().headlessAuthentication(consent.data.consentId, tppResource.tpp.registrationResponse, psu, tppResource.tpp)
        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentAS().clientCredentialsAuthentication(consent.data.consentId, tppResource.tpp.registrationResponse, tppResource.tpp)
        val paymentSubmissionRequest = OBWriteDomestic2().data(
                OBWriteDataDomestic2()
                        .consentId(consent.data.consentId)
                        .initiation(toOBDomestic2(consent.data.initiation))
        ).risk(consent.risk)
        val submissionResp = PaymentRS().submitPayment<OBWriteDomesticResponse4>(payment3_1_4.Links.links.CreateDomesticPayment, paymentSubmissionRequest, accessTokenAuthorizationCode, tppResource.tpp, v3_1_4)

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteDomesticResponse4>(payment3_1_4.Links.links.CreateDomesticPayment, submissionResp.data.domesticPaymentId, accessTokenClientCredentials, v3_1_4)

        // Then
        assertThat(consent.data.readRefundAccount).isNull()
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.refund).isNull()
        assertThat(paymentResult.data.domesticPaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_4.Links.links.CreateDomesticPayment+"/"+submissionResp.data.domesticPaymentId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfOpenBankingVersion(type = "payments", version = "v3.1.4", apis = ["domestic-payments", "domestic-payment-consents"])
    @Test
    fun shouldCreateSingleDomesticPayment_v3_1_4_readRefund_debtorAccountNotPresent() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent4()
        consentRequest.data.readRefundAccount(OBWriteDomesticConsent4Data.ReadRefundAccountEnum.YES)
        consentRequest.data.initiation.debtorAccount(null)
        val consent = PaymentRS().consent<OBWriteDomesticConsentResponse4>(payment3_1_4.Links.links.CreateDomesticPaymentConsent, consentRequest, tppResource.tpp, v3_1_4)
        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().headlessAuthentication(consent.data.consentId, tppResource.tpp.registrationResponse, psu, tppResource.tpp)
        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentAS().clientCredentialsAuthentication(consent.data.consentId, tppResource.tpp.registrationResponse, tppResource.tpp)
        val paymentSubmissionRequest = OBWriteDomestic2().data(
                OBWriteDataDomestic2()
                        .consentId(consent.data.consentId)
                        .initiation(toOBDomestic2(consent.data.initiation))
        ).risk(consent.risk)
        val submissionResp = PaymentRS().submitPayment<OBWriteDomesticResponse4>(payment3_1_4.Links.links.CreateDomesticPayment, paymentSubmissionRequest, accessTokenAuthorizationCode, tppResource.tpp, v3_1_4)

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteDomesticResponse4>(payment3_1_4.Links.links.CreateDomesticPayment, submissionResp.data.domesticPaymentId, accessTokenClientCredentials, v3_1_4)

        // Then
        assertThat(consent.data.readRefundAccount).isEqualTo(OBWriteDomesticConsentResponse4Data.ReadRefundAccountEnum.YES)
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.refund).isNull()
        assertThat(paymentResult.data.domesticPaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_4.Links.links.CreateDomesticPayment+"/"+submissionResp.data.domesticPaymentId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfOpenBankingVersion(type = "payments", version = "v3.1.4", apis = ["domestic-payments", "domestic-payment-consents"])
    @Test
    fun shouldCreateSingleDomesticPayment_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent4()
        val consent = PaymentRS().consent<OBWriteDomesticConsentResponse4>(payment3_1_4.Links.links.CreateDomesticPaymentConsent, consentRequest, tppResource.tpp, v3_1_4)
        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().headlessAuthentication(consent.data.consentId, tppResource.tpp.registrationResponse, psu, tppResource.tpp)
        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentAS().clientCredentialsAuthentication(consent.data.consentId, tppResource.tpp.registrationResponse, tppResource.tpp)
        val paymentSubmissionRequest = OBWriteDomestic2().data(
                OBWriteDataDomestic2()
                        .consentId(consent.data.consentId)
                        .initiation(toOBDomestic2(consent.data.initiation))
        ).risk(consent.risk)
        val submissionResp = PaymentRS().submitPayment<OBWriteDomesticResponse4>(payment3_1_4.Links.links.CreateDomesticPayment, paymentSubmissionRequest, accessTokenAuthorizationCode, tppResource.tpp, v3_1_4)

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteDomesticResponse4>(payment3_1_4.Links.links.CreateDomesticPayment, submissionResp.data.domesticPaymentId, accessTokenClientCredentials, v3_1_4)

        // Then
        assertThat(consent.data.readRefundAccount).isEqualTo(OBWriteDomesticConsentResponse4Data.ReadRefundAccountEnum.NO)
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.domesticPaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_4.Links.links.CreateDomesticPayment+"/"+submissionResp.data.domesticPaymentId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfOpenBankingVersion(type = "payments", version = "v3.1.6", apis = ["domestic-payment-consents"])
    @Test
    fun shouldCreateSingleDomesticPaymentConsent_noOptionalDebtorAccount_v3_1_6() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent4()
        consentRequest.data.initiation.debtorAccount(null)

        // When
        val consent = PaymentRS().consent<OBWriteDomesticConsentResponse5>(payment3_1_6.Links.links.CreateDomesticPaymentConsent, consentRequest, tppResource.tpp, v3_1_6)
        val getConsentResult = PaymentRS().getConsent<OBWriteDomesticConsentResponse5>(payment3_1_6.Links.links.CreateDomesticPaymentConsent+"/"+consent.data.consentId, tppResource.tpp)

        // Then
        assertThat(consent.data.readRefundAccount).isEqualTo(OBWriteDomesticConsentResponse5Data.ReadRefundAccountEnum.NO)
        assertThat(getConsentResult).isNotNull()
        assertThat(getConsentResult.data.consentId).isNotEmpty()
        Assertions.assertThat(getConsentResult.data.status.toString()).`is`(consentCondition)
        assertThat(getConsentResult.links.self).isEqualTo(payment3_1_6.Links.links.CreateDomesticPaymentConsent+"/"+consent.data.consentId)
        assertThat(getConsentResult.meta).isNotNull()
        assertThat(getConsentResult.data.initiation.debtorAccount).isNull()
    }

    @EnabledIfOpenBankingVersion(type = "payments", version = "v3.1.6", apis = ["domestic-payment-consents"])
    @Test
    fun shouldCreateSingleDomesticPaymentConsent_v3_1_6_readRefund() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent4()
        consentRequest.data.readRefundAccount(OBWriteDomesticConsent4Data.ReadRefundAccountEnum.YES)

        // When
        val consent = PaymentRS().consent<OBWriteDomesticConsentResponse5>(payment3_1_6.Links.links.CreateDomesticPaymentConsent, consentRequest, tppResource.tpp, v3_1_6)
        val getConsentResult = PaymentRS().getConsent<OBWriteDomesticConsentResponse5>(payment3_1_6.Links.links.CreateDomesticPaymentConsent+"/"+consent.data.consentId, tppResource.tpp)

        // Then
        assertThat(getConsentResult.data.readRefundAccount).isEqualTo(OBWriteDomesticConsentResponse5Data.ReadRefundAccountEnum.YES)
        assertThat(getConsentResult).isNotNull()
        assertThat(getConsentResult.data.consentId).isNotEmpty()
        Assertions.assertThat(getConsentResult.data.status.toString()).`is`(consentCondition)
        assertThat(getConsentResult.links.self).isEqualTo(payment3_1_6.Links.links.CreateDomesticPaymentConsent+"/"+consent.data.consentId)
        assertThat(getConsentResult.meta).isNotNull()
    }

    @EnabledIfOpenBankingVersion(type = "payments", version = "v3.1.6", apis = ["domestic-payments", "domestic-payment-consents"])
    @Test
    fun shouldCreateSingleDomesticPayment_v3_1_6() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent4()
        val consent = PaymentRS().consent<OBWriteDomesticConsentResponse5>(payment3_1_6.Links.links.CreateDomesticPaymentConsent, consentRequest, tppResource.tpp, v3_1_6)
        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().headlessAuthentication(consent.data.consentId, tppResource.tpp.registrationResponse, psu, tppResource.tpp)
        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentAS().clientCredentialsAuthentication(consent.data.consentId, tppResource.tpp.registrationResponse, tppResource.tpp)
        val paymentSubmissionRequest = OBWriteDomestic2().data(
                OBWriteDataDomestic2()
                        .consentId(consent.data.consentId)
                        .initiation(toOBDomestic2(consent.data.initiation))
        ).risk(consent.risk)
        val submissionResp = PaymentRS().submitPayment<OBWriteDomesticResponse5>(payment3_1_6.Links.links.CreateDomesticPayment, paymentSubmissionRequest, accessTokenAuthorizationCode, tppResource.tpp, v3_1_6)

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteDomesticResponse5>(payment3_1_6.Links.links.CreateDomesticPayment, submissionResp.data.domesticPaymentId, accessTokenClientCredentials, v3_1_6)

        // Then
        assertThat(consent.data.readRefundAccount).isEqualTo(OBWriteDomesticConsentResponse5Data.ReadRefundAccountEnum.NO)
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.refund).isNull()
        assertThat(paymentResult.data.domesticPaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        assertThat(paymentResult.data.debtor.name).isEqualTo(consentRequest.data.initiation.debtorAccount.name)
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_6.Links.links.CreateDomesticPayment+"/"+submissionResp.data.domesticPaymentId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfOpenBankingVersion(type = "payments", version = "v3.1.6", apis = ["domestic-payments", "domestic-payment-consents"])
    @Test
    fun shouldCreateSingleDomesticPayment_v3_1_6_readRefund() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent4()
        consentRequest.data.readRefundAccount(OBWriteDomesticConsent4Data.ReadRefundAccountEnum.YES)
        val consent = PaymentRS().consent<OBWriteDomesticConsentResponse5>(payment3_1_6.Links.links.CreateDomesticPaymentConsent, consentRequest, tppResource.tpp, v3_1_6)
        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().headlessAuthentication(consent.data.consentId, tppResource.tpp.registrationResponse, psu, tppResource.tpp)
        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentAS().clientCredentialsAuthentication(consent.data.consentId, tppResource.tpp.registrationResponse, tppResource.tpp)
        val paymentSubmissionRequest = OBWriteDomestic2().data(
                OBWriteDataDomestic2()
                        .consentId(consent.data.consentId)
                        .initiation(toOBDomestic2(consent.data.initiation))
        ).risk(consent.risk)
        val submissionResp = PaymentRS().submitPayment<OBWriteDomesticResponse5>(payment3_1_6.Links.links.CreateDomesticPayment, paymentSubmissionRequest, accessTokenAuthorizationCode, tppResource.tpp, v3_1_6)

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteDomesticResponse5>(payment3_1_6.Links.links.CreateDomesticPayment, submissionResp.data.domesticPaymentId, accessTokenClientCredentials, v3_1_6)

        // Then
        assertThat(consent.data.readRefundAccount).isEqualTo(OBWriteDomesticConsentResponse5Data.ReadRefundAccountEnum.YES)
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.domesticPaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        assertThat(paymentResult.data.refund).isNotNull()
        assertThat(paymentResult.data.refund.account.identification).isEqualTo(consent.data.initiation.debtorAccount.identification)
        assertThat(paymentResult.data.debtor.name).isEqualTo(consentRequest.data.initiation.debtorAccount.name)
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_6.Links.links.CreateDomesticPayment+"/"+submissionResp.data.domesticPaymentId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfOpenBankingVersion(type = "payments", version = "v3.1.6", apis = ["domestic-payments", "domestic-payment-consents"])
    @Test
    fun shouldCreateSingleDomesticPayment_v3_1_6_readRefund_Null() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent4()
        consentRequest.data.readRefundAccount(null)
        val consent = PaymentRS().consent<OBWriteDomesticConsentResponse5>(payment3_1_6.Links.links.CreateDomesticPaymentConsent, consentRequest, tppResource.tpp, v3_1_6)
        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().headlessAuthentication(consent.data.consentId, tppResource.tpp.registrationResponse, psu, tppResource.tpp)
        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentAS().clientCredentialsAuthentication(consent.data.consentId, tppResource.tpp.registrationResponse, tppResource.tpp)
        val paymentSubmissionRequest = OBWriteDomestic2().data(
                OBWriteDataDomestic2()
                        .consentId(consent.data.consentId)
                        .initiation(toOBDomestic2(consent.data.initiation))
        ).risk(consent.risk)
        val submissionResp = PaymentRS().submitPayment<OBWriteDomesticResponse5>(payment3_1_6.Links.links.CreateDomesticPayment, paymentSubmissionRequest, accessTokenAuthorizationCode, tppResource.tpp, v3_1_6)

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteDomesticResponse5>(payment3_1_6.Links.links.CreateDomesticPayment, submissionResp.data.domesticPaymentId, accessTokenClientCredentials, v3_1_6)

        // Then
        assertThat(consent.data.readRefundAccount).isNull()
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.refund).isNull()
        assertThat(paymentResult.data.domesticPaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        assertThat(paymentResult.data.debtor.name).isEqualTo(consentRequest.data.initiation.debtorAccount.name)
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_6.Links.links.CreateDomesticPayment+"/"+submissionResp.data.domesticPaymentId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfOpenBankingVersion(type = "payments", version = "v3.1.6", apis = ["domestic-payments", "domestic-payment-consents"])
    @Test
    fun shouldCreateSingleDomesticPayment_v3_1_6_readRefund_debtorAccountNotPresent() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent4()
        consentRequest.data.readRefundAccount(OBWriteDomesticConsent4Data.ReadRefundAccountEnum.YES)
        consentRequest.data.initiation.debtorAccount(null)
        val consent = PaymentRS().consent<OBWriteDomesticConsentResponse5>(payment3_1_6.Links.links.CreateDomesticPaymentConsent, consentRequest, tppResource.tpp, v3_1_6)
        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().headlessAuthentication(consent.data.consentId, tppResource.tpp.registrationResponse, psu, tppResource.tpp)
        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentAS().clientCredentialsAuthentication(consent.data.consentId, tppResource.tpp.registrationResponse, tppResource.tpp)
        val paymentSubmissionRequest = OBWriteDomestic2().data(
                OBWriteDataDomestic2()
                        .consentId(consent.data.consentId)
                        .initiation(toOBDomestic2(consent.data.initiation))
        ).risk(consent.risk)
        val submissionResp = PaymentRS().submitPayment<OBWriteDomesticResponse5>(payment3_1_6.Links.links.CreateDomesticPayment, paymentSubmissionRequest, accessTokenAuthorizationCode, tppResource.tpp, v3_1_6)

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteDomesticResponse5>(payment3_1_6.Links.links.CreateDomesticPayment, submissionResp.data.domesticPaymentId, accessTokenClientCredentials, v3_1_6)

        // Then
        assertThat(consent.data.readRefundAccount).isEqualTo(OBWriteDomesticConsentResponse5Data.ReadRefundAccountEnum.YES)
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.refund).isNull()
        assertThat(paymentResult.data.domesticPaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_6.Links.links.CreateDomesticPayment+"/"+submissionResp.data.domesticPaymentId)
        assertThat(paymentResult.meta).isNotNull()
    }

    // API version not important - functionality applies to all versions
    @EnabledIfOpenBankingVersion(type = "payments", version = "v3.1", apis = ["domestic-payment-consents"])
    @Test
    fun shouldFailToCreateConsent_InvalidDetachedJws() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent2()

        // When
        val response = PaymentRS().consentRequest_InvalidDetachedJws<OBWriteDomesticConsentResponse2>(payment3_1.Links.links.CreateDomesticPaymentConsent, consentRequest, tppResource.tpp)

        // Then
        assertThat(response.statusCode).isEqualTo(401)
        assertThat(response.responseMessage).isEqualTo("Unauthorized")
    }

    // API version not important - functionality applies to all versions
    @EnabledIfOpenBankingVersion(type = "payments", version = "v3.1", apis = ["domestic-payments", "domestic-payment-consents"])
    @Test
    fun shouldFailToCreatePayment_InvalidDetachedJws() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent2()
        val consent = PaymentRS().consent<OBWriteDomesticConsentResponse2>(payment3_1.Links.links.CreateDomesticPaymentConsent, consentRequest, tppResource.tpp)
        // accessToken to submit payment use the grant type headless
        val accessTokenSubmitPayment = PaymentAS().headlessAuthentication(consent.data.consentId, tppResource.tpp.registrationResponse, psu, tppResource.tpp)
        val paymentSubmissionRequest = OBWriteDomestic2().data(
                OBWriteDataDomestic2()
                        .consentId(consent.data.consentId)
                        .initiation(consent.data.initiation)
        ).risk(consent.risk)

        // When
        val response = PaymentRS().submitPayment_InvalidDetachedJws<OBWriteDomesticResponse2>(payment3_1.Links.links.CreateDomesticPayment, paymentSubmissionRequest, accessTokenSubmitPayment)

        // Then
        assertThat(response.statusCode).isEqualTo(401)
        assertThat(response.responseMessage).isEqualTo("Unauthorized")
    }

    // API version not important - functionality applies to all versions
    @EnabledIfOpenBankingVersion(type = "payments", version = "v3.1", apis = ["domestic-payment-consents"])
    @Test
    fun shouldFailToCreateConsent_DetachedJwsWithoutB64Claim() {
        // Given
        val consentRequest = aValidOBWriteDomesticConsent2()

        // When
        val response = PaymentRS().consentRequest_DetachedJwsMissingB64Claim<OBWriteDomesticConsentResponse2>(payment3_1.Links.links.CreateDomesticPaymentConsent, consentRequest, tppResource.tpp)

        // Then
        assertThat(response.statusCode).isEqualTo(401)
        assertThat(response.responseMessage).isEqualTo("Unauthorized")
    }

}