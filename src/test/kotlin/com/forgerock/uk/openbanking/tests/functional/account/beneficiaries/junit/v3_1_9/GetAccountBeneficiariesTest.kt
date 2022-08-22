package com.forgerock.uk.openbanking.tests.functional.account.beneficiaries.junit.v3_1_9

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.account.beneficiaries.api.v3_1_8.GetAccountBeneficiaries
import org.junit.jupiter.api.Test

class GetAccountBeneficiariesTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v3.1.9",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountBeneficiaries"],
        apis = ["beneficiaries"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun shouldGetAccountBeneficiaries_v3_1_9() {
        GetAccountBeneficiaries(OBVersion.v3_1_9, tppResource).shouldGetAccountBeneficiariesTest()
    }
}