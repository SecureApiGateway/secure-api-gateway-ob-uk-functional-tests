package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.scheduled.payments.consents.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.scheduled.payments.consents.api.v4_0_0.GetInternationalScheduledPaymentsConsentFundsConfirmation
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetInternationalScheduledPaymentsConsentFundsConfirmationTest(val tppResource: CreateTppCallback.TppResource) {

    private lateinit var getInternationalScheduledPaymentsConsentFundsConfirmation: GetInternationalScheduledPaymentsConsentFundsConfirmation

    @BeforeEach
    fun setUp() {
        getInternationalScheduledPaymentsConsentFundsConfirmation =
            GetInternationalScheduledPaymentsConsentFundsConfirmation(OBVersion.v4_0_0, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_true_rateType_AGREED_v4_0_0() {
        getInternationalScheduledPaymentsConsentFundsConfirmation.shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_true_rateType_AGREED_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_true_rateType_ACTUAL_v4_0_0() {
        getInternationalScheduledPaymentsConsentFundsConfirmation.shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_true_rateType_ACTUAL_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_true_rateType_INDICATIVE_v4_0_0() {
        getInternationalScheduledPaymentsConsentFundsConfirmation.shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_true_rateType_INDICATIVE_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_false_rateType_AGREED_v4_0_0() {
        getInternationalScheduledPaymentsConsentFundsConfirmation.shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_false_rateType_AGREED_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_false_rateType_ACTUAL_v4_0_0() {
        getInternationalScheduledPaymentsConsentFundsConfirmation.shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_false_rateType_ACTUAL_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_false_rateType_INDICATIVE_v4_0_0() {
        getInternationalScheduledPaymentsConsentFundsConfirmation.shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_false_rateType_INDICATIVE_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_throwsWrongGrantType_v4_0_0() {
        getInternationalScheduledPaymentsConsentFundsConfirmation.shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_throwsWrongGrantType_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_throwsInvalidConsentStatus_v4_0_0() {
        getInternationalScheduledPaymentsConsentFundsConfirmation.shouldGetInternationalScheduledPaymentConsentsFundsConfirmation_throwsInvalidConsentStatus_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalScheduledPaymentConsent", "GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-scheduled-payment-consents"]
    )
    @Test
    fun shouldFailIfAccessTokenConsentIdDoesNotMatchRequestUriPathParamConsentId_v4_0_0() {
        getInternationalScheduledPaymentsConsentFundsConfirmation.shouldFailIfAccessTokenConsentIdDoesNotMatchRequestUriPathParamConsentId()
    }

}
