package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.balances.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.balances.api.v4_0_0.GetAccountBalances
import org.junit.jupiter.api.Test

class GetAccountBalancesTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v4.0.0",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountBalances"],
        apis = ["balances"]
    )
    @Test
    fun shouldGetAccountBalances_v4_0_0() {
        GetAccountBalances(OBVersion.v4_0_0, tppResource).shouldGetAccountBalancesTest()
    }
}
