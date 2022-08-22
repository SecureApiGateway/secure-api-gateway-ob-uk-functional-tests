package com.forgerock.uk.openbanking.tests.functional.account.standing.orders.junit.v3_1_9

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.account.standing.orders.api.v3_1_8.GetStandingOrders
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