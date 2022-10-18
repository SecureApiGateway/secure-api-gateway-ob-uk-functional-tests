package com.forgerock.uk.openbanking.tests.functional.payment.international.standing.orders.junit.v3_1_9

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.payment.international.standing.orders.api.v3_1_8.GetInternationalStandingOrderInternationalStandingOrderIdPaymentDetails
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetInternationalStandingOrderInternationalStandingOrderIdPaymentDetailsTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getInternationalStandingOrderInternationalStandingOrderIdPaymentDetails: GetInternationalStandingOrderInternationalStandingOrderIdPaymentDetails

    @BeforeEach
    fun setUp() {
        getInternationalStandingOrderInternationalStandingOrderIdPaymentDetails =
            GetInternationalStandingOrderInternationalStandingOrderIdPaymentDetails(OBVersion.v3_1_9, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["GetInternationalStandingOrder", "CreateInternationalStandingOrder", "CreateInternationalStandingOrderConsent", "GetInternationalStandingOrderConsent", "GetInternationalStandingOrderInternationalStandingOrderIdPaymentDetails"],
        apis = ["international-standing-orders", "international-standing-order-consents"]
    )
    @Test
    fun getInternationalStandingOrderInternationalStandingOrderIdPaymentDetails_v3_1_9() {
        getInternationalStandingOrderInternationalStandingOrderIdPaymentDetails.getInternationalStandingOrderInternationalStandingOrderIdPaymentDetailsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["GetInternationalStandingOrder", "CreateInternationalStandingOrder", "CreateInternationalStandingOrderConsent", "GetInternationalStandingOrderConsent", "GetInternationalStandingOrderInternationalStandingOrderIdPaymentDetails"],
        apis = ["international-standing-orders", "international-standing-order-consents"]
    )
    @Test
    fun getInternationalStandingOrderInternationalStandingOrderIdPaymentDetails_mandatoryFields_v3_1_9() {
        getInternationalStandingOrderInternationalStandingOrderIdPaymentDetails.getInternationalStandingOrderInternationalStandingOrderIdPaymentDetails_mandatoryFieldsTest()
    }
}