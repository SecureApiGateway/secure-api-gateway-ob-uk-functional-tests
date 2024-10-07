package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.scheduled.payments.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.scheduled.payments.api.v3_1_8.GetDomesticScheduledPaymentDetails
import org.junit.jupiter.api.Test

class GetDomesticScheduledPaymentDetailsTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["GetDomesticScheduledPayment", "CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentDomesticPaymentIdPaymentDetails"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"]
    )
    @Test
    fun getDomesticScheduledPaymentDomesticPaymentIdPaymentDetails_v3_1_10() {
        GetDomesticScheduledPaymentDetails(
            OBVersion.v3_1_10,
            tppResource
        ).getDomesticScheduledPaymentDomesticPaymentIdPaymentDetailsTest()
    }

}