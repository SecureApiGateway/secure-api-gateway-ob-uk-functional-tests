package com.forgerock.uk.openbanking.tests.functional.payment.international.payments.junit.v3_1_9

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.payment.international.payments.api.v3_1_8.GetInternationalPaymentInternationalPaymentIdPaymentDetails
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetInternationalPaymentInternationalPaymentIdPaymentDetailsTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getInternationalPaymentInternationalPaymentIdPaymentDetails: GetInternationalPaymentInternationalPaymentIdPaymentDetails

    @BeforeEach
    fun setUp() {
        getInternationalPaymentInternationalPaymentIdPaymentDetails =
            GetInternationalPaymentInternationalPaymentIdPaymentDetails(OBVersion.v3_1_9, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["GetInternationalPayment", "CreateInternationalPayment", "CreateInternationalPaymentConsent", "GetInternationalPaymentConsent", "GetInternationalPaymentInternationalPaymentIdPaymentDetails"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun getInternationalPaymentInternationalPaymentIdPaymentDetails_rateType_AGREED_v3_1_9() {
        getInternationalPaymentInternationalPaymentIdPaymentDetails.getInternationalPaymentInternationalPaymentIdPaymentDetails_rateType_AGREED_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["GetInternationalPayment", "CreateInternationalPayment", "CreateInternationalPaymentConsent", "GetInternationalPaymentConsent", "GetInternationalPaymentInternationalPaymentIdPaymentDetails"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun getInternationalPaymentInternationalPaymentIdPaymentDetails_rateType_ACTUAL_v3_1_9() {
        getInternationalPaymentInternationalPaymentIdPaymentDetails.getInternationalPaymentInternationalPaymentIdPaymentDetails_rateType_ACTUAL_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["GetInternationalPayment", "CreateInternationalPayment", "CreateInternationalPaymentConsent", "GetInternationalPaymentConsent", "GetInternationalPaymentInternationalPaymentIdPaymentDetails"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun getInternationalPaymentInternationalPaymentIdPaymentDetails_rateType_INDICATIVE_v3_1_9() {
        getInternationalPaymentInternationalPaymentIdPaymentDetails.getInternationalPaymentInternationalPaymentIdPaymentDetails_rateType_INDICATIVE_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["GetInternationalPayment", "CreateInternationalPayment", "CreateInternationalPaymentConsent", "GetInternationalPaymentConsent", "GetInternationalPaymentInternationalPaymentIdPaymentDetails"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun getInternationalPaymentInternationalPaymentIdPaymentDetails_v3_1_9_mandatoryFields() {
        getInternationalPaymentInternationalPaymentIdPaymentDetails.getInternationalPaymentInternationalPaymentIdPaymentDetails_mandatoryFields_Test()
    }
}