package com.forgerock.uk.openbanking.tests.functional.payment.international.payments.consents.junit.v3_1_9

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.payment.international.payments.consents.api.v3_1_8.CreateInternationalPaymentsConsents
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CreateInternationalPaymentsConsentsTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var createInternationalPaymentsConsents: CreateInternationalPaymentsConsents

    @BeforeEach
    fun setUp() {
        createInternationalPaymentsConsents = CreateInternationalPaymentsConsents(OBVersion.v3_1_9, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateInternationalPaymentConsent"],
        apis = ["international-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun createInternationalPaymentsConsents_v3_1_9() {
        createInternationalPaymentsConsents.createInternationalPaymentsConsents()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateInternationalPaymentConsent"],
        apis = ["international-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun createInternationalPaymentsConsents_mandatoryFields_v3_1_9() {
        createInternationalPaymentsConsents.createInternationalPaymentsConsents_mandatoryFieldsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateInternationalPaymentConsent"],
        apis = ["international-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateInternationalPaymentConsents_throwsSendInvalidFormatDetachedJws_v3_1_9() {
        createInternationalPaymentsConsents.shouldCreateInternationalPaymentConsents_throwsSendInvalidFormatDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateInternationalPaymentConsent"],
        apis = ["international-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateInternationalPaymentConsents_throwsNoDetachedJws_v3_1_9() {
        createInternationalPaymentsConsents.shouldCreateInternationalPaymentConsents_throwsNoDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateInternationalPaymentConsent"],
        apis = ["international-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateInternationalPaymentConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJws_v3_1_9() {
        createInternationalPaymentsConsents.shouldCreateInternationalPaymentConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.9",
        operations = ["CreateInternationalPaymentConsent"],
        apis = ["international-payment-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateInternationalPaymentConsents_throwsSendInvalidKidDetachedJws_v3_1_9() {
        createInternationalPaymentsConsents.shouldCreateInternationalPaymentConsents_throwsSendInvalidKidDetachedJwsTest()
    }
}