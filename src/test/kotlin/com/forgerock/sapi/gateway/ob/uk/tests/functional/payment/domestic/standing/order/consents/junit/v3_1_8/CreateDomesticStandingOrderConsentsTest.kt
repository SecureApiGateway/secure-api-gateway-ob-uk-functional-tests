package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.standing.order.consents.junit.v3_1_8

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.standing.order.consents.api.v3_1_8.CreateDomesticStandingOrderConsents
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CreateDomesticStandingOrderConsentsTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var createDomesticStandingOrderConsents: CreateDomesticStandingOrderConsents

    @BeforeEach
    fun setUp() {
        createDomesticStandingOrderConsents = CreateDomesticStandingOrderConsents(OBVersion.v3_1_8, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateDomesticStandingOrderConsent"],
        apis = ["domestic-standing-order-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun createDomesticStandingOrdersConsents_v3_1_8() {
        createDomesticStandingOrderConsents.createDomesticStandingOrdersConsentsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateDomesticStandingOrderConsent"],
        apis = ["domestic-standing-order-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun createDomesticPaymentsConsents_SameIdempotencyKeyMultipleRequestTest_v3_1_8(){
        createDomesticStandingOrderConsents.createDomesticPaymentsConsents_SameIdempotencyKeyMultipleRequestTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateDomesticStandingOrderConsent"],
        apis = ["domestic-standing-order-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun createDomesticStandingOrdersConsents_NoIdempotencyKey_throwsBadRequestTest_v3_1_8(){
        createDomesticStandingOrderConsents.createDomesticStandingOrdersConsents_NoIdempotencyKey_throwsBadRequestTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateDomesticStandingOrderConsent"],
        apis = ["domestic-standing-order-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun createDomesticStandingOrdersConsents_withDebtorAccount_v3_1_8() {
        createDomesticStandingOrderConsents.createDomesticStandingOrdersConsents_withDebtorAccountTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateDomesticStandingOrderConsent"],
        apis = ["domestic-standing-order-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun createDomesticStandingOrdersConsents_throwsInvalidDebtorAccount_v3_1_8() {
        createDomesticStandingOrderConsents.createDomesticStandingOrdersConsents_throwsInvalidDebtorAccountTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateDomesticStandingOrderConsent"],
        apis = ["domestic-standing-order-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun createDomesticStandingOrdersConsents_mandatoryFields_v3_1_8() {
        createDomesticStandingOrderConsents.createDomesticStandingOrdersConsents_mandatoryFields()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateDomesticStandingOrderConsent"],
        apis = ["domestic-standing-order-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateDomesticStandingOrdersConsents_throwsInvalidFrequencyValue_v3_1_8() {
        createDomesticStandingOrderConsents.shouldCreateDomesticStandingOrdersConsents_throwsInvalidFrequencyValue()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateDomesticStandingOrderConsent"],
        apis = ["domestic-standing-order-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateDomesticStandingOrdersConsents_throwsSendInvalidFormatDetachedJws_v3_1_8() {
        createDomesticStandingOrderConsents.shouldCreateDomesticStandingOrdersConsents_throwsSendInvalidFormatDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateDomesticStandingOrderConsent"],
        apis = ["domestic-standing-order-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateDomesticStandingOrdersConsents_throwsNoDetachedJws_v3_1_8() {
        createDomesticStandingOrderConsents.shouldCreateDomesticStandingOrdersConsents_throwsNoDetachedJws()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateDomesticStandingOrderConsent"],
        apis = ["domestic-standing-order-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateDomesticStandingOrdersConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJws_v3_1_8() {
        createDomesticStandingOrderConsents.shouldCreateDomesticStandingOrdersConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateDomesticStandingOrderConsent"],
        apis = ["domestic-standing-order-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateDomesticStandingOrdersConsents_throwsSendInvalidKidDetachedJws_v3_1_8() {
        createDomesticStandingOrderConsents.shouldCreateDomesticStandingOrdersConsents_throwsSendInvalidKidDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["CreateDomesticStandingOrderConsent"],
        apis = ["domestic-standing-order-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldCreateDomesticStandingOrdersConsents_throwsRejectedConsent_v3_1_8() {
        createDomesticStandingOrderConsents.shouldCreateDomesticStandingOrdersConsents_throwsRejectedConsentTest()
    }
}