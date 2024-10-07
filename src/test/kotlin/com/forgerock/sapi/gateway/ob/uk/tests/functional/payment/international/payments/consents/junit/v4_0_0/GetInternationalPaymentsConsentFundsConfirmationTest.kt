package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.payments.consents.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.payments.consents.api.v4_0_0.GetInternationalPaymentsConsentFundsConfirmation
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetInternationalPaymentsConsentFundsConfirmationTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getInternationalPaymentsConsentFundsConfirmation: GetInternationalPaymentsConsentFundsConfirmation

    @BeforeEach
    fun setUp() {
        getInternationalPaymentsConsentFundsConfirmation =
            GetInternationalPaymentsConsentFundsConfirmation(OBVersion.v4_0_0, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldGetInternationalPaymentConsentsFundsConfirmation_true_rateType_AGREED_v4_0_0() {
        getInternationalPaymentsConsentFundsConfirmation.shouldGetInternationalPaymentConsentsFundsConfirmation_true_rateType_AGREED_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldGetInternationalPaymentConsentsFundsConfirmation_true_rateType_ACTUAL_v4_0_0() {
        getInternationalPaymentsConsentFundsConfirmation.shouldGetInternationalPaymentConsentsFundsConfirmation_true_rateType_ACTUAL_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldGetInternationalPaymentConsentsFundsConfirmation_true_rateType_INDICATIVE_v4_0_0() {
        getInternationalPaymentsConsentFundsConfirmation.shouldGetInternationalPaymentConsentsFundsConfirmation_true_rateType_INDICATIVE_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldGetInternationalPaymentConsentsFundsConfirmation_false_rateType_AGREED_v4_0_0() {
        getInternationalPaymentsConsentFundsConfirmation.shouldGetInternationalPaymentConsentsFundsConfirmation_false_rateType_AGREED_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldGetInternationalPaymentConsentsFundsConfirmation_false_rateType_ACTUAL_v4_0_0() {
        getInternationalPaymentsConsentFundsConfirmation.shouldGetInternationalPaymentConsentsFundsConfirmation_false_rateType_ACTUAL_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldGetInternationalPaymentConsentsFundsConfirmation_false_rateType_INDICATIVE_v4_0_0() {
        getInternationalPaymentsConsentFundsConfirmation.shouldGetInternationalPaymentConsentsFundsConfirmation_false_rateType_INDICATIVE_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldGetInternationalPaymentConsentsFundsConfirmation_throwsWrongGrantType_v4_0_0() {
        getInternationalPaymentsConsentFundsConfirmation.shouldGetInternationalPaymentConsentsFundsConfirmation_throwsWrongGrantType_Test()
    }


    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldGetInternationalPaymentConsentsFundsConfirmation_throwsInvalidConsentStatus_v4_0_0() {
        getInternationalPaymentsConsentFundsConfirmation.shouldGetInternationalPaymentConsentsFundsConfirmation_throwsInvalidConsentStatus_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalPaymentConsent", "GetInternationalPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["international-payment-consents"]
    )
    @Test
    fun shouldFailIfAccessTokenConsentIdDoesNotMatchRequestUriPathParamConsentId_v4_0_0() {
        getInternationalPaymentsConsentFundsConfirmation.shouldFailIfAccessTokenConsentIdDoesNotMatchRequestUriPathParamConsentId()
    }
}
