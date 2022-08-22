package com.forgerock.uk.openbanking.tests.functional.payment.international.payments.consents.junit.v3_1_8

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.payment.international.payments.consents.api.v3_1_8.GetInternationalPaymentsConsents
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetInternationalPaymentsConsentsTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getInternationalPaymentsConsents: GetInternationalPaymentsConsents

    @BeforeEach
    fun setUp() {
        getInternationalPaymentsConsents = GetInternationalPaymentsConsents(OBVersion.v3_1_8, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldGetInternationalPaymentsConsents_rateType_AGREED_v3_1_8() {
        getInternationalPaymentsConsents.shouldGetInternationalPaymentsConsents_rateType_AGREED_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldGetInternationalPaymentsConsents_rateType_ACTUAL_v3_1_8() {
        getInternationalPaymentsConsents.shouldGetInternationalPaymentsConsents_rateType_ACTUAL_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldGetInternationalPaymentsConsents_rateType_INDICATIVE_v3_1_8() {
        getInternationalPaymentsConsents.shouldGetInternationalPaymentsConsents_rateType_INDICATIVE_Test()
    }
}