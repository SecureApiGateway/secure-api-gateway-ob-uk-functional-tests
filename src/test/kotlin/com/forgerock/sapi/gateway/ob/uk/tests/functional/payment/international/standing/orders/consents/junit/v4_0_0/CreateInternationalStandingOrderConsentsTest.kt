package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.standing.orders.consents.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.standing.orders.consents.api.v4_0_0.CreateInternationalStandingOrderConsents
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class CreateInternationalStandingOrderConsentsTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var createInternationalStandingOrderConsents: CreateInternationalStandingOrderConsents

    @BeforeEach
    fun setUp() {
        createInternationalStandingOrderConsents = CreateInternationalStandingOrderConsents(OBVersion.v4_0_0, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalStandingOrderConsent"],
        apis = ["international-standing-order-consents"],
        compatibleVersions = ["v.3.1.9", "v.3.1.8", "v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun createInternationalStandingOrdersConsents_v4_0_0() {
        createInternationalStandingOrderConsents.createInternationalStandingOrdersConsentsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalStandingOrderConsent"],
        apis = ["international-standing-order-consents"],
        compatibleVersions = ["v.3.1.9", "v.3.1.8", "v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun createDomesticPaymentsConsents_SameIdempotencyKeyMultipleRequestTest_v4_0_0() {
        createInternationalStandingOrderConsents.createDomesticPaymentsConsents_SameIdempotencyKeyMultipleRequestTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalStandingOrderConsent"],
        apis = ["international-standing-order-consents"],
        compatibleVersions = ["v.3.1.9", "v.3.1.8", "v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun createDomesticPaymentsConsents_NoIdempotencyKey_throwsBadRequestTest_v4_0_0() {
        createInternationalStandingOrderConsents.createDomesticPaymentsConsents_NoIdempotencyKey_throwsBadRequestTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalStandingOrderConsent"],
        apis = ["international-standing-order-consents"],
        compatibleVersions = ["v.3.1.9", "v.3.1.8", "v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun createInternationalStandingOrdersConsents_withDebtorAccount_v4_0_0() {
        createInternationalStandingOrderConsents.createInternationalStandingOrdersConsents_withDebtorAccountTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalStandingOrderConsent"],
        apis = ["international-standing-order-consents"],
        compatibleVersions = ["v.3.1.9", "v.3.1.8", "v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    @Disabled("This has not been implemented in the RS impl of the Consent API")
    fun createInternationalStandingOrdersConsents_throwsInvalidDebtorAccount_v4_0_0() {
        createInternationalStandingOrderConsents.createInternationalStandingOrdersConsents_throwsInvalidDebtorAccountTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalStandingOrderConsent"],
        apis = ["international-standing-order-consents"],
        compatibleVersions = ["v.3.1.9", "v.3.1.8", "v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun createInternationalStandingOrdersConsents_mandatoryFields_v4_0_0() {
        createInternationalStandingOrderConsents.createInternationalStandingOrdersConsents_mandatoryFields()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalStandingOrderConsent"],
        apis = ["international-standing-order-consents"],
        compatibleVersions = ["v.3.1.9", "v.3.1.8", "v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateInternationalStandingOrdersConsents_throwsSendInvalidFormatDetachedJws_v4_0_0() {
        createInternationalStandingOrderConsents.shouldCreateInternationalStandingOrdersConsents_throwsSendInvalidFormatDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalStandingOrderConsent"],
        apis = ["international-standing-order-consents"],
        compatibleVersions = ["v.3.1.9", "v.3.1.8", "v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateInternationalStandingOrdersConsents_throwsNoDetachedJws_v4_0_0() {
        createInternationalStandingOrderConsents.shouldCreateInternationalStandingOrdersConsents_throwsNoDetachedJws()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalStandingOrderConsent"],
        apis = ["international-standing-order-consents"],
        compatibleVersions = ["v.3.1.9", "v.3.1.8", "v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateInternationalStandingOrdersConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJws_v4_0_0() {
        createInternationalStandingOrderConsents.shouldCreateInternationalStandingOrdersConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalStandingOrderConsent"],
        apis = ["international-standing-order-consents"],
        compatibleVersions = ["v.3.1.9", "v.3.1.8", "v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateInternationalStandingOrdersConsents_throwsSendInvalidKidDetachedJws_v4_0_0() {
        createInternationalStandingOrderConsents.shouldCreateInternationalStandingOrdersConsents_throwsSendInvalidKidDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateInternationalStandingOrderConsent"],
        apis = ["international-standing-order-consents"],
        compatibleVersions = ["v.3.1.9", "v.3.1.8", "v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateInternationalStandingOrdersConsents_throwsRejectedConsent_v4_0_0() {
        createInternationalStandingOrderConsents.shouldCreateInternationalStandingOrdersConsents_throwsRejectedConsentTest()
    }
}
