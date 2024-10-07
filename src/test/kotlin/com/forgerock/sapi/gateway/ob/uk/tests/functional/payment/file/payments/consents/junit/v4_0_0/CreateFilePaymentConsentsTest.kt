package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.file.payments.consents.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.file.payments.consents.api.v4_0_0.CreateFilePaymentsConsents
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CreateFilePaymentConsentsTest(val tppResource: CreateTppCallback.TppResource) {

    private lateinit var createFilePaymentsConsentsApi: CreateFilePaymentsConsents

    @BeforeEach
    fun setUp() {
        createFilePaymentsConsentsApi = CreateFilePaymentsConsents(OBVersion.v4_0_0, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateFilePaymentConsent"],
        apis = ["file-payment-consents"]
    )
    @Test
    fun submitXMLFile_v4_0_0() {
        createFilePaymentsConsentsApi.submitXMLFileTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateFilePaymentConsent"],
        apis = ["file-payment-consents"]
    )
    @Test
    fun createFilePaymentsConsents_SameIdempotencyKeyMultipleRequestTest_v4_0_0() {
        createFilePaymentsConsentsApi.createFilePaymentsConsents_SameIdempotencyKeyMultipleRequestTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateFilePaymentConsent"],
        apis = ["file-payment-consents"]
    )
    @Test
    fun createFilePaymentConsents_NoIdempotencyKey_throwsBadRequestTest_v4_0_0() {
        createFilePaymentsConsentsApi.createFilePaymentConsents_NoIdempotencyKey_throwsBadRequestTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateFilePaymentConsent"],
        apis = ["file-payment-consents"]
    )
    @Test
    fun submitFile_SameIdempotencyKeyMultipleRequestTest_v4_0_0() {
        createFilePaymentsConsentsApi.submitFile_SameIdempotencyKeyMultipleRequestTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateFilePaymentConsent"],
        apis = ["file-payment-consents"]
    )
    @Test
    fun submitFile_NoIdempotencyKey_throwsBadRequestTest_v4_0_0() {
        createFilePaymentsConsentsApi.submitFile_NoIdempotencyKey_throwsBadRequestTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateFilePaymentConsent"],
        apis = ["file-payment-consents"]
    )
    @Test
    fun submitJSONFile_v4_0_0() {
        createFilePaymentsConsentsApi.submitJSONFileTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateFilePaymentConsent"],
        apis = ["file-payment-consents"]
    )
    @Test
    fun createFilePaymentsConsents_v4_0_0() {
        createFilePaymentsConsentsApi.createFilePaymentsConsentsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateFilePaymentConsent"],
        apis = ["file-payment-consents"]
    )
    @Test
    fun createFilePaymentsConsents_WithDebtorAccount_v4_0_0() {
        createFilePaymentsConsentsApi.createFilePaymentsConsentsWithDebtorAccountTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateFilePaymentConsent"],
        apis = ["file-payment-consents"]
    )
    @Test
    fun createFilePaymentsConsentsMandatoryFields_v4_0_0() {
        createFilePaymentsConsentsApi.createFilePaymentsConsents_mandatoryFields()
    }


    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateFilePaymentConsent"],
        apis = ["file-payment-consents"]
    )
    @Test
    fun shouldCreateFilePaymentsConsents_throwsNoDetachedJws_v4_0_0() {
        createFilePaymentsConsentsApi.shouldCreateFilePaymentsConsents_throwsNoDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateFilePaymentConsent"],
        apis = ["file-payment-consents"]
    )
    @Test
    fun shouldCreateFilePaymentsConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJws_v4_0_0() {
        createFilePaymentsConsentsApi.shouldCreateFilePaymentsConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateFilePaymentConsent"],
        apis = ["file-payment-consents"]
    )
    @Test
    fun shouldCreateFilePaymentsConsents_throwsSendInvalidFormatDetachedJws_v4_0_0() {
        createFilePaymentsConsentsApi.shouldCreateFilePaymentsConsents_throwsSendInvalidFormatDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateFilePaymentConsent"],
        apis = ["file-payment-consents"]
    )
    @Test
    fun shouldCreateFilePaymentsConsents_throwsSendInvalidKidDetachedJws_v4_0_0() {
        createFilePaymentsConsentsApi.shouldCreateFilePaymentsConsents_throwsSendInvalidKidDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateFilePaymentConsent"],
        apis = ["file-payment-consents"]
    )
    @Test
    fun shouldCreateFilePaymentsConsents_throwsRejectedConsent_v4_0_0() {
        createFilePaymentsConsentsApi.shouldCreateFilePaymentsConsents_throwsRejectedConsentTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateFilePaymentConsent"],
        apis = ["file-payment-consents"]
    )
    @Test
    fun failToCreateConsentForUnsupportedFileType_v4_0_0() {
        createFilePaymentsConsentsApi.failToCreateConsentForUnsupportedFileType()
    }
}