package com.forgerock.uk.openbanking.tests.functional.account.balances.junit.v3_1_10

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.account.balances.api.v3_1_8.GetAccountBalances
import org.junit.jupiter.api.Test

class GetAccountBalancesTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.10",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountBalances"],
        apis = ["balances"]
    )
    @Test
    fun shouldGetAccountBalances_v3_1_10() {
        GetAccountBalances(OBVersion.v3_1_10, tppResource).shouldGetAccountBalancesTest()
    }
}
