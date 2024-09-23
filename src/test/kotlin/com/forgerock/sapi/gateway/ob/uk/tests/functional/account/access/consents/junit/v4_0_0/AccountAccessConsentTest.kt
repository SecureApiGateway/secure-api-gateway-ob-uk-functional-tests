package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.access.consents.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.access.consents.api.v4_0_0.AccountAccessConsent
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AccountAccessConsentTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var accountAccessConsentApi: AccountAccessConsent

    @BeforeEach
    fun setUp() {
        accountAccessConsentApi = AccountAccessConsent(OBVersion.v4_0_0, tppResource)
    }

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v4.0",
        operations = ["CreateAccountAccessConsent"]
    )
    @Test
    fun createAccountAccessConsents_v4_0_0() {
        accountAccessConsentApi.createAccountAccessConsentTest()
    }

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v4.0",
        operations = ["DeleteAccountAccessConsent"]
    )
    @Test
    fun deleteAccountAccessConsents_v4_0_0() {
        accountAccessConsentApi.deleteAccountAccessConsentTest()
    }

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v4.0",
        operations = ["GetAccountAccessConsent"]
    )
    @Test
    fun getAccountAccessConsents_v4_0_0() {
        accountAccessConsentApi.getAccountAccessConsentTest()
    }
}