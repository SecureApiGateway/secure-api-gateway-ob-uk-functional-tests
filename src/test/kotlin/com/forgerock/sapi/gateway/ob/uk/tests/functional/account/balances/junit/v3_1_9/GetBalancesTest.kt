package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.balances.junit.v3_1_9

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.balances.api.v3_1_8.GetBalances
import org.junit.jupiter.api.Test

class GetBalancesTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.9",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetBalances"],
        apis = ["balances"]
    )
    @Test
    fun shouldGetBalances_v3_1_9() {
        GetBalances(OBVersion.v3_1_9, tppResource).shouldGetBalancesTest()
    }
}
