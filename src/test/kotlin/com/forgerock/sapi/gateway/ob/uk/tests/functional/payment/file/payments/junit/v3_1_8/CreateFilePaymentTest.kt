package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.file.payments.junit.v3_1_8

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.file.payments.api.v3_1_8.CreateFilePayment
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class CreateFilePaymentTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var createFilePayment: CreateFilePayment

    @BeforeEach
    fun setUp() {
        createFilePayment = CreateFilePayment(OBVersion.v3_1_8, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateFilePayment", "CreateFilePaymentConsent", "GetFilePaymentConsent"],
        apis = ["file-payments", "file-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun createFilePayment_v3_1_8() {
        createFilePayment.createFilePaymentTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateFilePayment", "CreateFilePaymentConsent", "GetFilePaymentConsent"],
        apis = ["file-payments", "file-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun createFilePayment_mandatoryFields_v3_1_8() {
        createFilePayment.createFilePayment_mandatoryFieldsTest()
    }

    @Disabled("Bug: https://github.com/SecureBankingAccessToolkit/SecureBankingAccessToolkit/issues/336")
    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateFilePayment", "CreateFilePaymentConsent", "GetFilePaymentConsent"],
        apis = ["file-payments", "file-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateFilePayment_throwsFilePaymentAlreadyExists_v3_1_8() {
        createFilePayment.shouldCreateFilePayment_throwsFilePaymentAlreadyExistsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateFilePayment", "CreateFilePaymentConsent", "GetFilePaymentConsent"],
        apis = ["file-payments", "file-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateFilePayment_throwsSendInvalidFormatDetachedJws_v3_1_8() {
        createFilePayment.shouldCreateFilePayment_throwsSendInvalidFormatDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateFilePayment", "CreateFilePaymentConsent", "GetFilePaymentConsent"],
        apis = ["file-payments", "file-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateFilePayment_throwsNoDetachedJws_v3_1_8() {
        createFilePayment.shouldCreateFilePayment_throwsNoDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateFilePayment", "CreateFilePaymentConsent", "GetFilePaymentConsent"],
        apis = ["file-payments", "file-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateFilePayment_throwsNotPermittedB64HeaderAddedInTheDetachedJws_v3_1_8() {
        createFilePayment.shouldCreateFilePayment_throwsNotPermittedB64HeaderAddedInTheDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateFilePayment", "CreateFilePaymentConsent", "GetFilePaymentConsent"],
        apis = ["file-payments", "file-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateFilePayment_throwsSendInvalidKidDetachedJws_v3_1_8() {
        createFilePayment.shouldCreateFilePayment_throwsSendInvalidKidDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateFilePayment", "CreateFilePaymentConsent", "GetFilePaymentConsent"],
        apis = ["file-payments", "file-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateFilePayment_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBody_v3_1_8() {
        createFilePayment.shouldCreateFilePayment_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBodyTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateFilePayment", "CreateFilePaymentConsent", "GetFilePaymentConsent"],
        apis = ["file-payments", "file-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateFilePayment_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBody_v3_1_8() {
        createFilePayment.shouldCreateFilePayment_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBodyTest()
    }
}