package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.statements.api.v4_0_0

import assertk.assertThat
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.support.account.AccountRS
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.access.BaseAccountApi4_0_0
import uk.org.openbanking.datamodel.v4.account.OBReadStatement2
import uk.org.openbanking.datamodel.v4.account.OBInternalPermissions1Code

class GetStatements(version: OBVersion, tppResource: CreateTppCallback.TppResource) :
    BaseAccountApi4_0_0(version, tppResource) {

    fun shouldGetStatementsTest() {
        // Given
        val permissions = listOf(
            OBInternalPermissions1Code.READSTATEMENTSBASIC,
            OBInternalPermissions1Code.READSTATEMENTSDETAIL,
            OBInternalPermissions1Code.READACCOUNTSDETAIL
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