package com.forgerock.uk.openbanking.tests.functional.account.statements.junit.v3_1_10

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.account.statements.api.v3_1_8.GetAccountStatement
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