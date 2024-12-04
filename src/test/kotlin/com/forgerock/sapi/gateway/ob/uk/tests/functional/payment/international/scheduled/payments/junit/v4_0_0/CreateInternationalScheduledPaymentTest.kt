package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.scheduled.payments.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.scheduled.payments.api.v4_0_0.CreateInternationalScheduledPayment
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class CreateInternationalScheduledPaymentTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var createInternationalScheduledPayment: CreateInternationalScheduledPayment

    @BeforeEach
    fun setUp() {
        createInternationalScheduledPayment = CreateInternationalScheduledPayment(OBVersion.v4_0_0, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun createInternationalScheduledPayment_rateType_AGREED_v4_0_0() {
        createInternationalScheduledPayment.createInternationalScheduledPayment_rateType_AGREED_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun createInternationalScheduledPayment_invalidInitiation_v4_0_0() {
        createInternationalScheduledPayment.shouldCreateInternationalScheduledPayment_throwsInvalidInitiation_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun createInternationalScheduledPayment_WithDebtorAccount_v4_0_0() {
        createInternationalScheduledPayment.createInternationalScheduledPayment_withDebtorAccount_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun createInternationalScheduledPayment_rateType_ACTUAL_v4_0_0() {
        createInternationalScheduledPayment.createInternationalScheduledPayment_rateType_ACTUAL_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun createInternationalScheduledPayment_rateType_INDICATIVE_v4_0_0() {
        createInternationalScheduledPayment.createInternationalScheduledPayment_rateType_INDICATIVE_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun createInternationalScheduledPayment_mandatoryFields_v4_0_0() {
        createInternationalScheduledPayment.createInternationalScheduledPayment_mandatoryFields_Test()
    }

    @Disabled("Bug: https://github.com/SecureBankingAccessToolkit/SecureBankingAccessToolkit/issues/336")
    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsInternationalScheduledPaymentAlreadyExists_v4_0_0() {
        createInternationalScheduledPayment.shouldCreateInternationalScheduledPayment_throwsInternationalScheduledPaymentAlreadyExists_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsSendInvalidFormatDetachedJws_v4_0_0() {
        createInternationalScheduledPayment.shouldCreateInternationalScheduledPayment_throwsSendInvalidFormatDetachedJws_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsNoDetachedJws_v4_0_0() {
        createInternationalScheduledPayment.shouldCreateInternationalScheduledPayment_throwsNoDetachedJws_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsNotPermittedB64HeaderAddedInTheDetachedJws_v4_0_0() {
        createInternationalScheduledPayment.shouldCreateInternationalScheduledPayment_throwsNotPermittedB64HeaderAddedInTheDetachedJws_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsSendInvalidKidDetachedJws_v4_0_0() {
        createInternationalScheduledPayment.shouldCreateInternationalScheduledPayment_throwsSendInvalidKidDetachedJws_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBody_v4_0_0() {
        createInternationalScheduledPayment.shouldCreateInternationalScheduledPayment_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBody_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBody_v4_0_0() {
        createInternationalScheduledPayment.shouldCreateInternationalScheduledPayment_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBody_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayments_throwsInvalidRiskTest_v4_0_0() {
        createInternationalScheduledPayment.shouldCreateInternationalScheduledPayments_throwsInvalidRiskTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun testCreatingPaymentIsIdempotent_v4_0_0() {
        createInternationalScheduledPayment.testCreatingPaymentIsIdempotent()
    }
}