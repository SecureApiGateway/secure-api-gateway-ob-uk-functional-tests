package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.vrp.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.vrp.api.v4_0_0.GetDomesticVrpDetails
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetDomesticVrpDetailsTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getDomesticVrpDetails: GetDomesticVrpDetails

    @BeforeEach
    fun setUp() {
        getDomesticVrpDetails =
            GetDomesticVrpDetails(OBVersion.v4_0_0, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["GetDomesticVRPPayment", "CreateDomesticVRPPayment", "CreateDomesticVRPConsent", "GetDomesticVRPConsent", "GetDomesticVRPPaymentDetails"],
        apis = ["domestic-vrps", "domestic-vrp-consents"]
    )
    @Test
    fun getDomesticVrpDetails_v4_0_0() {
        getDomesticVrpDetails.getDomesticVrpDetailsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["GetDomesticVRPPayment", "CreateDomesticVRPPayment", "CreateDomesticVRPConsent", "GetDomesticVRPConsent", "GetDomesticVRPPaymentDetails"],
        apis = ["domestic-vrps", "domestic-vrp-consents"]
    )
    @Test
    fun getDomesticVrpDetailsWithMultiplePaymentsTest_v4_0_0() {
        getDomesticVrpDetails.getDomesticVrpDetailsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["GetDomesticVRPPayment", "CreateDomesticVRPPayment", "CreateDomesticVRPConsent", "GetDomesticVRPConsent", "GetDomesticVRPPaymentDetails"],
        apis = ["domestic-vrps", "domestic-vrp-consents"]
    )
    @Test
    fun getDomesticVrpDetails_mandatoryFields_v4_0_0() {
        getDomesticVrpDetails.getDomesticVrpDetails_mandatoryFieldsTest()
    }
}