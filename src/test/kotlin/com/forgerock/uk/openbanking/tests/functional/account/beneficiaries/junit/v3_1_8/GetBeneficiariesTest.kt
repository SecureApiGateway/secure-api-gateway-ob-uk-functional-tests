package com.forgerock.uk.openbanking.tests.functional.account.beneficiaries.junit.v3_1_8

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.account.beneficiaries.api.v3_1_8.GetBeneficiaries
import org.junit.jupiter.api.Test

class GetBeneficiariesTest(val tppResource: CreateTppCallback.TppResource) {
    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.8",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetBeneficiaries"],
        apis = ["beneficiaries"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldGetBeneficiaries_v3_1_8() {
        GetBeneficiaries(OBVersion.v3_1_8, tppResource).shouldGetBeneficiariesTest()
    }
}