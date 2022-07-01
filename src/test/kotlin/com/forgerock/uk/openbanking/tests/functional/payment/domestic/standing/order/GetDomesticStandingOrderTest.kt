package com.forgerock.uk.openbanking.tests.functional.payment.domestic.standing.order

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.securebanking.framework.conditions.Status
import com.forgerock.securebanking.framework.configuration.psu
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.framework.http.fuel.defaultMapper
import com.forgerock.securebanking.framework.signature.signPayloadSubmitPayment
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.support.discovery.*
import com.forgerock.uk.openbanking.support.payment.PaymentAS
import com.forgerock.uk.openbanking.support.payment.PaymentFactory
import com.forgerock.uk.openbanking.support.payment.PaymentFactory.Companion.mapOBDomesticStandingOrder3ToOBWriteDomesticStandingOrder3DataInitiation
import com.forgerock.uk.openbanking.support.payment.PaymentFactory.Companion.urlWithDomesticStandingOrderId
import com.forgerock.uk.openbanking.support.payment.PaymentRS
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.payment.*
import uk.org.openbanking.testsupport.payment.OBWriteDomesticStandingOrderConsentTestDataFactory.*

class GetDomesticStandingOrderTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["GetDomesticStandingOrder", "CreateDomesticStandingOrder", "CreateDomesticStandingOrderConsent", "GetDomesticStandingOrderConsent"],
        apis = ["domestic-standing-orders", "domestic-standing-order-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun getDomesticStandingOrders_v3_1_8() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent5()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse6>(
            payment3_1_8.Links.links.CreateDomesticStandingOrderConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_8,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentRS().getAccessToken(tppResource.tpp)

        val patchedConsent = PaymentRS().getConsent<OBWriteDomesticStandingOrderConsentResponse6>(
            PaymentFactory.urlWithConsentId(
                payment3_1_8.Links.links.GetDomesticStandingOrderConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            OBVersion.v3_1_8
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
            payment3_1_8.Links.links.CreateDomesticStandingOrder,
            standingOrderSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1_8
        )

        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.domesticStandingOrderId).isNotEmpty()

        // When
        val result = PaymentRS().getPayment<OBWriteDomesticStandingOrderResponse6>(
            urlWithDomesticStandingOrderId(
                payment3_1_8.Links.links.GetDomesticStandingOrder,
                submissionResponse.data.domesticStandingOrderId
            ),
            accessTokenClientCredentials,
            tppResource.tpp
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.domesticStandingOrderId).isNotEmpty()
        assertThat(result.data.creationDateTime).isNotNull()
        Assertions.assertThat(result.data.status.toString()).`is`(Status.paymentCondition)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["GetDomesticStandingOrder", "CreateDomesticStandingOrder", "CreateDomesticStandingOrderConsent", "GetDomesticStandingOrderConsent"],
        apis = ["domestic-standing-orders", "domestic-standing-order-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Test
    fun getDomesticStandingOrders_v3_1_8_mandatoryFields() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent5MandatoryFields()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse6>(
            payment3_1_8.Links.links.CreateDomesticStandingOrderConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_8,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentRS().getAccessToken(tppResource.tpp)

        val patchedConsent = PaymentRS().getConsent<OBWriteDomesticStandingOrderConsentResponse6>(
            PaymentFactory.urlWithConsentId(
                payment3_1_8.Links.links.GetDomesticStandingOrderConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            OBVersion.v3_1_8
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
            payment3_1_8.Links.links.CreateDomesticStandingOrder,
            standingOrderSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1_8
        )

        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.domesticStandingOrderId).isNotEmpty()

        // When
        val result = PaymentRS().getPayment<OBWriteDomesticStandingOrderResponse6>(
            urlWithDomesticStandingOrderId(
                payment3_1_8.Links.links.GetDomesticStandingOrder,
                submissionResponse.data.domesticStandingOrderId
            ),
            accessTokenClientCredentials,
            tppResource.tpp
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.domesticStandingOrderId).isNotEmpty()
        assertThat(result.data.creationDateTime).isNotNull()
        Assertions.assertThat(result.data.status.toString()).`is`(Status.paymentCondition)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.8",
        operations = ["GetDomesticStandingOrder", "CreateDomesticStandingOrder", "CreateDomesticStandingOrderConsent", "GetDomesticStandingOrderConsent"],
        apis = ["domestic-standing-orders", "domestic-standing-order-consents"],
        compatibleVersions = ["v.3.1.7", "v.3.1.6", "v.3.1.5"]
    )
    @Disabled
    @Test
    fun shouldGetDomesticStandingOrders_withReadRefund_v3_1_8() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent5()
        consentRequest.data.readRefundAccount = OBReadRefundAccountEnum.YES

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse6>(
            payment3_1_8.Links.links.CreateDomesticStandingOrderConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_8,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        assertThat(consent.data.readRefundAccount).isEqualTo(OBReadRefundAccountEnum.YES)
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentRS().getAccessToken(tppResource.tpp)

        val patchedConsent = PaymentRS().getConsent<OBWriteDomesticStandingOrderConsentResponse6>(
            PaymentFactory.urlWithConsentId(
                payment3_1_8.Links.links.GetDomesticStandingOrderConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            OBVersion.v3_1_8
        )

        assertThat(patchedConsent).isNotNull()
        assertThat(patchedConsent.data).isNotNull()
        assertThat(patchedConsent.risk).isNotNull()
        assertThat(patchedConsent.data.consentId).isNotEmpty()
        assertThat(patchedConsent.data.readRefundAccount).isEqualTo(OBReadRefundAccountEnum.YES)
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
            payment3_1_8.Links.links.CreateDomesticStandingOrder,
            standingOrderSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1_8
        )

        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.domesticStandingOrderId).isNotEmpty()

        // When
        val result = PaymentRS().getPayment<OBWriteDomesticStandingOrderResponse6>(
            urlWithDomesticStandingOrderId(
                payment3_1_8.Links.links.GetDomesticStandingOrder,
                submissionResponse.data.domesticStandingOrderId
            ),
            accessTokenClientCredentials,
            tppResource.tpp
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.domesticStandingOrderId).isNotEmpty()
        assertThat(result.data.creationDateTime).isNotNull()
        //TODO: Waiting for the fix from the issue: https://github.com/SecureBankingAccessToolkit/SecureBankingAccessToolkit/issues/241
//        assertThat(result.data.refund.account.identification).isEqualTo(consent.data.initiation.debtorAccount.identification)
        Assertions.assertThat(result.data.status.toString()).`is`(Status.paymentCondition)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["GetDomesticStandingOrder", "CreateDomesticStandingOrder", "CreateDomesticStandingOrderConsent", "GetDomesticStandingOrderConsent"],
        apis = ["domestic-standing-orders", "domestic-standing-order-consents"]
    )
    @Test
    fun getDomesticStandingOrders_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent5()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse5>(
            payment3_1_4.Links.links.CreateDomesticStandingOrderConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_4,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentRS().getAccessToken(tppResource.tpp)

        val patchedConsent = PaymentRS().getConsent<OBWriteDomesticStandingOrderConsentResponse5>(
            PaymentFactory.urlWithConsentId(
                payment3_1_4.Links.links.GetDomesticStandingOrderConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            OBVersion.v3_1_4
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

        val submissionResponse = PaymentRS().submitPayment<OBWriteDomesticStandingOrderResponse5>(
            payment3_1_4.Links.links.CreateDomesticStandingOrder,
            standingOrderSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1_4
        )

        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.domesticStandingOrderId).isNotEmpty()


        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.domesticStandingOrderId).isNotEmpty()

        // When
        val result = PaymentRS().getPayment<OBWriteDomesticStandingOrderResponse5>(
            urlWithDomesticStandingOrderId(
                payment3_1_4.Links.links.GetDomesticStandingOrder,
                submissionResponse.data.domesticStandingOrderId
            ),
            accessTokenClientCredentials,
            tppResource.tpp
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.domesticStandingOrderId).isNotEmpty()
        assertThat(result.data.creationDateTime).isNotNull()
        Assertions.assertThat(result.data.status.toString()).`is`(Status.paymentCondition)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["GetDomesticStandingOrder", "CreateDomesticStandingOrder", "CreateDomesticStandingOrderConsent", "GetDomesticStandingOrderConsent"],
        apis = ["domestic-standing-orders", "domestic-standing-order-consents"]
    )
    @Test
    fun getDomesticStandingOrders_v3_1_4_mandatoryFields() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent5MandatoryFields()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse5>(
            payment3_1_4.Links.links.CreateDomesticStandingOrderConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_4,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentRS().getAccessToken(tppResource.tpp)

        val patchedConsent = PaymentRS().getConsent<OBWriteDomesticStandingOrderConsentResponse5>(
            PaymentFactory.urlWithConsentId(
                payment3_1_4.Links.links.GetDomesticStandingOrderConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            OBVersion.v3_1_4
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

        val submissionResponse = PaymentRS().submitPayment<OBWriteDomesticStandingOrderResponse5>(
            payment3_1_4.Links.links.CreateDomesticStandingOrder,
            standingOrderSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1_4
        )

        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.domesticStandingOrderId).isNotEmpty()


        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.domesticStandingOrderId).isNotEmpty()

        // When
        val result = PaymentRS().getPayment<OBWriteDomesticStandingOrderResponse5>(
            urlWithDomesticStandingOrderId(
                payment3_1_4.Links.links.GetDomesticStandingOrder,
                submissionResponse.data.domesticStandingOrderId
            ),
            accessTokenClientCredentials,
            tppResource.tpp
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.domesticStandingOrderId).isNotEmpty()
        assertThat(result.data.creationDateTime).isNotNull()
        Assertions.assertThat(result.data.status.toString()).`is`(Status.paymentCondition)
    }


    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["GetDomesticStandingOrder", "CreateDomesticStandingOrder", "CreateDomesticStandingOrderConsent", "GetDomesticStandingOrderConsent"],
        apis = ["domestic-standing-orders", "domestic-standing-order-consents"]
    )
    @Disabled
    @Test
    fun shouldGetDomesticStandingOrders_withReadRefund_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent5()
        consentRequest.data.readRefundAccount = OBReadRefundAccountEnum.YES

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse5>(
            payment3_1_4.Links.links.CreateDomesticStandingOrderConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_4,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        assertThat(consent.data.readRefundAccount).isEqualTo(OBReadRefundAccountEnum.YES)
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentRS().getAccessToken(tppResource.tpp)

        val patchedConsent = PaymentRS().getConsent<OBWriteDomesticStandingOrderConsentResponse5>(
            PaymentFactory.urlWithConsentId(
                payment3_1_4.Links.links.GetDomesticStandingOrderConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            OBVersion.v3_1_4
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

        val submissionResponse = PaymentRS().submitPayment<OBWriteDomesticStandingOrderResponse5>(
            payment3_1_4.Links.links.CreateDomesticStandingOrder,
            standingOrderSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1_4
        )

        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.domesticStandingOrderId).isNotEmpty()


        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.domesticStandingOrderId).isNotEmpty()

        // When
        val result = PaymentRS().getPayment<OBWriteDomesticStandingOrderResponse5>(
            urlWithDomesticStandingOrderId(
                payment3_1_4.Links.links.GetDomesticStandingOrder,
                submissionResponse.data.domesticStandingOrderId
            ),
            accessTokenClientCredentials,
            tppResource.tpp
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.domesticStandingOrderId).isNotEmpty()
        assertThat(result.data.creationDateTime).isNotNull()
        //TODO: Waiting for the fix from the issue: https://github.com/SecureBankingAccessToolkit/SecureBankingAccessToolkit/issues/241
//        assertThat(result.data.refund.account.identification).isEqualTo(consent.data.initiation.debtorAccount.identification)
        Assertions.assertThat(result.data.status.toString()).`is`(Status.paymentCondition)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["GetDomesticStandingOrder", "CreateDomesticStandingOrder", "CreateDomesticStandingOrderConsent", "GetDomesticStandingOrderConsent"],
        apis = ["domestic-standing-orders", "domestic-standing-order-consents"],
        compatibleVersions = ["v.3.1.2"]
    )
    @Test
    fun getDomesticStandingOrders_v3_1_3() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent4()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse4>(
            payment3_1_3.Links.links.CreateDomesticStandingOrderConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_3,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentRS().getAccessToken(tppResource.tpp)

        val patchedConsent = PaymentRS().getConsent<OBWriteDomesticStandingOrderConsentResponse4>(
            PaymentFactory.urlWithConsentId(
                payment3_1_3.Links.links.GetDomesticStandingOrderConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            OBVersion.v3_1_3
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

        val signedPayload =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(standingOrderSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        val submissionResponse = PaymentRS().submitPayment<OBWriteDomesticStandingOrderResponse4>(
            payment3_1_3.Links.links.CreateDomesticStandingOrder,
            standingOrderSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1_3
        )

        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.domesticStandingOrderId).isNotEmpty()

        // When
        val result = PaymentRS().getPayment<OBWriteDomesticStandingOrderResponse4>(
            urlWithDomesticStandingOrderId(
                payment3_1_3.Links.links.GetDomesticStandingOrder,
                submissionResponse.data.domesticStandingOrderId
            ),
            accessTokenClientCredentials,
            tppResource.tpp
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.domesticStandingOrderId).isNotEmpty()
        assertThat(result.data.creationDateTime).isNotNull()
        Assertions.assertThat(result.data.status.toString()).`is`(Status.paymentCondition)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.3",
        operations = ["GetDomesticStandingOrder", "CreateDomesticStandingOrder", "CreateDomesticStandingOrderConsent", "GetDomesticStandingOrderConsent"],
        apis = ["domestic-standing-orders", "domestic-standing-order-consents"],
        compatibleVersions = ["v.3.1.2"]
    )
    @Test
    fun getDomesticStandingOrders_v3_1_3_mandatoryFields() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent4MandatoryFields()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse4>(
            payment3_1_3.Links.links.CreateDomesticStandingOrderConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_3,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentRS().getAccessToken(tppResource.tpp)

        val patchedConsent = PaymentRS().getConsent<OBWriteDomesticStandingOrderConsentResponse4>(
            PaymentFactory.urlWithConsentId(
                payment3_1_3.Links.links.GetDomesticStandingOrderConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            OBVersion.v3_1_3
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

        val signedPayload =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(standingOrderSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        val submissionResponse = PaymentRS().submitPayment<OBWriteDomesticStandingOrderResponse4>(
            payment3_1_3.Links.links.CreateDomesticStandingOrder,
            standingOrderSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1_3
        )

        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.domesticStandingOrderId).isNotEmpty()

        // When
        val result = PaymentRS().getPayment<OBWriteDomesticStandingOrderResponse4>(
            urlWithDomesticStandingOrderId(
                payment3_1_3.Links.links.GetDomesticStandingOrder,
                submissionResponse.data.domesticStandingOrderId
            ),
            accessTokenClientCredentials,
            tppResource.tpp
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.domesticStandingOrderId).isNotEmpty()
        assertThat(result.data.creationDateTime).isNotNull()
        Assertions.assertThat(result.data.status.toString()).`is`(Status.paymentCondition)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["GetDomesticStandingOrder", "CreateDomesticStandingOrder", "CreateDomesticStandingOrderConsent", "GetDomesticStandingOrderConsent"],
        apis = ["domestic-standing-orders", "domestic-standing-order-consents"]
    )
    @Test
    fun getDomesticStandingOrders_v3_1_1() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent3()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse3>(
            payment3_1_1.Links.links.CreateDomesticStandingOrderConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_1,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentRS().getAccessToken(tppResource.tpp)

        val patchedConsent = PaymentRS().getConsent<OBWriteDomesticStandingOrderConsentResponse3>(
            PaymentFactory.urlWithConsentId(
                payment3_1_1.Links.links.GetDomesticStandingOrderConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            OBVersion.v3_1_1
        )

        assertThat(patchedConsent).isNotNull()
        assertThat(patchedConsent.data).isNotNull()
        assertThat(patchedConsent.risk).isNotNull()
        assertThat(patchedConsent.data.consentId).isNotEmpty()
        Assertions.assertThat(patchedConsent.data.status.toString()).`is`(Status.consentCondition)

        val standingOrderSubmissionRequest = OBWriteDomesticStandingOrder3().data(
            OBWriteDomesticStandingOrder3Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(
                    mapOBDomesticStandingOrder3ToOBWriteDomesticStandingOrder3DataInitiation(
                        patchedConsent.data.initiation
                    )
                )
        ).risk(patchedConsent.risk)

        val signedPayload =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(standingOrderSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        val submissionResponse = PaymentRS().submitPayment<OBWriteDomesticStandingOrderResponse3>(
            payment3_1_1.Links.links.CreateDomesticStandingOrder,
            standingOrderSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1_1
        )

        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.domesticStandingOrderId).isNotEmpty()

        // When
        val result = PaymentRS().getPayment<OBWriteDomesticStandingOrderResponse3>(
            urlWithDomesticStandingOrderId(
                payment3_1_1.Links.links.GetDomesticStandingOrder,
                submissionResponse.data.domesticStandingOrderId
            ),
            accessTokenClientCredentials,
            tppResource.tpp
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.domesticStandingOrderId).isNotEmpty()
        assertThat(result.data.creationDateTime).isNotNull()
        Assertions.assertThat(result.data.status.toString()).`is`(Status.paymentCondition)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.1",
        operations = ["GetDomesticStandingOrder", "CreateDomesticStandingOrder", "CreateDomesticStandingOrderConsent", "GetDomesticStandingOrderConsent"],
        apis = ["domestic-standing-orders", "domestic-standing-order-consents"]
    )
    @Test
    fun getDomesticStandingOrders_v3_1_1_mandatoryFields() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent3MandatoryFields()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse3>(
            payment3_1_1.Links.links.CreateDomesticStandingOrderConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1_1,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentRS().getAccessToken(tppResource.tpp)

        val patchedConsent = PaymentRS().getConsent<OBWriteDomesticStandingOrderConsentResponse3>(
            PaymentFactory.urlWithConsentId(
                payment3_1_1.Links.links.GetDomesticStandingOrderConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            OBVersion.v3_1_1
        )

        assertThat(patchedConsent).isNotNull()
        assertThat(patchedConsent.data).isNotNull()
        assertThat(patchedConsent.risk).isNotNull()
        assertThat(patchedConsent.data.consentId).isNotEmpty()
        Assertions.assertThat(patchedConsent.data.status.toString()).`is`(Status.consentCondition)

        val standingOrderSubmissionRequest = OBWriteDomesticStandingOrder3().data(
            OBWriteDomesticStandingOrder3Data()
                .consentId(patchedConsent.data.consentId)
                .initiation(
                    mapOBDomesticStandingOrder3ToOBWriteDomesticStandingOrder3DataInitiation(
                        patchedConsent.data.initiation
                    )
                )
        ).risk(patchedConsent.risk)

        val signedPayload =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(standingOrderSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        val submissionResponse = PaymentRS().submitPayment<OBWriteDomesticStandingOrderResponse3>(
            payment3_1_1.Links.links.CreateDomesticStandingOrder,
            standingOrderSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1_1
        )

        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.domesticStandingOrderId).isNotEmpty()

        // When
        val result = PaymentRS().getPayment<OBWriteDomesticStandingOrderResponse3>(
            urlWithDomesticStandingOrderId(
                payment3_1_1.Links.links.GetDomesticStandingOrder,
                submissionResponse.data.domesticStandingOrderId
            ),
            accessTokenClientCredentials,
            tppResource.tpp
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.domesticStandingOrderId).isNotEmpty()
        assertThat(result.data.creationDateTime).isNotNull()
        Assertions.assertThat(result.data.status.toString()).`is`(Status.paymentCondition)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1",
        operations = ["GetDomesticStandingOrder", "CreateDomesticStandingOrder", "CreateDomesticStandingOrderConsent", "GetDomesticStandingOrderConsent"],
        apis = ["domestic-standing-orders", "domestic-standing-order-consents"]
    )
    @Test
    fun getDomesticStandingOrders_v3_1() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent2()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse2>(
            payment3_1.Links.links.CreateDomesticStandingOrderConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentRS().getAccessToken(tppResource.tpp)

        val patchedConsent = PaymentRS().getConsent<OBWriteDomesticStandingOrderConsentResponse2>(
            PaymentFactory.urlWithConsentId(
                payment3_1.Links.links.GetDomesticStandingOrderConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            OBVersion.v3_1
        )

        assertThat(patchedConsent).isNotNull()
        assertThat(patchedConsent.data).isNotNull()
        assertThat(patchedConsent.risk).isNotNull()
        assertThat(patchedConsent.data.consentId).isNotEmpty()
        Assertions.assertThat(patchedConsent.data.status.toString()).`is`(Status.consentCondition)

        val standingOrderSubmissionRequest = OBWriteDomesticStandingOrder2().data(
            OBWriteDataDomesticStandingOrder2()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        val signedPayload =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(standingOrderSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        val submissionResponse = PaymentRS().submitPayment<OBWriteDomesticStandingOrderResponse2>(
            payment3_1.Links.links.CreateDomesticStandingOrder,
            standingOrderSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1
        )

        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.domesticStandingOrderId).isNotEmpty()

        // When
        val result = PaymentRS().getPayment<OBWriteDomesticStandingOrderResponse2>(
            urlWithDomesticStandingOrderId(
                payment3_1.Links.links.GetDomesticStandingOrder,
                submissionResponse.data.domesticStandingOrderId
            ),
            accessTokenClientCredentials,
            tppResource.tpp
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.domesticStandingOrderId).isNotEmpty()
        assertThat(result.data.creationDateTime).isNotNull()
        Assertions.assertThat(result.data.status.toString()).`is`(Status.paymentCondition)
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1",
        operations = ["GetDomesticStandingOrder", "CreateDomesticStandingOrder", "CreateDomesticStandingOrderConsent", "GetDomesticStandingOrderConsent"],
        apis = ["domestic-standing-orders", "domestic-standing-order-consents"]
    )
    @Test
    fun getDomesticStandingOrders_v3_1_mandatoryFields() {
        // Given
        val consentRequest = aValidOBWriteDomesticStandingOrderConsent2MandatoryFields()

        val signedPayloadConsent =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(consentRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid
            )

        val consent = PaymentRS().consent<OBWriteDomesticStandingOrderConsentResponse2>(
            payment3_1.Links.links.CreateDomesticStandingOrderConsent,
            consentRequest,
            tppResource.tpp,
            OBVersion.v3_1,
            signedPayloadConsent
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().getAccessToken(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )

        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentRS().getAccessToken(tppResource.tpp)

        val patchedConsent = PaymentRS().getConsent<OBWriteDomesticStandingOrderConsentResponse2>(
            PaymentFactory.urlWithConsentId(
                payment3_1.Links.links.GetDomesticStandingOrderConsent,
                consent.data.consentId
            ),
            tppResource.tpp,
            OBVersion.v3_1
        )

        assertThat(patchedConsent).isNotNull()
        assertThat(patchedConsent.data).isNotNull()
        assertThat(patchedConsent.risk).isNotNull()
        assertThat(patchedConsent.data.consentId).isNotEmpty()
        Assertions.assertThat(patchedConsent.data.status.toString()).`is`(Status.consentCondition)

        val standingOrderSubmissionRequest = OBWriteDomesticStandingOrder2().data(
            OBWriteDataDomesticStandingOrder2()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
        ).risk(patchedConsent.risk)

        val signedPayload =
            signPayloadSubmitPayment(
                defaultMapper.writeValueAsString(standingOrderSubmissionRequest),
                tppResource.tpp.signingKey,
                tppResource.tpp.signingKid,
                true
            )

        val submissionResponse = PaymentRS().submitPayment<OBWriteDomesticStandingOrderResponse2>(
            payment3_1.Links.links.CreateDomesticStandingOrder,
            standingOrderSubmissionRequest,
            accessTokenAuthorizationCode,
            signedPayload,
            tppResource.tpp,
            OBVersion.v3_1
        )

        assertThat(submissionResponse).isNotNull()
        assertThat(submissionResponse.data).isNotNull()
        assertThat(submissionResponse.data.consentId).isEqualTo(patchedConsent.data.consentId)
        assertThat(submissionResponse.data.domesticStandingOrderId).isNotEmpty()

        // When
        val result = PaymentRS().getPayment<OBWriteDomesticStandingOrderResponse2>(
            urlWithDomesticStandingOrderId(
                payment3_1.Links.links.GetDomesticStandingOrder,
                submissionResponse.data.domesticStandingOrderId
            ),
            accessTokenClientCredentials,
            tppResource.tpp
        )

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data.domesticStandingOrderId).isNotEmpty()
        assertThat(result.data.creationDateTime).isNotNull()
        Assertions.assertThat(result.data.status.toString()).`is`(Status.paymentCondition)
    }
}
