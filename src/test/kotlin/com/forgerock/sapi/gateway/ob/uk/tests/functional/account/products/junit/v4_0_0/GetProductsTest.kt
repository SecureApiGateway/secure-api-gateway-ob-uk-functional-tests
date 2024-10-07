package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.products.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.products.api.v4_0_0.GetProducts
import org.junit.jupiter.api.Test

class GetProductsTest(val tppResource: CreateTppCallback.TppResource) {
    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v4.0.0",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetProducts"],
        apis = ["products"]
    )
    @Test
    fun shouldGetProducts_v4_0_0() {
        GetProducts(OBVersion.v4_0_0, tppResource).shouldGetProductsTest()
    }
}