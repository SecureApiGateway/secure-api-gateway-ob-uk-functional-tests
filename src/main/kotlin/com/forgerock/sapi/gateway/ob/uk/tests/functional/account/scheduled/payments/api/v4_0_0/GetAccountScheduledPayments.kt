package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.scheduled.payments.api.v4_0_0

import assertk.assertThat
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.sapi.gateway.framework.configuration.USER_ACCOUNT_ID
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.support.account.AccountFactoryV4
import com.forgerock.sapi.gateway.ob.uk.support.account.AccountRS
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.access.BaseAccountApi4_0_0
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import uk.org.openbanking.datamodel.v4.account.OBInternalPermissions1Code
import uk.org.openbanking.datamodel.v4.account.OBReadScheduledPayment3

class GetAccountScheduledPayments(version: OBVersion, tppResource: CreateTppCallback.TppResource) :
    BaseAccountApi4_0_0(version, tppResource) {

    fun shouldGetAccountScheduledPaymentsTest() {
        // Given
        val permissions = listOf(
            OBInternalPermissions1Code.READACCOUNTSDETAIL,
            OBInternalPermissions1Code.READSCHEDULEDPAYMENTSDETAIL
        )
        val (_, accessToken) = accountAccessConsentApi.createConsentAndGetAccessToken(permissions)

        // When
        val result = AccountRS().getAccountsData<OBReadScheduledPayment3>(
            AccountFactoryV4.urlWithAccountId(
                accountsApiLinks.GetAccountScheduledPayments,
                USER_ACCOUNT_ID
            ), accessToken
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.scheduledPayment).isNotEmpty()
    }
}