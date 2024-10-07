package com.forgerock.sapi.gateway.ob.uk.tests.functional.funds.consents.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.funds.consents.api.v4_0_0.FundsConfirmationConsents
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FundsConfirmationConsentTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var fundsConfirmationConsentsApi: FundsConfirmationConsents

    @BeforeEach
    fun setUp() {
        fundsConfirmationConsentsApi = FundsConfirmationConsents(OBVersion.v4_0_0, tppResource)
    }

    @EnabledIfVersion(
            type = "funds",
            apiVersion = "v4.0.0",
            operations = ["CreateFundsConfirmationConsent"]
    )
    @Test
    fun createFundsConfirmationConsents_v4_0_0() {
        fundsConfirmationConsentsApi.createFundsConfirmationConsentTest()
    }

    @EnabledIfVersion(
            type = "funds",
            apiVersion = "v4.0.0",
            operations = ["DeleteFundsConfirmationConsent"]
    )
    @Test
    fun deleteFundsConfirmationConsents_v4_0_0() {
        fundsConfirmationConsentsApi.deleteFundsConfirmationConsentTest()
    }

    @EnabledIfVersion(
            type = "funds",
            apiVersion = "v4.0.0",
            operations = ["GetFundsConfirmationConsent"]
    )
    @Test
    fun getFundsConfirmationConsents_v4_0_0() {
        fundsConfirmationConsentsApi.getFundsConfirmationConsentTest()
    }
}