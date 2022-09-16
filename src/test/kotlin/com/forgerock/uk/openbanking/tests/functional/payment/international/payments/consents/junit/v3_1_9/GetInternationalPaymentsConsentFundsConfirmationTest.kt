package com.forgerock.uk.openbanking.tests.functional.payment.international.payments.consents.junit.v3_1_9

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.payment.international.payments.consents.api.v3_1_8.GetInternationalPaymentsConsentFundsConfirmation
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetInternationalPaymentsConsentFundsConfirmationTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getInternationalPaymentsConsentFundsConfirmation: GetInternationalPaymentsConsentFundsConfirmation

    @BeforeEach
    fun setUp() {
        getInternationalPaymentsConsentFundsConfirmation =
            GetInternationalPaymentsConsentFundsConfirmation(OBVersion.v3_1_9, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldGetInternationalPaymentConsentsFundsConfirmation_true_rateType_AGREED_v3_1_9() {
        getInternationalPaymentsConsentFundsConfirmation.shouldGetInternationalPaymentConsentsFundsConfirmation_true_rateType_AGREED_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldGetInternationalPaymentConsentsFundsConfirmation_true_rateType_ACTUAL_v3_1_9() {
        getInternationalPaymentsConsentFundsConfirmation.shouldGetInternationalPaymentConsentsFundsConfirmation_true_rateType_ACTUAL_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldGetInternationalPaymentConsentsFundsConfirmation_true_rateType_INDICATIVE_v3_1_9() {
        getInternationalPaymentsConsentFundsConfirmation.shouldGetInternationalPaymentConsentsFundsConfirmation_true_rateType_INDICATIVE_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldGetInternationalPaymentConsentsFundsConfirmation_false_rateType_AGREED_v3_1_9() {
        getInternationalPaymentsConsentFundsConfirmation.shouldGetInternationalPaymentConsentsFundsConfirmation_false_rateType_AGREED_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldGetInternationalPaymentConsentsFundsConfirmation_false_rateType_ACTUAL_v3_1_9() {
        getInternationalPaymentsConsentFundsConfirmation.shouldGetInternationalPaymentConsentsFundsConfirmation_false_rateType_ACTUAL_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldGetInternationalPaymentConsentsFundsConfirmation_false_rateType_INDICATIVE_v3_1_9() {
        getInternationalPaymentsConsentFundsConfirmation.shouldGetInternationalPaymentConsentsFundsConfirmation_false_rateType_INDICATIVE_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldGetInternationalPaymentConsentsFundsConfirmation_throwsWrongGrantType_v3_1_9() {
        getInternationalPaymentsConsentFundsConfirmation.shouldGetInternationalPaymentConsentsFundsConfirmation_throwsWrongGrantType_Test()
    }


    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldGetInternationalPaymentConsentsFundsConfirmation_throwsInvalidConsentStatus_v3_1_9() {
        getInternationalPaymentsConsentFundsConfirmation.shouldGetInternationalPaymentConsentsFundsConfirmation_throwsInvalidConsentStatus_Test()
    }
}
