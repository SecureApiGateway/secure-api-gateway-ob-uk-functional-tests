package com.forgerock.uk.openbanking.tests.functional.payment.domestic.vrp.consents.junit.v3_1_10

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.payment.domestic.vrp.consents.api.v3_1_10.CreateDomesticVrpConsents
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CreateDomesticVrpConsentsTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var createDomesticVrpConsents: CreateDomesticVrpConsents

    @BeforeEach
    fun setUp() {
        createDomesticVrpConsents = CreateDomesticVrpConsents(OBVersion.v3_1_10, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticVRPConsent"],
        apis = ["domestic-vrp-consents"]
    )
    @Test
    fun createDomesticVrpConsents_v3_1_10() {
        createDomesticVrpConsents.createDomesticVrpConsent()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticVRPConsent"],
        apis = ["domestic-vrp-consents"]
    )
    @Test
    fun createDomesticVrpConsents_NoIdempotencyKey_throwsBadRequestTest_v3_1_10(){
        createDomesticVrpConsents.createDomesticVrpConsents_NoIdempotencyKey_throwsBadRequestTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticVRPConsent"],
        apis = ["domestic-vrp-consents"]
    )
    @Test
    fun createDomesticVrpConsents_throwsInvalidDebtorAccount_v3_1_10() {
        createDomesticVrpConsents.createDomesticVrpConsent_throwsInvalidDebtorAccountTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticVRPConsent"],
        apis = ["domestic-vrp-consents"]
    )
    @Test
    fun createDomesticVrpConsents_mandatoryFields_v3_1_10() {
        createDomesticVrpConsents.createDomesticVrpConsents_mandatoryFieldsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticVRPConsent"],
        apis = ["domestic-vrp-consents"]
    )
    @Test
    fun shouldCreateDomesticVrpConsents_throwsSendInvalidFormatDetachedJws_v3_1_10() {
        createDomesticVrpConsents.createDomesticVrpConsents_mandatoryFieldsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticVRPConsent"],
        apis = ["domestic-vrp-consents"]
    )
    @Test
    fun shouldCreateDomesticVrpConsents_throwsNoDetachedJws_v3_1_10() {
        createDomesticVrpConsents.shouldCreateDomesticVrpConsents_throwsNoDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticVRPConsent"],
        apis = ["domestic-vrp-consents"]
    )
    @Test
    fun shouldCreateDomesticVrpConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJws_v3_1_10() {
        createDomesticVrpConsents.shouldCreateDomesticVrpConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticVRPConsent"],
        apis = ["domestic-vrp-consents"]
    )
    @Test
    fun shouldCreateDomesticVrpConsents_throwsSendInvalidKidDetachedJws_v3_1_10() {
        createDomesticVrpConsents.shouldCreateDomesticVrpConsents_throwsSendInvalidKidDetachedJwsTest()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticVRPConsent"],
        apis = ["domestic-vrp-consents"]
    )
    @Test
    fun shouldCreateDomesticVrpConsents_throwsRejectedConsent_v3_1_10() {
        createDomesticVrpConsents.shouldCreateDomesticVrpConsents_throwsRejectedConsent_Test()
    }
}