package com.forgerock.uk.openbanking.tests.functional.payment.international.standing.orders.consents.junit.v3_1_8

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.payment.international.standing.orders.consents.api.v3_1_8.GetInternationalStandingOrderConsents
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetInternationalStandingOrderConsentsTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getInternationalStandingOrderConsents: GetInternationalStandingOrderConsents

    @BeforeEach
    fun setUp() {
        getInternationalStandingOrderConsents = GetInternationalStandingOrderConsents(OBVersion.v3_1_8, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateInternationalStandingOrder", "CreateInternationalStandingOrderConsent", "GetInternationalStandingOrderConsent"],
        apis = ["international-standing-orders", "international-standing-order-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun createInternationalStandingOrder_v3_1_8() {
        getInternationalStandingOrderConsents.shouldGetInternationalStandingOrdersConsents_Test()
    }
}
