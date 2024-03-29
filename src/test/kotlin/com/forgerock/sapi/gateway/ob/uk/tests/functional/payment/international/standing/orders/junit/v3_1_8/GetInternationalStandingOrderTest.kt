package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.standing.orders.junit.v3_1_8

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.standing.orders.api.v3_1_8.GetInternationalStandingOrder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class GetInternationalStandingOrderTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getInternationalStandingOrder: GetInternationalStandingOrder

    @BeforeEach
    fun setUp() {
        getInternationalStandingOrder = GetInternationalStandingOrder(OBVersion.v3_1_8, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["GetInternationalStandingOrder", "CreateInternationalStandingOrder", "CreateInternationalStandingOrderConsent", "GetInternationalStandingOrderConsent"],
        apis = ["international-standing-orders", "international-standing-order-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun getInternationalStandingOrders_v3_1_8() {
        getInternationalStandingOrder.getInternationalStandingOrdersTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["GetInternationalStandingOrder", "CreateInternationalStandingOrder", "CreateInternationalStandingOrderConsent", "GetInternationalStandingOrderConsent"],
        apis = ["international-standing-orders", "international-standing-order-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun getInternationalStandingOrders_mandatoryFields_v3_1_8() {
        getInternationalStandingOrder.getInternationalStandingOrders_mandatoryFieldsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["GetInternationalStandingOrder", "CreateInternationalStandingOrder", "CreateInternationalStandingOrderConsent", "GetInternationalStandingOrderConsent"],
        apis = ["international-standing-orders", "international-standing-order-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldGetInternationalStandingOrders_withReadRefund_v3_1_8() {
        getInternationalStandingOrder.shouldGetInternationalStandingOrders_withReadRefundTest()
    }

}