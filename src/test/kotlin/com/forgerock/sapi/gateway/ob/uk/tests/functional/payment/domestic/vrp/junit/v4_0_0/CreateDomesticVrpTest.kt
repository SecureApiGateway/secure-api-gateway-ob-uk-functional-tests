package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.vrp.junit.v4_0_0


import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.vrp.api.v4_0_0.CreateDomesticVrp
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CreateDomesticVrpTest(val tppResource: CreateTppCallback.TppResource) {

    private lateinit var createDomesticVrpPayment: CreateDomesticVrp

    @BeforeEach
    fun setUp() {
        createDomesticVrpPayment = CreateDomesticVrp(OBVersion.v4_0_0, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticVRPPayment", "CreateDomesticVRPConsent", "GetDomesticVRPConsent"],
        apis = ["domestic-vrps", "domestic-vrp-consents"]
    )
    @Test
    fun createDomesticVrpPayment_v4_0_0() {
        createDomesticVrpPayment.createDomesticVrpPaymentTest()
    }

    @EnabledIfVersion(
            type = "payments",
            apiVersion = "v4.0.0",
            operations = ["CreateDomesticVRPPayment", "CreateDomesticVRPConsent", "GetDomesticVRPConsent"],
            apis = ["domestic-vrps", "domestic-vrp-consents"]
    )
    @Test
    fun createDomesticVrpPaymentWithDebtorAccount_v4_0_0() {
        createDomesticVrpPayment.createDomesticVrpPaymentWithDebtorAccountTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticVRPPayment", "CreateDomesticVRPConsent", "GetDomesticVRPConsent"],
        apis = ["domestic-vrps", "domestic-vrp-consents"]
    )
    @Test
    fun createDomesticVrpPayment_v4_0_0_limitBreachSimulation() {
        createDomesticVrpPayment.limitBreachSimulationDomesticVrpPaymentTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticVRPPayment", "CreateDomesticVRPConsent", "GetDomesticVRPConsent"],
        apis = ["domestic-vrps", "domestic-vrp-consents"]
    )
    @Test
    fun createDomesticVrpPayment_mandatoryFields_v4_0_0() {
        createDomesticVrpPayment.createDomesticVrpPayment_mandatoryFieldsTest()
    }

     @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticVRPPayment", "CreateDomesticVRPConsent", "GetDomesticVRPConsent"],
        apis = ["domestic-vrps", "domestic-vrp-consents"]
    )
    @Test
    fun shouldCreateDomesticVrp_throwsSendInvalidFormatDetachedJws_v4_0_0() {
        createDomesticVrpPayment.shouldCreateDomesticVrp_throwsSendInvalidFormatDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticVRPPayment", "CreateDomesticVRPConsent", "GetDomesticVRPConsent"],
        apis = ["domestic-vrps", "domestic-vrp-consents"]
    )
    @Test
    fun shouldCreateDomesticVrp_throwsNoDetachedJws_v4_0_0() {
        createDomesticVrpPayment.shouldCreateDomesticVrp_throwsNoDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticVRPPayment", "CreateDomesticVRPConsent", "GetDomesticVRPConsent"],
        apis = ["domestic-vrps", "domestic-vrp-consents"]
    )
    @Test
    fun shouldCreateDomesticVrp_throwsNotPermittedB64HeaderAddedInTheDetachedJws_v4_0_0() {
        createDomesticVrpPayment.shouldCreateDomesticVrp_throwsNotPermittedB64HeaderAddedInTheDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticVRPPayment", "CreateDomesticVRPConsent", "GetDomesticVRPConsent"],
        apis = ["domestic-vrps", "domestic-vrp-consents"]
    )
    @Test
    fun shouldCreateDomesticVrp_throwsSendInvalidKidDetachedJws_v4_0_0() {
        createDomesticVrpPayment.shouldCreateDomesticVrp_throwsSendInvalidKidDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticVRPPayment", "CreateDomesticVRPConsent", "GetDomesticVRPConsent"],
        apis = ["domestic-vrps", "domestic-vrp-consents"]
    )
    @Test
    fun shouldCreateDomesticVrp_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBody_v4_0_0() {
        createDomesticVrpPayment.shouldCreateDomesticVrp_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBodyTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticVRPPayment", "CreateDomesticVRPConsent", "GetDomesticVRPConsent"],
        apis = ["domestic-vrps", "domestic-vrp-consents"]
    )
    @Test
    fun shouldCreateDomesticVrp_throwsBadRequestWhenNotSweepingVrpType_v4_0_0() {
        createDomesticVrpPayment.shouldCreateDomesticVrpConsent_throwsBadRequestWhenNotSweepingVrpTypeTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticVRPPayment", "CreateDomesticVRPConsent", "GetDomesticVRPConsent"],
        apis = ["domestic-vrps", "domestic-vrp-consents"]
    )
    @Test
    fun shouldCreateDomesticVrp_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBody_v4_0_0() {
        createDomesticVrpPayment.shouldCreateDomesticVrp_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBodyTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticVRPPayment", "CreateDomesticVRPConsent", "GetDomesticVRPConsent"],
        apis = ["domestic-vrps", "domestic-vrp-consents"]
    )
    @Test
    fun shouldCreateDomesticVrp_throwsPolicyValidationErrorConsent_v4_0_0() {
        createDomesticVrpPayment.shouldCreateDomesticVrp_throwsPolicyValidationErrorTest()
    }

    @EnabledIfVersion(
            type = "payments",
            apiVersion = "v4.0.0",
            operations = ["CreateDomesticVRPPayment", "CreateDomesticVRPConsent", "GetDomesticVRPConsent"],
            apis = ["domestic-vrps", "domestic-vrp-consents"]
    )
    @Test
    fun shouldFailToCreateVrpWhenMaxIndividualAmountBreachedTest_v4_0_0() {
        createDomesticVrpPayment.shouldFailToCreateVrpWhenMaxIndividualAmountBreachedTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticVRPPayment", "CreateDomesticVRPConsent", "GetDomesticVRPConsent"],
        apis = ["domestic-vrps", "domestic-vrp-consents"]
    )
    @Test
    fun testCreatingPaymentIsIdempotent_v4_0_0() {
        createDomesticVrpPayment.testCreatingPaymentIsIdempotent()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticVRPPayment", "CreateDomesticVRPConsent", "GetDomesticVRPConsent"],
        apis = ["domestic-vrps", "domestic-vrp-consents"]
    )
    @Test
    fun shouldCreateMultiplePaymentsForConsent_v4_0_0() {
        createDomesticVrpPayment.shouldCreateMultiplePaymentsForConsent()
    }

}