package com.forgerock.uk.openbanking.tests.functional.payment.domestic.scheduled.payments.consents.api.v3_1_8

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import com.forgerock.securebanking.framework.conditions.Status
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.support.payment.PaymentFactory
import com.forgerock.uk.openbanking.support.payment.PaymentRS
import org.assertj.core.api.Assertions
import uk.org.openbanking.datamodel.payment.OBWriteDomesticScheduledConsentResponse5
import uk.org.openbanking.testsupport.payment.OBWriteDomesticScheduledConsentTestDataFactory

class GetDomesticScheduledPaymentsConsents(val version: OBVersion, val tppResource: CreateTppCallback.TppResource) {

    val createDomesticScheduledPaymentsConsents = CreateDomesticScheduledPaymentsConsents(version, tppResource)

    fun shouldGetDomesticScheduledPaymentsConsentsTest() {
        // Given
        val consentRequest = OBWriteDomesticScheduledConsentTestDataFactory.aValidOBWriteDomesticScheduledConsent4()
        val consent = createDomesticScheduledPaymentsConsents.createDomesticScheduledPaymentConsent(consentRequest)

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()

        // When
        val result = getConsent(consent.data.consentId)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.risk).isNotNull()
        assertThat(result.data).isEqualTo(consent.data)
        assertThat(result.risk).isEqualTo(consent.risk)
    }

    fun shouldGetDomesticScheduledPaymentsConsents_withoutOptionalDebtorAccountTest() {
        // Given
        val consentRequest = OBWriteDomesticScheduledConsentTestDataFactory.aValidOBWriteDomesticScheduledConsent4()
        consentRequest.data.initiation.debtorAccount(null)
        val consent = createDomesticScheduledPaymentsConsents.createDomesticScheduledPaymentConsent(consentRequest)

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()
        assertThat(consent.data.initiation.debtorAccount).isNull()

        // When
        val result = getConsent(consent.data.consentId)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.risk).isNotNull()
        assertThat(result.data).isEqualTo(consent.data)
        assertThat(result.risk).isEqualTo(consent.risk)
        assertThat(result.data.initiation.debtorAccount).isNull()
    }

    private fun getConsent(consentId: String): OBWriteDomesticScheduledConsentResponse5 {
        return createDomesticScheduledPaymentsConsents.paymentApiClient.sendGetRequest<OBWriteDomesticScheduledConsentResponse5>(
            PaymentFactory.urlWithConsentId(
                createDomesticScheduledPaymentsConsents.paymentLinks.GetDomesticScheduledPaymentConsent,
                consentId
            ), PaymentRS().getClientCredentialsAccessToken(tppResource.tpp)
        )
    }
}
