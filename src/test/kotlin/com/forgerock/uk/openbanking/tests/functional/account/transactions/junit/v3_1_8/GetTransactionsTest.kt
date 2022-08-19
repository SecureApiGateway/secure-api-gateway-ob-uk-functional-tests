package com.forgerock.uk.openbanking.tests.functional.account.transactions.junit.v3_1_8

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.account.transactions.api.v3_1_8.GetTransactions
import org.junit.jupiter.api.Test

class GetTransactionsTest(val tppResource: CreateTppCallback.TppResource) {
    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.8",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetTransactions"],
        apis = ["transactions"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldGetTransactions_v3_1_8() {
        GetTransactions(OBVersion.v3_1_8, tppResource).shouldGetTransactionsTest()
    }
}