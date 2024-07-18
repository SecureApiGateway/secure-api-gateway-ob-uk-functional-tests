package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.beneficiaries.api.v3_1_8

import assertk.assertThat
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.support.account.AccountRS
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.access.BaseAccountApi3_1_8
import uk.org.openbanking.datamodel.v3.account.*
import uk.org.openbanking.datamodel.v3.common.OBExternalPermissions1Code

class GetBeneficiaries(version: OBVersion, tppResource: CreateTppCallback.TppResource): BaseAccountApi3_1_8(version, tppResource) {
    fun shouldGetBeneficiariesTest() {
        // Given
        val permissions = listOf(
            OBExternalPermissions1Code.READACCOUNTSDETAIL,
            OBExternalPermissions1Code.READBENEFICIARIESDETAIL
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
