package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.standing.orders.junit.v3_1_10

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.standing.orders.api.v3_1_8.GetAccountStandingOrders
import org.junit.jupiter.api.Test

class GetAccountStandingOrdersTest(val tppResource: CreateTppCallback.TppResource) {
    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.10",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountStandingOrders"],
        apis = ["standing-orders"]
    )
    @Test
    fun shouldGetAccountStandingOrders_v3_1_10() {
        GetAccountStandingOrders(OBVersion.v3_1_10, tppResource).shouldGetAccountStandingOrdersTest()
    }
}