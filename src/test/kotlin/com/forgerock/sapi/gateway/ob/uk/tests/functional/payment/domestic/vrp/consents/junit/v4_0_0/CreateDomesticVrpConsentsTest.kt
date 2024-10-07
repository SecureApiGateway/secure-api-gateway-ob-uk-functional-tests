package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.vrp.consents.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.vrp.consents.api.v4_0_0.CreateDomesticVrpConsents
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class CreateDomesticVrpConsentsTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var createDomesticVrpConsents: CreateDomesticVrpConsents

    @BeforeEach
    fun setUp() {
        createDomesticVrpConsents = CreateDomesticVrpConsents(OBVersion.v4_0_0, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticVRPConsent"],
        apis = ["domestic-vrp-consents"]
    )
    @Test
    fun createDomesticVrpConsents_v4_0_0() {
        createDomesticVrpConsents.createDomesticVrpConsent()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["DeleteDomesticVRPConsent"],
        apis = ["domestic-vrp-consents"]
    )
    @Test
    fun deleteDomesticVrpConsent_v4_0_0(){
        createDomesticVrpConsents.deleteDomesticVrpConsent()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticVRPConsent"],
        apis = ["domestic-vrp-consents"]
    )
    @Test
    fun createDomesticPaymentsConsents_SameIdempotencyKeyMultipleRequestTest_v4_0_0(){
        createDomesticVrpConsents.createDomesticPaymentsConsents_SameIdempotencyKeyMultipleRequestTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticVRPConsent"],
        apis = ["domestic-vrp-consents"]
    )
    @Test
    fun createDomesticVrpConsents_NoIdempotencyKey_throwsBadRequestTest_v4_0_0(){
        createDomesticVrpConsents.createDomesticVrpConsents_NoIdempotencyKey_throwsBadRequestTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticVRPConsent"],
        apis = ["domestic-vrp-consents"]
    )
    @Test
    @Disabled("Functionality not yet implemented in the RS Consent API - issue: https://github.com/SecureApiGateway/SecureApiGateway/issues/1041")
    fun createDomesticVrpConsents_throwsInvalidDebtorAccount_v4_0_0() {
        createDomesticVrpConsents.createDomesticVrpConsent_throwsInvalidDebtorAccountTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticVRPConsent"],
        apis = ["domestic-vrp-consents"]
    )
    @Test
    fun createDomesticVrpConsents_mandatoryFields_v4_0_0() {
        createDomesticVrpConsents.createDomesticVrpConsents_mandatoryFieldsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticVRPConsent"],
        apis = ["domestic-vrp-consents"]
    )
    @Test
    fun shouldCreateDomesticVrpConsents_throwsSendInvalidFormatDetachedJws_v4_0_0() {
        createDomesticVrpConsents.createDomesticVrpConsents_mandatoryFieldsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticVRPConsent"],
        apis = ["domestic-vrp-consents"]
    )
    @Test
    fun shouldCreateDomesticVrpConsents_throwsNoDetachedJws_v4_0_0() {
        createDomesticVrpConsents.shouldCreateDomesticVrpConsents_throwsNoDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticVRPConsent"],
        apis = ["domestic-vrp-consents"]
    )
    @Test
    fun shouldCreateDomesticVrpConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJws_v4_0_0() {
        createDomesticVrpConsents.shouldCreateDomesticVrpConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticVRPConsent"],
        apis = ["domestic-vrp-consents"]
    )
    @Test
    fun shouldCreateDomesticVrpConsents_throwsSendInvalidKidDetachedJws_v4_0_0() {
        createDomesticVrpConsents.shouldCreateDomesticVrpConsents_throwsSendInvalidKidDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v4.0.0",
        operations = ["CreateDomesticVRPConsent"],
        apis = ["domestic-vrp-consents"]
    )
    @Test
    fun shouldCreateDomesticVrpConsents_throwsRejectedConsent_v4_0_0() {
        createDomesticVrpConsents.shouldCreateDomesticVrpConsents_throwsRejectedConsent_Test()
    }
}