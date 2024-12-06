package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.standing.orders.consents.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.standing.orders.consents.api.v4_0_0.GetInternationalStandingOrderConsents
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetInternationalStandingOrderConsentsTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getInternationalStandingOrderConsents: GetInternationalStandingOrderConsents

    @BeforeEach
    fun setUp() {
        getInternationalStandingOrderConsents = GetInternationalStandingOrderConsents(OBVersion.v4_0_0, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalStandingOrder", "CreateInternationalStandingOrderConsent", "GetInternationalStandingOrderConsent"],
        apis = ["international-standing-orders", "international-standing-order-consents"]
    )
    @Test
    fun createInternationalStandingOrder_v4_0_0() {
        getInternationalStandingOrderConsents.shouldGetInternationalStandingOrdersConsents_Test()
    }
}
