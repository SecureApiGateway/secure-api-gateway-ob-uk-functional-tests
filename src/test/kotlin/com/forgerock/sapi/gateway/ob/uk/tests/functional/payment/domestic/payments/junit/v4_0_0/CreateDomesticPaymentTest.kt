package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.payments.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.payments.api.v4_0_0.CreateDomesticPayment
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CreateDomesticPaymentTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var createDomesticPaymentApi: CreateDomesticPayment

    @BeforeEach
    fun setUp() {
        createDomesticPaymentApi = CreateDomesticPayment(OBVersion.v4_0_0, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"]
    )
    @Test
    fun createDomesticPayments_withDebtorAccount_v4_0_0() {
        createDomesticPaymentApi.createDomesticPaymentsWithDebtorAccountTest()
    }

    @EnabledIfVersion(
            type = "payments",
            apiVersion = "v4.0.0",
            operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
            apis = ["domestic-payments", "domestic-payment-consents"]
    )
    @Test
    fun createDomesticPayments_v4_0_0() {
        createDomesticPaymentApi.createDomesticPaymentsTest()
    }

    @EnabledIfVersion(
            type = "payments",
            apiVersion = "v4.0.0",
            operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
            apis = ["domestic-payments", "domestic-payment-consents"]
    )
    @Test
    fun createDomesticPayments_ThrowsInvalidInitiation_v4_0_0() {
        createDomesticPaymentApi.createDomesticPayments_throwsInvalidInitiationTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticPayments_throwsPaymentAlreadyExists_v4_0_0() {
        createDomesticPaymentApi.shouldCreateDomesticPayments_throwsPaymentAlreadyExistsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticPayments_throwsSendInvalidFormatDetachedJws_v4_0_0() {
        createDomesticPaymentApi.shouldCreateDomesticPayments_throwsSendInvalidFormatDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticPayments_throwsNoDetachedJws_v4_0_0() {
        createDomesticPaymentApi.shouldCreateDomesticPayments_throwsNoDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticPayments_throwsNotPermittedB64HeaderAddedInTheDetachedJws_v4_0_0() {
        createDomesticPaymentApi.shouldCreateDomesticPayments_throwsNotPermittedB64HeaderAddedInTheDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticPayments_throwsSendInvalidKidDetachedJws_v4_0_0() {
        createDomesticPaymentApi.shouldCreateDomesticPayments_throwsSendInvalidKidDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticPayments_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBody_v4_0_0() {
        createDomesticPaymentApi.shouldCreateDomesticPayments_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBodyTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticPayments_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBody_v4_0_0() {
        createDomesticPaymentApi.shouldCreateDomesticPayments_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBodyTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticPayments_throwsInvalidRisk_v4_0_0() {
        createDomesticPaymentApi.shouldCreateDomesticPayments_throwsInvalidRiskTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"]
    )
    @Test
    fun testCreatingPaymentIsIdempotent_v4_0_0() {
        createDomesticPaymentApi.testCreatingPaymentIsIdempotent()
    }

}