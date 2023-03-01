package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.direct.debits.junit.v3_1_8

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.direct.debits.api.v3_1_8.GetAccountDirectDebits
import org.junit.jupiter.api.Test

class GetAccountDirectDebitsTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.8",
        operations = ["CreateAccountAccessConsent", "GetAccountDirectDebits"],
        apis = ["direct-debits"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5", "v.3.1.4", "v.3.1.3"]
    )
    @Test
    fun shouldGetAccountDirectDebits_v3_1_8() {
        GetAccountDirectDebits(OBVersion.v3_1_8, tppResource).shouldGetAccountDirectDebitsTest()
    }
}