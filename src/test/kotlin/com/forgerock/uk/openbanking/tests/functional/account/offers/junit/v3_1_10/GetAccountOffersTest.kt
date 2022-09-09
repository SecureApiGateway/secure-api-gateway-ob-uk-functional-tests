package com.forgerock.uk.openbanking.tests.functional.account.offers.junit.v3_1_10

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.account.offers.api.v3_1_8.GetAccountOffers
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