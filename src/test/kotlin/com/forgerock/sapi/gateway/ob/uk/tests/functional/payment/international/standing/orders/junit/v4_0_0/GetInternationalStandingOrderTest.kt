package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.standing.orders.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.standing.orders.api.v4_0_0.GetInternationalStandingOrder
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetInternationalStandingOrderTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getInternationalStandingOrder: GetInternationalStandingOrder

    @BeforeEach
    fun setUp() {
        getInternationalStandingOrder = GetInternationalStandingOrder(OBVersion.v4_0_0, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["GetInternationalStandingOrder", "CreateInternationalStandingOrder", "CreateInternationalStandingOrderConsent", "GetInternationalStandingOrderConsent"],
        apis = ["international-standing-orders", "international-standing-order-consents"]
    )
    @Test
    fun getInternationalStandingOrders_v4_0_0() {
        getInternationalStandingOrder.getInternationalStandingOrdersTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["GetInternationalStandingOrder", "CreateInternationalStandingOrder", "CreateInternationalStandingOrderConsent", "GetInternationalStandingOrderConsent"],
        apis = ["international-standing-orders", "international-standing-order-consents"]
    )
    @Test
    fun getInternationalStandingOrders_mandatoryFields_v4_0_0() {
        getInternationalStandingOrder.getInternationalStandingOrders_mandatoryFieldsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["GetInternationalStandingOrder", "CreateInternationalStandingOrder", "CreateInternationalStandingOrderConsent", "GetInternationalStandingOrderConsent"],
        apis = ["international-standing-orders", "international-standing-order-consents"]
    )
    @Test
    fun shouldGetInternationalStandingOrders_withReadRefund_v4_0_0() {
        getInternationalStandingOrder.shouldGetInternationalStandingOrders_withReadRefundTest()
    }

}