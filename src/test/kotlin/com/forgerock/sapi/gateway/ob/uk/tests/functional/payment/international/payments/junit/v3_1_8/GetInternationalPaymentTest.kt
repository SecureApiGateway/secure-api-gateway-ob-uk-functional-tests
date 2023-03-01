package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.payments.junit.v3_1_8

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.payments.api.v3_1_8.GetInternationalPayment
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class GetInternationalPaymentTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getInternationalPayment: GetInternationalPayment

    @BeforeEach
    fun setUp() {
        getInternationalPayment = GetInternationalPayment(OBVersion.v3_1_8, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["GetInternationalPayment", "CreateInternationalPayment", "CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payments", "international-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun getInternationalPayments_rateType_AGREED_v3_1_8() {
        getInternationalPayment.getInternationalPayments_rateType_AGREED_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["GetInternationalPayment", "CreateInternationalPayment", "CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payments", "international-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun getInternationalPayments_rateType_ACTUAL_v3_1_8() {
        getInternationalPayment.getInternationalPayments_rateType_ACTUAL_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["GetInternationalPayment", "CreateInternationalPayment", "CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payments", "international-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun getInternationalPayments_rateType_INDICATIVE_v3_1_8() {
        getInternationalPayment.getInternationalPayments_rateType_INDICATIVE_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["GetInternationalPayment", "CreateInternationalPayment", "CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payments", "international-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun getInternationalPayments_mandatoryFields_v3_1_8() {
        getInternationalPayment.getInternationalPayments_mandatoryFields_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["GetInternationalPayment", "CreateInternationalPayment", "CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payments", "international-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Disabled
    @Test
    fun shouldGetInternationalPayments_withReadRefund_v3_1_8() {
        getInternationalPayment.shouldGetInternationalPayments_withReadRefund_Test()
    }
}