package com.forgerock.uk.openbanking.tests.functional.account.scheduled.payments.junit.v3_1_10

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.account.scheduled.payments.api.v3_1_8.GetScheduledPayments
import org.junit.jupiter.api.Test

class GetScheduledPaymentsTest(val tppResource: CreateTppCallback.TppResource) {
    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.10",
        operations = ["CreateAccountAccessConsent", "GetScheduledPayments"],
        apis = ["scheduled-payments"]
    )
    @Test
    fun shouldGetScheduledPayments_v3_1_10() {
        GetScheduledPayments(OBVersion.v3_1_10, tppResource).shouldGetScheduledPaymentsTest()
    }
}