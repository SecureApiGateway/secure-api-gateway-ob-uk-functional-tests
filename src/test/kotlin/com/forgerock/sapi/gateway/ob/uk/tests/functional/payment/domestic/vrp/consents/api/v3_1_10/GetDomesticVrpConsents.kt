package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.vrp.consents.api.v3_1_10

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.sapi.gateway.framework.conditions.Status
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.support.payment.PsuData
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import org.assertj.core.api.Assertions
import uk.org.openbanking.datamodel.vrp.OBCashAccountDebtorWithName
import uk.org.openbanking.datamodel.vrp.OBDomesticVRPConsentRequest
import uk.org.openbanking.testsupport.vrp.OBDomesticVrpConsentRequestTestDataFactory

class GetDomesticVrpConsents(
    val version: OBVersion,
    val tppResource: CreateTppCallback.TppResource
) {
    private val createDomesticVrpConsents = CreateDomesticVrpConsents(version, tppResource)

    fun shouldGetDomesticVrpConsents() {
        // Given
        val consentRequest = OBDomesticVrpConsentRequestTestDataFactory.aValidOBDomesticVRPConsentRequest()
        populateDebtorAccount(consentRequest)

        val consent = createDomesticVrpConsents.createDomesticVrpConsent(consentRequest)

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()

        // When
        val result = createDomesticVrpConsents.getPatchedConsent(consent)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.risk).isNotNull()
        assertThat(result.data).isEqualTo(consent.data)
        assertThat(result.risk).isEqualTo(consent.risk)
    }

    private fun populateDebtorAccount(consentRequest: OBDomesticVRPConsentRequest){
        val debtorAccount = PsuData().getDebtorAccount()
        consentRequest.data.initiation.debtorAccount(
            OBCashAccountDebtorWithName()
                .identification(debtorAccount?.Identification)
                .name(debtorAccount?.Name)
                .schemeName(debtorAccount?.SchemeName)
                .secondaryIdentification(debtorAccount?.SecondaryIdentification)
        )
    }

}
