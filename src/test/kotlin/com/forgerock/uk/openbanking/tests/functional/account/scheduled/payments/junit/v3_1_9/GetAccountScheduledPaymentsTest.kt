package com.forgerock.uk.openbanking.tests.functional.account.scheduled.payments.junit.v3_1_9

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.account.scheduled.payments.api.v3_1_8.GetAccountScheduledPayments
import org.junit.jupiter.api.Test

class GetAccountScheduledPaymentsTest(val tppResource: CreateTppCallback.TppResource) {
    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.9",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountScheduledPayments"],
        apis = ["scheduled-payments"]
    )
    @Test
    fun shouldGetAccountScheduledPayments_v3_1_9() {
        GetAccountScheduledPayments(OBVersion.v3_1_9, tppResource).shouldGetAccountScheduledPaymentsTest()
    }
}