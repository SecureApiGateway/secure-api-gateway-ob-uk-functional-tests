package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.accounts.junit.v3_1_8

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.accounts.api.v3_1_8.GetAccounts
import org.junit.jupiter.api.Test


class GetAccountsTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.8",
        operations = ["CreateAccountAccessConsent", "GetAccounts"],
        apis = ["accounts"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6"]
    )
    @Test
    fun shouldGetAccounts_v3_1_8() {
        GetAccounts(OBVersion.v3_1_8, tppResource).shouldGetAccountsTest()
    }
}