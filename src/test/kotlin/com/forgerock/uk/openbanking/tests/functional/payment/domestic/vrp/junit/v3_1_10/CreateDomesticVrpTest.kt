package com.forgerock.uk.openbanking.tests.functional.payment.domestic.vrp.junit.v3_1_10


import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.payment.domestic.vrp.api.v3_1_10.CreateDomesticVrp
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CreateDomesticVrpTest(val tppResource: CreateTppCallback.TppResource) {

    private lateinit var createDomesticVrpPayment: CreateDomesticVrp

    @BeforeEach
    fun setUp() {
        createDomesticVrpPayment = CreateDomesticVrp(OBVersion.v3_1_10, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticVrpPayment", "CreateDomesticVRPConsent", "GetDomesticVRPConsent"],
        apis = ["domestic-vrps", "domestic-vrp-consents"]
    )
    @Test
    fun createDomesticVrpPayment_v3_1_10() {
        createDomesticVrpPayment.createDomesticVrpPaymentTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticVrpPayment", "CreateDomesticVRPConsent", "GetDomesticVRPConsent"],
        apis = ["domestic-vrps", "domestic-vrp-consents"]
    )
    @Test
    fun createDomesticVrpPayment_mandatoryFields_v3_1_10() {
        createDomesticVrpPayment.createDomesticVrpPayment_mandatoryFieldsTest()
    }

     @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticVrpPayment", "CreateDomesticVRPConsent", "GetDomesticVRPConsent"],
        apis = ["domestic-vrps", "domestic-vrp-consents"]
    )
    @Test
    fun shouldCreateDomesticVrp_throwsSendInvalidFormatDetachedJws_v3_1_10() {
        createDomesticVrpPayment.shouldCreateDomesticVrp_throwsSendInvalidFormatDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticVrpPayment", "CreateDomesticVRPConsent", "GetDomesticVRPConsent"],
        apis = ["domestic-vrps", "domestic-vrp-consents"]
    )
    @Test
    fun shouldCreateDomesticVrp_throwsNoDetachedJws_v3_1_10() {
        createDomesticVrpPayment.shouldCreateDomesticVrp_throwsNoDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticVrpPayment", "CreateDomesticVRPConsent", "GetDomesticVRPConsent"],
        apis = ["domestic-vrps", "domestic-vrp-consents"]
    )
    @Test
    fun shouldCreateDomesticVrp_throwsNotPermittedB64HeaderAddedInTheDetachedJws_v3_1_10() {
        createDomesticVrpPayment.shouldCreateDomesticVrp_throwsNotPermittedB64HeaderAddedInTheDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticVrpPayment", "CreateDomesticVRPConsent", "GetDomesticVRPConsent"],
        apis = ["domestic-vrps", "domestic-vrp-consents"]
    )
    @Test
    fun shouldCreateDomesticVrp_throwsSendInvalidKidDetachedJws_v3_1_10() {
        createDomesticVrpPayment.shouldCreateDomesticVrp_throwsSendInvalidKidDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticVrpPayment", "CreateDomesticVRPConsent", "GetDomesticVRPConsent"],
        apis = ["domestic-vrps", "domestic-vrp-consents"]
    )
    @Test
    fun shouldCreateDomesticVrp_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBody_v3_1_10() {
        createDomesticVrpPayment.shouldCreateDomesticVrp_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBodyTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticVrpPayment", "CreateDomesticVRPConsent", "GetDomesticVRPConsent"],
        apis = ["domestic-vrps", "domestic-vrp-consents"]
    )
    @Test
    fun shouldCreateDomesticVrp_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBody_v3_1_10() {
        createDomesticVrpPayment.shouldCreateDomesticVrp_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBodyTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticVrpPayment", "CreateDomesticVRPConsent", "GetDomesticVRPConsent"],
        apis = ["domestic-vrps", "domestic-vrp-consents"]
    )
    @Test
    fun shouldCreateDomesticVrp_throwsPolicyValidationErrorConsent_v3_1_10() {
        createDomesticVrpPayment.shouldCreateDomesticVrp_throwsPolicyValidationErrorTest()
    }

}