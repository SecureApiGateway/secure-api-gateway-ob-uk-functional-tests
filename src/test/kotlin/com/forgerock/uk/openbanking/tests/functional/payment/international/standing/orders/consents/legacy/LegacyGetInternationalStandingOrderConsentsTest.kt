package com.forgerock.uk.openbanking.tests.functional.payment.international.standing.orders.consents.legacy

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.securebanking.framework.conditions.Status
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.framework.http.fuel.defaultMapper
import com.forgerock.securebanking.framework.signature.signPayloadSubmitPayment
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.support.discovery.payment3_1_2
import com.forgerock.uk.openbanking.support.discovery.payment3_1_3
import com.forgerock.uk.openbanking.support.discovery.payment3_1_4
import com.forgerock.uk.openbanking.support.discovery.payment3_1_5
import com.forgerock.uk.openbanking.support.payment.PaymentFactory
import com.forgerock.uk.openbanking.support.payment.PaymentRS
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.payment.OBWriteInternationalStandingOrderConsentResponse4
import uk.org.openbanking.datamodel.payment.OBWriteInternationalStandingOrderConsentResponse5
import uk.org.openbanking.datamodel.payment.OBWriteInternationalStandingOrderConsentResponse6
import uk.org.openbanking.datamodel.payment.OBWriteInternationalStandingOrderConsentResponse7
import uk.org.openbanking.testsupport.payment.OBWriteInternationalStandingOrderConsentTestDataFactory.*

class LegacyGetInternationalStandingOrderConsentsTest(val tppResource: CreateTppCallback.TppResource) {
    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.5",
        operations = ["CreateInternationalStandingOrderConsent", "GetInternationalStandingOrderConsent"],
        apis = ["international-standing-order-consents"],
        compatibleVersions = ["v.3.1.6, v.3.1.7, v.3.1.8"]
    )
    @Test
    fun shouldGetInternationalStandingOrdersConsents_v3_1_5() {
        // Given
        val consentRequest = aValidOBWriteInternationalStandingOrderConsent6()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )


        val consent = PaymentRS().consent<OBWriteInternationalStandingOrderConsentResponse7>(
            payment3_1_5.Links.links.CreateInternationalStandingOrderConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_5,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()

        // When
        val result = PaymentRS().getConsent<OBWriteInternationalStandingOrderConsentResponse7>(
            PaymentFactory.urlWithConsentId(
                payment3_1_5.Links.links.GetInternationalStandingOrderConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            OBVersion.v3_1_5
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.risk).isNotNull()
        assertThat(result.data).isEqualTo(consent.data)
        assertThat(result.risk).isEqualTo(consent.risk)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateInternationalStandingOrderConsent", "GetInternationalStandingOrderConsent"],
        apis = ["international-standing-order-consents"]
    )
    @Test
    fun shouldGetInternationalStandingOrdersConsents_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteInternationalStandingOrderConsent6()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )


        val consent = PaymentRS().consent<OBWriteInternationalStandingOrderConsentResponse6>(
            payment3_1_4.Links.links.CreateInternationalStandingOrderConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_4,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()

        // When
        val result = PaymentRS().getConsent<OBWriteInternationalStandingOrderConsentResponse6>(
            PaymentFactory.urlWithConsentId(
                payment3_1_4.Links.links.GetInternationalStandingOrderConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            OBVersion.v3_1_4
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.risk).isNotNull()
        assertThat(result.data).isEqualTo(consent.data)
        assertThat(result.risk).isEqualTo(consent.risk)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["CreateInternationalStandingOrderConsent", "GetInternationalStandingOrderConsent"],
        apis = ["international-standing-order-consents"],
        //compatibleVersions = ["v.3.1.2"]
    )
    @Test
    fun shouldGetInternationalStandingOrdersConsents_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteInternationalStandingOrderConsent5()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        val consent = PaymentRS().consent<OBWriteInternationalStandingOrderConsentResponse5>(
            payment3_1_3.Links.links.CreateInternationalStandingOrderConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_3,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()

        // When
        val result = PaymentRS().getConsent<OBWriteInternationalStandingOrderConsentResponse5>(
            PaymentFactory.urlWithConsentId(
                payment3_1_3.Links.links.GetInternationalStandingOrderConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            OBVersion.v3_1_3
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.risk).isNotNull()
        assertThat(result.data).isEqualTo(consent.data)
        assertThat(result.risk).isEqualTo(consent.risk)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.2",
        operations = ["CreateInternationalStandingOrderConsent", "GetInternationalStandingOrderConsent"],
        apis = ["international-standing-order-consents"]
    )
    @Test
    fun shouldGetDomesticStandingOrdersConsents_v3_1_2() {
        // Given
        val consentRequest = aValidOBWriteInternationalStandingOrderConsent4()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        val consent = PaymentRS().consent<OBWriteInternationalStandingOrderConsentResponse4>(
            payment3_1_2.Links.links.CreateInternationalStandingOrderConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_2,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()

        // When
        val result = PaymentRS().getConsent<OBWriteInternationalStandingOrderConsentResponse4>(
            PaymentFactory.urlWithConsentId(
                payment3_1_2.Links.links.GetInternationalStandingOrderConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            OBVersion.v3_1_2
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.risk).isNotNull()
        assertThat(result.data).isEqualTo(consent.data)
        assertThat(result.risk).isEqualTo(consent.risk)
    }
}
