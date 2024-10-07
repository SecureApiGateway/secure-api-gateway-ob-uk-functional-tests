package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.parties.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.parties.api.v4_0_0.GetAccountParties
import org.junit.jupiter.api.Test

class GetAccountPartiesTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v4.0.0",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountParties"],
        apis = ["party"]
    )
    @Test
    fun shouldGetAccountParties_v4_0_0() {
        GetAccountParties(OBVersion.v4_0_0, tppResource).shouldGetAccountPartiesTest()
    }
}