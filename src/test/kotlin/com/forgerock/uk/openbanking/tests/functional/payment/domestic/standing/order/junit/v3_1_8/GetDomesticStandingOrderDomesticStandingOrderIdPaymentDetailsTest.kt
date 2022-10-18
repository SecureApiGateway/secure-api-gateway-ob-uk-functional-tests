package com.forgerock.uk.openbanking.tests.functional.payment.domestic.standing.order.junit.v3_1_8

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.payment.domestic.standing.order.api.v3_1_8.GetDomesticStandingOrderDomesticStandingOrderIdPaymentDetails
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetDomesticStandingOrderDomesticStandingOrderIdPaymentDetailsTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getDomesticStandingOrderDomesticStandingOrderIdPaymentDetails: GetDomesticStandingOrderDomesticStandingOrderIdPaymentDetails

    @BeforeEach
    fun setUp() {
        getDomesticStandingOrderDomesticStandingOrderIdPaymentDetails =
            GetDomesticStandingOrderDomesticStandingOrderIdPaymentDetails(OBVersion.v3_1_8, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["GetDomesticStandingOrder", "CreateDomesticStandingOrder", "CreateDomesticStandingOrderConsent", "GetDomesticStandingOrderConsent", "GetDomesticStandingOrderDomesticStandingOrderIdPaymentDetails"],
        apis = ["domestic-standing-orders", "domestic-standing-order-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun getDomesticStandingOrderDomesticStandingOrderIdPaymentDetails_v3_1_8() {
        getDomesticStandingOrderDomesticStandingOrderIdPaymentDetails.getDomesticStandingOrderDomesticStandingOrderIdPaymentDetailsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["GetDomesticStandingOrder", "CreateDomesticStandingOrder", "CreateDomesticStandingOrderConsent", "GetDomesticStandingOrderConsent", "GetDomesticStandingOrderDomesticStandingOrderIdPaymentDetails"],
        apis = ["domestic-standing-orders", "domestic-standing-order-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun getDomesticStandingOrderDomesticStandingOrderIdPaymentDetails_mandatoryFields_v3_1_8() {
        getDomesticStandingOrderDomesticStandingOrderIdPaymentDetails.getDomesticStandingOrderDomesticStandingOrderIdPaymentDetails_mandatoryFieldsTest()
    }
}