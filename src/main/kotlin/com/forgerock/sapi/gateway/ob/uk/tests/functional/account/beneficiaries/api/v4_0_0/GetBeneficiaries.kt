package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.beneficiaries.api.v4_0_0

import assertk.assertThat
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.support.account.AccountRS
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.access.BaseAccountApi4_0_0
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import uk.org.openbanking.datamodel.v4.account.OBInternalPermissions1Code
import uk.org.openbanking.datamodel.v4.account.OBReadBeneficiary5

class GetBeneficiaries(version: OBVersion, tppResource: CreateTppCallback.TppResource) : BaseAccountApi4_0_0(version, tppResource) {
    fun shouldGetBeneficiariesTest() {
        // Given
        val permissions = listOf(
                OBInternalPermissions1Code.READACCOUNTSDETAIL,
                OBInternalPermissions1Code.READBENEFICIARIESDETAIL
        )
        val (_, accessToken) = accountAccessConsentApi.createConsentAndGetAccessToken(permissions)

        // When
        val result = AccountRS().getAccountsData<OBReadBeneficiary5>(
                accountsApiLinks.GetBeneficiaries,
                accessToken
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.beneficiary).isNotEmpty()
    }
}
