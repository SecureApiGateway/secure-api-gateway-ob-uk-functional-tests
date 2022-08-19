package com.forgerock.uk.openbanking.tests.functional.account.standing.orders.junit.v3_1_8

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.account.standing.orders.api.v3_1_8.GetAccountStandingOrders
import org.junit.jupiter.api.Test

class GetAccountStandingOrdersTest(val tppResource: CreateTppCallback.TppResource) {
    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.8",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountStandingOrders"],
        apis = ["standing-orders"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldGetAccountStandingOrders_v3_1_8() {
        GetAccountStandingOrders(OBVersion.v3_1_8, tppResource).shouldGetAccountStandingOrdersTest()
    }
}