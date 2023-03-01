package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.standing.orders.junit.v3_1_9

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.standing.orders.api.v3_1_8.GetStandingOrders
import org.junit.jupiter.api.Test

class GetStandingOrdersTest(val tppResource: CreateTppCallback.TppResource) {
    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.9",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetStandingOrders"],
        apis = ["standing-orders"]
    )
    @Test
    fun shouldGetStandingOrders_v3_1_9() {
        GetStandingOrders(OBVersion.v3_1_9, tppResource).shouldGetStandingOrdersTest()
    }
}