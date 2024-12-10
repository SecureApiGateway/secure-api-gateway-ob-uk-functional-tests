package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.accounts.api.v4_0_0

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThan
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.support.account.AccountRS
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.access.BaseAccountApi4_0_0
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import uk.org.openbanking.datamodel.v4.account.OBInternalPermissions1Code
import uk.org.openbanking.datamodel.v4.account.OBReadAccount6

class GetAccounts(version: OBVersion, tppResource: CreateTppCallback.TppResource) : BaseAccountApi4_0_0(version, tppResource) {
    fun shouldGetAccountsTest() {
        // Given
        val permissions = listOf(OBInternalPermissions1Code.READACCOUNTSDETAIL)
        val (_, accessToken) = accountAccessConsentApi.createConsentAndGetAccessToken(permissions)

        // When
        val result = AccountRS().getAccountsData<OBReadAccount6>(accountsApiLinks.GetAccounts, accessToken)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.account).isNotEmpty()
        assertThat(result.data.account.size).isGreaterThan(0)
    }

    fun shouldGetAccountsTest_getV4Fields() {
        // Given
        val permissions = listOf(OBInternalPermissions1Code.READACCOUNTSDETAIL)
        val (_, accessToken) = accountAccessConsentApi.createConsentAndGetAccessToken(permissions)

        // When
        val result = AccountRS().getAccountsData<OBReadAccount6>(accountsApiLinks.GetAccounts, accessToken)

        val allHaveAccountCategory = result.data.account.all { it.accountCategory != null }
        val allHaveAccountTypeCode = result.data.account.all { it.accountTypeCode != null }
        val allHaveStatementFrequencyAndFormat = result.data.account.all { it.statementFrequencyAndFormat != null }

        // Then
        assertThat(result).isNotNull()
        assertThat(allHaveAccountCategory).isEqualTo(true)
        assertThat(allHaveAccountTypeCode).isEqualTo(true)
        assertThat(allHaveStatementFrequencyAndFormat).isEqualTo(true)
        assertThat(result.data.account).isNotEmpty()
        assertThat(result.data.account.size).isGreaterThan(0)
        assertThat(result.data.account)
    }
}