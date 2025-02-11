package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.scheduled.payments.consents.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.scheduled.payments.consents.api.v4_0_0.GetInternationalScheduledPaymentsConsents
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetInternationalScheduledPaymentsConsentsTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getInternationalScheduledPaymentsConsentsTest: GetInternationalScheduledPaymentsConsents

    @BeforeEach
    fun setUp() {
        getInternationalScheduledPaymentsConsentsTest =
            GetInternationalScheduledPaymentsConsents(OBVersion.v4_0_0, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldGetInternationalScheduledPaymentsConsents_rateType_AGREED_v4_0_0() {
        getInternationalScheduledPaymentsConsentsTest.shouldGetInternationalScheduledPaymentsConsents_rateType_AGREED_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldGetInternationalScheduledPaymentsConsents_rateType_ACTUAL_v4_0_0() {
        getInternationalScheduledPaymentsConsentsTest.shouldGetInternationalScheduledPaymentsConsents_rateType_ACTUAL_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldGetInternationalScheduledPaymentsConsents_rateType_INDICATIVE_v4_0_0() {
        getInternationalScheduledPaymentsConsentsTest.shouldGetInternationalScheduledPaymentsConsents_rateType_INDICATIVE_Test()
    }
}