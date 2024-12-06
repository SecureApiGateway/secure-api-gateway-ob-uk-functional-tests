package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.payments.consents.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.payments.consents.api.v4_0_0.CreateInternationalPaymentsConsents
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class CreateInternationalPaymentsConsentsTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var createInternationalPaymentsConsents: CreateInternationalPaymentsConsents

    @BeforeEach
    fun setUp() {
        createInternationalPaymentsConsents = CreateInternationalPaymentsConsents(OBVersion.v4_0_0, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalPaymentConsent"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun createInternationalPaymentsConsents_v4_0_0() {
        createInternationalPaymentsConsents.createInternationalPaymentsConsents()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalPaymentConsent"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun createInternationalPaymentsConsent_NoIdempotencyKey_throwsBadRequestTest_v4_0_0(){
        createInternationalPaymentsConsents.createInternationalPaymentsConsents_NoIdempotencyKey_throwsBadRequestTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalPaymentConsent"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun createInternationalPaymentsConsents_SameIdempotencyKeyMultipleRequestTest_v4_0_0(){
        createInternationalPaymentsConsents.createInternationalPaymentsConsents_SameIdempotencyKeyMultipleRequestTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalPaymentConsent"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun createInternationalPaymentsConsents_withDebtorAccount_v4_0_0() {
        createInternationalPaymentsConsents.createInternationalPaymentsConsents_withDebtorAccountTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalPaymentConsent"],
        apis = ["international-payment-consents"]
    )
    @Test
    @Disabled("This has not been implemented in the RS impl of the Consent API")
    fun createInternationalPaymentsConsents_throwsInvalidDebtorAccount_v4_0_0() {
        createInternationalPaymentsConsents.createInternationalPaymentsConsents_throwsInvalidDebtorAccountTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalPaymentConsent"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun createInternationalPaymentsConsents_mandatoryFields_v4_0_0() {
        createInternationalPaymentsConsents.createInternationalPaymentsConsents_mandatoryFieldsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalPaymentConsent"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalPaymentConsents_throwsSendInvalidFormatDetachedJws_v4_0_0() {
        createInternationalPaymentsConsents.shouldCreateInternationalPaymentConsents_throwsSendInvalidFormatDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalPaymentConsent"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalPaymentConsents_throwsNoDetachedJws_v4_0_0() {
        createInternationalPaymentsConsents.shouldCreateInternationalPaymentConsents_throwsNoDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalPaymentConsent"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalPaymentConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJws_v4_0_0() {
        createInternationalPaymentsConsents.shouldCreateInternationalPaymentConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalPaymentConsent"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalPaymentConsents_throwsSendInvalidKidDetachedJws_v4_0_0() {
        createInternationalPaymentsConsents.shouldCreateInternationalPaymentConsents_throwsSendInvalidKidDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalPaymentConsent"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalPaymentConsents_throwsRejectedConsent_v4_0_0() {
        createInternationalPaymentsConsents.shouldCreateInternationalPaymentConsents_throwsRejectedConsent_Test()
    }
}