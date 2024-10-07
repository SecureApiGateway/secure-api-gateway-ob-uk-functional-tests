package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.parties.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.parties.api.v4_0_0.GetAccountParty
import org.junit.jupiter.api.Test

class GetAccountPartyTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v4.0.0",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountParty"],
        apis = ["party"]
    )
    @Test
    fun shouldGetAccountParty_v4_0_0() {
        GetAccountParty(OBVersion.v4_0_0, tppResource).shouldGetAccountPartyTest()
    }
}