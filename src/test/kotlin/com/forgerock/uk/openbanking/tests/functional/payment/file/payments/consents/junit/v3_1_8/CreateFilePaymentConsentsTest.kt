package com.forgerock.uk.openbanking.tests.functional.payment.file.payments.consents.junit.v3_1_8

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.payment.file.payments.api.v3_1_8.CreateFilePaymentsConsents
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CreateFilePaymentConsentsTest(val tppResource: CreateTppCallback.TppResource) {

    private lateinit var createFilePaymentsConsentsApi: CreateFilePaymentsConsents

    @BeforeEach
    fun setUp() {
        createFilePaymentsConsentsApi = CreateFilePaymentsConsents(OBVersion.v3_1_8, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateFilePaymentConsent"],
        apis = ["file-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun submitXMLFile_v3_1_8() {
        createFilePaymentsConsentsApi.submitXMLFileTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateFilePaymentConsent"],
        apis = ["file-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun submitJSONFile_v3_1_8() {
        createFilePaymentsConsentsApi.submitJSONFileTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateFilePaymentConsent"],
        apis = ["file-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun createFilePaymentsConsents_v3_1_8() {
        createFilePaymentsConsentsApi.createFilePaymentsConsentsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateFilePaymentConsent"],
        apis = ["file-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun createFilePaymentsConsentsMandatoryFields_v3_1_8() {
        createFilePaymentsConsentsApi.createFilePaymentsConsents_mandatoryFields()
    }


    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateFilePaymentConsent"],
        apis = ["file-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateFilePaymentsConsents_throwsNoDetachedJws_v3_1_8() {
        createFilePaymentsConsentsApi.shouldCreateFilePaymentsConsents_throwsNoDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateFilePaymentConsent"],
        apis = ["file-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateFilePaymentsConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJws_v3_1_8() {
        createFilePaymentsConsentsApi.shouldCreateFilePaymentsConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateFilePaymentConsent"],
        apis = ["file-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateFilePaymentsConsents_throwsSendInvalidFormatDetachedJws_v3_1_8() {
        createFilePaymentsConsentsApi.shouldCreateFilePaymentsConsents_throwsSendInvalidFormatDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateFilePaymentConsent"],
        apis = ["file-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateFilePaymentsConsents_throwsSendInvalidKidDetachedJws_v3_1_8() {
        createFilePaymentsConsentsApi.shouldCreateFilePaymentsConsents_throwsSendInvalidKidDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateFilePaymentConsent"],
        apis = ["file-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateFilePaymentsConsents_throwsRejectedConsent_v3_1_8() {
        createFilePaymentsConsentsApi.shouldCreateFilePaymentsConsents_throwsRejectedConsentTest()
    }
}