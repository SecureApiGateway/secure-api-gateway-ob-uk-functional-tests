package com.forgerock.uk.openbanking.tests.functional.payment.domestic.standing.order.api.v3_1_8

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.securebanking.framework.conditions.Status
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.http.fuel.defaultMapper
import com.forgerock.securebanking.framework.signature.signPayloadSubmitPayment
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.support.payment.PaymentFactory
import com.forgerock.uk.openbanking.support.payment.PaymentRS
import com.forgerock.uk.openbanking.tests.functional.payment.domestic.standing.order.consents.api.v3_1_8.CreateDomesticStandingOrderConsents
import org.assertj.core.api.Assertions
import uk.org.openbanking.datamodel.payment.*
import uk.org.openbanking.testsupport.payment.OBWriteDomesticStandingOrderConsentTestDataFactory

class GetDomesticStandingOrderDomesticStandingOrderIdPaymentDetails(
    val version: OBVersion,
    val tppResource: CreateTppCallback.TppResource
) {

    private val createDomesticStandingOrderConsentsApi = CreateDomesticStandingOrderConsents(version, tppResource)

    fun getDomesticStandingOrderDomesticStandingOrderIdPaymentDetailsTest() {
        // Given
        val consentRequest =
            OBWriteDomesticStandingOrderConsentTestDataFactory.aValidOBWriteDomesticStandingOrderConsent5()
        val (consent, accessTokenAuthorizationCode) = createDomesticStandingOrderConsentsApi.createDomesticStandingOrderConsentAndGetAccessToken(
            consentRequest
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentRS().getClientCredentialsAccessToken(tppResource.tpp)

        val patchedConsent = PaymentRS().getConsent<OBWriteDomesticStandingOrderConsentResponse6>(
            PaymentFactory.urlWithConsentId(
                createDomesticStandingOrderConsentsApi.paymentLinks.GetDomesticStandingOrderConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            version
        )

        assertThat(patchedConsent).isNotNull()
        assertThat(patchedConsent.data).isNotNull()
        assertThat(patchedConsent.risk).isNotNull()
        assertThat(patchedConsent.data.consentId).isNotEmpty()
        Assertions.assertThat(patchedConsent.data.status.toString()).`is`(Status.consentCondition)

        val standingOrderSubmissionRequest = OBWriteDomesticStandingOrder3().data(
            OBWriteDomesticStandingOrder3Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        val signedPayload = signPayloadSubmitPayment(
            defaultMapper.writeValueAsString(standingOrderSubmissionRequest),
            tppResource.tpp.signingKey,
            tppResource.tpp.signingKid
        )

        val submissionResponse = PaymentRS().submitPayment<OBWriteDomesticStandingOrderResponse6>(
            createDomesticStandingOrderConsentsApi.paymentLinks.CreateDomesticStandingOrder,
            standingOrderSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            version
        )

        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.domesticStandingOrderId).isNotEmpty()

        // When
        val result = PaymentRS().getPayment<OBWritePaymentDetailsResponse1>(
            PaymentFactory.urlWithDomesticStandingOrderId(
                createDomesticStandingOrderConsentsApi.paymentLinks.GetDomesticStandingOrderDomesticStandingOrderIdPaymentDetails,
                submissionResponse.data.domesticStandingOrderId
            ),
            accessTokenClientCredentials,
            tppResource.tpp
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.paymentStatus).isNotNull()
    }

    fun getDomesticStandingOrderDomesticStandingOrderIdPaymentDetails_mandatoryFieldsTest() {
        // Given
        val consentRequest =
            OBWriteDomesticStandingOrderConsentTestDataFactory.aValidOBWriteDomesticStandingOrderConsent5MandatoryFields()
        val (consent, accessTokenAuthorizationCode) = createDomesticStandingOrderConsentsApi.createDomesticStandingOrderConsentAndGetAccessToken(
            consentRequest
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentRS().getClientCredentialsAccessToken(tppResource.tpp)

        val patchedConsent = PaymentRS().getConsent<OBWriteDomesticStandingOrderConsentResponse6>(
            PaymentFactory.urlWithConsentId(
                createDomesticStandingOrderConsentsApi.paymentLinks.GetDomesticStandingOrderConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            version
        )

        assertThat(patchedConsent).isNotNull()
        assertThat(patchedConsent.data).isNotNull()
        assertThat(patchedConsent.risk).isNotNull()
        assertThat(patchedConsent.data.consentId).isNotEmpty()
        Assertions.assertThat(patchedConsent.data.status.toString()).`is`(Status.consentCondition)

        val standingOrderSubmissionRequest = OBWriteDomesticStandingOrder3().data(
            OBWriteDomesticStandingOrder3Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        val signedPayload = signPayloadSubmitPayment(
            defaultMapper.writeValueAsString(standingOrderSubmissionRequest),
            tppResource.tpp.signingKey,
            tppResource.tpp.signingKid
        )

        val submissionResponse = PaymentRS().submitPayment<OBWriteDomesticStandingOrderResponse6>(
            createDomesticStandingOrderConsentsApi.paymentLinks.CreateDomesticStandingOrder,
            standingOrderSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            version
        )

        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.domesticStandingOrderId).isNotEmpty()

        // When
        val result = PaymentRS().getPayment<OBWritePaymentDetailsResponse1>(
            PaymentFactory.urlWithDomesticStandingOrderId(
                createDomesticStandingOrderConsentsApi.paymentLinks.GetDomesticStandingOrderDomesticStandingOrderIdPaymentDetails,
                submissionResponse.data.domesticStandingOrderId
            ),
            accessTokenClientCredentials,
            tppResource.tpp
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.paymentStatus).isNotNull()
    }

}