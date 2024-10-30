package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.standing.orders.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.standing.orders.api.v4_0_0.GetInternationalStandingOrderDetails
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetInternationalStandingOrderDetailsTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getInternationalStandingOrderInternationalStandingOrderIdPaymentDetails: GetInternationalStandingOrderDetails

    @BeforeEach
    fun setUp() {
        getInternationalStandingOrderInternationalStandingOrderIdPaymentDetails =
            GetInternationalStandingOrderDetails(OBVersion.v4_0_0, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["GetInternationalStandingOrder", "CreateInternationalStandingOrder", "CreateInternationalStandingOrderConsent", "GetInternationalStandingOrderConsent", "GetInternationalStandingOrderInternationalStandingOrderIdPaymentDetails"],
        apis = ["international-standing-orders", "international-standing-order-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun getInternationalStandingOrderInternationalStandingOrderIdPaymentDetails_v4_0_0() {
        getInternationalStandingOrderInternationalStandingOrderIdPaymentDetails.getInternationalStandingOrderInternationalStandingOrderIdPaymentDetailsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["GetInternationalStandingOrder", "CreateInternationalStandingOrder", "CreateInternationalStandingOrderConsent", "GetInternationalStandingOrderConsent", "GetInternationalStandingOrderInternationalStandingOrderIdPaymentDetails"],
        apis = ["international-standing-orders", "international-standing-order-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun getInternationalStandingOrderInternationalStandingOrderIdPaymentDetails_mandatoryFields_v4_0_0() {
        getInternationalStandingOrderInternationalStandingOrderIdPaymentDetails.getInternationalStandingOrderInternationalStandingOrderIdPaymentDetails_mandatoryFieldsTest()
    }
}