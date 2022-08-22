package com.forgerock.uk.openbanking.tests.functional.account.transactions.junit.v3_1_9

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.account.transactions.api.v3_1_8.GetAccountTransactions
import org.junit.jupiter.api.Test

class GetAccountTransactionsTest(val tppResource: CreateTppCallback.TppResource) {
    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.9",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountTransactions"],
        apis = ["transactions"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldGetAccountTransactions_v3_1_9() {
        GetAccountTransactions(OBVersion.v3_1_9, tppResource).shouldGetAccountTransactionsTest()
    }
}