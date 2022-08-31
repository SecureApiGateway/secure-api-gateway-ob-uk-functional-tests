package com.forgerock.uk.openbanking.tests.functional.payment.international.scheduled.payments.consents.junit.v3_1_9

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.payment.international.scheduled.payments.consents.api.v3_1_8.GetInternationalScheduledPaymentsConsentFundsConfirmation
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

@Disabled("Not implemented")
class GetInternationalScheduledPaymentsConsentFundsConfirmationTest(val tppResource: CreateTppCallback.TppResource) {

    private lateinit var getInternationalScheduledPaymentsConsentFundsConfirmation: GetInternationalScheduledPaymentsConsentFundsConfirmation

    @BeforeEach
    fun setUp() {
        getInternationalScheduledPaymentsConsentFundsConfirmation =
            GetInternationalScheduledPaymentsConsentFundsConfirmation(OBVersion.v3_1_9, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_true_rateType_AGREED_v3_1_9() {
        getInternationalScheduledPaymentsConsentFundsConfirmation.shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_true_rateType_AGREED_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_true_rateType_ACTUAL_v3_1_9() {
        getInternationalScheduledPaymentsConsentFundsConfirmation.shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_true_rateType_ACTUAL_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_true_rateType_INDICATIVE_v3_1_9() {
        getInternationalScheduledPaymentsConsentFundsConfirmation.shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_true_rateType_INDICATIVE_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_false_rateType_AGREED_v3_1_9() {
        getInternationalScheduledPaymentsConsentFundsConfirmation.shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_false_rateType_AGREED_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_false_rateType_ACTUAL_v3_1_9() {
        getInternationalScheduledPaymentsConsentFundsConfirmation.shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_false_rateType_ACTUAL_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_false_rateType_INDICATIVE_v3_1_9() {
        getInternationalScheduledPaymentsConsentFundsConfirmation.shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_false_rateType_INDICATIVE_Test()
    }

    @Disabled("Enhancement: https://github.com/SecureBankingAccessToolkit/SecureBankingAccessToolkit/issues/337")
    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_throwsWrongGrantType_v3_1_9() {
        getInternationalScheduledPaymentsConsentFundsConfirmation.shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_throwsWrongGrantType_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_throwsInvalidConsentStatus_v3_1_9() {
        getInternationalScheduledPaymentsConsentFundsConfirmation.shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_throwsInvalidConsentStatus_Test()
    }
}