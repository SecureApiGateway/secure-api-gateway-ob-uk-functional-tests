package com.forgerock.uk.openbanking.tests.functional.payment.international.standing.orders.junit.v3_1_8

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
            GetInternationalStandingOrderInternationalStandingOrderIdPaymentDetails(OBVersion.v3_1_8, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["GetInternationalStandingOrder", "CreateInternationalStandingOrder", "CreateInternationalStandingOrderConsent", "GetInternationalStandingOrderConsent", "GetInternationalStandingOrderInternationalStandingOrderIdPaymentDetails"],
        apis = ["international-standing-orders", "international-standing-order-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun getInternationalStandingOrderInternationalStandingOrderIdPaymentDetails_v3_1_8() {
        getInternationalStandingOrderInternationalStandingOrderIdPaymentDetails.getInternationalStandingOrderInternationalStandingOrderIdPaymentDetailsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["GetInternationalStandingOrder", "CreateInternationalStandingOrder", "CreateInternationalStandingOrderConsent", "GetInternationalStandingOrderConsent", "GetInternationalStandingOrderInternationalStandingOrderIdPaymentDetails"],
        apis = ["international-standing-orders", "international-standing-order-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun getInternationalStandingOrderInternationalStandingOrderIdPaymentDetails_mandatoryFields_v3_1_8() {
        getInternationalStandingOrderInternationalStandingOrderIdPaymentDetails.getInternationalStandingOrderInternationalStandingOrderIdPaymentDetails_mandatoryFieldsTest()
    }
}