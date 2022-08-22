package com.forgerock.uk.openbanking.tests.functional.payment.domestic.payments.consents.api.v3_1_8

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
import uk.org.openbanking.datamodel.payment.OBWriteDomesticConsentResponse5
import uk.org.openbanking.testsupport.payment.OBWriteDomesticConsentTestDataFactory

class GetDomesticPaymentsConsents(val version: OBVersion, val tppResource: CreateTppCallback.TppResource) {

    private val createDomesticPaymentsConsentsApi = CreateDomesticPaymentsConsents(version, tppResource)

    fun shouldGetDomesticPaymentsConsents() {
        // Given
        val consentRequest = OBWriteDomesticConsentTestDataFactory.aValidOBWriteDomesticConsent4()
        val consent = createDomesticPaymentsConsentsApi.createDomesticPaymentsConsent(consentRequest)

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()

        // When
        val result = PaymentRS().getConsent<OBWriteDomesticConsentResponse5>(
            PaymentFactory.urlWithConsentId(
                createDomesticPaymentsConsentsApi.paymentLinks.GetDomesticPaymentConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            version
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.risk).isNotNull()
        assertThat(result.data).isEqualTo(consent.data)
        assertThat(result.risk).isEqualTo(consent.risk)
    }

    fun shouldGetDomesticPaymentsConsents_withoutOptionalDebtorAccountTest() {
        // Given
        val consentRequest = OBWriteDomesticConsentTestDataFactory.aValidOBWriteDomesticConsent4()
        consentRequest.data.initiation.debtorAccount(null)

        val consent = createDomesticPaymentsConsentsApi.createDomesticPaymentsConsent(consentRequest)

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()
        assertThat(consent.data.initiation.debtorAccount).isNull()

        // When
        val result = PaymentRS().getConsent<OBWriteDomesticConsentResponse5>(
            PaymentFactory.urlWithConsentId(
                createDomesticPaymentsConsentsApi.paymentLinks.GetDomesticPaymentConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            version
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.risk).isNotNull()
        assertThat(result.data).isEqualTo(consent.data)
        assertThat(result.risk).isEqualTo(consent.risk)
        assertThat(result.data.initiation.debtorAccount).isNull()
    }
}