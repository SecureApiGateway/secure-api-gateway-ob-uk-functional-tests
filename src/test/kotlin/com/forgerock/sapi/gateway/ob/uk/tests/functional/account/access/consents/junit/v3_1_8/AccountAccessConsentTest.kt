package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.access.consents.junit.v3_1_8

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.access.consents.api.v3_1_8.AccountAccessConsent
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AccountAccessConsentTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var accountAccessConsentApi: AccountAccessConsent

    @BeforeEach
    fun setUp() {
        accountAccessConsentApi = AccountAccessConsent(OBVersion.v3_1_8, tppResource)
    }

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.8",
        operations = ["CreateAccountAccessConsent"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5", "v.3.1.4", "v.3.1.3", "v.3.1.2", "v.3.1.1", "v.3.1", "v.3.0"]
    )
    @Test
    fun createAccountAccessConsents_v3_1_8() {
        accountAccessConsentApi.createAccountAccessConsentTest()
    }

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.8",
        operations = ["DeleteAccountAccessConsent"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5", "v.3.1.4", "v.3.1.3", "v.3.1.2", "v.3.1.1", "v.3.1", "v.3.0"]
    )
    @Test
    fun deleteAccountAccessConsents_v3_1_8() {
        accountAccessConsentApi.deleteAccountAccessConsentTest()
    }

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.8",
        operations = ["GetAccountAccessConsent"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5", "v.3.1.4", "v.3.1.3", "v.3.1.2", "v.3.1.1", "v.3.1", "v.3.0"]
    )
    @Test
    fun getAccountAccessConsents_v3_1_8() {
        accountAccessConsentApi.getAccountAccessConsentTest()
    }
}