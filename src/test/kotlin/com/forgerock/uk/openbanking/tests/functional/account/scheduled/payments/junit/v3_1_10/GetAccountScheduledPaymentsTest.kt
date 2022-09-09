package com.forgerock.uk.openbanking.tests.functional.account.scheduled.payments.junit.v3_1_10

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.account.scheduled.payments.api.v3_1_8.GetAccountScheduledPayments
import org.junit.jupiter.api.Test

class GetAccountScheduledPaymentsTest(val tppResource: CreateTppCallback.TppResource) {
    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.10",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountScheduledPayments"],
        apis = ["scheduled-payments"]
    )
    @Test
    fun shouldGetAccountScheduledPayments_v3_1_10() {
        GetAccountScheduledPayments(OBVersion.v3_1_10, tppResource).shouldGetAccountScheduledPaymentsTest()
    }
}