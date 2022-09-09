package com.forgerock.uk.openbanking.tests.functional.payment.international.payments.junit.v3_1_10

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.payment.international.payments.api.v3_1_8.CreateInternationalPayment
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CreateInternationalPaymentTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var createInternationalPayment: CreateInternationalPayment

    @BeforeEach
    fun setUp() {
        createInternationalPayment = CreateInternationalPayment(OBVersion.v3_1_10, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateInternationalPayment", "CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun createInternationalPayment_rateType_AGREED_v3_1_10() {
        createInternationalPayment.createInternationalPayment_rateType_AGREED_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateInternationalPayment", "CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun createInternationalPayment_rateType_ACTUAL_v3_1_10() {
        createInternationalPayment.createInternationalPayment_rateType_ACTUAL_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateInternationalPayment", "CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun createInternationalPayment_rateType_INDICATIVE_v3_1_10() {
        createInternationalPayment.createInternationalPayment_rateType_INDICATIVE_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateInternationalPayment", "CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun createInternationalPayment_mandatoryFields_v3_1_10() {
        createInternationalPayment.createInternationalPayment_mandatoryFields_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateInternationalPayment", "CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalPayment_throwsInternationalPaymentAlreadyExists_v3_1_10() {
        createInternationalPayment.shouldCreateInternationalPayment_throwsInternationalPaymentAlreadyExists_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateInternationalPayment", "CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalPayment_throwsSendInvalidFormatDetachedJws_v3_1_10() {
        createInternationalPayment.shouldCreateInternationalPayment_throwsSendInvalidFormatDetachedJws_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateInternationalPayment", "CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalPayment_throwsNoDetachedJws_v3_1_10() {
        createInternationalPayment.shouldCreateInternationalPayment_throwsNoDetachedJws_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateInternationalPayment", "CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalPayment_throwsNotPermittedB64HeaderAddedInTheDetachedJws_v3_1_10() {
        createInternationalPayment.shouldCreateInternationalPayment_throwsNotPermittedB64HeaderAddedInTheDetachedJws_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateInternationalPayment", "CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalPayment_throwsSendInvalidKidDetachedJws_v3_1_10() {
        createInternationalPayment.shouldCreateInternationalPayment_throwsSendInvalidKidDetachedJws_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateInternationalPayment", "CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalPayment_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBody_v3_1_10() {
        createInternationalPayment.shouldCreateInternationalPayment_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBody_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateInternationalPayment", "CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalPayment_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBody_v3_1_10() {
        createInternationalPayment.shouldCreateInternationalPayment_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBody_Test()
    }
}