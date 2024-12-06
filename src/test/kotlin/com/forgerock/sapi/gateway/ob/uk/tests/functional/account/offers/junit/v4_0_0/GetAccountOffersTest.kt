package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.offers.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.offers.api.v4_0_0.GetAccountOffers
import org.junit.jupiter.api.Test

class GetAccountOffersTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v4.0.0",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountOffers"],
        apis = ["offers"]
    )
    @Test
    fun shouldGetAccountOffers_v4_0_0() {
        GetAccountOffers(OBVersion.v4_0_0, tppResource).shouldGetAccountOffersTest()
    }
}