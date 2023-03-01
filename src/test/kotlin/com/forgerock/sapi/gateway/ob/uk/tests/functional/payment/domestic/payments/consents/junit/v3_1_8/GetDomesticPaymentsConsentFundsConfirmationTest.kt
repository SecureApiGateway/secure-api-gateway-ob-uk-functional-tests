package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.payments.consents.junit.v3_1_8

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.payments.consents.api.v3_1_8.GetDomesticPaymentsConsentFundsConfirmation
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetDomesticPaymentsConsentFundsConfirmationTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getDomesticPaymentsConsentFundsConfirmationApi: GetDomesticPaymentsConsentFundsConfirmation

    @BeforeEach
    fun setUp() {
        getDomesticPaymentsConsentFundsConfirmationApi =
            GetDomesticPaymentsConsentFundsConfirmation(OBVersion.v3_1_8, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateDomesticPaymentConsent", "GetDomesticPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["domestic-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldGetDomesticPaymentConsentsFundsConfirmation_false_v3_1_8() {
        getDomesticPaymentsConsentFundsConfirmationApi.shouldGetDomesticPaymentConsentsFundsConfirmation_false()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateDomesticPaymentConsent", "GetDomesticPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["domestic-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldGetDomesticPaymentConsentsFundsConfirmation_throwsWrongGrantType_v3_1_8() {
        getDomesticPaymentsConsentFundsConfirmationApi.shouldGetDomesticPaymentConsentsFundsConfirmation_throwsWrongGrantType()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateDomesticPaymentConsent", "GetDomesticPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["domestic-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldGetDomesticPaymentConsentsFundsConfirmation_true_v3_1_8() {
        getDomesticPaymentsConsentFundsConfirmationApi.shouldGetDomesticPaymentConsentsFundsConfirmation_true()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateDomesticPaymentConsent", "GetDomesticPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["domestic-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldGetDomesticPaymentConsentsFundsConfirmation_throwsInvalidConsentStatus_v3_1_8() {
        getDomesticPaymentsConsentFundsConfirmationApi.shouldGetDomesticPaymentConsentsFundsConfirmation_throwsInvalidConsentStatus_Test()
    }
}