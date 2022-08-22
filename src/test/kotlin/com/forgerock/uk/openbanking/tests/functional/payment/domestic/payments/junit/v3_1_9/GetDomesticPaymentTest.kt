package com.forgerock.uk.openbanking.tests.functional.payment.domestic.payments.junit.v3_1_9

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.payment.domestic.payments.api.v3_1_8.GetDomesticPayment
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class GetDomesticPaymentTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getDomesticPaymentApi: GetDomesticPayment

    @BeforeEach
    fun setUp() {
        getDomesticPaymentApi = GetDomesticPayment(OBVersion.v3_1_9, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["GetDomesticPayment", "CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun getDomesticPayments_v3_1_9() {
        getDomesticPaymentApi.getDomesticPaymentsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["GetDomesticPayment", "CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    @Disabled("Issue to fix this test: https://github.com/SecureBankingAccessToolkit/SecureBankingAccessToolkit/issues/492")
    fun shouldGetDomesticPayments_withReadRefund_v3_1_9() {
        getDomesticPaymentApi.shouldGetDomesticPayments_withReadRefundTest()
    }
}