package com.forgerock.uk.openbanking.tests.functional.payment.international.scheduled.payments.api.v3_1_8

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
import com.forgerock.uk.openbanking.tests.functional.payment.international.scheduled.payments.consents.api.v3_1_8.CreateInternationalScheduledPaymentsConsents
import org.assertj.core.api.Assertions
import uk.org.openbanking.datamodel.payment.*
import uk.org.openbanking.testsupport.payment.OBWriteInternationalScheduledConsentTestDataFactory

class GetInternationalScheduledPayment(val version: OBVersion, val tppResource: CreateTppCallback.TppResource) {

    private val createInternationalScheduledPaymentsConsents =
        CreateInternationalScheduledPaymentsConsents(version, tppResource)

    fun getInternationalScheduledPayments_rateType_AGREED_Test() {
        // Given
        val consentRequest =
            OBWriteInternationalScheduledConsentTestDataFactory.aValidOBWriteInternationalScheduledConsent5()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.AGREED

        val (consent, accessTokenAuthorizationCode) = createInternationalScheduledPaymentsConsents.createInternationalScheduledPaymentConsentAndGetAccessToken(
            consentRequest
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        assertThat(consent.data.initiation.exchangeRateInformation.exchangeRate).isNotNull()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentRS().getClientCredentialsAccessToken(tppResource.tpp)

        val patchedConsent = PaymentRS().getConsent<OBWriteInternationalScheduledConsentResponse6>(
            PaymentFactory.urlWithConsentId(
                createInternationalScheduledPaymentsConsents.paymentLinks.GetInternationalScheduledPaymentConsent,
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

        val standingOrderSubmissionRequest = OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(
                    PaymentFactory.mapOBWriteInternationalScheduledConsentResponse6DataInitiationToOBWriteInternationalScheduled3DataInitiation(
                        patchedConsent.data.initiation
                    )
                )
        ).risk(patchedConsent.risk)

        val signedPayload = signPayloadSubmitPayment(
            defaultMapper.writeValueAsString(standingOrderSubmissionRequest),
            tppResource.tpp.signingKey,
            tppResource.tpp.signingKid
        )

        val submissionResponse = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse5>(
            createInternationalScheduledPaymentsConsents.paymentLinks.CreateInternationalScheduledPayment,
            standingOrderSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            version
        )

        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.internationalScheduledPaymentId).isNotEmpty()

        // When
        val result = PaymentRS().getPayment<OBWriteInternationalScheduledResponse5>(
            PaymentFactory.urlWithInternationalScheduledPaymentId(
                createInternationalScheduledPaymentsConsents.paymentLinks.GetInternationalScheduledPayment,
                submissionResponse.data.internationalScheduledPaymentId
            ),
            accessTokenClientCredentials,
            tppResource.tpp
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.internationalScheduledPaymentId).isNotEmpty()
        assertThat(result.data.creationDateTime).isNotNull()
        Assertions.assertThat(result.data.status.toString()).`is`(Status.paymentCondition)
        assertThat(result.data.exchangeRateInformation.exchangeRate).isNotNull()
    }

    fun getInternationalScheduledPayments_rateType_ACTUAL_Test() {
        // Given
        val consentRequest =
            OBWriteInternationalScheduledConsentTestDataFactory.aValidOBWriteInternationalScheduledConsent5()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.ACTUAL
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null

        val (consent, accessTokenAuthorizationCode) = createInternationalScheduledPaymentsConsents.createInternationalScheduledPaymentConsentAndGetAccessToken(
            consentRequest
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentRS().getClientCredentialsAccessToken(tppResource.tpp)

        val patchedConsent = PaymentRS().getConsent<OBWriteInternationalScheduledConsentResponse6>(
            PaymentFactory.urlWithConsentId(
                createInternationalScheduledPaymentsConsents.paymentLinks.GetInternationalScheduledPaymentConsent,
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

        val standingOrderSubmissionRequest = OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(
                    PaymentFactory.mapOBWriteInternationalScheduledConsentResponse6DataInitiationToOBWriteInternationalScheduled3DataInitiation(
                        patchedConsent.data.initiation
                    )
                )
        ).risk(patchedConsent.risk)

        val signedPayload = signPayloadSubmitPayment(
            defaultMapper.writeValueAsString(standingOrderSubmissionRequest),
            tppResource.tpp.signingKey,
            tppResource.tpp.signingKid
        )

        val submissionResponse = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse5>(
            createInternationalScheduledPaymentsConsents.paymentLinks.CreateInternationalScheduledPayment,
            standingOrderSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            version
        )

        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.internationalScheduledPaymentId).isNotEmpty()

        // When
        val result = PaymentRS().getPayment<OBWriteInternationalScheduledResponse5>(
            PaymentFactory.urlWithInternationalScheduledPaymentId(
                createInternationalScheduledPaymentsConsents.paymentLinks.GetInternationalScheduledPayment,
                submissionResponse.data.internationalScheduledPaymentId
            ),
            accessTokenClientCredentials,
            tppResource.tpp
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.internationalScheduledPaymentId).isNotEmpty()
        assertThat(result.data.creationDateTime).isNotNull()
        Assertions.assertThat(result.data.status.toString()).`is`(Status.paymentCondition)
    }

    fun getInternationalScheduledPayments_rateType_INDICATIVE_Test() {
        // Given
        val consentRequest =
            OBWriteInternationalScheduledConsentTestDataFactory.aValidOBWriteInternationalScheduledConsent5()
        consentRequest.data.initiation.exchangeRateInformation.rateType = OBExchangeRateType2Code.INDICATIVE
        consentRequest.data.initiation.exchangeRateInformation.exchangeRate = null
        consentRequest.data.initiation.exchangeRateInformation.contractIdentification = null

        val (consent, accessTokenAuthorizationCode) = createInternationalScheduledPaymentsConsents.createInternationalScheduledPaymentConsentAndGetAccessToken(
            consentRequest
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentRS().getClientCredentialsAccessToken(tppResource.tpp)

        val patchedConsent = PaymentRS().getConsent<OBWriteInternationalScheduledConsentResponse6>(
            PaymentFactory.urlWithConsentId(
                createInternationalScheduledPaymentsConsents.paymentLinks.GetInternationalScheduledPaymentConsent,
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

        val standingOrderSubmissionRequest = OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(
                    PaymentFactory.mapOBWriteInternationalScheduledConsentResponse6DataInitiationToOBWriteInternationalScheduled3DataInitiation(
                        patchedConsent.data.initiation
                    )
                )
        ).risk(patchedConsent.risk)

        val signedPayload = signPayloadSubmitPayment(
            defaultMapper.writeValueAsString(standingOrderSubmissionRequest),
            tppResource.tpp.signingKey,
            tppResource.tpp.signingKid
        )

        val submissionResponse = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse5>(
            createInternationalScheduledPaymentsConsents.paymentLinks.CreateInternationalScheduledPayment,
            standingOrderSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            version
        )

        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.internationalScheduledPaymentId).isNotEmpty()

        // When
        val result = PaymentRS().getPayment<OBWriteInternationalScheduledResponse5>(
            PaymentFactory.urlWithInternationalScheduledPaymentId(
                createInternationalScheduledPaymentsConsents.paymentLinks.GetInternationalScheduledPayment,
                submissionResponse.data.internationalScheduledPaymentId
            ),
            accessTokenClientCredentials,
            tppResource.tpp
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.internationalScheduledPaymentId).isNotEmpty()
        assertThat(result.data.creationDateTime).isNotNull()
        Assertions.assertThat(result.data.status.toString()).`is`(Status.paymentCondition)
    }

    fun getInternationalScheduledPayments_mandatoryFields_Test() {
        // Given
        val consentRequest =
            OBWriteInternationalScheduledConsentTestDataFactory.aValidOBWriteInternationalScheduledConsent5MandatoryFields()

        val (consent, accessTokenAuthorizationCode) = createInternationalScheduledPaymentsConsents.createInternationalScheduledPaymentConsentAndGetAccessToken(
            consentRequest
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentRS().getClientCredentialsAccessToken(tppResource.tpp)

        val patchedConsent = PaymentRS().getConsent<OBWriteInternationalScheduledConsentResponse6>(
            PaymentFactory.urlWithConsentId(
                createInternationalScheduledPaymentsConsents.paymentLinks.GetInternationalScheduledPaymentConsent,
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

        val standingOrderSubmissionRequest = OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(
                    PaymentFactory.mapOBWriteInternationalScheduledConsentResponse6DataInitiationToOBWriteInternationalScheduled3DataInitiation(
                        patchedConsent.data.initiation
                    )
                )
        ).risk(patchedConsent.risk)

        val signedPayload = signPayloadSubmitPayment(
            defaultMapper.writeValueAsString(standingOrderSubmissionRequest),
            tppResource.tpp.signingKey,
            tppResource.tpp.signingKid
        )

        val submissionResponse = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse5>(
            createInternationalScheduledPaymentsConsents.paymentLinks.CreateInternationalScheduledPayment,
            standingOrderSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            version
        )

        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.internationalScheduledPaymentId).isNotEmpty()

        // When
        val result = PaymentRS().getPayment<OBWriteInternationalScheduledResponse5>(
            PaymentFactory.urlWithInternationalScheduledPaymentId(
                createInternationalScheduledPaymentsConsents.paymentLinks.GetInternationalScheduledPayment,
                submissionResponse.data.internationalScheduledPaymentId
            ),
            accessTokenClientCredentials,
            tppResource.tpp
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.internationalScheduledPaymentId).isNotEmpty()
        assertThat(result.data.creationDateTime).isNotNull()
        Assertions.assertThat(result.data.status.toString()).`is`(Status.paymentCondition)
    }

    fun shouldGetInternationalScheduledPayments_withReadRefund_Test() {
        // Given
        val consentRequest =
            OBWriteInternationalScheduledConsentTestDataFactory.aValidOBWriteInternationalScheduledConsent5()
        consentRequest.data.readRefundAccount = OBReadRefundAccountEnum.YES

        val (consent, accessTokenAuthorizationCode) = createInternationalScheduledPaymentsConsents.createInternationalScheduledPaymentConsentAndGetAccessToken(
            consentRequest
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        assertThat(consent.data.readRefundAccount).isEqualTo(OBReadRefundAccountEnum.YES)
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentRS().getClientCredentialsAccessToken(tppResource.tpp)

        val patchedConsent = PaymentRS().getConsent<OBWriteInternationalScheduledConsentResponse6>(
            PaymentFactory.urlWithConsentId(
                createInternationalScheduledPaymentsConsents.paymentLinks.GetInternationalScheduledPaymentConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            version
        )

        assertThat(patchedConsent).isNotNull()
        assertThat(patchedConsent.data).isNotNull()
        assertThat(patchedConsent.risk).isNotNull()
        assertThat(patchedConsent.data.consentId).isNotEmpty()
        assertThat(patchedConsent.data.readRefundAccount).isEqualTo(OBReadRefundAccountEnum.YES)
        Assertions.assertThat(patchedConsent.data.status.toString()).`is`(Status.consentCondition)

        val standingOrderSubmissionRequest = OBWriteInternationalScheduled3().data(
            OBWriteInternationalScheduled3Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(
                    PaymentFactory.mapOBWriteInternationalScheduledConsentResponse6DataInitiationToOBWriteInternationalScheduled3DataInitiation(
                        patchedConsent.data.initiation
                    )
                )
        ).risk(patchedConsent.risk)

        val signedPayload = signPayloadSubmitPayment(
            defaultMapper.writeValueAsString(standingOrderSubmissionRequest),
            tppResource.tpp.signingKey,
            tppResource.tpp.signingKid
        )

        val submissionResponse = PaymentRS().submitPayment<OBWriteInternationalScheduledResponse5>(
            createInternationalScheduledPaymentsConsents.paymentLinks.CreateInternationalScheduledPayment,
            standingOrderSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            version
        )

        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.internationalScheduledPaymentId).isNotEmpty()

        // When
        val result = PaymentRS().getPayment<OBWriteInternationalScheduledResponse5>(
            PaymentFactory.urlWithInternationalScheduledPaymentId(
                createInternationalScheduledPaymentsConsents.paymentLinks.GetInternationalScheduledPayment,
                submissionResponse.data.internationalScheduledPaymentId
            ),
            accessTokenClientCredentials,
            tppResource.tpp
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.internationalScheduledPaymentId).isNotEmpty()
        assertThat(result.data.creationDateTime).isNotNull()
        //TODO: Waiting for the fix from the issue: https://github.com/SecureBankingAccessToolkit/SecureBankingAccessToolkit/issues/241
//        assertThat(result.data.refund.account.identification).isEqualTo(consent.data.initiation.debtorAccount.identification)
        Assertions.assertThat(result.data.status.toString()).`is`(Status.paymentCondition)
    }
}