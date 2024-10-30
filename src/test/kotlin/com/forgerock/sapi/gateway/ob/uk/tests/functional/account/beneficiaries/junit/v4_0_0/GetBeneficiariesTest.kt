package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.beneficiaries.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.beneficiaries.api.v4_0_0.GetBeneficiaries
import org.junit.jupiter.api.Test

class GetBeneficiariesTest(val tppResource: CreateTppCallback.TppResource) {
    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v4.0.0",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetBeneficiaries"],
        apis = ["beneficiaries"]
    )
    @Test
    fun shouldGetBeneficiaries_v4_0_0() {
        GetBeneficiaries(OBVersion.v4_0_0, tppResource).shouldGetBeneficiariesTest()
    }
}