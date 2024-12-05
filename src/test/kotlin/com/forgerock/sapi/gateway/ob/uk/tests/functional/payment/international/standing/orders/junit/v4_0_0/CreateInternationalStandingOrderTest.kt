package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.standing.orders.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.standing.orders.api.v4_0_0.CreateInternationalStandingOrder
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class CreateInternationalStandingOrderTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var createInternationalStandingOrder: CreateInternationalStandingOrder

    @BeforeEach
    fun setUp() {
        createInternationalStandingOrder = CreateInternationalStandingOrder(OBVersion.v4_0_0, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalStandingOrder", "CreateInternationalStandingOrderConsent", "GetInternationalStandingOrderConsent"],
        apis = ["international-standing-orders", "international-standing-order-consents"]
    )
    @Test
    fun createInternationalStandingOrder_v4_0_0() {
        createInternationalStandingOrder.createInternationalStandingOrderTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalStandingOrder", "CreateInternationalStandingOrderConsent", "GetInternationalStandingOrderConsent"],
        apis = ["international-standing-orders", "international-standing-order-consents"]
    )
    @Test
    fun createInternationalStandingOrder_throwsInvalidInitiation_v4_0_0() {
        createInternationalStandingOrder.shouldCreateInternationalStandingOrder_throwsInvalidInitiationTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalStandingOrder", "CreateInternationalStandingOrderConsent", "GetInternationalStandingOrderConsent"],
        apis = ["international-standing-orders", "international-standing-order-consents"]
    )
    @Test
    fun createInternationalStandingOrder_WithDebtorAccount_v4_0_0() {
        createInternationalStandingOrder.createInternationalStandingOrderWithDebtorAccountTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalStandingOrder", "CreateInternationalStandingOrderConsent", "GetInternationalStandingOrderConsent"],
        apis = ["international-standing-orders", "international-standing-order-consents"]
    )
    @Test
    fun createInternationalStandingOrder_mandatoryFields_v4_0_0() {
        createInternationalStandingOrder.createInternationalStandingOrder_mandatoryFieldsTest()
    }

    @Disabled("Bug: https://github.com/SecureBankingAccessToolkit/SecureBankingAccessToolkit/issues/336")
    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalStandingOrder", "CreateInternationalStandingOrderConsent", "GetInternationalStandingOrderConsent"],
        apis = ["international-standing-orders", "international-standing-order-consents"]
    )
    @Test
    fun shouldCreateInternationalStandingOrder_throwsStandingOrderAlreadyExists_v4_0_0() {
        createInternationalStandingOrder.shouldCreateInternationalStandingOrder_throwsStandingOrderAlreadyExistsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalStandingOrder", "CreateInternationalStandingOrderConsent", "GetInternationalStandingOrderConsent"],
        apis = ["international-standing-orders", "international-standing-order-consents"]
    )
    @Test
    fun shouldCreateInternationalStandingOrder_throwsSendInvalidFormatDetachedJws_v4_0_0() {
        createInternationalStandingOrder.shouldCreateInternationalStandingOrder_throwsSendInvalidFormatDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalStandingOrder", "CreateInternationalStandingOrderConsent", "GetInternationalStandingOrderConsent"],
        apis = ["international-standing-orders", "international-standing-order-consents"]
    )
    @Test
    fun shouldCreateInternationalStandingOrder_throwsNoDetachedJws_v4_0_0() {
        createInternationalStandingOrder.shouldCreateInternationalStandingOrder_throwsNoDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalStandingOrder", "CreateInternationalStandingOrderConsent", "GetInternationalStandingOrderConsent"],
        apis = ["international-standing-orders", "international-standing-order-consents"]
    )
    @Test
    fun shouldCreateInternationalStandingOrder_throwsNotPermittedB64HeaderAddedInTheDetachedJws_v4_0_0() {
        createInternationalStandingOrder.shouldCreateInternationalStandingOrder_throwsNotPermittedB64HeaderAddedInTheDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalStandingOrder", "CreateInternationalStandingOrderConsent", "GetInternationalStandingOrderConsent"],
        apis = ["international-standing-orders", "international-standing-order-consents"]
    )
    @Test
    fun shouldCreateInternationalStandingOrder_throwsSendInvalidKidDetachedJws_v4_0_0() {
        createInternationalStandingOrder.shouldCreateInternationalStandingOrder_throwsSendInvalidKidDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalStandingOrder", "CreateInternationalStandingOrderConsent", "GetInternationalStandingOrderConsent"],
        apis = ["international-standing-orders", "international-standing-order-consents"]
    )
    @Test
    fun shouldCreateInternationalStandingOrder_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBody_v4_0_0() {
        createInternationalStandingOrder.shouldCreateInternationalStandingOrder_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBodyTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalStandingOrder", "CreateInternationalStandingOrderConsent", "GetInternationalStandingOrderConsent"],
        apis = ["international-standing-orders", "international-standing-order-consents"]
    )
    @Test
    fun shouldCreateInternationalStandingOrder_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBody_v4_0_0() {
        createInternationalStandingOrder.shouldCreateInternationalStandingOrder_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBodyTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalStandingOrder", "CreateInternationalStandingOrderConsent", "GetInternationalStandingOrderConsent"],
        apis = ["international-standing-orders", "international-standing-order-consents"]
    )
    @Test
    fun shouldCreateInternationalStandingOrder_throwsInvalidRiskTest_v4_0_0() {
        createInternationalStandingOrder.shouldCreateInternationalStandingOrder_throwsInvalidRiskTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalStandingOrder", "CreateInternationalStandingOrderConsent", "GetInternationalStandingOrderConsent"],
        apis = ["international-standing-orders", "international-standing-order-consents"]
    )
    @Test
    fun testCreatingPaymentIsIdempotent_v4_0_0() {
        createInternationalStandingOrder.testCreatingPaymentIsIdempotent()
    }
}