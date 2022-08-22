package com.forgerock.uk.openbanking.tests.functional.payment.international.scheduled.payments.consents.junit.v3_1_9

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.payment.international.scheduled.payments.consents.api.v3_1_8.CreateInternationalScheduledPaymentsConsents
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CreateInternationalScheduledPaymentsConsentsTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var createInternationalScheduledPaymentsConsents: CreateInternationalScheduledPaymentsConsents

    @BeforeEach
    fun setUp() {
        createInternationalScheduledPaymentsConsents =
            CreateInternationalScheduledPaymentsConsents(OBVersion.v3_1_9, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun createInternationalScheduledPaymentsConsents_v3_1_9() {
        createInternationalScheduledPaymentsConsents.createInternationalScheduledPaymentsConsentsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun createInternationalScheduledPaymentsConsents_mandatoryFields_v3_1_9() {
        createInternationalScheduledPaymentsConsents.createInternationalScheduledPaymentsConsents_mandatoryFields_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPaymentConsents_throwsSendInvalidFormatDetachedJws_v3_1_9() {
        createInternationalScheduledPaymentsConsents.shouldCreateInternationalScheduledPaymentConsents_throwsSendInvalidFormatDetachedJws_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPaymentConsents_throwsNoDetachedJws_v3_1_9() {
        createInternationalScheduledPaymentsConsents.shouldCreateInternationalScheduledPaymentConsents_throwsNoDetachedJws_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPaymentConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJws_v3_1_9() {
        createInternationalScheduledPaymentsConsents.shouldCreateInternationalScheduledPaymentConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJws_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPaymentConsents_throwsSendInvalidKidDetachedJws_v3_1_9() {
        createInternationalScheduledPaymentsConsents.shouldCreateInternationalScheduledPaymentConsents_throwsSendInvalidKidDetachedJws_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPaymentConsents_throwsRequestExecutionTimeInThePast_v3_1_9() {
        createInternationalScheduledPaymentsConsents.shouldCreateInternationalScheduledPaymentConsents_throwsRequestExecutionTimeInThePast_Test()
    }
}