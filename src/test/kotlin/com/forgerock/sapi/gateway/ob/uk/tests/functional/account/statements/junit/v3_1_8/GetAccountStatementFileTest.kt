package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.statements.junit.v3_1_8

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.statements.api.v3_1_8.GetAccountStatementFile
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetAccountStatementFileTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getAccountStatementFileApi: GetAccountStatementFile

    @BeforeEach
    fun setUp() {
        getAccountStatementFileApi = GetAccountStatementFile(OBVersion.v3_1_8, tppResource)
    }

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.8",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountStatementFile"],
        apis = ["statements"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5", "v.3.1.4", "v.3.1.3"]
    )
    @Test
    fun shouldGet_badRequest_StatementFile_v3_1_8() {
        getAccountStatementFileApi.shouldGet_badRequest_StatementFileTest()
    }

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.8",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountStatementFile"],
        apis = ["statements"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5", "v.3.1.4", "v.3.1.3"]
    )
    @Test
    fun shouldGetStatementFile_v3_1_8() {
        getAccountStatementFileApi.shouldGetStatementFileTest()
    }
}