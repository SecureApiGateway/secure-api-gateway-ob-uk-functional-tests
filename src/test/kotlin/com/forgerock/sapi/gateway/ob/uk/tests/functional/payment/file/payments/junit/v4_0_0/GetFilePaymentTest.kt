package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.file.payments.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.file.payments.api.v4_0_0.GetFilePayment
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetFilePaymentTest(val tppResource: CreateTppCallback.TppResource) {

    private lateinit var getFilePayment: GetFilePayment

    @BeforeEach
    fun setUp() {
        getFilePayment = GetFilePayment(OBVersion.v4_0_0, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["GetFilePayment", "CreateFilePayment", "CreateFilePaymentConsent", "GetFilePaymentConsent"],
        apis = ["file-payments", "file-payment-consents"]
    )
    @Test
    fun getFilePayments_v4_0_0() {
        getFilePayment.getFilePaymentsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["GetFilePayment", "CreateFilePayment", "CreateFilePaymentConsent", "GetFilePaymentConsent"],
        apis = ["file-payments", "file-payment-consents"]
    )
    @Test
    fun getFilePayments_mandatoryFields_v4_0_0() {
        getFilePayment.getFilePayments_mandatoryFieldsTest()
    }

}