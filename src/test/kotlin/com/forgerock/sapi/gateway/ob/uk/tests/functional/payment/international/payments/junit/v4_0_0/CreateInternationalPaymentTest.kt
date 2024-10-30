package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.payments.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.payments.api.v4_0_0.CreateInternationalPayment
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CreateInternationalPaymentTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var createInternationalPayment: CreateInternationalPayment

    @BeforeEach
    fun setUp() {
        createInternationalPayment = CreateInternationalPayment(OBVersion.v4_0_0, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalPayment", "CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun createInternationalPayment_rateType_AGREED_v4_0_0() {
        createInternationalPayment.createInternationalPayment_rateType_AGREED_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalPayment", "CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun createInternationalPayment_rateType_ACTUAL_v4_0_0() {
        createInternationalPayment.createInternationalPayment_rateType_ACTUAL_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalPayment", "CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun createInternationalPayment_throwsInvalidInitiation_v4_0_0() {
        createInternationalPayment.shouldCreateInternationalPayment_throwsInvalidInitiation_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalPayment", "CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun createInternationalPayment_withDebtorAccount_v4_0_0() {
        createInternationalPayment.createInternationalPayment_withDebtorAccount_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalPayment", "CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun createInternationalPayment_rateType_INDICATIVE_v4_0_0() {
        createInternationalPayment.createInternationalPayment_rateType_INDICATIVE_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalPayment", "CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun createInternationalPayment_mandatoryFields_v4_0_0() {
        createInternationalPayment.createInternationalPayment_mandatoryFields_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalPayment", "CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalPayment_throwsInternationalPaymentAlreadyExists_v4_0_0() {
        createInternationalPayment.shouldCreateInternationalPayment_throwsInternationalPaymentAlreadyExists_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalPayment", "CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalPayment_throwsSendInvalidFormatDetachedJws_v4_0_0() {
        createInternationalPayment.shouldCreateInternationalPayment_throwsSendInvalidFormatDetachedJws_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalPayment", "CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalPayment_throwsNoDetachedJws_v4_0_0() {
        createInternationalPayment.shouldCreateInternationalPayment_throwsNoDetachedJws_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalPayment", "CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalPayment_throwsNotPermittedB64HeaderAddedInTheDetachedJws_v4_0_0() {
        createInternationalPayment.shouldCreateInternationalPayment_throwsNotPermittedB64HeaderAddedInTheDetachedJws_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalPayment", "CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalPayment_throwsSendInvalidKidDetachedJws_v4_0_0() {
        createInternationalPayment.shouldCreateInternationalPayment_throwsSendInvalidKidDetachedJws_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalPayment", "CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalPayment_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBody_v4_0_0() {
        createInternationalPayment.shouldCreateInternationalPayment_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBody_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalPayment", "CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalPayment_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBody_v4_0_0() {
        createInternationalPayment.shouldCreateInternationalPayment_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBody_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalPayment", "CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalPayments_throwsInvalidRiskTest_v4_0_0() {
        createInternationalPayment.shouldCreateInternationalPayments_throwsInvalidRiskTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalPayment", "CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun testCreatingPaymentIsIdempotent_v4_0_0() {
        createInternationalPayment.testCreatingPaymentIsIdempotent()
    }
}