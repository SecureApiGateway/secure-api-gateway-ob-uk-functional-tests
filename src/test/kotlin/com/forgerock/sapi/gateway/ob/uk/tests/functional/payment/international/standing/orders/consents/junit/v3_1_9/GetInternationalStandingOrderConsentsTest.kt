package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.standing.orders.consents.junit.v3_1_9

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.standing.orders.consents.api.v3_1_8.GetInternationalStandingOrderConsents
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetInternationalStandingOrderConsentsTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getInternationalStandingOrderConsents: GetInternationalStandingOrderConsents

    @BeforeEach
    fun setUp() {
        getInternationalStandingOrderConsents = GetInternationalStandingOrderConsents(OBVersion.v3_1_9, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateInternationalStandingOrder", "CreateInternationalStandingOrderConsent", "GetInternationalStandingOrderConsent"],
        apis = ["international-standing-orders", "international-standing-order-consents"],
        compatibleVersions = ["v.3.1.8", "v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun createInternationalStandingOrder_v3_1_9() {
        getInternationalStandingOrderConsents.shouldGetInternationalStandingOrdersConsents_Test()
    }
}
