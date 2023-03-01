package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.payments.junit.v3_1_9

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.payments.api.v3_1_8.GetDomesticPaymentDetails
import org.junit.jupiter.api.Test

class GetDomesticPaymentDetailsTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["GetDomesticPayment", "CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent", "GetDomesticPaymentDomesticPaymentIdPaymentDetails"],
        apis = ["domestic-payments", "domestic-payment-consents"]
    )
    @Test
    fun getDomesticPaymentDomesticPaymentIdPaymentDetails_v3_1_9() {
        GetDomesticPaymentDetails(
            OBVersion.v3_1_9,
            tppResource
        ).getDomesticPaymentDomesticPaymentIdPaymentDetailsTest()
    }
}