package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.standing.order.junit.v3_1_10

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.standing.order.api.v3_1_8.CreateDomesticStandingOrder
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class CreateDomesticStandingOrderTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var createDomesticStandingOrder: CreateDomesticStandingOrder

    @BeforeEach
    fun setUp() {
        createDomesticStandingOrder = CreateDomesticStandingOrder(OBVersion.v3_1_10, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticStandingOrder", "CreateDomesticStandingOrderConsent", "GetDomesticStandingOrderConsent"],
        apis = ["domestic-standing-orders", "domestic-standing-order-consents"]
    )
    @Test
    fun createDomesticStandingOrder_v3_1_10() {
        createDomesticStandingOrder.createDomesticStandingOrderTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticStandingOrder", "CreateDomesticStandingOrderConsent", "GetDomesticStandingOrderConsent"],
        apis = ["domestic-standing-orders", "domestic-standing-order-consents"]
    )
    @Test
    fun createDomesticStandingOrder_throwsInvalidInitiation_v3_1_10() {
        createDomesticStandingOrder.shouldCreateDomesticStandingOrder_throwsInvalidInitiationTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticStandingOrder", "CreateDomesticStandingOrderConsent", "GetDomesticStandingOrderConsent"],
        apis = ["domestic-standing-orders", "domestic-standing-order-consents"]
    )
    @Test
    fun createDomesticStandingOrder_withDebtorAccount_v3_1_10() {
        createDomesticStandingOrder.createDomesticStandingOrderWithDebtorAccountTest()
    }


    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticStandingOrder", "CreateDomesticStandingOrderConsent", "GetDomesticStandingOrderConsent"],
        apis = ["domestic-standing-orders", "domestic-standing-order-consents"]
    )
    @Test
    fun createDomesticStandingOrder_mandatoryFields_v3_1_10() {
        createDomesticStandingOrder.createDomesticStandingOrder_mandatoryFieldsTest()
    }

    @Disabled("Bug: https://github.com/SecureBankingAccessToolkit/SecureBankingAccessToolkit/issues/336")
    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticStandingOrder", "CreateDomesticStandingOrderConsent", "GetDomesticStandingOrderConsent"],
        apis = ["domestic-standing-orders", "domestic-standing-order-consents"]
    )
    @Test
    fun shouldCreateDomesticStandingOrder_throwsStandingOrderAlreadyExists_v3_1_10() {
        createDomesticStandingOrder.shouldCreateDomesticStandingOrder_throwsStandingOrderAlreadyExistsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticStandingOrder", "CreateDomesticStandingOrderConsent", "GetDomesticStandingOrderConsent"],
        apis = ["domestic-standing-orders", "domestic-standing-order-consents"]
    )
    @Test
    fun shouldCreateDomesticStandingOrder_throwsSendInvalidFormatDetachedJws_v3_1_10() {
        createDomesticStandingOrder.shouldCreateDomesticStandingOrder_throwsSendInvalidFormatDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticStandingOrder", "CreateDomesticStandingOrderConsent", "GetDomesticStandingOrderConsent"],
        apis = ["domestic-standing-orders", "domestic-standing-order-consents"]
    )
    @Test
    fun shouldCreateDomesticStandingOrder_throwsNoDetachedJws_v3_1_10() {
        createDomesticStandingOrder.shouldCreateDomesticStandingOrder_throwsNoDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticStandingOrder", "CreateDomesticStandingOrderConsent", "GetDomesticStandingOrderConsent"],
        apis = ["domestic-standing-orders", "domestic-standing-order-consents"]
    )
    @Test
    fun shouldCreateDomesticStandingOrder_throwsNotPermittedB64HeaderAddedInTheDetachedJws_v3_1_10() {
        createDomesticStandingOrder.shouldCreateDomesticStandingOrder_throwsNotPermittedB64HeaderAddedInTheDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticStandingOrder", "CreateDomesticStandingOrderConsent", "GetDomesticStandingOrderConsent"],
        apis = ["domestic-standing-orders", "domestic-standing-order-consents"]
    )
    @Test
    fun shouldCreateDomesticStandingOrder_throwsSendInvalidKidDetachedJws_v3_1_10() {
        createDomesticStandingOrder.shouldCreateDomesticStandingOrder_throwsSendInvalidKidDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticStandingOrder", "CreateDomesticStandingOrderConsent", "GetDomesticStandingOrderConsent"],
        apis = ["domestic-standing-orders", "domestic-standing-order-consents"]
    )
    @Test
    fun shouldCreateDomesticStandingOrder_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBody_v3_1_10() {
        createDomesticStandingOrder.shouldCreateDomesticStandingOrder_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBodyTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticStandingOrder", "CreateDomesticStandingOrderConsent", "GetDomesticStandingOrderConsent"],
        apis = ["domestic-standing-orders", "domestic-standing-order-consents"]
    )
    @Test
    fun shouldCreateDomesticStandingOrder_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBody_v3_1_10() {
        createDomesticStandingOrder.shouldCreateDomesticStandingOrder_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBodyTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticStandingOrder", "CreateDomesticStandingOrderConsent", "GetDomesticStandingOrderConsent"],
        apis = ["domestic-standing-orders", "domestic-standing-order-consents"]
    )
    @Test
    fun shouldCreateDomesticStandingOrder_throwsInvalidRiskTest_v3_1_10() {
        createDomesticStandingOrder.shouldCreateDomesticStandingOrder_throwsInvalidRiskTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticStandingOrder", "CreateDomesticStandingOrderConsent", "GetDomesticStandingOrderConsent"],
        apis = ["domestic-standing-orders", "domestic-standing-order-consents"]
    )
    @Test
    fun testCreatingPaymentIsIdempotent_v3_1_10() {
        createDomesticStandingOrder.testCreatingPaymentIsIdempotent()
    }
}