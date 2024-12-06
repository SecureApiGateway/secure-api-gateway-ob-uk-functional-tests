package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.products.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.products.api.v4_0_0.GetAccountProduct
import org.junit.jupiter.api.Test

class GetAccountProductTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v4.0.0",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountProduct"],
        apis = ["products"]
    )
    @Test
    fun shouldGetAccountProduct_v4_0_0() {
        GetAccountProduct(OBVersion.v4_0_0, tppResource).shouldGetAccountProductTest()
    }
}