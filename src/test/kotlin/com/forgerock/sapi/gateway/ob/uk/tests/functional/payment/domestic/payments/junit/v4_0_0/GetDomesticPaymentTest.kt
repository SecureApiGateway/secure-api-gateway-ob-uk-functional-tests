package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.payments.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.payments.api.v4_0_0.GetDomesticPayment
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetDomesticPaymentTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getDomesticPaymentApi: GetDomesticPayment

    @BeforeEach
    fun setUp() {
        getDomesticPaymentApi = GetDomesticPayment(OBVersion.v4_0_0, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["GetDomesticPayment", "CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"]
    )
    @Test
    fun getDomesticPayments_v4_0_0() {
        getDomesticPaymentApi.getDomesticPaymentsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["GetDomesticPayment", "CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"]
    )
    @Test
    fun shouldGetDomesticPayments_withReadRefund_v4_0_0() {
        getDomesticPaymentApi.shouldGetDomesticPayments_withReadRefundTest()
    }
}