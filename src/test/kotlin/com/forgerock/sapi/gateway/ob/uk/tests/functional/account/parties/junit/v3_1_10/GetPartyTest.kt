package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.parties.junit.v3_1_10

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.parties.api.v3_1_8.GetParty
import org.junit.jupiter.api.Test

class GetPartyTest(val tppResource: CreateTppCallback.TppResource) {
    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.10",
        operations = ["CreateAccountAccessConsent", "GetParty"],
        apis = ["party"]
    )
    @Test
    fun shouldGetParty_v3_1_10() {
        GetParty(OBVersion.v3_1_10, tppResource).shouldGetPartyTest()
    }
}