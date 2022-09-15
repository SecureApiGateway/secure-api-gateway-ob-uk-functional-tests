package com.forgerock.uk.openbanking.tests.functional.payment.domestic.payments.consents.junit.v3_1_9

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.payment.domestic.payments.consents.api.v3_1_8.GetDomesticPaymentsConsentFundsConfirmation
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class GetDomesticPaymentsConsentFundsConfirmationTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getDomesticPaymentsConsentFundsConfirmationApi: GetDomesticPaymentsConsentFundsConfirmation

    @BeforeEach
    fun setUp() {
        getDomesticPaymentsConsentFundsConfirmationApi =
            GetDomesticPaymentsConsentFundsConfirmation(OBVersion.v3_1_9, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateDomesticPaymentConsent", "GetDomesticPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["domestic-payment-consents"]
    )
    @Test
    fun shouldGetDomesticPaymentConsentsFundsConfirmation_false_v3_1_9() {
        getDomesticPaymentsConsentFundsConfirmationApi.shouldGetDomesticPaymentConsentsFundsConfirmation_false()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateDomesticPaymentConsent", "GetDomesticPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["domestic-payment-consents"]
    )
    @Test
    fun shouldGetDomesticPaymentConsentsFundsConfirmation_throwsWrongGrantType_v3_1_9() {
        getDomesticPaymentsConsentFundsConfirmationApi.shouldGetDomesticPaymentConsentsFundsConfirmation_throwsWrongGrantType()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateDomesticPaymentConsent", "GetDomesticPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["domestic-payment-consents"]
    )
    @Test
    fun shouldGetDomesticPaymentConsentsFundsConfirmation_true_v3_1_9() {
        getDomesticPaymentsConsentFundsConfirmationApi.shouldGetDomesticPaymentConsentsFundsConfirmation_true()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateDomesticPaymentConsent", "GetDomesticPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["domestic-payment-consents"]
    )
    @Test
    fun shouldGetDomesticPaymentConsentsFundsConfirmation_throwsInvalidConsentStatus_v3_1_8() {
        getDomesticPaymentsConsentFundsConfirmationApi.shouldGetDomesticPaymentConsentsFundsConfirmation_throwsInvalidConsentStatus_Test()
    }
}