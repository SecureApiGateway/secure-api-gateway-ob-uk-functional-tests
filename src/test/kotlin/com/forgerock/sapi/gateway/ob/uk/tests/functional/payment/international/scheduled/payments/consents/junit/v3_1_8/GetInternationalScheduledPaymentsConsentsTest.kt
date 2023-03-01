package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.scheduled.payments.consents.junit.v3_1_8

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.scheduled.payments.consents.api.v3_1_8.GetInternationalScheduledPaymentsConsents
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetInternationalScheduledPaymentsConsentsTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getInternationalScheduledPaymentsConsentsTest: GetInternationalScheduledPaymentsConsents

    @BeforeEach
    fun setUp() {
        getInternationalScheduledPaymentsConsentsTest =
            GetInternationalScheduledPaymentsConsents(OBVersion.v3_1_8, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldGetInternationalScheduledPaymentsConsents_rateType_AGREED_v3_1_8() {
        getInternationalScheduledPaymentsConsentsTest.shouldGetInternationalScheduledPaymentsConsents_rateType_AGREED_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldGetInternationalScheduledPaymentsConsents_rateType_ACTUAL_v3_1_8() {
        getInternationalScheduledPaymentsConsentsTest.shouldGetInternationalScheduledPaymentsConsents_rateType_ACTUAL_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldGetInternationalScheduledPaymentsConsents_rateType_INDICATIVE_v3_1_8() {
        getInternationalScheduledPaymentsConsentsTest.shouldGetInternationalScheduledPaymentsConsents_rateType_INDICATIVE_Test()
    }
}