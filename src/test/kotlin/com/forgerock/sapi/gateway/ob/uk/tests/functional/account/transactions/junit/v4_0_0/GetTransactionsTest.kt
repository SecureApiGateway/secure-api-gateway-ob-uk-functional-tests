package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.transactions.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.transactions.api.v4_0_0.GetTransactions
import org.junit.jupiter.api.Test

class GetTransactionsTest(val tppResource: CreateTppCallback.TppResource) {
    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v4.0.0",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetTransactions"],
        apis = ["transactions"]
    )
    @Test
    fun shouldGetTransactions_v4_0_0() {
        GetTransactions(OBVersion.v4_0_0, tppResource).shouldGetTransactionsTest()
    }

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v4.0.0",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetTransactions"],
        apis = ["transactions"]
    )
    @Test
    fun shouldGetTransactionsTest_getV4Fields_v4_0_0() {
        GetTransactions(OBVersion.v4_0_0, tppResource).shouldGetTransactionsTest_getV4Fields()
    }
}