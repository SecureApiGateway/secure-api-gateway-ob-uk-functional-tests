package com.forgerock.uk.openbanking.tests.functional.account.statements.junit.v3_1_10

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.account.statements.api.v3_1_8.GetAccountStatementTransactions
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