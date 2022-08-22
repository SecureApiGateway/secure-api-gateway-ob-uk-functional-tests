package com.forgerock.uk.openbanking.tests.functional.account.statements.junit.v3_1_9

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.account.statements.api.v3_1_8.GetAccountStatementFile
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetAccountStatementFileTest(val tppResource: CreateTppCallback.TppResource) {

    lateinit var getAccountStatementFileApi: GetAccountStatementFile

    @BeforeEach
    fun setUp() {
        getAccountStatementFileApi = GetAccountStatementFile(OBVersion.v3_1_9, tppResource)
    }

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.9",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountStatementFile"],
        apis = ["statements"]
    )
    @Test
    fun shouldGet_badRequest_StatementFile_v3_1_9() {
        getAccountStatementFileApi.shouldGet_badRequest_StatementFileTest()
    }

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.9",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountStatementFile"],
        apis = ["statements"]
    )
    @Test
    fun shouldGetStatementFile_v3_1_9() {
        getAccountStatementFileApi.shouldGetStatementFileTest()
    }
}