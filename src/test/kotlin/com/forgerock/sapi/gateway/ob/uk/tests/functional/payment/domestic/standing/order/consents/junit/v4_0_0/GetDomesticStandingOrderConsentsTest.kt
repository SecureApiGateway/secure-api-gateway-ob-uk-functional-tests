package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.standing.order.consents.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.standing.order.consents.api.v4_0_0.GetDomesticStandingOrderConsents
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetDomesticStandingOrderConsentsTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getDomesticStandingOrderConsents: GetDomesticStandingOrderConsents

    @BeforeEach
    fun setUp() {
        getDomesticStandingOrderConsents = GetDomesticStandingOrderConsents(OBVersion.v4_0_0, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticStandingOrder", "CreateDomesticStandingOrderConsent", "GetDomesticStandingOrderConsent"],
        apis = ["domestic-standing-orders", "domestic-standing-order-consents"]
    )
    @Test
    fun createDomesticStandingOrder_v4_0_0() {
        getDomesticStandingOrderConsents.shouldGetDomesticStandingOrdersConsents_Test()
    }
}