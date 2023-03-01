package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.direct.debits.junit.v3_1_10

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.direct.debits.api.v3_1_8.GetDirectDebits
import org.junit.jupiter.api.Test

class GetDirectDebitsTest(val tppResource: CreateTppCallback.TppResource) {
    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.10",
        operations = ["CreateAccountAccessConsent", "GetDirectDebits"],
        apis = ["direct-debits"]
    )
    @Test
    fun shouldGetDirectDebits_v3_1_10() {
        GetDirectDebits(OBVersion.v3_1_10, tppResource).shouldGetDirectDebitsTest()
    }
}