package com.forgerock.uk.openbanking.tests.functional.payment.domestic.vrp.consents.junit.v3_1_10

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.payment.domestic.vrp.consents.api.v3_1_8.GetDomesticVrpConsents
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetDomesticVrpConsentsTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getDomesticVrpConsents: GetDomesticVrpConsents

    @BeforeEach
    fun setUp() {
        getDomesticVrpConsents = GetDomesticVrpConsents(OBVersion.v3_1_10, tppResource)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.10",
        operations = ["CreateDomesticVRPConsent", "GetDomesticVRPConsent"],
        apis = ["domestic-vrp-consents"]
    )
    @Test
    fun shouldGetDomesticVrpConsents_v3_1_10() {
        getDomesticVrpConsents.shouldGetDomesticVrpConsents()
    }

}