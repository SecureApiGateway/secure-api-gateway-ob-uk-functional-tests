package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.file.payments.junit.v3_1_8

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.file.payments.api.v3_1_8.GetFilePayment
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetFilePaymentTest(val tppResource: CreateTppCallback.TppResource) {

    private lateinit var getFilePayment: GetFilePayment

    @BeforeEach
    fun setUp() {
        getFilePayment = GetFilePayment(OBVersion.v3_1_8, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["GetFilePayment", "CreateFilePayment", "CreateFilePaymentConsent", "GetFilePaymentConsent"],
        apis = ["file-payments", "file-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun getFilePayments_v3_1_8() {
        getFilePayment.getFilePaymentsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["GetFilePayment", "CreateFilePayment", "CreateFilePaymentConsent", "GetFilePaymentConsent"],
        apis = ["file-payments", "file-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun getFilePayments_mandatoryFields_v3_1_8() {
        getFilePayment.getFilePayments_mandatoryFieldsTest()
    }

}