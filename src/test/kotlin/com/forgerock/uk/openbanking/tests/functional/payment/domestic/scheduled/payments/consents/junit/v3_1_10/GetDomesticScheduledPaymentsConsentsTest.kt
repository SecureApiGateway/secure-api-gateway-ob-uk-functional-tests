package com.forgerock.uk.openbanking.tests.functional.payment.domestic.scheduled.payments.consents.junit.v3_1_10

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.payment.domestic.scheduled.payments.consents.api.v3_1_8.GetDomesticScheduledPaymentsConsents
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetDomesticScheduledPaymentsConsentsTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getDomesticScheduledPaymentsConsentsApi: GetDomesticScheduledPaymentsConsents

    @BeforeEach
    fun setUp() {
        getDomesticScheduledPaymentsConsentsApi = GetDomesticScheduledPaymentsConsents(OBVersion.v3_1_10, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payment-consents"]
    )
    @Test
    fun shouldGetDomesticScheduledPaymentsConsents_v3_1_10() {
        getDomesticScheduledPaymentsConsentsApi.shouldGetDomesticScheduledPaymentsConsentsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticScheduledPaymentConsent", "GetDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payment-consents"]
    )
    @Test
    fun shouldGetDomesticScheduledPaymentsConsents_withoutOptionalDebtorAccount_v3_1_10() {
        getDomesticScheduledPaymentsConsentsApi.shouldGetDomesticScheduledPaymentsConsents_withoutOptionalDebtorAccountTest()
    }
}