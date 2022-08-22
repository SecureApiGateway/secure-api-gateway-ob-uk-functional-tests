package com.forgerock.uk.openbanking.tests.functional.payment.domestic.scheduled.payments.junit.v3_1_9

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.payment.domestic.scheduled.payments.api.v3_1_8.GetDomesticScheduledPayment
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class GetDomesticScheduledPaymentTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getDomesticScheduledPayment: GetDomesticScheduledPayment

    @BeforeEach
    fun setUp() {
        getDomesticScheduledPayment = GetDomesticScheduledPayment(OBVersion.v3_1_9, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["GetDomesticScheduledPayment", "CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"]
    )
    @Test
    fun getDomesticScheduledPayments_v3_1_9() {
        getDomesticScheduledPayment.getDomesticScheduledPaymentsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["GetDomesticScheduledPayment", "CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"]
    )
    @Disabled
    @Test
    fun shouldGetDomesticScheduledPayments_withReadRefund_v3_1_9() {
        getDomesticScheduledPayment.shouldGetDomesticScheduledPayments_withReadRefundTest()
    }
}