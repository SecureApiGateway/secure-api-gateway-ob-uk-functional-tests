package com.forgerock.sapi.gateway.ob.uk.tests.functional.funds.consents.junit.v3_1_10

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.funds.consents.api.v3_1_10.FundsConfirmationConsents
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FundsConfirmationConsentTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var fundsConfirmationConsentsApi: FundsConfirmationConsents

    @BeforeEach
    fun setUp() {
        fundsConfirmationConsentsApi = FundsConfirmationConsents(OBVersion.v3_1_10, tppResource)
    }

    @EnabledIfVersion(
            type = "funds",
            apiVersion = "v3.1.10",
            operations = ["CreateFundsConfirmationConsent"]
    )
    @Test
    fun createFundsConfirmationConsents_v3_1_10() {
        fundsConfirmationConsentsApi.createFundsConfirmationConsentTest()
    }

    @EnabledIfVersion(
            type = "funds",
            apiVersion = "v3.1.10",
            operations = ["DeleteFundsConfirmationConsent"]
    )
    @Test
    fun deleteFundsConfirmationConsents_v3_1_10() {
        fundsConfirmationConsentsApi.deleteFundsConfirmationConsentTest()
    }

    @EnabledIfVersion(
            type = "funds",
            apiVersion = "v3.1.10",
            operations = ["GetFundsConfirmationConsent"]
    )
    @Test
    fun getFundsConfirmationConsents_v3_1_10() {
        fundsConfirmationConsentsApi.getFundsConfirmationConsentTest()
    }
}