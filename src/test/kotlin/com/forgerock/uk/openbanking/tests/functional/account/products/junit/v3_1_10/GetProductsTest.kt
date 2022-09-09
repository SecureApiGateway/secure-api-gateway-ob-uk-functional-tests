package com.forgerock.uk.openbanking.tests.functional.account.products.junit.v3_1_10

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.account.products.api.v3_1_8.GetProducts
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