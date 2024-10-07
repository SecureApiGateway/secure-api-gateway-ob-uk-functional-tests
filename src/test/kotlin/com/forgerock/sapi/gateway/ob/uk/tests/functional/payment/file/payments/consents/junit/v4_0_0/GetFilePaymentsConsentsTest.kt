package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.file.payments.consents.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.file.payments.consents.api.v4_0_0.GetFilePaymentsConsents
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetFilePaymentsConsentsTest(val tppResource: CreateTppCallback.TppResource) {

    private lateinit var getFilePaymentsConsentsApi: GetFilePaymentsConsents

    @BeforeEach
    fun setUp() {
        getFilePaymentsConsentsApi = GetFilePaymentsConsents(OBVersion.v4_0_0, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateFilePaymentConsent", "GetFilePaymentConsent"],
        apis = ["file-payment-consents"],
    )
    @Test
    fun shouldGetFilePaymentsConsents_v4_0_0() {
        getFilePaymentsConsentsApi.shouldGetFilePaymentsConsents()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateFilePaymentConsent", "GetFilePaymentConsent"],
        apis = ["file-payment-consents"],
    )
    @Test
    fun shouldGetFilePaymentsConsents_withoutOptionalDebtorAccount_v4_0_0() {
        getFilePaymentsConsentsApi.shouldGetFilePaymentsConsents_withoutOptionalDebtorAccountTest()
    }
}