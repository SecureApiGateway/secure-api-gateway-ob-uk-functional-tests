package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.accounts.api.v4_0_0

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.sapi.gateway.framework.configuration.USER_ACCOUNT_ID
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.support.account.AccountRS
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.access.BaseAccountApi4_0_0
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import uk.org.openbanking.datamodel.v4.account.OBInternalPermissions1Code
import uk.org.openbanking.datamodel.v4.account.OBReadAccount6

class GetAccount(version: OBVersion, tppResource: CreateTppCallback.TppResource) : BaseAccountApi4_0_0(version, tppResource) {
    fun shouldGetAccountTest() {
        // Given
        val permissions = listOf(OBInternalPermissions1Code.READACCOUNTSDETAIL)
        val (_, accessToken) = accountAccessConsentApi.createConsentAndGetAccessToken(permissions)

        // When
        val result = AccountRS().getAccountData<OBReadAccount6>(
                accountsApiLinks.GetAccount,
                accessToken,
                USER_ACCOUNT_ID
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.account).isNotEmpty()
        assertThat(result.data.account.size).isEqualTo(1)
        assertThat(result.data.account[0].accountId).isEqualTo(USER_ACCOUNT_ID)
    }

    fun shouldGetAccount_getV4FieldsTest() {
        // Given
        val permissions = listOf(OBInternalPermissions1Code.READACCOUNTSDETAIL)
        val (_, accessToken) = accountAccessConsentApi.createConsentAndGetAccessToken(permissions)

        // When
        val result = AccountRS().getAccountData<OBReadAccount6>(
            accountsApiLinks.GetAccount,
            accessToken,
            USER_ACCOUNT_ID
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.account).isNotEmpty()
        assertThat(result.data.account.size).isEqualTo(1)
        assertThat(result.data.account[0].accountId).isEqualTo(USER_ACCOUNT_ID)
        assertThat(result.data.account[0].accountCategory).isNotNull()
        assertThat(result.data.account[0].accountTypeCode).isNotNull()
        assertThat(result.data.account[0].statementFrequencyAndFormat).isNotNull()
        assertThat(result.data.account[0].servicer.name).isNotNull()
        assertThat(result.data.account[0].account[0].lei).isNotNull()
    }
}