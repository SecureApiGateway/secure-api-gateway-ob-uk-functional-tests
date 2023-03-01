package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.parties.junit.v3_1_10

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.parties.api.v3_1_8.GetAccountParty
import org.junit.jupiter.api.Test

class GetAccountPartyTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.10",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountParty"],
        apis = ["party"]
    )
    @Test
    fun shouldGetAccountParty_v3_1_10() {
        GetAccountParty(OBVersion.v3_1_10, tppResource).shouldGetAccountPartyTest()
    }
}