package com.forgerock.uk.openbanking.tests.functional.account.statements.junit.v3_1_8

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.account.statements.api.v3_1_8.GetAccountStatements
import org.junit.jupiter.api.Test

class GetAccountStatementsTest(val tppResource: CreateTppCallback.TppResource) {
    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.8",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountStatements"],
        apis = ["statements"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5", "v.3.1.4", "v.3.1.3"]
    )
    @Test
    fun shouldGetAccountStatements_v3_1_8() {
        GetAccountStatements(OBVersion.v3_1_8, tppResource).shouldGetAccountStatementsTest()
    }
}