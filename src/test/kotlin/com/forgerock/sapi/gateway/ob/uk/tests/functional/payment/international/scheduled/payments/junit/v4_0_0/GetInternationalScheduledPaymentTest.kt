package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.scheduled.payments.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.scheduled.payments.api.v4_0_0.GetInternationalScheduledPayment
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetInternationalScheduledPaymentTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getInternationalScheduledPayment: GetInternationalScheduledPayment

    @BeforeEach
    fun setUp() {
        getInternationalScheduledPayment = GetInternationalScheduledPayment(OBVersion.v4_0_0, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["GetInternationalScheduledPayment", "CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun getInternationalScheduledPayments_rateType_AGREED_v4_0_0() {
        getInternationalScheduledPayment.getInternationalScheduledPayments_rateType_AGREED_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["GetInternationalScheduledPayment", "CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun getInternationalScheduledPayments_rateType_ACTUAL_v4_0_0() {
        getInternationalScheduledPayment.getInternationalScheduledPayments_rateType_ACTUAL_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["GetInternationalScheduledPayment", "CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun getInternationalScheduledPayments_rateType_INDICATIVE_v4_0_0() {
        getInternationalScheduledPayment.getInternationalScheduledPayments_rateType_INDICATIVE_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["GetInternationalScheduledPayment", "CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun getInternationalScheduledPayments__mandatoryFieldsv4_0_0() {
        getInternationalScheduledPayment.getInternationalScheduledPayments_mandatoryFields_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["GetInternationalScheduledPayment", "CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun shouldGetInternationalScheduledPayments_withReadRefund_v4_0_0() {
        getInternationalScheduledPayment.shouldGetInternationalScheduledPayments_withReadRefund_Test()
    }
}