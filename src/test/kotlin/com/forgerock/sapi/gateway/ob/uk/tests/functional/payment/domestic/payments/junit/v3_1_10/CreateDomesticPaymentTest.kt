package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.payments.junit.v3_1_10

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.payments.api.v3_1_8.CreateDomesticPayment
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CreateDomesticPaymentTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var createDomesticPaymentApi: CreateDomesticPayment

    @BeforeEach
    fun setUp() {
        createDomesticPaymentApi = CreateDomesticPayment(OBVersion.v3_1_10, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"]
    )
    @Test
    fun createDomesticPayments_withDebtorAccount_v3_1_10() {
        createDomesticPaymentApi.createDomesticPaymentsWithDebtorAccountTest()
    }

    @EnabledIfVersion(
            type = "payments",
            apiVersion = "v3.1.10",
            operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
            apis = ["domestic-payments", "domestic-payment-consents"]
    )
    @Test
    fun createDomesticPayments_v3_1_10() {
        createDomesticPaymentApi.createDomesticPaymentsTest()
    }

    @EnabledIfVersion(
            type = "payments",
            apiVersion = "v3.1.10",
            operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
            apis = ["domestic-payments", "domestic-payment-consents"]
    )
    @Test
    fun createDomesticPayments_ThrowsInvalidInitiation_v3_1_10() {
        createDomesticPaymentApi.createDomesticPayments_throwsInvalidInitiationTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticPayments_throwsPaymentAlreadyExists_v3_1_10() {
        createDomesticPaymentApi.shouldCreateDomesticPayments_throwsPaymentAlreadyExistsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticPayments_throwsSendInvalidFormatDetachedJws_v3_1_10() {
        createDomesticPaymentApi.shouldCreateDomesticPayments_throwsSendInvalidFormatDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticPayments_throwsNoDetachedJws_v3_1_10() {
        createDomesticPaymentApi.shouldCreateDomesticPayments_throwsNoDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticPayments_throwsNotPermittedB64HeaderAddedInTheDetachedJws_v3_1_10() {
        createDomesticPaymentApi.shouldCreateDomesticPayments_throwsNotPermittedB64HeaderAddedInTheDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticPayments_throwsSendInvalidKidDetachedJws_v3_1_10() {
        createDomesticPaymentApi.shouldCreateDomesticPayments_throwsSendInvalidKidDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticPayments_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBody_v3_1_10() {
        createDomesticPaymentApi.shouldCreateDomesticPayments_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBodyTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticPayments_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBody_v3_1_10() {
        createDomesticPaymentApi.shouldCreateDomesticPayments_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBodyTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticPayments_throwsInvalidRisk_v3_1_10() {
        createDomesticPaymentApi.shouldCreateDomesticPayments_throwsInvalidRiskTest()
    }
}