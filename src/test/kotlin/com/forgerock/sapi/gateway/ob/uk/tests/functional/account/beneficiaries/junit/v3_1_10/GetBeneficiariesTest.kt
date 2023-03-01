package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.beneficiaries.junit.v3_1_10

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.beneficiaries.api.v3_1_8.GetBeneficiaries
import org.junit.jupiter.api.Test

class GetBeneficiariesTest(val tppResource: CreateTppCallback.TppResource) {
    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.10",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetBeneficiaries"],
        apis = ["beneficiaries"]
    )
    @Test
    fun shouldGetBeneficiaries_v3_1_10() {
        GetBeneficiaries(OBVersion.v3_1_10, tppResource).shouldGetBeneficiariesTest()
    }
}