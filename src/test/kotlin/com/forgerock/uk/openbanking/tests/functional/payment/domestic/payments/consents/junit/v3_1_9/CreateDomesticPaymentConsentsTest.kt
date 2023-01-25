package com.forgerock.uk.openbanking.tests.functional.payment.domestic.payments.consents.junit.v3_1_9

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
        createDomesticPaymentsConsentsApi = CreateDomesticPaymentsConsents(OBVersion.v3_1_9, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"]
    )
    @Test
    fun createDomesticPaymentsConsents_v3_1_9() {
        createDomesticPaymentsConsentsApi.createDomesticPaymentsConsentsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"]
    )
    @Test
    fun createDomesticPaymentsConsents_withDebtorAccount_v3_1_9() {
        createDomesticPaymentsConsentsApi.createDomesticPaymentsConsents_withDebtorAccountTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"]
    )
    @Test
    fun createDomesticPaymentsConsents_throwsInvalidDebtorAccount_v3_1_9() {
        createDomesticPaymentsConsentsApi.createDomesticPaymentsConsents_throwsInvalidDebtorAccountTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticPaymentsConsents_throwsNoDetachedJws_v3_1_9() {
        createDomesticPaymentsConsentsApi.shouldCreateDomesticPaymentsConsents_throwsNoDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticPaymentsConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJws_v3_1_9() {
        createDomesticPaymentsConsentsApi.shouldCreateDomesticPaymentsConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticPaymentsConsents_throwsSendInvalidFormatDetachedJws_v3_1_9() {
        createDomesticPaymentsConsentsApi.shouldCreateDomesticPaymentsConsents_throwsSendInvalidFormatDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticPaymentsConsents_throwsSendInvalidKidDetachedJws_v3_1_9() {
        createDomesticPaymentsConsentsApi.shouldCreateDomesticPaymentsConsents_throwsSendInvalidKidDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticPaymentsConsents_throwsRejectedConsent_v3_1_9() {
        createDomesticPaymentsConsentsApi.shouldCreateDomesticPaymentsConsents_throwsRejectedConsent_Test()
    }
}