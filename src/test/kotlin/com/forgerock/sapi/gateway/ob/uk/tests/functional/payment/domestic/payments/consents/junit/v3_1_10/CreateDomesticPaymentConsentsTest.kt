package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.payments.consents.junit.v3_1_10

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.payments.consents.api.v3_1_8.CreateDomesticPaymentsConsents
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CreateDomesticPaymentConsentsTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var createDomesticPaymentsConsentsApi: CreateDomesticPaymentsConsents

    @BeforeEach
    fun setUp() {
        createDomesticPaymentsConsentsApi = CreateDomesticPaymentsConsents(OBVersion.v3_1_10, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"]
    )
    @Test
    fun createDomesticPaymentsConsents_v3_1_10() {
        createDomesticPaymentsConsentsApi.createDomesticPaymentsConsentsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"]
    )
    @Test
    fun createDomesticPaymentsConsents_SameIdempotencyKeyMultipleRequestTest_v3_1_10(){
        createDomesticPaymentsConsentsApi.createDomesticPaymentsConsents_SameIdempotencyKeyMultipleRequestTest();
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"]
    )
    @Test
    fun createDomesticPaymentsConsents_NoIdempotencyKey_throwsBadRequestTest_v3_1_10(){
        createDomesticPaymentsConsentsApi.createDomesticPaymentsConsents_NoIdempotencyKey_throwsBadRequestTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"]
    )
    @Test
    fun createDomesticPaymentsConsents_withDebtorAccount_v3_1_10() {
        createDomesticPaymentsConsentsApi.createDomesticPaymentsConsents_withDebtorAccountTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"]
    )
    @Test
    fun createDomesticPaymentsConsents_withNonExistentDebtorAccount_v3_1_10() {
        createDomesticPaymentsConsentsApi.createDomesticPaymentsConsents_throwsInvalidDebtorAccountTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticPaymentsConsents_throwsNoDetachedJws_v3_1_10() {
        createDomesticPaymentsConsentsApi.shouldCreateDomesticPaymentsConsents_throwsNoDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticPaymentsConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJws_v3_1_10() {
        createDomesticPaymentsConsentsApi.shouldCreateDomesticPaymentsConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticPaymentsConsents_throwsSendInvalidFormatDetachedJws_v3_1_10() {
        createDomesticPaymentsConsentsApi.shouldCreateDomesticPaymentsConsents_throwsSendInvalidFormatDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticPaymentsConsents_throwsSendInvalidKidDetachedJws_v3_1_10() {
        createDomesticPaymentsConsentsApi.shouldCreateDomesticPaymentsConsents_throwsSendInvalidKidDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticPaymentsConsents_throwsRejectedConsent_v3_1_10() {
        createDomesticPaymentsConsentsApi.shouldCreateDomesticPaymentsConsents_throwsRejectedConsent_Test()
    }
}