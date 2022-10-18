package com.forgerock.uk.openbanking.tests.functional.payment.international.standing.orders.junit.v3_1_9

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.payment.international.standing.orders.api.v3_1_8.GetInternationalStandingOrder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class GetInternationalStandingOrderTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getInternationalStandingOrder: GetInternationalStandingOrder

    @BeforeEach
    fun setUp() {
        getInternationalStandingOrder = GetInternationalStandingOrder(OBVersion.v3_1_9, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["GetInternationalStandingOrder", "CreateInternationalStandingOrder", "CreateInternationalStandingOrderConsent", "GetInternationalStandingOrderConsent"],
        apis = ["international-standing-orders", "international-standing-order-consents"]
    )
    @Test
    fun getInternationalStandingOrders_v3_1_9() {
        getInternationalStandingOrder.getInternationalStandingOrdersTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["GetInternationalStandingOrder", "CreateInternationalStandingOrder", "CreateInternationalStandingOrderConsent", "GetInternationalStandingOrderConsent"],
        apis = ["international-standing-orders", "international-standing-order-consents"]
    )
    @Test
    fun getInternationalStandingOrders_mandatoryFields_v3_1_9() {
        getInternationalStandingOrder.getInternationalStandingOrders_mandatoryFieldsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["GetInternationalStandingOrder", "CreateInternationalStandingOrder", "CreateInternationalStandingOrderConsent", "GetInternationalStandingOrderConsent"],
        apis = ["international-standing-orders", "international-standing-order-consents"]
    )
    @Disabled
    @Test
    fun shouldGetInternationalStandingOrders_withReadRefund_v3_1_9() {
        getInternationalStandingOrder.shouldGetInternationalStandingOrders_withReadRefundTest()
    }

}