package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.transactions.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.transactions.api.v4_0_0.GetAccountTransactions
import org.junit.jupiter.api.Test

class GetAccountTransactionsTest(val tppResource: CreateTppCallback.TppResource) {
    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v4.0.0",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountTransactions"],
        apis = ["transactions"]
    )
    @Test
    fun shouldGetAccountTransactions_v4_0_0() {
        GetAccountTransactions(OBVersion.v4_0_0, tppResource).shouldGetAccountTransactionsTest()
    }

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v4.0.0",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountTransactions"],
        apis = ["transactions"]
    )
    @Test
    fun shouldGetAccountTransactionsTest_getV4Fields_v4_0_0() {
        GetAccountTransactions(OBVersion.v4_0_0, tppResource).shouldGetAccountTransactionsTest_getV4Fields()
    }
}