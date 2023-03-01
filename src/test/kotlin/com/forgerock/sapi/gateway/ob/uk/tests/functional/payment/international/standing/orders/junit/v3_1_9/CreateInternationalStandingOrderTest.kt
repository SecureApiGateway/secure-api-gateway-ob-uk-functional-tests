package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.standing.orders.junit.v3_1_9

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.standing.orders.api.v3_1_8.CreateInternationalStandingOrder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class CreateInternationalStandingOrderTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var createInternationalStandingOrder: CreateInternationalStandingOrder

    @BeforeEach
    fun setUp() {
        createInternationalStandingOrder = CreateInternationalStandingOrder(OBVersion.v3_1_9, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateInternationalStandingOrder", "CreateInternationalStandingOrderConsent", "GetInternationalStandingOrderConsent"],
        apis = ["international-standing-orders", "international-standing-order-consents"]
    )
    @Test
    fun createInternationalStandingOrder_v3_1_9() {
        createInternationalStandingOrder.createInternationalStandingOrderTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateInternationalStandingOrder", "CreateInternationalStandingOrderConsent", "GetInternationalStandingOrderConsent"],
        apis = ["international-standing-orders", "international-standing-order-consents"]
    )
    @Test
    fun createInternationalStandingOrder_mandatoryFields_v3_1_9() {
        createInternationalStandingOrder.createInternationalStandingOrder_mandatoryFieldsTest()
    }

    @Disabled("Bug: https://github.com/SecureBankingAccessToolkit/SecureBankingAccessToolkit/issues/336")
    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateInternationalStandingOrder", "CreateInternationalStandingOrderConsent", "GetInternationalStandingOrderConsent"],
        apis = ["international-standing-orders", "international-standing-order-consents"]
    )
    @Test
    fun shouldCreateInternationalStandingOrder_throwsStandingOrderAlreadyExists_v3_1_9() {
        createInternationalStandingOrder.shouldCreateInternationalStandingOrder_throwsStandingOrderAlreadyExistsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateInternationalStandingOrder", "CreateInternationalStandingOrderConsent", "GetInternationalStandingOrderConsent"],
        apis = ["international-standing-orders", "international-standing-order-consents"]
    )
    @Test
    fun shouldCreateInternationalStandingOrder_throwsSendInvalidFormatDetachedJws_v3_1_9() {
        createInternationalStandingOrder.shouldCreateInternationalStandingOrder_throwsSendInvalidFormatDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateInternationalStandingOrder", "CreateInternationalStandingOrderConsent", "GetInternationalStandingOrderConsent"],
        apis = ["international-standing-orders", "international-standing-order-consents"]
    )
    @Test
    fun shouldCreateInternationalStandingOrder_throwsNoDetachedJws_v3_1_9() {
        createInternationalStandingOrder.shouldCreateInternationalStandingOrder_throwsNoDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateInternationalStandingOrder", "CreateInternationalStandingOrderConsent", "GetInternationalStandingOrderConsent"],
        apis = ["international-standing-orders", "international-standing-order-consents"]
    )
    @Test
    fun shouldCreateInternationalStandingOrder_throwsNotPermittedB64HeaderAddedInTheDetachedJws_v3_1_9() {
        createInternationalStandingOrder.shouldCreateInternationalStandingOrder_throwsNotPermittedB64HeaderAddedInTheDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateInternationalStandingOrder", "CreateInternationalStandingOrderConsent", "GetInternationalStandingOrderConsent"],
        apis = ["international-standing-orders", "international-standing-order-consents"]
    )
    @Test
    fun shouldCreateInternationalStandingOrder_throwsSendInvalidKidDetachedJws_v3_1_9() {
        createInternationalStandingOrder.shouldCreateInternationalStandingOrder_throwsSendInvalidKidDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateInternationalStandingOrder", "CreateInternationalStandingOrderConsent", "GetInternationalStandingOrderConsent"],
        apis = ["international-standing-orders", "international-standing-order-consents"]
    )
    @Test
    fun shouldCreateInternationalStandingOrder_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBody_v3_1_9() {
        createInternationalStandingOrder.shouldCreateInternationalStandingOrder_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBodyTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateInternationalStandingOrder", "CreateInternationalStandingOrderConsent", "GetInternationalStandingOrderConsent"],
        apis = ["international-standing-orders", "international-standing-order-consents"]
    )
    @Test
    fun shouldCreateInternationalStandingOrder_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBody_v3_1_9() {
        createInternationalStandingOrder.shouldCreateInternationalStandingOrder_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBodyTest()
    }
}