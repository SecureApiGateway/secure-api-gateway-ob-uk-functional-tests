package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.products.api.v3_1_8

import assertk.assertThat
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.support.account.AccountRS
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.access.BaseAccountApi3_1_8
import uk.org.openbanking.datamodel.v3.account.*
import uk.org.openbanking.datamodel.v3.common.OBExternalPermissions1Code

class GetProducts(version: OBVersion, tppResource: CreateTppCallback.TppResource): BaseAccountApi3_1_8(version, tppResource) {
    fun shouldGetProductsTest() {
        // Given
        val permissions = listOf(
            OBExternalPermissions1Code.READACCOUNTSDETAIL, OBExternalPermissions1Code.READPRODUCTS
        )
        val (_, accessToken) = accountAccessConsentApi.createConsentAndGetAccessToken(permissions)

        // When
        val result = AccountRS().getAccountsData<OBReadProduct2>(
            accountsApiLinks.GetProducts, accessToken
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.product).isNotEmpty()
    }
}
