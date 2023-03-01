package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.transactions.junit.v3_1_10

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.transactions.api.v3_1_8.GetTransactions
import org.junit.jupiter.api.Test

class GetTransactionsTest(val tppResource: CreateTppCallback.TppResource) {
    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.10",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetTransactions"],
        apis = ["transactions"]
    )
    @Test
    fun shouldGetTransactions_v3_1_10() {
        GetTransactions(OBVersion.v3_1_10, tppResource).shouldGetTransactionsTest()
    }
}