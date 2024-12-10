package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.direct.debits.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.direct.debits.api.v4_0_0.GetAccountDirectDebits
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.direct.debits.api.v4_0_0.GetDirectDebits
import org.junit.jupiter.api.Test

class GetDirectDebitsTest(val tppResource: CreateTppCallback.TppResource) {
    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v4.0.0",
        operations = ["CreateAccountAccessConsent", "GetDirectDebits"],
        apis = ["direct-debits"]
    )
    @Test
    fun shouldGetDirectDebits_v4_0_0() {
        GetDirectDebits(OBVersion.v4_0_0, tppResource).shouldGetDirectDebitsTest()
    }

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v4.0.0",
        operations = ["CreateAccountAccessConsent", "GetAccountDirectDebits"],
        apis = ["direct-debits"]
    )
    @Test
    fun shouldGetDirectDebitsTest_mandateRelatedInformation_v4_0_0() {
        GetAccountDirectDebits(OBVersion.v4_0_0, tppResource).shouldGetAccountDirectDebitsTest_mandateRelatedInformation()
    }
}