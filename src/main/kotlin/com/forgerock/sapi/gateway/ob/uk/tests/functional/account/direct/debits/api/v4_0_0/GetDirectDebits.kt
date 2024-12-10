package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.direct.debits.api.v4_0_0

import assertk.assertThat
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.support.account.AccountRS
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.access.BaseAccountApi4_0_0
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import uk.org.openbanking.datamodel.v4.account.OBInternalPermissions1Code
import uk.org.openbanking.datamodel.v4.account.OBReadDirectDebit2

class GetDirectDebits(version: OBVersion, tppResource: CreateTppCallback.TppResource) : BaseAccountApi4_0_0(version, tppResource) {
    fun shouldGetDirectDebitsTest() {
        // Given
        val permissions = listOf(OBInternalPermissions1Code.READDIRECTDEBITS)
        val (_, accessToken) = accountAccessConsentApi.createConsentAndGetAccessToken(permissions)

        // When
        val result = AccountRS().getAccountsData<OBReadDirectDebit2>(
                accountsApiLinks.GetDirectDebits,
                accessToken
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.directDebit).isNotEmpty()
    }

    fun shouldGetDirectDebitsTest_mandateRelatedInformation() {
        // Given
        val permissions = listOf(OBInternalPermissions1Code.READDIRECTDEBITS)
        val (_, accessToken) = accountAccessConsentApi.createConsentAndGetAccessToken(permissions)

        // When
        val result = AccountRS().getAccountsData<OBReadDirectDebit2>(
                accountsApiLinks.GetDirectDebits,
                accessToken
        )

        val allHaveMandateRelatedInformation = result.data.directDebit.all { it.mandateRelatedInformation != null }

        // Then
        assertThat(result).isNotNull()
        assertThat(allHaveMandateRelatedInformation).isNotNull()
        assertThat(result.data.directDebit).isNotEmpty()
    }
}