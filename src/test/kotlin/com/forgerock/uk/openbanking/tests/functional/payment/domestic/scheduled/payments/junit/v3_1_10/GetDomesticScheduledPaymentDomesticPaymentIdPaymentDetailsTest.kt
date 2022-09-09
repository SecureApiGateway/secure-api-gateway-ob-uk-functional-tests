package com.forgerock.uk.openbanking.tests.functional.payment.domestic.scheduled.payments.junit.v3_1_10

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.payment.domestic.scheduled.payments.api.v3_1_8.GetDomesticScheduledPaymentDomesticPaymentIdPaymentDetails
import org.junit.jupiter.api.Test

class GetDomesticScheduledPaymentDomesticPaymentIdPaymentDetailsTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["GetDomesticScheduledPayment", "CreateDomesticScheduledPayment", "CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentDomesticPaymentIdPaymentDetails"],
        apis = ["domestic-scheduled-payments", "domestic-scheduled-payment-consents"]
    )
    @Test
    fun getDomesticScheduledPaymentDomesticPaymentIdPaymentDetails_v3_1_10() {
        GetDomesticScheduledPaymentDomesticPaymentIdPaymentDetails(
            OBVersion.v3_1_10,
            tppResource
        ).getDomesticScheduledPaymentDomesticPaymentIdPaymentDetailsTest()
    }

}