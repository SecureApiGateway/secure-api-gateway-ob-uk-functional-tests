package com.forgerock.uk.openbanking.tests.functional.payment.domestic.payments.consents.junit.v3_1_9

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.payment.domestic.payments.consents.api.v3_1_8.GetDomesticPaymentsConsents
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetDomesticPaymentsConsentsTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getDomesticPaymentsConsentsApi: GetDomesticPaymentsConsents

    @BeforeEach
    fun setUp() {
        getDomesticPaymentsConsentsApi = GetDomesticPaymentsConsents(OBVersion.v3_1_9, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldGetDomesticPaymentsConsents_v3_1_9() {
        getDomesticPaymentsConsentsApi.shouldGetDomesticPaymentsConsents()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateDomesticPaymentConsent", "GetDomesticPaymentConsent"],
        apis = ["domestic-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldGetDomesticPaymentsConsents_withoutOptionalDebtorAccount_v3_1_9() {
        getDomesticPaymentsConsentsApi.shouldGetDomesticPaymentsConsents_withoutOptionalDebtorAccountTest()
    }
}