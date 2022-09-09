package com.forgerock.uk.openbanking.tests.functional.account.parties.junit.v3_1_10

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.account.parties.api.v3_1_8.GetAccountParty
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