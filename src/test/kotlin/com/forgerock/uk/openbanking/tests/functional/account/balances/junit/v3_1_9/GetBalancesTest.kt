package com.forgerock.uk.openbanking.tests.functional.account.balances.junit.v3_1_9

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.account.balances.api.v3_1_8.GetBalances
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
