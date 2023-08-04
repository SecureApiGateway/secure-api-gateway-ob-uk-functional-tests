package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.scheduled.payments.junit.v3_1_10

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
        createDomesticScheduledPayment = CreateDomesticScheduledPayment(OBVersion.v3_1_10, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"]
    )
    @Test
    fun createDomesticScheduledPayments_v3_1_10() {
        createDomesticScheduledPayment.createDomesticScheduledPaymentsTest()
    }

    @EnabledIfVersion(
            type = "payments",
            apiVersion = "v3.1.10",
            operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
            apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"]
    )
    @Test
    fun createDomesticScheduledPayments_throwsInvalidInitiation_v3_1_10() {
        createDomesticScheduledPayment.shouldCreateDomesticScheduledPayments_throwsInvalidInitiation()
    }


    @EnabledIfVersion(
            type = "payments",
            apiVersion = "v3.1.10",
            operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
            apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"]
    )
    @Test
    fun createDomesticScheduledPayments_withDebtorAccount_v3_1_10() {
        createDomesticScheduledPayment.createDomesticScheduledPaymentsWithDebtorAccountTest()
    }

    @Disabled("Bug: https://github.com/SecureBankingAccessToolkit/SecureBankingAccessToolkit/issues/336")
    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsPaymentAlreadyExists_v3_1_10() {
        createDomesticScheduledPayment.shouldCreateDomesticScheduledPayments_throwsPaymentAlreadyExists()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsSendInvalidFormatDetachedJws_v3_1_10() {
        createDomesticScheduledPayment.shouldCreateDomesticScheduledPayments_throwsSendInvalidFormatDetachedJws()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsNoDetachedJws_v3_1_10() {
        createDomesticScheduledPayment.shouldCreateDomesticScheduledPayments_throwsNoDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsNotPermittedB64HeaderAddedInTheDetachedJws_v3_1_10() {
        createDomesticScheduledPayment.shouldCreateDomesticScheduledPayments_throwsNotPermittedB64HeaderAddedInTheDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsSendInvalidKidDetachedJws_v3_1_10() {
        createDomesticScheduledPayment.shouldCreateDomesticScheduledPayments_throwsSendInvalidKidDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBody_v3_1_10() {
        createDomesticScheduledPayment.shouldCreateDomesticScheduledPayments_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBodyTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayments_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBody_v3_1_10() {
        createDomesticScheduledPayment.shouldCreateDomesticScheduledPayments_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBodyTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticPayments_throwsInvalidRisk_v3_1_10() {
        createDomesticScheduledPayment.shouldCreateDomesticScheduledPayments_throwsInvalidRiskTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payments", "domestic-payment-consents"]
    )
    @Test
    fun testCreatingPaymentIsIdempotent_v3_1_10() {
        createDomesticScheduledPayment.testCreatingPaymentIsIdempotent()
    }
}
