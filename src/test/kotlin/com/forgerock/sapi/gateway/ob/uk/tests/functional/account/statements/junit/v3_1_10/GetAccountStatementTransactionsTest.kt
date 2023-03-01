package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.statements.junit.v3_1_10

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.statements.api.v3_1_8.GetAccountStatementTransactions
import org.junit.jupiter.api.Test

class GetAccountStatementTransactionsTest(val tppResource: CreateTppCallback.TppResource) {
    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.10",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountStatementTransactions"],
        apis = ["statements"]
    )
    @Test
    fun shouldGetAccountStatementTransactions_v3_1_10() {
        GetAccountStatementTransactions(OBVersion.v3_1_10, tppResource).shouldGetAccountStatementTransactionsTest()
    }
}