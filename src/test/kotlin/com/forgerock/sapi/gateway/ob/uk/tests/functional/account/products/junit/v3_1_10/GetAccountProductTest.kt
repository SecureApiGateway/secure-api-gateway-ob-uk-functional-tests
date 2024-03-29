package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.products.junit.v3_1_10

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.products.api.v3_1_8.GetAccountProduct
import org.junit.jupiter.api.Test

class GetAccountProductTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.10",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountProduct"],
        apis = ["products"]
    )
    @Test
    fun shouldGetAccountProduct_v3_1_10() {
        GetAccountProduct(OBVersion.v3_1_10, tppResource).shouldGetAccountProductTest()
    }
}