package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.scheduled.payments.junit.v3_1_8

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.scheduled.payments.api.v3_1_8.GetAccountScheduledPayments
import org.junit.jupiter.api.Test

class GetAccountScheduledPaymentsTest(val tppResource: CreateTppCallback.TppResource) {
    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.8",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountScheduledPayments"],
        apis = ["scheduled-payments"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldGetAccountScheduledPayments_v3_1_8() {
        GetAccountScheduledPayments(OBVersion.v3_1_8, tppResource).shouldGetAccountScheduledPaymentsTest()
    }
}