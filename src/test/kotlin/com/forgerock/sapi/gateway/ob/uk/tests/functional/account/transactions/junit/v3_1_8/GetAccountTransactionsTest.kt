package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.transactions.junit.v3_1_8

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.transactions.api.v3_1_8.GetAccountTransactions
import org.junit.jupiter.api.Test

class GetAccountTransactionsTest(val tppResource: CreateTppCallback.TppResource) {
    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.8",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountTransactions"],
        apis = ["transactions"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldGetAccountTransactions_v3_1_8() {
        GetAccountTransactions(OBVersion.v3_1_8, tppResource).shouldGetAccountTransactionsTest()
    }
}