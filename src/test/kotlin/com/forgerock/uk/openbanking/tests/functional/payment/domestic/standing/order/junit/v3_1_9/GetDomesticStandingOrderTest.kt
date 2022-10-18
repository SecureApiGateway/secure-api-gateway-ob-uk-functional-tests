package com.forgerock.uk.openbanking.tests.functional.payment.domestic.standing.order.junit.v3_1_9

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.payment.domestic.standing.order.api.v3_1_8.GetDomesticStandingOrder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class GetDomesticStandingOrderTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getDomesticStandingOrder: GetDomesticStandingOrder

    @BeforeEach
    fun setUp() {
        getDomesticStandingOrder = GetDomesticStandingOrder(OBVersion.v3_1_9, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["GetDomesticStandingOrder", "CreateDomesticStandingOrder", "CreateDomesticStandingOrderConsent", "GetDomesticStandingOrderConsent"],
        apis = ["domestic-standing-orders", "domestic-standing-order-consents"]
    )
    @Test
    fun getDomesticStandingOrders_v3_1_9() {
        getDomesticStandingOrder.getDomesticStandingOrdersTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["GetDomesticStandingOrder", "CreateDomesticStandingOrder", "CreateDomesticStandingOrderConsent", "GetDomesticStandingOrderConsent"],
        apis = ["domestic-standing-orders", "domestic-standing-order-consents"]
    )
    @Test
    fun getDomesticStandingOrders_mandatoryFields_v3_1_9() {
        getDomesticStandingOrder.getDomesticStandingOrders_mandatoryFieldsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["GetDomesticStandingOrder", "CreateDomesticStandingOrder", "CreateDomesticStandingOrderConsent", "GetDomesticStandingOrderConsent"],
        apis = ["domestic-standing-orders", "domestic-standing-order-consents"]
    )
    @Disabled
    @Test
    fun shouldGetDomesticStandingOrders_withReadRefund_v3_1_9() {
        getDomesticStandingOrder.shouldGetDomesticStandingOrders_withReadRefundTest()
    }

}