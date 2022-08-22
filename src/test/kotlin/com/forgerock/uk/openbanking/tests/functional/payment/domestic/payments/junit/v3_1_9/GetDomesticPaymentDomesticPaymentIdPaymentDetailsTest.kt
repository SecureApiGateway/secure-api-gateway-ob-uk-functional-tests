package com.forgerock.uk.openbanking.tests.functional.payment.domestic.payments.junit.v3_1_9

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.payment.domestic.payments.api.v3_1_8.GetDomesticPaymentDomesticPaymentIdPaymentDetails
import org.junit.jupiter.api.Test

class GetDomesticPaymentDomesticPaymentIdPaymentDetailsTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["GetDomesticPayment", "CreateDomesticPayment", "CreateDomesticPaymentConsent", "GetDomesticPaymentConsent", "GetDomesticPaymentDomesticPaymentIdPaymentDetails"],
        apis = ["domestic-payments", "domestic-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun getDomesticPaymentDomesticPaymentIdPaymentDetails_v3_1_9() {
        GetDomesticPaymentDomesticPaymentIdPaymentDetails(
            OBVersion.v3_1_9,
            tppResource
        ).getDomesticPaymentDomesticPaymentIdPaymentDetailsTest()
    }
}