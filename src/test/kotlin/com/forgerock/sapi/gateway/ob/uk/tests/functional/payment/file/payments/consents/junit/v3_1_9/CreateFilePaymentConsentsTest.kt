package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.file.payments.consents.junit.v3_1_9

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.file.payments.consents.api.v3_1_8.CreateFilePaymentsConsents
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CreateFilePaymentConsentsTest(val tppResource: CreateTppCallback.TppResource) {

    private lateinit var createFilePaymentsConsentsApi: CreateFilePaymentsConsents

    @BeforeEach
    fun setUp() {
        createFilePaymentsConsentsApi = CreateFilePaymentsConsents(OBVersion.v3_1_9, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateFilePaymentConsent"],
        apis = ["file-payment-consents"]
    )
    @Test
    fun submitXMLFile_v3_1_9() {
        createFilePaymentsConsentsApi.submitXMLFileTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateFilePaymentConsent"],
        apis = ["file-payment-consents"]
    )
    @Test
    fun createDomesticPaymentsConsents_SameIdempotencyKeyMultipleRequestTest_v3_1_9(){
        createFilePaymentsConsentsApi.createFilePaymentsConsents_SameIdempotencyKeyMultipleRequestTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateFilePaymentConsent"],
        apis = ["file-payment-consents"]
    )
    @Test
    fun submitFile_SameIdempotencyKeyMultipleRequestTest_v3_1_9(){
        createFilePaymentsConsentsApi.submitFile_SameIdempotencyKeyMultipleRequestTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateFilePaymentConsent"],
        apis = ["file-payment-consents"]
    )
    @Test
    fun submitFile_NoIdempotencyKey_throwsBadRequestTest_v3_1_9(){
        createFilePaymentsConsentsApi.submitFile_NoIdempotencyKey_throwsBadRequestTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateFilePaymentConsent"],
        apis = ["file-payment-consents"]
    )
    @Test
    fun createDomesticVrpConsents_NoIdempotencyKey_throwsBadRequestTest_v3_1_9() {
        createFilePaymentsConsentsApi.createFilePaymentConsents_NoIdempotencyKey_throwsBadRequestTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateFilePaymentConsent"],
        apis = ["file-payment-consents"]
    )
    @Test
    fun submitJSONFile_v3_1_9() {
        createFilePaymentsConsentsApi.submitJSONFileTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateFilePaymentConsent"],
        apis = ["file-payment-consents"]
    )
    @Test
    fun createFilePaymentsConsents_v3_1_9() {
        createFilePaymentsConsentsApi.createFilePaymentsConsentsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateFilePaymentConsent"],
        apis = ["file-payment-consents"]
    )
    @Test
    fun createFilePaymentsConsentsMandatoryFields_v3_1_9() {
        createFilePaymentsConsentsApi.createFilePaymentsConsents_mandatoryFields()
    }


    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateFilePaymentConsent"],
        apis = ["file-payment-consents"]
    )
    @Test
    fun shouldCreateFilePaymentsConsents_throwsNoDetachedJws_v3_1_9() {
        createFilePaymentsConsentsApi.shouldCreateFilePaymentsConsents_throwsNoDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateFilePaymentConsent"],
        apis = ["file-payment-consents"]
    )
    @Test
    fun shouldCreateFilePaymentsConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJws_v3_1_9() {
        createFilePaymentsConsentsApi.shouldCreateFilePaymentsConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateFilePaymentConsent"],
        apis = ["file-payment-consents"]
    )
    @Test
    fun shouldCreateFilePaymentsConsents_throwsSendInvalidFormatDetachedJws_v3_1_9() {
        createFilePaymentsConsentsApi.shouldCreateFilePaymentsConsents_throwsSendInvalidFormatDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateFilePaymentConsent"],
        apis = ["file-payment-consents"]
    )
    @Test
    fun shouldCreateFilePaymentsConsents_throwsSendInvalidKidDetachedJws_v3_1_9() {
        createFilePaymentsConsentsApi.shouldCreateFilePaymentsConsents_throwsSendInvalidKidDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateFilePaymentConsent"],
        apis = ["file-payment-consents"]
    )
    @Test
    fun shouldCreateFilePaymentsConsents_throwsRejectedConsent_v3_1_9() {
        createFilePaymentsConsentsApi.shouldCreateFilePaymentsConsents_throwsRejectedConsentTest()
    }
}