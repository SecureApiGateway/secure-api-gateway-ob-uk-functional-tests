package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.vrp.consents.junit.v3_1_10

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.vrp.consents.api.v3_1_10.GetDomesticVrpConsentsFundsConfirmation
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetDomesticVrpConsentsFundsConfirmationTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getDomesticVrpConsentsFundsConfirmationApi: GetDomesticVrpConsentsFundsConfirmation

    @BeforeEach
    fun setUp() {
        getDomesticVrpConsentsFundsConfirmationApi =
            GetDomesticVrpConsentsFundsConfirmation(OBVersion.v3_1_10, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticVRPConsent", "GetDomesticVRPConsentsConsentIdFundsConfirmation"],
        apis = ["domestic-vrp-consents"]
    )
    @Test
    fun shouldGetDomesticVrpPaymentConsentsFundsConfirmation_false_v3_1_10() {
        getDomesticVrpConsentsFundsConfirmationApi.shouldGetDomesticVrpPaymentConsentsFundsConfirmation_false()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticVRPConsent", "GetDomesticVRPConsentsConsentIdFundsConfirmation"],
        apis = ["domestic-vrp-consents"]
    )
    @Test
    fun shouldGetDomesticVrpPaymentConsentsFundsConfirmation_throwsWrongGrantType_v3_1_10() {
        getDomesticVrpConsentsFundsConfirmationApi.shouldGetDomesticVrpPaymentConsentsFundsConfirmation_throwsWrongGrantType()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticVRPConsent", "GetDomesticVRPConsentsConsentIdFundsConfirmation"],
        apis = ["domestic-vrp-consents"]
    )
    @Test
    fun shouldGetDomesticVrpPaymentConsentsFundsConfirmation_true_v3_1_10() {
        getDomesticVrpConsentsFundsConfirmationApi.shouldGetDomesticVrpPaymentConsentsFundsConfirmation_true()
    }
}

