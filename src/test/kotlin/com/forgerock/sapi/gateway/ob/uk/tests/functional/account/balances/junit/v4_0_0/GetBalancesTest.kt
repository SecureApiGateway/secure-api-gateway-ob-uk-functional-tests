package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.balances.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.balances.api.v4_0_0.GetBalances
import org.junit.jupiter.api.Test

class GetBalancesTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v4.0.0",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetBalances"],
        apis = ["balances"]
    )
    @Test
    fun shouldGetBalances_v4_0_0() {
        GetBalances(OBVersion.v4_0_0, tppResource).shouldGetBalancesTest()
    }
}
