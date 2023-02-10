package com.forgerock.uk.openbanking.tests.functional.payment.domestic.vrp.junit.v3_1_10

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.payment.domestic.vrp.api.v3_1_10.GetDomesticVrpDetails
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class GetDomesticVrpDetailsTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getDomesticVrpDetails: GetDomesticVrpDetails

    @BeforeEach
    fun setUp() {
        getDomesticVrpDetails =
            GetDomesticVrpDetails(OBVersion.v3_1_10, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["GetDomesticVrpPayment", "CreateDomesticVrpPayment", "CreateDomesticVRPConsent", "GetDomesticVRPConsent", "GetDomesticVrpPaymentDetails"],
        apis = ["domestic-vrps", "domestic-vrp-consents"]
    )
    @Test
    fun getDomesticVrpDetails_v3_1_10() {
        getDomesticVrpDetails.getDomesticVrpDetailsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["GetDomesticVrpPayment", "CreateDomesticVrpPayment", "CreateDomesticVRPConsent", "GetDomesticVRPConsent", "GetDomesticVrpPaymentDetails"],
        apis = ["domestic-vrps", "domestic-vrp-consents"]
    )
    @Test
    fun getDomesticVrpDetailsWithMultiplePaymentsTest_v3_1_10() {
        getDomesticVrpDetails.getDomesticVrpDetailsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["GetDomesticVrpPayment", "CreateDomesticVrpPayment", "CreateDomesticVRPConsent", "GetDomesticVRPConsent", "GetDomesticVrpPaymentDetails"],
        apis = ["domestic-vrps", "domestic-vrp-consents"]
    )
    @Test
    fun getDomesticVrpDetails_mandatoryFields_v3_1_10() {
        getDomesticVrpDetails.getDomesticVrpDetails_mandatoryFieldsTest()
    }
}