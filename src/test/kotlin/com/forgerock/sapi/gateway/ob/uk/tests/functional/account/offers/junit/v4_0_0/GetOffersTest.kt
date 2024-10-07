package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.offers.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.offers.api.v4_0_0.GetOffers
import org.junit.jupiter.api.Test

class GetOffersTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v4.0.0",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetOffers"],
        apis = ["offers"]
    )
    @Test
    fun shouldGetOffers_v4_0_0() {
        GetOffers(OBVersion.v4_0_0, tppResource).shouldGetOffersTest()
    }
}