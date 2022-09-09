package com.forgerock.uk.openbanking.tests.functional.account.standing.orders.junit.v3_1_10

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.account.standing.orders.api.v3_1_8.GetAccountStandingOrders
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