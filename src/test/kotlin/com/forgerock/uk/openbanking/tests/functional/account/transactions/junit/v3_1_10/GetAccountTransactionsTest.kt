package com.forgerock.uk.openbanking.tests.functional.account.transactions.junit.v3_1_10

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.account.transactions.api.v3_1_8.GetAccountTransactions
import org.junit.jupiter.api.Test

class GetAccountTransactionsTest(val tppResource: CreateTppCallback.TppResource) {
    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.10",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountTransactions"],
        apis = ["transactions"]
    )
    @Test
    fun shouldGetAccountTransactions_v3_1_10() {
        GetAccountTransactions(OBVersion.v3_1_10, tppResource).shouldGetAccountTransactionsTest()
    }
}