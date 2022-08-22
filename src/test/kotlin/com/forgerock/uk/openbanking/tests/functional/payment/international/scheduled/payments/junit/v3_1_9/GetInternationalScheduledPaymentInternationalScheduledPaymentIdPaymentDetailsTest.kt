package com.forgerock.uk.openbanking.tests.functional.payment.international.scheduled.payments.junit.v3_1_9

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.payment.international.scheduled.payments.api.v3_1_8.GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetailsTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails: GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails

    @BeforeEach
    fun setUp() {
        getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails =
            GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails(OBVersion.v3_1_9, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["GetInternationalScheduledPayment", "CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails"],
        apis = ["international-payments", "international-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails_rateType_AGREED_v3_1_9() {
        getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails.getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails_rateType_AGREED_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["GetInternationalScheduledPayment", "CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails"],
        apis = ["international-payments", "international-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails_rateType_ACTUAL_v3_1_9() {
        getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails.getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails_rateType_ACTUAL_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["GetInternationalScheduledPayment", "CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails"],
        apis = ["international-payments", "international-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails_rateType_INDICATIVE_v3_1_9() {
        getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails.getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails_rateType_INDICATIVE_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["GetInternationalScheduledPayment", "CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails"],
        apis = ["international-payments", "international-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails_v3_1_9_mandatoryFields() {
        getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails.getInternationalScheduledPaymentInternationalScheduledPaymentIdPaymentDetails_Test()
    }
}