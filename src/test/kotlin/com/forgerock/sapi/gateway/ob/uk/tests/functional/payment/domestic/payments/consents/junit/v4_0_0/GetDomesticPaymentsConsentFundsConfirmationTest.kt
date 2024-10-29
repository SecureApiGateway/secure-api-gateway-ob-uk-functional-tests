package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.payments.consents.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.payments.consents.api.v4_0_0.GetDomesticPaymentsConsentFundsConfirmation
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetDomesticPaymentsConsentFundsConfirmationTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getDomesticPaymentsConsentFundsConfirmationApi: GetDomesticPaymentsConsentFundsConfirmation

    @BeforeEach
    fun setUp() {
        getDomesticPaymentsConsentFundsConfirmationApi =
            GetDomesticPaymentsConsentFundsConfirmation(OBVersion.v4_0_0, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticPaymentConsent", "GetDomesticPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["domestic-payment-consents"]
    )
    @Test
    fun shouldGetDomesticPaymentConsentsFundsConfirmation_false_v4_0_0() {
        getDomesticPaymentsConsentFundsConfirmationApi.shouldGetDomesticPaymentConsentsFundsConfirmation_false()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticPaymentConsent", "GetDomesticPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["domestic-payment-consents"]
    )
    @Test
    fun shouldGetDomesticPaymentConsentsFundsConfirmation_throwsWrongGrantType_v4_0_0() {
        getDomesticPaymentsConsentFundsConfirmationApi.shouldGetDomesticPaymentConsentsFundsConfirmation_throwsWrongGrantType()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticPaymentConsent", "GetDomesticPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["domestic-payment-consents"]
    )
    @Test
    fun shouldGetDomesticPaymentConsentsFundsConfirmation_true_v4_0_0() {
        getDomesticPaymentsConsentFundsConfirmationApi.shouldGetDomesticPaymentConsentsFundsConfirmation_true()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticPaymentConsent", "GetDomesticPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["domestic-payment-consents"]
    )
    @Test
    fun shouldGetDomesticPaymentConsentsFundsConfirmation_throwsInvalidConsentStatus_v4_0_0() {
        getDomesticPaymentsConsentFundsConfirmationApi.shouldGetDomesticPaymentConsentsFundsConfirmation_throwsInvalidConsentStatus_Test()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticPaymentConsent", "GetDomesticPaymentConsentsConsentIdFundsConfirmation"],
        apis = ["domestic-payment-consents"]
    )
    @Test
    fun shouldFailIfAccessTokenConsentIdDoesNotMatchRequestUriPathParamConsentId_v4_0_0() {
        getDomesticPaymentsConsentFundsConfirmationApi.shouldFailIfAccessTokenConsentIdDoesNotMatchRequestUriPathParamConsentId()
    }
}
