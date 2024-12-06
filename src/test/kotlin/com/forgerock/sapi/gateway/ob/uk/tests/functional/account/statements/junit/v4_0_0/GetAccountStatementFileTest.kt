package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.statements.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.statements.api.v4_0_0.GetAccountStatementFile
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetAccountStatementFileTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getAccountStatementFileApi: GetAccountStatementFile

    @BeforeEach
    fun setUp() {
        getAccountStatementFileApi = GetAccountStatementFile(OBVersion.v4_0_0, tppResource)
    }

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v4.0.0",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountStatementFile"],
        apis = ["statements"]
    )
    @Test
    fun shouldGet_badRequest_StatementFile_v4_0_0() {
        getAccountStatementFileApi.shouldGet_badRequest_StatementFileTest()
    }

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v4.0.0",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountStatementFile"],
        apis = ["statements"]
    )
    @Test
    fun shouldGetStatementFile_v4_0_0() {
        getAccountStatementFileApi.shouldGetStatementFileTest()
    }
}