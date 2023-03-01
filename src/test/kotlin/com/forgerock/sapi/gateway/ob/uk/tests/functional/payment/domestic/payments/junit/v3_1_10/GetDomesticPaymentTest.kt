package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.payments.junit.v3_1_10

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.payments.api.v3_1_8.GetDomesticPayment
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class GetDomesticPaymentTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getDomesticPaymentApi: GetDomesticPayment

    @BeforeEach
    fun setUp() {
        getDomesticPaymentApi = GetDomesticPayment(OBVersion.v3_1_10, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["GetDomesticPayment", "CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"]
    )
    @Test
    fun getDomesticPayments_v3_1_10() {
        getDomesticPaymentApi.getDomesticPaymentsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["GetDomesticPayment", "CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"]
    )
    @Test
    @Disabled("Issue to fix this test: https://github.com/SecureBankingAccessToolkit/SecureBankingAccessToolkit/issues/492")
    fun shouldGetDomesticPayments_withReadRefund_v3_1_10() {
        getDomesticPaymentApi.shouldGetDomesticPayments_withReadRefundTest()
    }
}