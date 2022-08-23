package com.forgerock.uk.openbanking.tests.functional.account.parties.junit.v3_1_9

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.account.parties.api.v3_1_8.GetParty
import org.junit.jupiter.api.Test

class GetPartyTest(val tppResource: CreateTppCallback.TppResource) {
    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.9",
        operations = ["CreateAccountAccessConsent", "GetParty"],
        apis = ["party"]
    )
    @Test
    fun shouldGetParty_v3_1_9() {
        GetParty(OBVersion.v3_1_9, tppResource).shouldGetPartyTest()
    }
}