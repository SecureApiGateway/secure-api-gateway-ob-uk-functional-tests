package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.scheduled.payments.junit.v3_1_9

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.scheduled.payments.api.v3_1_8.CreateDomesticScheduledPayment
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class CreateDomesticScheduledPaymentTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var createDomesticScheduledPayment: CreateDomesticScheduledPayment

    @BeforeEach
    fun setUp() {
        createDomesticScheduledPayment = CreateDomesticScheduledPayment(OBVersion.v3_1_9, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"]
    )
    @Test
    fun createDomesticScheduledPayments_v3_1_9() {
        createDomesticScheduledPayment.createDomesticScheduledPaymentsTest()
    }

    @Disabled("Bug: https://github.com/SecureBankingAccessToolkit/SecureBankingAccessToolkit/issues/336")
    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsPaymentAlreadyExists_v3_1_9() {
        createDomesticScheduledPayment.shouldCreateDomesticScheduledPayments_throwsPaymentAlreadyExists()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsSendInvalidFormatDetachedJws_v3_1_9() {
        createDomesticScheduledPayment.shouldCreateDomesticScheduledPayments_throwsSendInvalidFormatDetachedJws()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsNoDetachedJws_v3_1_9() {
        createDomesticScheduledPayment.shouldCreateDomesticScheduledPayments_throwsNoDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsNotPermittedB64HeaderAddedInTheDetachedJws_v3_1_9() {
        createDomesticScheduledPayment.shouldCreateDomesticScheduledPayments_throwsNotPermittedB64HeaderAddedInTheDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsSendInvalidKidDetachedJws_v3_1_9() {
        createDomesticScheduledPayment.shouldCreateDomesticScheduledPayments_throwsSendInvalidKidDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBody_v3_1_9() {
        createDomesticScheduledPayment.shouldCreateDomesticScheduledPayments_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBodyTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBody_v3_1_9() {
        createDomesticScheduledPayment.shouldCreateDomesticScheduledPayments_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBodyTest()
    }
}