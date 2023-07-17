package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.file.payments.consents.junit.v3_1_10

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.file.payments.api.v3_1_8.GetFilePaymentsConsents
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetFilePaymentsConsentsTest(val tppResource: CreateTppCallback.TppResource) {

    private lateinit var getFilePaymentsConsentsApi: GetFilePaymentsConsents

    @BeforeEach
    fun setUp() {
        getFilePaymentsConsentsApi = GetFilePaymentsConsents(OBVersion.v3_1_10, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateFilePaymentConsent", "GetFilePaymentConsent"],
        apis = ["file-payment-consents"],
    )
    @Test
    fun shouldGetFilePaymentsConsents_v3_1_8() {
        getFilePaymentsConsentsApi.shouldGetFilePaymentsConsents()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateFilePaymentConsent", "GetFilePaymentConsent"],
        apis = ["file-payment-consents"],
    )
    @Test
    fun shouldGetFilePaymentsConsents_withoutOptionalDebtorAccount_v3_1_8() {
        getFilePaymentsConsentsApi.shouldGetFilePaymentsConsents_withoutOptionalDebtorAccountTest()
    }
}