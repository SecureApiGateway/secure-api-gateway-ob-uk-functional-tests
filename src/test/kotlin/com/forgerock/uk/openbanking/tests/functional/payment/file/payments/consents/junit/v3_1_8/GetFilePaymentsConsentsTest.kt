package com.forgerock.uk.openbanking.tests.functional.payment.file.payments.consents.junit.v3_1_8

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.payment.file.payments.api.v3_1_8.GetFilePaymentsConsents
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetFilePaymentsConsentsTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getFilePaymentsConsentsApi: GetFilePaymentsConsents

    @BeforeEach
    fun setUp() {
        getFilePaymentsConsentsApi = GetFilePaymentsConsents(OBVersion.v3_1_8, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateFilePaymentConsent", "GetFilePaymentConsent"],
        apis = ["file-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldGetFilePaymentsConsents_v3_1_8() {
        getFilePaymentsConsentsApi.shouldGetFilePaymentsConsents()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateFilePaymentConsent", "GetFilePaymentConsent"],
        apis = ["file-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldGetFilePaymentsConsents_withoutOptionalDebtorAccount_v3_1_8() {
        getFilePaymentsConsentsApi.shouldGetFilePaymentsConsents_withoutOptionalDebtorAccountTest()
    }
}