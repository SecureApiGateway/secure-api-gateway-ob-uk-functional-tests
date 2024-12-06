package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.scheduled.payments.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.scheduled.payments.api.v4_0_0.GetScheduledPayments
import org.junit.jupiter.api.Test

class GetScheduledPaymentsTest(val tppResource: CreateTppCallback.TppResource) {
    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v4.0.0",
        operations = ["CreateAccountAccessConsent", "GetScheduledPayments"],
        apis = ["scheduled-payments"]
    )
    @Test
    fun shouldGetScheduledPayments_v4_0_0() {
        GetScheduledPayments(OBVersion.v4_0_0, tppResource).shouldGetScheduledPaymentsTest()
    }
}