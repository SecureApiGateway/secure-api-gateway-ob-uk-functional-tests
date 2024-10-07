package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.vrp.junit.v4_0_0


import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.vrp.api.v4_0_0.GetDomesticVrp
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetDomesticVrpTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getDomesticVrpPaymentApi: GetDomesticVrp

    @BeforeEach
    fun setUp() {
        getDomesticVrpPaymentApi = GetDomesticVrp(OBVersion.v4_0_0, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["GetDomesticVRPPayment", "CreateDomesticVRPPayment", "CreateDomesticVRPConsent", "GetDomesticVRPConsent"],
        apis = ["domestic-vrps", "domestic-vrp-consents"]
    )
    @Test
    fun getDomesticVrpPayments_v4_0_0() {
        getDomesticVrpPaymentApi.getDomesticVrpPaymentsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["GetDomesticVRPPayment", "CreateDomesticVRPPayment", "CreateDomesticVRPConsent", "GetDomesticVRPConsent"],
        apis = ["domestic-vrps", "domestic-vrp-consents"]
    )
    @Test
    fun getDomesticVrpPaymentsWithRefundAccount_v4_0_0() {
        getDomesticVrpPaymentApi.getDomesticVrpPaymentsWithRefundAccountTest()
    }

}