package com.forgerock.uk.openbanking.tests.functional.payment.international.scheduled.payments.consents.junit.v3_1_9

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.payment.international.scheduled.payments.consents.api.v3_1_8.GetInternationalScheduledPaymentsConsents
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetInternationalScheduledPaymentsConsentsTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getInternationalScheduledPaymentsConsentsTest: GetInternationalScheduledPaymentsConsents

    @BeforeEach
    fun setUp() {
        getInternationalScheduledPaymentsConsentsTest =
            GetInternationalScheduledPaymentsConsents(OBVersion.v3_1_9, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldGetInternationalScheduledPaymentsConsents_rateType_AGREED_v3_1_9() {
        getInternationalScheduledPaymentsConsentsTest.shouldGetInternationalScheduledPaymentsConsents_rateType_AGREED_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldGetInternationalScheduledPaymentsConsents_rateType_ACTUAL_v3_1_9() {
        getInternationalScheduledPaymentsConsentsTest.shouldGetInternationalScheduledPaymentsConsents_rateType_ACTUAL_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldGetInternationalScheduledPaymentsConsents_rateType_INDICATIVE_v3_1_9() {
        getInternationalScheduledPaymentsConsentsTest.shouldGetInternationalScheduledPaymentsConsents_rateType_INDICATIVE_Test()
    }
}