package com.forgerock.uk.openbanking.tests.functional.account.offers.api.v3_1_8

import assertk.assertThat
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.securebanking.framework.configuration.USER_ACCOUNT_ID
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.support.account.AccountFactory
import com.forgerock.uk.openbanking.support.account.AccountRS
import com.forgerock.uk.openbanking.tests.functional.account.access.BaseAccountApi3_1_8
import uk.org.openbanking.datamodel.account.*

class GetAccountOffers(version: OBVersion, tppResource: CreateTppCallback.TppResource): BaseAccountApi3_1_8(version, tppResource) {
    fun shouldGetAccountOffersTest() {
        // Given
        val permissions = listOf(
            OBExternalPermissions1Code.READACCOUNTSDETAIL,
            OBExternalPermissions1Code.READOFFERS
        )
        val (_, accessToken) = accountAccessConsentApi.createConsentAndGetAccessToken(permissions)

        // When
        val result = AccountRS().getAccountsData<OBReadOffer1>(
            AccountFactory.urlWithAccountId(
                accountsApiLinks.GetAccountOffers,
                USER_ACCOUNT_ID
            ), accessToken
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.offer).isNotEmpty()
    }
}