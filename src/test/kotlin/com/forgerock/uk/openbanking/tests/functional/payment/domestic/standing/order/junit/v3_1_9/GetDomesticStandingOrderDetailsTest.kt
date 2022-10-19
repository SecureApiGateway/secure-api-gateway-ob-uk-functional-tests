package com.forgerock.uk.openbanking.tests.functional.payment.domestic.standing.order.junit.v3_1_9

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.payment.domestic.standing.order.api.v3_1_8.GetDomesticStandingOrderDetails
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetDomesticStandingOrderDetailsTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getDomesticStandingOrderDomesticStandingOrderIdPaymentDetails: GetDomesticStandingOrderDetails

    @BeforeEach
    fun setUp() {
        getDomesticStandingOrderDomesticStandingOrderIdPaymentDetails =
            GetDomesticStandingOrderDetails(OBVersion.v3_1_9, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["GetDomesticStandingOrder", "CreateDomesticStandingOrder", "CreateDomesticStandingOrderConsent", "GetDomesticStandingOrderConsent", "GetDomesticStandingOrderDomesticStandingOrderIdPaymentDetails"],
        apis = ["domestic-standing-orders", "domestic-standing-order-consents"]
    )
    @Test
    fun getDomesticStandingOrderDomesticStandingOrderIdPaymentDetails_v3_1_9() {
        getDomesticStandingOrderDomesticStandingOrderIdPaymentDetails.getDomesticStandingOrderDomesticStandingOrderIdPaymentDetailsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["GetDomesticStandingOrder", "CreateDomesticStandingOrder", "CreateDomesticStandingOrderConsent", "GetDomesticStandingOrderConsent", "GetDomesticStandingOrderDomesticStandingOrderIdPaymentDetails"],
        apis = ["domestic-standing-orders", "domestic-standing-order-consents"]
    )
    @Test
    fun getDomesticStandingOrderDomesticStandingOrderIdPaymentDetails_mandatoryFields_v3_1_9() {
        getDomesticStandingOrderDomesticStandingOrderIdPaymentDetails.getDomesticStandingOrderDomesticStandingOrderIdPaymentDetails_mandatoryFieldsTest()
    }
}