package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.scheduled.payments.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.scheduled.payments.api.v4_0_0.GetInternationalScheduledPaymentDetails
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetInternationalScheduledPaymentDetailsTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getInternationalScheduledPaymentDetails: GetInternationalScheduledPaymentDetails

    @BeforeEach
    fun setUp() {
        getInternationalScheduledPaymentDetails =
            GetInternationalScheduledPaymentDetails(OBVersion.v4_0_0, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["GetInternationalScheduledPayment", "CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentPaymentIdPaymentDetails"],
        apis = ["international-scheduled-payments", "international-scheduled-payments"]
    )
    @Test
    fun getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails_rateType_AGREED_v4_0_0() {
        getInternationalScheduledPaymentDetails.getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails_rateType_AGREED_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["GetInternationalScheduledPayment", "CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentPaymentIdPaymentDetails"],
        apis = ["international-scheduled-payments", "international-scheduled-payments"]
    )
    @Test
    fun getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails_rateType_ACTUAL_v4_0_0() {
        getInternationalScheduledPaymentDetails.getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails_rateType_ACTUAL_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["GetInternationalScheduledPayment", "CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentPaymentIdPaymentDetails"],
        apis = ["international-scheduled-payments", "international-scheduled-payments"]
    )
    @Test
    fun getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails_rateType_INDICATIVE_v4_0_0() {
        getInternationalScheduledPaymentDetails.getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails_rateType_INDICATIVE_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["GetInternationalScheduledPayment", "CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentPaymentIdPaymentDetails"],
        apis = ["international-scheduled-payments", "international-scheduled-payments"]
    )
    @Test
    fun getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails_mandatoryFields_v4_0_0() {
        getInternationalScheduledPaymentDetails.getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails_Test()
    }
}