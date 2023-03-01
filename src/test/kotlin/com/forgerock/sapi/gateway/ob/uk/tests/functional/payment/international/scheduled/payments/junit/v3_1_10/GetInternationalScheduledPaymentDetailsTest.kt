package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.scheduled.payments.junit.v3_1_10

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.scheduled.payments.api.v3_1_8.GetInternationalScheduledPaymentDetails
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetInternationalScheduledPaymentDetailsTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getInternationalScheduledPaymentDetails: GetInternationalScheduledPaymentDetails

    @BeforeEach
    fun setUp() {
        getInternationalScheduledPaymentDetails =
            GetInternationalScheduledPaymentDetails(OBVersion.v3_1_10, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["GetInternationalScheduledPayment", "CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails_rateType_AGREED_v3_1_10() {
        getInternationalScheduledPaymentDetails.getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails_rateType_AGREED_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["GetInternationalScheduledPayment", "CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails_rateType_ACTUAL_v3_1_10() {
        getInternationalScheduledPaymentDetails.getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails_rateType_ACTUAL_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["GetInternationalScheduledPayment", "CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails_rateType_INDICATIVE_v3_1_10() {
        getInternationalScheduledPaymentDetails.getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails_rateType_INDICATIVE_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["GetInternationalScheduledPayment", "CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails"],
        apis = ["international-payments", "international-payment-consents"]
    )
    @Test
    fun getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails_mandatoryFields_v3_1_10() {
        getInternationalScheduledPaymentDetails.getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails_Test()
    }
}