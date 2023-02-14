package com.forgerock.uk.openbanking.tests.functional.payment.domestic.payments.consents.junit.v3_1_8

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.payment.domestic.payments.consents.api.v3_1_8.CreateDomesticPaymentsConsents
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CreateDomesticPaymentConsentsTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var createDomesticPaymentsConsentsApi: CreateDomesticPaymentsConsents

    @BeforeEach
    fun setUp() {
        createDomesticPaymentsConsentsApi = CreateDomesticPaymentsConsents(OBVersion.v3_1_8, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun createDomesticPaymentsConsents_v3_1_8() {
        createDomesticPaymentsConsentsApi.createDomesticPaymentsConsentsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun createDomesticPaymentsConsents_withDebtorAccount_v3_1_8() {
        createDomesticPaymentsConsentsApi.createDomesticPaymentsConsents_withDebtorAccountTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun createDomesticPaymentsConsents_withNonExistentDebtorAccount_v3_1_8() {
        createDomesticPaymentsConsentsApi.createDomesticPaymentsConsents_throwsInvalidDebtorAccountTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun createDomesticPaymentsConsents_NoIdempotencyKey_throwsBadRequestTest_v3_1_8(){
        createDomesticPaymentsConsentsApi.createDomesticPaymentsConsents_NoIdempotencyKey_throwsBadRequestTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateDomesticPaymentsConsents_throwsNoDetachedJws_v3_1_8() {
        createDomesticPaymentsConsentsApi.shouldCreateDomesticPaymentsConsents_throwsNoDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateDomesticPaymentsConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJws_v3_1_8() {
        createDomesticPaymentsConsentsApi.shouldCreateDomesticPaymentsConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateDomesticPaymentsConsents_throwsSendInvalidFormatDetachedJws_v3_1_8() {
        createDomesticPaymentsConsentsApi.shouldCreateDomesticPaymentsConsents_throwsSendInvalidFormatDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateDomesticPaymentsConsents_throwsSendInvalidKidDetachedJws_v3_1_8() {
        createDomesticPaymentsConsentsApi.shouldCreateDomesticPaymentsConsents_throwsSendInvalidKidDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateDomesticPaymentsConsents_throwsRejectedConsent_v3_1_8() {
        createDomesticPaymentsConsentsApi.shouldCreateDomesticPaymentsConsents_throwsRejectedConsent_Test()
    }
}