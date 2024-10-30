package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.scheduled.payments.consents.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.scheduled.payments.consents.api.v4_0_0.CreateInternationalScheduledPaymentsConsents
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class CreateInternationalScheduledPaymentsConsentsTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var createInternationalScheduledPaymentsConsents: CreateInternationalScheduledPaymentsConsents

    @BeforeEach
    fun setUp() {
        createInternationalScheduledPaymentsConsents =
            CreateInternationalScheduledPaymentsConsents(OBVersion.v4_0_0, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun createInternationalScheduledPaymentsConsents_v4_0_0() {
        createInternationalScheduledPaymentsConsents.createInternationalScheduledPaymentsConsentsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun createInternationalScheduledPaymentsConsents_SameIdempotencyKeyMultipleRequestTest_v4_0_0(){
        createInternationalScheduledPaymentsConsents.createInternationalScheduledPaymentsConsents_SameIdempotencyKeyMultipleRequestTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun createInternationalScheduledPaymentsConsents_NoIdempotencyKey_throwsBadRequestTest_v4_0_0(){
        createInternationalScheduledPaymentsConsents.createInternationalScheduledPaymentsConsents_NoIdempotencyKey_throwsBadRequestTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun createInternationalScheduledPaymentsConsents_withDebtorAccount_v4_0_0() {
        createInternationalScheduledPaymentsConsents.createInternationalScheduledPaymentsConsents_withDebtorAccountTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    @Disabled("This has not been implemented in the RS impl of the Consent API")
    fun createInternationalScheduledPaymentsConsents_throwsInvalidDebtorAccount_v4_0_0() {
        createInternationalScheduledPaymentsConsents.createInternationalScheduledPaymentsConsents_throwsInvalidDebtorAccountTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun createInternationalScheduledPaymentsConsents_mandatoryFields_v4_0_0() {
        createInternationalScheduledPaymentsConsents.createInternationalScheduledPaymentsConsents_mandatoryFields_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPaymentConsents_throwsSendInvalidFormatDetachedJws_v4_0_0() {
        createInternationalScheduledPaymentsConsents.shouldCreateInternationalScheduledPaymentConsents_throwsSendInvalidFormatDetachedJws_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPaymentConsents_throwsNoDetachedJws_v4_0_0() {
        createInternationalScheduledPaymentsConsents.shouldCreateInternationalScheduledPaymentConsents_throwsNoDetachedJws_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPaymentConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJws_v4_0_0() {
        createInternationalScheduledPaymentsConsents.shouldCreateInternationalScheduledPaymentConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJws_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPaymentConsents_throwsSendInvalidKidDetachedJws_v4_0_0() {
        createInternationalScheduledPaymentsConsents.shouldCreateInternationalScheduledPaymentConsents_throwsSendInvalidKidDetachedJws_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPaymentConsents_throwsRequestExecutionTimeInThePast_v4_0_0() {
        createInternationalScheduledPaymentsConsents.shouldCreateInternationalScheduledPaymentConsents_throwsRequestExecutionTimeInThePast_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalScheduledPaymentConsents_throwsRejectedConsent_v4_0_0() {
        createInternationalScheduledPaymentsConsents.shouldCreateInternationalScheduledPaymentConsents_throwsRejectedConsent_Test()
    }
}