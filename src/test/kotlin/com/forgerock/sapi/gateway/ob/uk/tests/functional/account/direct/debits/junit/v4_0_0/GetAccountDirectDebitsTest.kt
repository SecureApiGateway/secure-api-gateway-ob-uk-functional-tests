package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.direct.debits.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.direct.debits.api.v4_0_0.GetAccountDirectDebits
import org.junit.jupiter.api.Test

class GetAccountDirectDebitsTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v4.0.0",
        operations = ["CreateAccountAccessConsent", "GetAccountDirectDebits"],
        apis = ["direct-debits"]
    )
    @Test
    fun shouldGetAccountDirectDebits_v4_0_0() {
        GetAccountDirectDebits(OBVersion.v4_0_0, tppResource).shouldGetAccountDirectDebitsTest()
    }

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v4.0.0",
        operations = ["CreateAccountAccessConsent", "GetAccountDirectDebits"],
        apis = ["direct-debits"]
    )
    @Test
    fun shouldGetAccountDirectDebitsTest_mandateRelatedInformation_v4_0_0() {
        GetAccountDirectDebits(OBVersion.v4_0_0, tppResource).shouldGetAccountDirectDebitsTest_mandateRelatedInformation()
    }
}