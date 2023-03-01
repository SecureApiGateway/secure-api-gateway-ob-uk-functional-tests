package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.payments.junit.v3_1_10

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.payments.api.v3_1_8.GetInternationalPaymentDetails
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetInternationalPaymentDetailsTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getInternationalPaymentInternationalPaymentIdPaymentDetails: GetInternationalPaymentDetails

    @BeforeEach
    fun setUp() {
        getInternationalPaymentInternationalPaymentIdPaymentDetails =
            GetInternationalPaymentDetails(OBVersion.v3_1_10, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["GetInternationalPayment", "CreateInternationalPayment", "CreateInternationalPaymentConsent", "GetInternationalPaymentConsent", "GetInternationalPaymentInternationalPaymentIdPaymentDetails"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun getInternationalPaymentInternationalPaymentIdPaymentDetails_rateType_AGREED_v3_1_10() {
        getInternationalPaymentInternationalPaymentIdPaymentDetails.getInternationalPaymentInternationalPaymentIdPaymentDetails_rateType_AGREED_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["GetInternationalPayment", "CreateInternationalPayment", "CreateInternationalPaymentConsent", "GetInternationalPaymentConsent", "GetInternationalPaymentInternationalPaymentIdPaymentDetails"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun getInternationalPaymentInternationalPaymentIdPaymentDetails_rateType_ACTUAL_v3_1_10() {
        getInternationalPaymentInternationalPaymentIdPaymentDetails.getInternationalPaymentInternationalPaymentIdPaymentDetails_rateType_ACTUAL_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["GetInternationalPayment", "CreateInternationalPayment", "CreateInternationalPaymentConsent", "GetInternationalPaymentConsent", "GetInternationalPaymentInternationalPaymentIdPaymentDetails"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun getInternationalPaymentInternationalPaymentIdPaymentDetails_rateType_INDICATIVE_v3_1_10() {
        getInternationalPaymentInternationalPaymentIdPaymentDetails.getInternationalPaymentInternationalPaymentIdPaymentDetails_rateType_INDICATIVE_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["GetInternationalPayment", "CreateInternationalPayment", "CreateInternationalPaymentConsent", "GetInternationalPaymentConsent", "GetInternationalPaymentInternationalPaymentIdPaymentDetails"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun getInternationalPaymentInternationalPaymentIdPaymentDetails_mandatoryFields_v3_1_10() {
        getInternationalPaymentInternationalPaymentIdPaymentDetails.getInternationalPaymentInternationalPaymentIdPaymentDetails_mandatoryFields_Test()
    }
}