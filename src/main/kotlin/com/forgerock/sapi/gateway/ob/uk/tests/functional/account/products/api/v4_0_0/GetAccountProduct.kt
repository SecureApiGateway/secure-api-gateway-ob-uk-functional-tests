package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.products.api.v4_0_0

import assertk.assertThat
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.sapi.gateway.framework.configuration.USER_ACCOUNT_ID
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.support.account.AccountFactoryV4
import com.forgerock.sapi.gateway.ob.uk.support.account.AccountRS
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.access.BaseAccountApi4_0_0
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import uk.org.openbanking.datamodel.v4.account.*

class GetAccountProduct(version: OBVersion, tppResource: CreateTppCallback.TppResource): BaseAccountApi4_0_0(version, tppResource) {
    fun shouldGetAccountProductTest() {
        // Given
        val permissions = listOf(
            OBInternalPermissions1Code.READACCOUNTSDETAIL, OBInternalPermissions1Code.READPRODUCTS
        )
        val (_, accessToken) = accountAccessConsentApi.createConsentAndGetAccessToken(permissions)

        // When
        val result = AccountRS().getAccountsData<OBReadProduct2>(
            AccountFactoryV4.urlWithAccountId(
                accountsApiLinks.GetAccountProduct,
                USER_ACCOUNT_ID
            ), accessToken
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.product).isNotEmpty()
    }
}