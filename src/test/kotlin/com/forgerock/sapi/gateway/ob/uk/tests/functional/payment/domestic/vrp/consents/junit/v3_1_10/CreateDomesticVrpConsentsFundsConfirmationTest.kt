package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.vrp.consents.junit.v3_1_10

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.vrp.consents.api.v3_1_10.CreateDomesticVrpConsentsFundsConfirmation
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CreateDomesticVrpConsentsFundsConfirmationTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var createDomesticVrpConsentsFundsConfirmationApi: CreateDomesticVrpConsentsFundsConfirmation

    @BeforeEach
    fun setUp() {
        createDomesticVrpConsentsFundsConfirmationApi =
            CreateDomesticVrpConsentsFundsConfirmation(OBVersion.v3_1_10, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticVRPConsent", "CreateDomesticVRPConsentsConsentIdFundsConfirmation"],
        apis = ["domestic-vrp-consents"]
    )
    @Test
    fun shouldCreateDomesticVRPConsentsConsentIdFundsConfirmation_NotAvailable_v3_1_10() {
        createDomesticVrpConsentsFundsConfirmationApi.shouldCreateDomesticVRPConsentsConsentIdFundsConfirmation_NotAvailable()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticVRPConsent", "CreateDomesticVRPConsentsConsentIdFundsConfirmation"],
        apis = ["domestic-vrp-consents"]
    )
    @Test
    fun shouldCreateDomesticVRPConsentsConsentIdFundsConfirmation_throwsWrongGrantType_v3_1_10() {
        createDomesticVrpConsentsFundsConfirmationApi.shouldCreateDomesticVRPConsentsConsentIdFundsConfirmation_throwsWrongGrantType()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticVRPConsent", "CreateDomesticVRPConsentsConsentIdFundsConfirmation"],
        apis = ["domestic-vrp-consents"]
    )
    @Test
    fun shouldCreateDomesticVRPConsentsConsentIdFundsConfirmation_available_v3_1_10() {
        createDomesticVrpConsentsFundsConfirmationApi.shouldCreateDomesticVRPConsentsConsentIdFundsConfirmation_available()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticVRPConsent", "CreateDomesticVRPConsentsConsentIdFundsConfirmation"],
        apis = ["domestic-vrp-consents"]
    )
    @Test
    fun shouldFailIfAccessTokenConsentIdDoesNotMatchRequestConsentId_3_1_10() {
        createDomesticVrpConsentsFundsConfirmationApi.shouldFailIfAccessTokenConsentIdDoesNotMatchFundsConfPostRequestConsentId()
    }
}

