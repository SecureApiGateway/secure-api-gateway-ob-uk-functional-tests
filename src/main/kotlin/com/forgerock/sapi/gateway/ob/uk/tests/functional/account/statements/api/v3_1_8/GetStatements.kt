package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.statements.api.v3_1_8

import assertk.assertThat
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.support.account.AccountRS
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.access.BaseAccountApi3_1_8
import uk.org.openbanking.datamodel.v3.common.OBExternalPermissions1Code
import uk.org.openbanking.datamodel.v3.account.OBReadStatement2

class GetStatements(version: OBVersion, tppResource: CreateTppCallback.TppResource) :
    BaseAccountApi3_1_8(version, tppResource) {

    fun shouldGetStatementsTest() {
        // Given
        val permissions = listOf(
            OBExternalPermissions1Code.READSTATEMENTSBASIC,
            OBExternalPermissions1Code.READSTATEMENTSDETAIL,
            OBExternalPermissions1Code.READACCOUNTSDETAIL
        )
        val (_, accessToken) = accountAccessConsentApi.createConsentAndGetAccessToken(permissions)

        // When
        val result = AccountRS().getAccountsData<OBReadStatement2>(
            accountsApiLinks.GetStatements,
            accessToken
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.statement).isNotEmpty()
    }
}