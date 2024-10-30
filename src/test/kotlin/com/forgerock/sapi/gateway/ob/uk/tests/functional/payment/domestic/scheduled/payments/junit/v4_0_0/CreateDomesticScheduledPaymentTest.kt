package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.scheduled.payments.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.scheduled.payments.api.v4_0_0.CreateDomesticScheduledPayment
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class CreateDomesticScheduledPaymentTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var createDomesticScheduledPayment: CreateDomesticScheduledPayment

    @BeforeEach
    fun setUp() {
        createDomesticScheduledPayment = CreateDomesticScheduledPayment(OBVersion.v4_0_0, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"]
    )
    @Test
    fun createDomesticScheduledPayments_v4_0_0() {
        createDomesticScheduledPayment.createDomesticScheduledPaymentsTest()
    }

    @EnabledIfVersion(
            type = "payments",
            apiVersion = "v4.0.0",
            operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
            apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"]
    )
    @Test
    fun createDomesticScheduledPayments_throwsInvalidInitiation_v4_0_0() {
        createDomesticScheduledPayment.shouldCreateDomesticScheduledPayments_throwsInvalidInitiation()
    }


    @EnabledIfVersion(
            type = "payments",
            apiVersion = "v4.0.0",
            operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
            apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"]
    )
    @Test
    fun createDomesticScheduledPayments_withDebtorAccount_v4_0_0() {
        createDomesticScheduledPayment.createDomesticScheduledPaymentsWithDebtorAccountTest()
    }

    @Disabled("Bug: https://github.com/SecureBankingAccessToolkit/SecureBankingAccessToolkit/issues/336")
    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsPaymentAlreadyExists_v4_0_0() {
        createDomesticScheduledPayment.shouldCreateDomesticScheduledPayments_throwsPaymentAlreadyExists()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsSendInvalidFormatDetachedJws_v4_0_0() {
        createDomesticScheduledPayment.shouldCreateDomesticScheduledPayments_throwsSendInvalidFormatDetachedJws()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsNoDetachedJws_v4_0_0() {
        createDomesticScheduledPayment.shouldCreateDomesticScheduledPayments_throwsNoDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsNotPermittedB64HeaderAddedInTheDetachedJws_v4_0_0() {
        createDomesticScheduledPayment.shouldCreateDomesticScheduledPayments_throwsNotPermittedB64HeaderAddedInTheDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsSendInvalidKidDetachedJws_v4_0_0() {
        createDomesticScheduledPayment.shouldCreateDomesticScheduledPayments_throwsSendInvalidKidDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBody_v4_0_0() {
        createDomesticScheduledPayment.shouldCreateDomesticScheduledPayments_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBodyTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBody_v4_0_0() {
        createDomesticScheduledPayment.shouldCreateDomesticScheduledPayments_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBodyTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticPayments_throwsInvalidRisk_v4_0_0() {
        createDomesticScheduledPayment.shouldCreateDomesticScheduledPayments_throwsInvalidRiskTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"]
    )
    @Test
    fun testCreatingPaymentIsIdempotent_v4_0_0() {
        createDomesticScheduledPayment.testCreatingPaymentIsIdempotent()
    }
}
