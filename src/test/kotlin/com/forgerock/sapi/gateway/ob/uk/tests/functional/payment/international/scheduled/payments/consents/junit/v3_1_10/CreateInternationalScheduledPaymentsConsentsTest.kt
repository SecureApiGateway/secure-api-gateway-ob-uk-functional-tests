package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.scheduled.payments.consents.junit.v3_1_10

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.scheduled.payments.consents.api.v3_1_8.CreateInternationalScheduledPaymentsConsents
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CreateInternationalScheduledPaymentsConsentsTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var createInternationalScheduledPaymentsConsents: CreateInternationalScheduledPaymentsConsents

    @BeforeEach
    fun setUp() {
        createInternationalScheduledPaymentsConsents =
            CreateInternationalScheduledPaymentsConsents(OBVersion.v3_1_10, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun createInternationalScheduledPaymentsConsents_v3_1_10() {
        createInternationalScheduledPaymentsConsents.createInternationalScheduledPaymentsConsentsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun createDomesticPaymentsConsents_SameIdempotencyKeyMultipleRequestTest_v3_1_10(){
        createInternationalScheduledPaymentsConsents.createDomesticPaymentsConsents_SameIdempotencyKeyMultipleRequestTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun createDomesticPaymentsConsents_NoIdempotencyKey_throwsBadRequestTest_v3_1_10(){
        createInternationalScheduledPaymentsConsents.createDomesticPaymentsConsents_NoIdempotencyKey_throwsBadRequestTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun createInternationalScheduledPaymentsConsents_withDebtorAccount_v3_1_10() {
        createInternationalScheduledPaymentsConsents.createInternationalScheduledPaymentsConsents_withDebtorAccountTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun createInternationalScheduledPaymentsConsents_throwsInvalidDebtorAccount_v3_1_10() {
        createInternationalScheduledPaymentsConsents.createInternationalScheduledPaymentsConsents_throwsInvalidDebtorAccountTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun createInternationalScheduledPaymentsConsents_mandatoryFields_v3_1_10() {
        createInternationalScheduledPaymentsConsents.createInternationalScheduledPaymentsConsents_mandatoryFields_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPaymentConsents_throwsSendInvalidFormatDetachedJws_v3_1_10() {
        createInternationalScheduledPaymentsConsents.shouldCreateInternationalScheduledPaymentConsents_throwsSendInvalidFormatDetachedJws_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPaymentConsents_throwsNoDetachedJws_v3_1_10() {
        createInternationalScheduledPaymentsConsents.shouldCreateInternationalScheduledPaymentConsents_throwsNoDetachedJws_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPaymentConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJws_v3_1_10() {
        createInternationalScheduledPaymentsConsents.shouldCreateInternationalScheduledPaymentConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJws_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPaymentConsents_throwsSendInvalidKidDetachedJws_v3_1_10() {
        createInternationalScheduledPaymentsConsents.shouldCreateInternationalScheduledPaymentConsents_throwsSendInvalidKidDetachedJws_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPaymentConsents_throwsRequestExecutionTimeInThePast_v3_1_10() {
        createInternationalScheduledPaymentsConsents.shouldCreateInternationalScheduledPaymentConsents_throwsRequestExecutionTimeInThePast_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPaymentConsents_throwsRejectedConsent_v3_1_10() {
        createInternationalScheduledPaymentsConsents.shouldCreateInternationalScheduledPaymentConsents_throwsRejectedConsent_Test()
    }
}