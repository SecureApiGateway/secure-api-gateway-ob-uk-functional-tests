package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.payments.consents.junit.v3_1_9

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.payments.consents.api.v3_1_8.GetInternationalPaymentsConsents
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetInternationalPaymentsConsentsTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getInternationalPaymentsConsents: GetInternationalPaymentsConsents

    @BeforeEach
    fun setUp() {
        getInternationalPaymentsConsents = GetInternationalPaymentsConsents(OBVersion.v3_1_9, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldGetInternationalPaymentsConsents_rateType_AGREED_v3_1_9() {
        getInternationalPaymentsConsents.shouldGetInternationalPaymentsConsents_rateType_AGREED_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldGetInternationalPaymentsConsents_rateType_ACTUAL_v3_1_9() {
        getInternationalPaymentsConsents.shouldGetInternationalPaymentsConsents_rateType_ACTUAL_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalPaymentConsent"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldGetInternationalPaymentsConsents_rateType_INDICATIVE_v3_1_9() {
        getInternationalPaymentsConsents.shouldGetInternationalPaymentsConsents_rateType_INDICATIVE_Test()
    }
}