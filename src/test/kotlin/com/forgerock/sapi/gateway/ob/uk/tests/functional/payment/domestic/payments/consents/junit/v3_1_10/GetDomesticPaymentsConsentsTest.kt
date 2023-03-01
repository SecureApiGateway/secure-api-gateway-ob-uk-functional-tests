package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.payments.consents.junit.v3_1_10

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.payments.consents.api.v3_1_8.GetDomesticPaymentsConsents
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetDomesticPaymentsConsentsTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getDomesticPaymentsConsentsApi: GetDomesticPaymentsConsents

    @BeforeEach
    fun setUp() {
        getDomesticPaymentsConsentsApi = GetDomesticPaymentsConsents(OBVersion.v3_1_10, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"]
    )
    @Test
    fun shouldGetDomesticPaymentsConsents_v3_1_10() {
        getDomesticPaymentsConsentsApi.shouldGetDomesticPaymentsConsents()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"]
    )
    @Test
    fun shouldGetDomesticPaymentsConsents_withoutOptionalDebtorAccount_v3_1_10() {
        getDomesticPaymentsConsentsApi.shouldGetDomesticPaymentsConsents_withoutOptionalDebtorAccountTest()
    }
}