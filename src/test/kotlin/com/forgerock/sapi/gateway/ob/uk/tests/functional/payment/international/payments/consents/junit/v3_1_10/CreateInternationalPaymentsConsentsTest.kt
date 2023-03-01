package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.payments.consents.junit.v3_1_10

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.payments.consents.api.v3_1_8.CreateInternationalPaymentsConsents
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CreateInternationalPaymentsConsentsTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var createInternationalPaymentsConsents: CreateInternationalPaymentsConsents

    @BeforeEach
    fun setUp() {
        createInternationalPaymentsConsents = CreateInternationalPaymentsConsents(OBVersion.v3_1_10, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateInternationalPaymentConsent"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun createInternationalPaymentsConsents_v3_1_10() {
        createInternationalPaymentsConsents.createInternationalPaymentsConsents()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateInternationalPaymentConsent"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun createDomesticPaymentsConsents_NoIdempotencyKey_throwsBadRequestTest_v3_1_10(){
        createInternationalPaymentsConsents.createDomesticPaymentsConsents_NoIdempotencyKey_throwsBadRequestTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateInternationalPaymentConsent"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun createDomesticPaymentsConsents_SameIdempotencyKeyMultipleRequestTest_v3_1_10(){
        createInternationalPaymentsConsents.createDomesticPaymentsConsents_SameIdempotencyKeyMultipleRequestTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateInternationalPaymentConsent"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun createInternationalPaymentsConsents_withDebtorAccount_v3_1_10() {
        createInternationalPaymentsConsents.createInternationalPaymentsConsents_withDebtorAccountTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateInternationalPaymentConsent"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun createInternationalPaymentsConsents_throwsInvalidDebtorAccount_v3_1_10() {
        createInternationalPaymentsConsents.createInternationalPaymentsConsents_throwsInvalidDebtorAccountTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateInternationalPaymentConsent"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun createInternationalPaymentsConsents_mandatoryFields_v3_1_10() {
        createInternationalPaymentsConsents.createInternationalPaymentsConsents_mandatoryFieldsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateInternationalPaymentConsent"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalPaymentConsents_throwsSendInvalidFormatDetachedJws_v3_1_10() {
        createInternationalPaymentsConsents.shouldCreateInternationalPaymentConsents_throwsSendInvalidFormatDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateInternationalPaymentConsent"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalPaymentConsents_throwsNoDetachedJws_v3_1_10() {
        createInternationalPaymentsConsents.shouldCreateInternationalPaymentConsents_throwsNoDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateInternationalPaymentConsent"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalPaymentConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJws_v3_1_10() {
        createInternationalPaymentsConsents.shouldCreateInternationalPaymentConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateInternationalPaymentConsent"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalPaymentConsents_throwsSendInvalidKidDetachedJws_v3_1_10() {
        createInternationalPaymentsConsents.shouldCreateInternationalPaymentConsents_throwsSendInvalidKidDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateInternationalPaymentConsent"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldCreateInternationalPaymentConsents_throwsRejectedConsent_v3_1_10() {
        createInternationalPaymentsConsents.shouldCreateInternationalPaymentConsents_throwsRejectedConsent_Test()
    }
}