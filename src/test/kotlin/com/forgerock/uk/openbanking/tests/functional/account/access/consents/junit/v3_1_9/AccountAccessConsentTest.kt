package com.forgerock.uk.openbanking.tests.functional.account.access.consents.junit.v3_1_9

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.account.access.consents.api.v3_1_8.AccountAccessConsent
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AccountAccessConsentTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var accountAccessConsentApi: AccountAccessConsent

    @BeforeEach
    fun setUp() {
        accountAccessConsentApi = AccountAccessConsent(OBVersion.v3_1_9, tppResource)
    }

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.9",
        operations = ["CreateAccountAccessConsent"]
    )
    @Test
    fun createAccountAccessConsents_v3_1_9() {
        accountAccessConsentApi.createAccountAccessConsentTest()
    }

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.9",
        operations = ["DeleteAccountAccessConsent"]
    )
    @Test
    fun deleteAccountAccessConsents_v3_1_9() {
        accountAccessConsentApi.deleteAccountAccessConsentTest()
    }

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.9",
        operations = ["GetAccountAccessConsent"]
    )
    @Test
    fun getAccountAccessConsents_v3_1_9() {
        accountAccessConsentApi.getAccountAccessConsentTest()
    }
}