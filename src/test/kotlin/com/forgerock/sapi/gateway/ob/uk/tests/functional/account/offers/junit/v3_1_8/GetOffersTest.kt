package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.offers.junit.v3_1_8

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.offers.api.v3_1_8.GetOffers
import org.junit.jupiter.api.Test

class GetOffersTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.8",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetOffers"],
        apis = ["offers"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5", "v.3.1.4", "v.3.1.3"]
    )
    @Test
    fun shouldGetOffers_v3_1_8() {
        GetOffers(OBVersion.v3_1_8, tppResource).shouldGetOffersTest()
    }
}