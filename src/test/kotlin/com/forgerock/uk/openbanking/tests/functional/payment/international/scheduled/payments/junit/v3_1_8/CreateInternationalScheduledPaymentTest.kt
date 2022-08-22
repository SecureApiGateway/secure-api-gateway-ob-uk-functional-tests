package com.forgerock.uk.openbanking.tests.functional.payment.international.scheduled.payments.junit.v3_1_8

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.payment.international.scheduled.payments.api.v3_1_8.CreateInternationalScheduledPayment
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class CreateInternationalScheduledPaymentTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var createInternationalScheduledPayment: CreateInternationalScheduledPayment

    @BeforeEach
    fun setUp() {
        createInternationalScheduledPayment = CreateInternationalScheduledPayment(OBVersion.v3_1_8, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun createInternationalScheduledPayment_rateType_AGREED_v3_1_8() {
        createInternationalScheduledPayment.createInternationalScheduledPayment_rateType_AGREED_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun createInternationalScheduledPayment_rateType_ACTUAL_v3_1_8() {
        createInternationalScheduledPayment.createInternationalScheduledPayment_rateType_ACTUAL_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun createInternationalScheduledPayment_rateType_INDICATIVE_v3_1_8() {
        createInternationalScheduledPayment.createInternationalScheduledPayment_rateType_INDICATIVE_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun createInternationalScheduledPayment_mandatoryFields_v3_1_8() {
        createInternationalScheduledPayment.createInternationalScheduledPayment_mandatoryFields_Test()
    }

    @Disabled("Bug: https://github.com/SecureBankingAccessToolkit/SecureBankingAccessToolkit/issues/336")
    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsInternationalScheduledPaymentAlreadyExists_v3_1_8() {
        createInternationalScheduledPayment.shouldCreateInternationalScheduledPayment_throwsInternationalScheduledPaymentAlreadyExists_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsSendInvalidFormatDetachedJws_v3_1_8() {
        createInternationalScheduledPayment.shouldCreateInternationalScheduledPayment_throwsSendInvalidFormatDetachedJws_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsNoDetachedJws_v3_1_8() {
        createInternationalScheduledPayment.shouldCreateInternationalScheduledPayment_throwsNoDetachedJws_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsNotPermittedB64HeaderAddedInTheDetachedJws_v3_1_8() {
        createInternationalScheduledPayment.shouldCreateInternationalScheduledPayment_throwsNotPermittedB64HeaderAddedInTheDetachedJws_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsSendInvalidKidDetachedJws_v3_1_8() {
        createInternationalScheduledPayment.shouldCreateInternationalScheduledPayment_throwsSendInvalidKidDetachedJws_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBody_v3_1_8() {
        createInternationalScheduledPayment.shouldCreateInternationalScheduledPayment_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBody_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateInternationalScheduledPayment", "CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsent"],
        apis = ["international-scheduled-payments", "international-scheduled-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateInternationalScheduledPayment_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBody_v3_1_8() {
        createInternationalScheduledPayment.shouldCreateInternationalScheduledPayment_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBody_Test()
    }
}