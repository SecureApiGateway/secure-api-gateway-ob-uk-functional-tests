package com.forgerock.uk.openbanking.tests.functional.account.direct.debits.junit.v3_1_9

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.account.direct.debits.api.v3_1_8.GetAccountDirectDebits
import org.junit.jupiter.api.Test

class GetAccountDirectDebitsTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.9",
        operations = ["CreateAccountAccessConsent", "GetAccountDirectDebits"],
        apis = ["direct-debits"]
    )
    @Test
    fun shouldGetAccountDirectDebits_v3_1_9() {
        GetAccountDirectDebits(OBVersion.v3_1_9, tppResource).shouldGetAccountDirectDebitsTest()
    }
}