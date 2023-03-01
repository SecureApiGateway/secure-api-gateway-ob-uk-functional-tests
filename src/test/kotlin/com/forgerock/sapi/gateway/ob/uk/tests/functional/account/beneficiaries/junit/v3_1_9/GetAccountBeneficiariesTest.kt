package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.beneficiaries.junit.v3_1_9

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.beneficiaries.api.v3_1_8.GetAccountBeneficiaries
import org.junit.jupiter.api.Test

class GetAccountBeneficiariesTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.9",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountBeneficiaries"],
        apis = ["beneficiaries"]
    )
    @Test
    fun shouldGetAccountBeneficiaries_v3_1_9() {
        GetAccountBeneficiaries(OBVersion.v3_1_9, tppResource).shouldGetAccountBeneficiariesTest()
    }
}