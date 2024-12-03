package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.scheduled.payments.consents.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.scheduled.payments.consents.api.v4_0_0.CreateDomesticScheduledPaymentsConsents
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class CreateDomesticScheduledPaymentsConsentsTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var createDomesticScheduledPaymentsConsentsApi: CreateDomesticScheduledPaymentsConsents

    @BeforeEach
    fun setUp() {
        createDomesticScheduledPaymentsConsentsApi =
            CreateDomesticScheduledPaymentsConsents(OBVersion.v4_0_0, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payment-consents"]
    )
    @Test
    fun createDomesticScheduledPaymentsConsents_v4_0_0() {
        createDomesticScheduledPaymentsConsentsApi.createDomesticScheduledPaymentsConsentsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payment-consents"]
    )
    @Test
    fun createDomesticPaymentsConsents_SameIdempotencyKeyMultipleRequestTest_v4_0_0(){
        createDomesticScheduledPaymentsConsentsApi.createDomesticPaymentsConsents_SameIdempotencyKeyMultipleRequestTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payment-consents"]
    )
    @Test
    fun createDomesticScheduledPaymentsConsents_NoIdempotencyKey_throwsBadRequestTest_v4_0_0() {
        createDomesticScheduledPaymentsConsentsApi.createDomesticScheduledPaymentsConsents_NoIdempotencyKey_throwsBadRequestTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payment-consents"]
    )
    @Test
    fun createDomesticScheduledPaymentsConsents_withDebtorAccount_v4_0_0() {
        createDomesticScheduledPaymentsConsentsApi.createDomesticScheduledPaymentsConsents_withDebtorAccountTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payment-consents"]
    )
    @Test
    @Disabled("Functionality not yet implemented in the RS Consent API - issue: https://github.com/SecureApiGateway/SecureApiGateway/issues/1041")
    fun createDomesticScheduledPaymentsConsents_throwsInvalidDebtorAccount_v4_0_0() {
        createDomesticScheduledPaymentsConsentsApi.createDomesticScheduledPaymentsConsents_throwsInvalidDebtorAccountTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticScheduledPaymentsConsents_throwsSendInvalidFormatDetachedJws_v4_0_0() {
        createDomesticScheduledPaymentsConsentsApi.shouldCreateDomesticScheduledPaymentsConsents_throwsSendInvalidFormatDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticScheduledPaymentsConsents_throwsNoDetachedJws_v4_0_0() {
        createDomesticScheduledPaymentsConsentsApi.shouldCreateDomesticScheduledPaymentsConsents_throwsNoDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticScheduledPaymentsConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJws_v4_0_0() {
        createDomesticScheduledPaymentsConsentsApi.shouldCreateDomesticScheduledPaymentsConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticScheduledPaymentsConsents_throwsSendInvalidKidDetachedJws_v4_0_0() {
        createDomesticScheduledPaymentsConsentsApi.shouldCreateDomesticScheduledPaymentsConsents_throwsSendInvalidKidDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticScheduledPaymentsConsents_throwsRequestExecutionTimeInThePast_v4_0_0() {
        createDomesticScheduledPaymentsConsentsApi.shouldCreateDomesticScheduledPaymentsConsents_throwsRequestExecutionTimeInThePastTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticScheduledPaymentsConsents_throwsRejectedConsent_v4_0_0() {
        createDomesticScheduledPaymentsConsentsApi.shouldCreateDomesticScheduledPaymentsConsents_throwsRejectedConsentTest()
    }
}