package com.forgerock.sapi.gateway.ob.uk.tests.functional.funds.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.funds.api.v4_0_0.FundsConfirmations
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FundsConfirmationsTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var fundsConfirmationApi: FundsConfirmations

    @BeforeEach
    fun setUp() {
        fundsConfirmationApi = FundsConfirmations(OBVersion.v4_0_0, tppResource)
    }

    @EnabledIfVersion(
            type = "funds",
            apiVersion = "v4.0.0",
            operations = ["CreateFundsConfirmation"]
    )
    @Test
    fun createFundsConfirmationAvailableTrue_v4_0_0() {
        fundsConfirmationApi.shouldCreateFundsConfirmationAvailableTrueTest()
    }

    @EnabledIfVersion(
            type = "funds",
            apiVersion = "v4.0.0",
            operations = ["CreateFundsConfirmation"]
    )
    @Test
    fun createFundsConfirmationAvailableFalse_v4_0_0() {
        fundsConfirmationApi.shouldCreateFundsConfirmationAvailableFalseTest()
    }

    @EnabledIfVersion(
            type = "funds",
            apiVersion = "v4.0.0",
            operations = ["CreateFundsConfirmation"]
    )
    @Test
    fun createFundsConfirmation_currencyMismatch_v4_0_0() {
        fundsConfirmationApi.createFundsConfirmation_currencyMismatch_Test()
    }

    @EnabledIfVersion(
            type = "funds",
            apiVersion = "v4.0.0",
            operations = ["CreateFundsConfirmation"]
    )
    @Test
    fun fundsConfirmation_consentExpired_v4_0_0() {
        fundsConfirmationApi.createFundsConfirmation_consentExpired_Test()
    }

}