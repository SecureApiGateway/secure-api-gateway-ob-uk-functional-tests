package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.standing.orders.consents.junit.v3_1_10

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.standing.orders.consents.api.v3_1_8.CreateInternationalStandingOrderConsents
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class CreateInternationalStandingOrderConsentsTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var createInternationalStandingOrderConsents: CreateInternationalStandingOrderConsents

    @BeforeEach
    fun setUp() {
        createInternationalStandingOrderConsents = CreateInternationalStandingOrderConsents(OBVersion.v3_1_10, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateInternationalStandingOrderConsent"],
        apis = ["international-standing-order-consents"],
        compatibleVersions = ["v.3.1.9", "v.3.1.8", "v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun createInternationalStandingOrdersConsents_v3_1_10() {
        createInternationalStandingOrderConsents.createInternationalStandingOrdersConsentsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateInternationalStandingOrderConsent"],
        apis = ["international-standing-order-consents"],
        compatibleVersions = ["v.3.1.9", "v.3.1.8", "v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun createDomesticPaymentsConsents_SameIdempotencyKeyMultipleRequestTest_v3_1_10() {
        createInternationalStandingOrderConsents.createDomesticPaymentsConsents_SameIdempotencyKeyMultipleRequestTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateInternationalStandingOrderConsent"],
        apis = ["international-standing-order-consents"],
        compatibleVersions = ["v.3.1.9", "v.3.1.8", "v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun createDomesticPaymentsConsents_NoIdempotencyKey_throwsBadRequestTest_v3_1_10() {
        createInternationalStandingOrderConsents.createDomesticPaymentsConsents_NoIdempotencyKey_throwsBadRequestTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateInternationalStandingOrderConsent"],
        apis = ["international-standing-order-consents"],
        compatibleVersions = ["v.3.1.9", "v.3.1.8", "v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun createInternationalStandingOrdersConsents_withDebtorAccount_v3_1_10() {
        createInternationalStandingOrderConsents.createInternationalStandingOrdersConsents_withDebtorAccountTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateInternationalStandingOrderConsent"],
        apis = ["international-standing-order-consents"],
        compatibleVersions = ["v.3.1.9", "v.3.1.8", "v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    @Disabled("This has not been implemented in the RS impl of the Consent API")
    fun createInternationalStandingOrdersConsents_throwsInvalidDebtorAccount_v3_1_10() {
        createInternationalStandingOrderConsents.createInternationalStandingOrdersConsents_throwsInvalidDebtorAccountTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateInternationalStandingOrderConsent"],
        apis = ["international-standing-order-consents"],
        compatibleVersions = ["v.3.1.9", "v.3.1.8", "v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun createInternationalStandingOrdersConsents_mandatoryFields_v3_1_10() {
        createInternationalStandingOrderConsents.createInternationalStandingOrdersConsents_mandatoryFields()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateInternationalStandingOrderConsent"],
        apis = ["international-standing-order-consents"],
        compatibleVersions = ["v.3.1.9", "v.3.1.8", "v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateInternationalStandingOrdersConsents_throwsInvalidFrequencyValue_v3_1_10() {
        createInternationalStandingOrderConsents.shouldCreateInternationalStandingOrdersConsents_throwsInvalidFrequencyValue()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateInternationalStandingOrderConsent"],
        apis = ["international-standing-order-consents"],
        compatibleVersions = ["v.3.1.9", "v.3.1.8", "v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateInternationalStandingOrdersConsents_throwsSendInvalidFormatDetachedJws_v3_1_10() {
        createInternationalStandingOrderConsents.shouldCreateInternationalStandingOrdersConsents_throwsSendInvalidFormatDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateInternationalStandingOrderConsent"],
        apis = ["international-standing-order-consents"],
        compatibleVersions = ["v.3.1.9", "v.3.1.8", "v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateInternationalStandingOrdersConsents_throwsNoDetachedJws_v3_1_10() {
        createInternationalStandingOrderConsents.shouldCreateInternationalStandingOrdersConsents_throwsNoDetachedJws()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateInternationalStandingOrderConsent"],
        apis = ["international-standing-order-consents"],
        compatibleVersions = ["v.3.1.9", "v.3.1.8", "v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateInternationalStandingOrdersConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJws_v3_1_10() {
        createInternationalStandingOrderConsents.shouldCreateInternationalStandingOrdersConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateInternationalStandingOrderConsent"],
        apis = ["international-standing-order-consents"],
        compatibleVersions = ["v.3.1.9", "v.3.1.8", "v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateInternationalStandingOrdersConsents_throwsSendInvalidKidDetachedJws_v3_1_10() {
        createInternationalStandingOrderConsents.shouldCreateInternationalStandingOrdersConsents_throwsSendInvalidKidDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateInternationalStandingOrderConsent"],
        apis = ["international-standing-order-consents"],
        compatibleVersions = ["v.3.1.9", "v.3.1.8", "v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateInternationalStandingOrdersConsents_throwsRejectedConsent_v3_1_10() {
        createInternationalStandingOrderConsents.shouldCreateInternationalStandingOrdersConsents_throwsRejectedConsentTest()
    }
}
