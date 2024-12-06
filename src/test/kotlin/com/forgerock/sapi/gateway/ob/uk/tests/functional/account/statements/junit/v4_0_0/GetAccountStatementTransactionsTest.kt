package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.statements.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.statements.api.v4_0_0.GetAccountStatementTransactions
import org.junit.jupiter.api.Test

class GetAccountStatementTransactionsTest(val tppResource: CreateTppCallback.TppResource) {
    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v4.0.0",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountStatementTransactions"],
        apis = ["statements"]
    )
    @Test
    fun shouldGetAccountStatementTransactions_v4_0_0() {
        GetAccountStatementTransactions(OBVersion.v4_0_0, tppResource).shouldGetAccountStatementTransactionsTest()
    }
}