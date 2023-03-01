package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.offers.junit.v3_1_10

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.offers.api.v3_1_8.GetAccountOffers
import org.junit.jupiter.api.Test

class GetAccountOffersTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.10",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountOffers"],
        apis = ["offers"]
    )
    @Test
    fun shouldGetAccountOffers_v3_1_10() {
        GetAccountOffers(OBVersion.v3_1_10, tppResource).shouldGetAccountOffersTest()
    }
}