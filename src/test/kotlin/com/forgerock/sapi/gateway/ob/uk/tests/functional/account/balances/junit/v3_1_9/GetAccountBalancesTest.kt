package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.balances.junit.v3_1_9

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.balances.api.v3_1_8.GetAccountBalances
import org.junit.jupiter.api.Test

class GetAccountBalancesTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.9",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountBalances"],
        apis = ["balances"]
    )
    @Test
    fun shouldGetAccountBalances_v3_1_9() {
        GetAccountBalances(OBVersion.v3_1_9, tppResource).shouldGetAccountBalancesTest()
    }
}
