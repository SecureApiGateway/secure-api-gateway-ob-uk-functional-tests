package com.forgerock.uk.openbanking.tests.functional.payment.domestic.standing.order.consents.junit.v3_1_9

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.payment.domestic.standing.order.consents.api.v3_1_8.GetDomesticStandingOrderConsents
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetDomesticStandingOrderConsentsTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getDomesticStandingOrderConsents: GetDomesticStandingOrderConsents

    @BeforeEach
    fun setUp() {
        getDomesticStandingOrderConsents = GetDomesticStandingOrderConsents(OBVersion.v3_1_9, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateDomesticStandingOrder", "CreateDomesticStandingOrderConsent", "GetDomesticStandingOrderConsent"],
        apis = ["domestic-standing-orders", "domestic-standing-order-consents"]
    )
    @Test
    fun createDomesticStandingOrder_v3_1_9() {
        getDomesticStandingOrderConsents.shouldGetDomesticStandingOrdersConsents_Test()
    }
}