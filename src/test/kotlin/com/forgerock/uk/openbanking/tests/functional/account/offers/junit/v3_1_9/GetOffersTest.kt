package com.forgerock.uk.openbanking.tests.functional.account.offers.junit.v3_1_9

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.account.offers.api.v3_1_8.GetOffers
import org.junit.jupiter.api.Test

class GetOffersTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.9",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetOffers"],
        apis = ["offers"]
    )
    @Test
    fun shouldGetOffers_v3_1_9() {
        GetOffers(OBVersion.v3_1_9, tppResource).shouldGetOffersTest()
    }
}