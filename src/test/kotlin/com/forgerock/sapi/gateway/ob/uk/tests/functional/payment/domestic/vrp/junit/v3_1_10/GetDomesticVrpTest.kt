package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.vrp.junit.v3_1_10


import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.vrp.api.v3_1_10.GetDomesticVrp
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetDomesticVrpTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getDomesticVrpPaymentApi: GetDomesticVrp

    @BeforeEach
    fun setUp() {
        getDomesticVrpPaymentApi = GetDomesticVrp(OBVersion.v3_1_10, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["GetDomesticVrpPayment", "CreateDomesticVrpPayment", "CreateDomesticVRPConsent", "GetDomesticVRPConsent"],
        apis = ["domestic-vrps", "domestic-vrp-consents"]
    )
    @Test
    fun getDomesticVrpPayments_v3_1_10() {
        getDomesticVrpPaymentApi.getDomesticVrpPaymentsTest()
    }

}