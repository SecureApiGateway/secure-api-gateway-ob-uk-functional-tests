package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.payments.consents.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.payments.consents.api.v4_0_0.CreateDomesticPaymentsConsents
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class CreateDomesticPaymentConsentsTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var createDomesticPaymentsConsentsApi: CreateDomesticPaymentsConsents

    @BeforeEach
    fun setUp() {
        createDomesticPaymentsConsentsApi = CreateDomesticPaymentsConsents(OBVersion.v4_0_0, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"]
    )
    @Test
    fun createDomesticPaymentsConsents_v4_0_0() {
        createDomesticPaymentsConsentsApi.createDomesticPaymentsConsentsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"]
    )
    @Test
    fun createDomesticPaymentsConsents_SameIdempotencyKeyMultipleRequestTest_v4_0_0(){
        createDomesticPaymentsConsentsApi.createDomesticPaymentsConsents_SameIdempotencyKeyMultipleRequestTest();
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"]
    )
    @Test
    fun createDomesticPaymentsConsents_NoIdempotencyKey_throwsBadRequestTest_v4_0_0(){
        createDomesticPaymentsConsentsApi.createDomesticPaymentsConsents_NoIdempotencyKey_throwsBadRequestTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"]
    )
    @Test
    fun createDomesticPaymentsConsents_withDebtorAccount_v4_0_0() {
        createDomesticPaymentsConsentsApi.createDomesticPaymentsConsents_withDebtorAccountTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"]
    )
    @Test
    @Disabled("Functionality not yet implemented in the RS Consent API - issue: https://github.com/SecureApiGateway/SecureApiGateway/issues/1041")
    fun createDomesticPaymentsConsents_withNonExistentDebtorAccount_v4_0_0() {
        createDomesticPaymentsConsentsApi.createDomesticPaymentsConsents_throwsInvalidDebtorAccountTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticPaymentsConsents_throwsNoDetachedJws_v4_0_0() {
        createDomesticPaymentsConsentsApi.shouldCreateDomesticPaymentsConsents_throwsNoDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticPaymentsConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJws_v4_0_0() {
        createDomesticPaymentsConsentsApi.shouldCreateDomesticPaymentsConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticPaymentsConsents_throwsSendInvalidFormatDetachedJws_v4_0_0() {
        createDomesticPaymentsConsentsApi.shouldCreateDomesticPaymentsConsents_throwsSendInvalidFormatDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticPaymentsConsents_throwsSendInvalidKidDetachedJws_v4_0_0() {
        createDomesticPaymentsConsentsApi.shouldCreateDomesticPaymentsConsents_throwsSendInvalidKidDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticPaymentsConsents_throwsRejectedConsent_v4_0_0() {
        createDomesticPaymentsConsentsApi.shouldCreateDomesticPaymentsConsents_throwsRejectedConsent_Test()
    }
}