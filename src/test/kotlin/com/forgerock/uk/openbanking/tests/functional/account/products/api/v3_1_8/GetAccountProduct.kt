package com.forgerock.uk.openbanking.tests.functional.account.products.api.v3_1_8

import assertk.assertThat
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.securebanking.framework.configuration.USER_ACCOUNT_ID
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.support.account.AccountFactory
import com.forgerock.uk.openbanking.support.account.AccountRS
import com.forgerock.uk.openbanking.tests.functional.account.access.BaseAccountApi3_1_8
import uk.org.openbanking.datamodel.account.OBExternalPermissions1Code
import uk.org.openbanking.datamodel.account.OBReadProduct2

class GetAccountProduct(version: OBVersion, tppResource: CreateTppCallback.TppResource): BaseAccountApi3_1_8(version, tppResource) {
    fun shouldGetAccountProductTest() {
        // Given
        val permissions = listOf(
            OBExternalPermissions1Code.READACCOUNTSDETAIL, OBExternalPermissions1Code.READPRODUCTS
        )
        val (_, accessToken) = accountAccessConsentApi.createConsentAndGetAccessToken(permissions)

        // When
        val result = AccountRS().getAccountsData<OBReadProduct2>(
            AccountFactory.urlWithAccountId(
                accountsApiLinks.GetAccountProduct,
                USER_ACCOUNT_ID
            ), accessToken
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.product).isNotEmpty()
    }
}