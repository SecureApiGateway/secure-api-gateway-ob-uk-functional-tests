package com.forgerock.uk.openbanking.tests.functional.payment.international.scheduled.payments.junit.v3_1_10

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.payment.international.scheduled.payments.api.v3_1_8.GetInternationalScheduledPayment
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class GetInternationalScheduledPaymentTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getInternationalScheduledPayment: GetInternationalScheduledPayment

    @BeforeEach
    fun setUp() {
        getInternationalScheduledPayment = GetInternationalScheduledPayment(OBVersion.v3_1_10, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["GetInternationalScheduledPayment", "CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun getInternationalScheduledPayments_rateType_AGREED_v3_1_10() {
        getInternationalScheduledPayment.getInternationalScheduledPayments_rateType_AGREED_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["GetInternationalScheduledPayment", "CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun getInternationalScheduledPayments_rateType_ACTUAL_v3_1_10() {
        getInternationalScheduledPayment.getInternationalScheduledPayments_rateType_ACTUAL_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["GetInternationalScheduledPayment", "CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun getInternationalScheduledPayments_rateType_INDICATIVE_v3_1_10() {
        getInternationalScheduledPayment.getInternationalScheduledPayments_rateType_INDICATIVE_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["GetInternationalScheduledPayment", "CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Test
    fun getInternationalScheduledPayments_v3_1_10_mandatoryFields() {
        getInternationalScheduledPayment.getInternationalScheduledPayments_mandatoryFields_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["GetInternationalScheduledPayment", "CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"]
    )
    @Disabled
    @Test
    fun shouldGetInternationalScheduledPayments_withReadRefund_v3_1_10() {
        getInternationalScheduledPayment.shouldGetInternationalScheduledPayments_withReadRefund_Test()
    }
}