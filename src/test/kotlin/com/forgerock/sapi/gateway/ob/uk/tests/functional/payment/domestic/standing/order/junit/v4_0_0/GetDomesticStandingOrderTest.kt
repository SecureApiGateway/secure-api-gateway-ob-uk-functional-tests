package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.standing.order.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.standing.order.api.v4_0_0.GetDomesticStandingOrder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class GetDomesticStandingOrderTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getDomesticStandingOrder: GetDomesticStandingOrder

    @BeforeEach
    fun setUp() {
        getDomesticStandingOrder = GetDomesticStandingOrder(OBVersion.v4_0_0, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["GetDomesticStandingOrder", "CreateDomesticStandingOrder", "CreateDomesticStandingOrderConsent", "GetDomesticStandingOrderConsent"],
        apis = ["domestic-standing-orders", "domestic-standing-order-consents"]
    )
    @Test
    fun getDomesticStandingOrders_v4_0_0() {
        getDomesticStandingOrder.getDomesticStandingOrdersTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["GetDomesticStandingOrder", "CreateDomesticStandingOrder", "CreateDomesticStandingOrderConsent", "GetDomesticStandingOrderConsent"],
        apis = ["domestic-standing-orders", "domestic-standing-order-consents"]
    )
    @Test
    fun getDomesticStandingOrders_mandatoryFields_v4_0_0() {
        getDomesticStandingOrder.getDomesticStandingOrders_mandatoryFieldsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["GetDomesticStandingOrder", "CreateDomesticStandingOrder", "CreateDomesticStandingOrderConsent", "GetDomesticStandingOrderConsent"],
        apis = ["domestic-standing-orders", "domestic-standing-order-consents"]
    )
    @Test
    fun shouldGetDomesticStandingOrders_withReadRefund_v4_0_0() {
        getDomesticStandingOrder.shouldGetDomesticStandingOrders_withReadRefundTest()
    }

}