package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.accounts.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.accounts.api.v4_0_0.GetAccounts
import org.junit.jupiter.api.Test


class GetAccountsTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v4.0.0",
        operations = ["CreateAccountAccessConsent", "GetAccounts"],
        apis = ["accounts"]
    )
    @Test
    fun shouldGetAccounts_v4_0_0() {
        GetAccounts(OBVersion.v4_0_0, tppResource).shouldGetAccountsTest()
    }
}