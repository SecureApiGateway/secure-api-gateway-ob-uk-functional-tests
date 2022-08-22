package com.forgerock.uk.openbanking.tests.functional.account.accounts.junit.v3_1_9

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.account.accounts.api.v3_1_8.GetAccount
import org.junit.jupiter.api.Test


class GetAccountTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.9",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccount"],
        apis = ["accounts"]
    )
    @Test
    fun shouldGetAccount_v3_1_9() {
        GetAccount(OBVersion.v3_1_9, tppResource).shouldGetAccountTest()
    }
}