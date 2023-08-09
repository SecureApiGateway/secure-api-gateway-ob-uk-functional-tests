package com.forgerock.sapi.gateway.ob.uk.tests.functional.funds.junit.v3_1_10

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.funds.api.v3_1_10.FundsConfirmations
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FundsConfirmationsTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var fundsConfirmationApi: FundsConfirmations

    @BeforeEach
    fun setUp() {
        fundsConfirmationApi = FundsConfirmations(OBVersion.v3_1_10, tppResource)
    }

    @EnabledIfVersion(
            type = "funds",
            apiVersion = "v3.1.10",
            operations = ["CreateFundsConfirmation"]
    )
    @Test
    fun createFundsConfirmationAvailableTrue_v3_1_10() {
        fundsConfirmationApi.shouldCreateFundsConfirmationAvailableTrueTest()
    }

    @EnabledIfVersion(
            type = "funds",
            apiVersion = "v3.1.10",
            operations = ["CreateFundsConfirmation"]
    )
    @Test
    fun createFundsConfirmationAvailableFalse_v3_1_10() {
        fundsConfirmationApi.shouldCreateFundsConfirmationAvailableFalseTest()
    }

    @EnabledIfVersion(
            type = "funds",
            apiVersion = "v3.1.10",
            operations = ["CreateFundsConfirmation"]
    )
    @Test
    fun createFundsConfirmation_currencyMismatch_v3_1_10() {
        fundsConfirmationApi.createFundsConfirmation_currencyMismatch_Test()
    }

    @EnabledIfVersion(
            type = "funds",
            apiVersion = "v3.1.10",
            operations = ["CreateFundsConfirmation"]
    )
    @Test
    fun fundsConfirmation_consentExpired_v3_1_10() {
        fundsConfirmationApi.createFundsConfirmation_consentExpired_Test()
    }

}