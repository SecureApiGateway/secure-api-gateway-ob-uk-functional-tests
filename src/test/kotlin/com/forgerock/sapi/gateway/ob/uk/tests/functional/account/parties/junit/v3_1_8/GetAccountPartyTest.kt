package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.parties.junit.v3_1_8

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.parties.api.v3_1_8.GetAccountParty
import org.junit.jupiter.api.Test

class GetAccountPartyTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.8",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountParty"],
        apis = ["party"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5", "v.3.1.4", "v.3.1.3"]
    )
    @Test
    fun shouldGetAccountParty_v3_1_8() {
        GetAccountParty(OBVersion.v3_1_8, tppResource).shouldGetAccountPartyTest()
    }
}