package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.statements.junit.v3_1_10

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.statements.api.v3_1_8.GetAccountStatement
import org.junit.jupiter.api.Test

class GetAccountStatementTest(val tppResource: CreateTppCallback.TppResource) {
    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.10",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountStatement"],
        apis = ["statements"]
    )
    @Test
    fun shouldGetAccountStatement_v3_1_10() {
        GetAccountStatement(OBVersion.v3_1_10, tppResource).shouldGetAccountStatementTest()
    }
}