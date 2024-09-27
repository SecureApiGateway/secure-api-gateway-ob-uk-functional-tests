package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.beneficiaries.junit.v4_0_0
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.beneficiaries.api.v4_0_0.GetAccountBeneficiaries
import org.junit.jupiter.api.Test

class GetAccountBeneficiariesTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "accounts",
        apiVersion = "v4.0.0",
        operations = ["CreateAccountAccessConsent", "GetAccounts", "GetAccountBeneficiaries"],
        apis = ["beneficiaries"]
    )
    @Test
    fun shouldGetAccountBeneficiaries_v4_0_0() {
        GetAccountBeneficiaries(OBVersion.v4_0_0, tppResource).shouldGetAccountBeneficiariesTest()
    }
}