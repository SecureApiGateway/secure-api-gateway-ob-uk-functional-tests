package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.scheduled.payments.consents.junit.v3_1_9

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.scheduled.payments.consents.api.v3_1_8.GetDomesticScheduledPaymentsConsents
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetDomesticScheduledPaymentsConsentsTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getDomesticScheduledPaymentsConsentsApi: GetDomesticScheduledPaymentsConsents

    @BeforeEach
    fun setUp() {
        getDomesticScheduledPaymentsConsentsApi = GetDomesticScheduledPaymentsConsents(OBVersion.v3_1_9, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payment-consents"]
    )
    @Test
    fun shouldGetDomesticScheduledPaymentsConsents_v3_1_9() {
        getDomesticScheduledPaymentsConsentsApi.shouldGetDomesticScheduledPaymentsConsentsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payment-consents"]
    )
    @Test
    fun shouldGetDomesticScheduledPaymentsConsents_withoutOptionalDebtorAccount_v3_1_9() {
        getDomesticScheduledPaymentsConsentsApi.shouldGetDomesticScheduledPaymentsConsents_withoutOptionalDebtorAccountTest()
    }
}