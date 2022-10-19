package com.forgerock.uk.openbanking.tests.functional.payment.domestic.payments.junit.v3_1_10

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.payment.domestic.payments.api.v3_1_8.GetDomesticPaymentDetails
import org.junit.jupiter.api.Test

class GetDomesticPaymentDetailsTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["GetDomesticPayment", "CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent", "GetDomesticPaymentDomesticPaymentIdPaymentDetails"],
        apis = ["domestic-payments", "domestic-payment-consents"]
    )
    @Test
    fun getDomesticPaymentDomesticPaymentIdPaymentDetails_v3_1_10() {
        GetDomesticPaymentDetails(
            OBVersion.v3_1_10,
            tppResource
        ).getDomesticPaymentDomesticPaymentIdPaymentDetailsTest()
    }
}