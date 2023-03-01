package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.products.junit.v3_1_10

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.products.api.v3_1_8.GetProducts
import org.junit.jupiter.api.Test

class GetProductsTest(val tppResource: CreateTppCallback.TppResource) {
    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.10",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetProducts"],
        apis = ["products"]
    )
    @Test
    fun shouldGetProducts_v3_1_10() {
        GetProducts(OBVersion.v3_1_10, tppResource).shouldGetProductsTest()
    }
}