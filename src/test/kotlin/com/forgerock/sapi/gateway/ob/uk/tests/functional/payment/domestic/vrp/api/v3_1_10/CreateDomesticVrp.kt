package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.vrp.api.v3_1_10

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.sapi.gateway.framework.conditions.Status
import com.forgerock.sapi.gateway.framework.data.AccessToken
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.http.fuel.defaultMapper
import com.forgerock.sapi.gateway.ob.uk.support.discovery.getPaymentsApiLinks
import com.forgerock.sapi.gateway.ob.uk.support.payment.BadJwsSignatureProducer
import com.forgerock.sapi.gateway.ob.uk.support.payment.DefaultJwsSignatureProducer
import com.forgerock.sapi.gateway.ob.uk.support.payment.InvalidKidJwsSignatureProducer
import com.forgerock.sapi.gateway.ob.uk.support.payment.PaymentFactory
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.vrp.consents.api.v3_1_10.CreateDomesticVrpConsents
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Headers
import org.assertj.core.api.Assertions
import uk.org.openbanking.datamodel.vrp.*
import uk.org.openbanking.testsupport.vrp.OBDomesticVrpCommonTestDataFactory
import uk.org.openbanking.testsupport.vrp.OBDomesticVrpConsentRequestTestDataFactory


class CreateDomesticVrp(val version: OBVersion, val tppResource: CreateTppCallback.TppResource) {

    private val createDomesticVrpConsentsApi = CreateDomesticVrpConsents(version, tppResource)
    private val paymentApiClient = tppResource.tpp.paymentApiClient
    private val paymentLinks = getPaymentsApiLinks(version)
    private val createPaymentUrl = paymentLinks.CreateDomesticVrpPayment

    fun createDomesticVrpPaymentTest() {
        // Given
        val consentRequest = OBDomesticVrpConsentRequestTestDataFactory.aValidOBDomesticVRPConsentRequest()
        val result = submitPayment(consentRequest)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.consentId).isNotEmpty()
        assertThat(result.data.charges).isNotNull().isNotEmpty()
    }

    fun limitBreachSimulationDomesticVrpPaymentTest() {
        // Given
        val headers = Headers()
        val consentRequest = OBDomesticVrpConsentRequestTestDataFactory.aValidOBDomesticVRPConsentRequest()
        val (consentResponse, authorizationToken) = createDomesticVrpConsentsApi.createDomesticVrpConsentAndAuthorize(
            consentRequest
        )
        val patchedConsent = getPatchedConsent(consentResponse)
        val paymentSubmissionRequest = createPaymentRequest(patchedConsent)
        // If the CreditorAccount was not specified in the consent, the CreditorAccount must be specified in the instruction
//        if(patchedConsent.data.initiation.creditorAccount == null && paymentSubmissionRequest.data.instruction.creditorAccount==null){
//            paymentSubmissionRequest.data.instruction.creditorAccount(
//                OBDomesticVrpCommonTestDataFactory.aValidOBCashAccountCreditor3()
//            )
//        }

        val periodType = consentRequest.data.controlParameters.periodicLimits[0].periodType.value
        val periodAlignment = consentRequest.data.controlParameters.periodicLimits[0].periodAlignment.value
        headers["x-vrp-limit-breach-response-simulation"] = "$periodType-$periodAlignment"

        // Then
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            paymentApiClient.buildSubmitPaymentRequest(
                createPaymentUrl, authorizationToken, paymentSubmissionRequest
            ).addHeaders(headers).sendRequest()
        }
        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        val amount = consentRequest.data.controlParameters.periodicLimits[0].amount
        val currency = consentRequest.data.controlParameters.periodicLimits[0].currency
        assertThat(exception.message.toString()).contains(
            "Unable to complete payment due to payment limit breach, periodic limit of '" + amount + "' '" + currency + "' " +
                    "for period '" + periodType + "' '" + periodAlignment + "' has been breached"
        )
    }

    fun createDomesticVrpPayment_mandatoryFieldsTest() {
        // Given
        val consentRequest =
            OBDomesticVrpConsentRequestTestDataFactory.aValidOBDomesticVRPConsentRequestMandatoryFields()
        // When
        val result = submitPayment(consentRequest)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.consentId).isNotEmpty()
    }


    fun shouldCreateDomesticVrp_throwsSendInvalidFormatDetachedJwsTest() {
        // Given
        val consentRequest = OBDomesticVrpConsentRequestTestDataFactory.aValidOBDomesticVRPConsentRequest()
        val (consent, accessToken) = createDomesticVrpConsentsApi.createDomesticVrpConsentAndAuthorize(
            consentRequest
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = createPaymentRequest(getPatchedConsent(consent))

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            paymentApiClient.buildSubmitPaymentRequest(createPaymentUrl, accessToken, paymentSubmissionRequest)
                .configureJwsSignatureProducer(BadJwsSignatureProducer()).sendRequest()
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.INVALID_FORMAT_DETACHED_JWS_ERROR)
    }

    fun shouldCreateDomesticVrp_throwsNoDetachedJwsTest() {
        // Given
        val consentRequest = OBDomesticVrpConsentRequestTestDataFactory.aValidOBDomesticVRPConsentRequest()
        val (consent, accessToken) = createDomesticVrpConsentsApi.createDomesticVrpConsentAndAuthorize(
            consentRequest
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = createPaymentRequest(getPatchedConsent(consent))

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            paymentApiClient.buildSubmitPaymentRequest(createPaymentUrl, accessToken, paymentSubmissionRequest)
                .configureJwsSignatureProducer(null).sendRequest()
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.NO_DETACHED_JWS)
    }

    fun shouldCreateDomesticVrp_throwsNotPermittedB64HeaderAddedInTheDetachedJwsTest() {
        // Given
        val consentRequest = OBDomesticVrpConsentRequestTestDataFactory.aValidOBDomesticVRPConsentRequest()
        val (consent, accessToken) = createDomesticVrpConsentsApi.createDomesticVrpConsentAndAuthorize(
            consentRequest
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = createPaymentRequest(getPatchedConsent(consent))

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            paymentApiClient.buildSubmitPaymentRequest(createPaymentUrl, accessToken, paymentSubmissionRequest)
                .configureJwsSignatureProducer(DefaultJwsSignatureProducer(tppResource.tpp, false)).sendRequest()
        }

        // Then
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
    }

    fun shouldCreateDomesticVrp_throwsSendInvalidKidDetachedJwsTest() {
        // Given
        val consentRequest = OBDomesticVrpConsentRequestTestDataFactory.aValidOBDomesticVRPConsentRequest()
        val (consent, accessToken) = createDomesticVrpConsentsApi.createDomesticVrpConsentAndAuthorize(
            consentRequest
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = createPaymentRequest(getPatchedConsent(consent))

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            paymentApiClient.buildSubmitPaymentRequest(createPaymentUrl, accessToken, paymentSubmissionRequest)
                .configureJwsSignatureProducer(InvalidKidJwsSignatureProducer(tppResource.tpp)).sendRequest()
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
    }

    fun shouldCreateDomesticVrp_throwsInvalidDetachedJws_detachedJwsHasDifferentConsentIdThanTheBodyTest() {
        // Given
        val consentRequest = OBDomesticVrpConsentRequestTestDataFactory.aValidOBDomesticVRPConsentRequest()
        val (consent, accessToken) = createDomesticVrpConsentsApi.createDomesticVrpConsentAndAuthorize(
            consentRequest
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        val patchedConsent = getPatchedConsent(consent)
        val paymentSubmissionRequest = createPaymentRequest(patchedConsent)

        patchedConsent.data.consentId = com.forgerock.sapi.gateway.ob.uk.framework.constants.INVALID_CONSENT_ID
        val paymentSubmissionWithInvalidConsentId = createPaymentRequest(patchedConsent)

        val signatureWithInvalidConsentId = DefaultJwsSignatureProducer(tppResource.tpp).createDetachedSignature(
            defaultMapper.writeValueAsString(paymentSubmissionWithInvalidConsentId)
        )

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            paymentApiClient.buildSubmitPaymentRequest(createPaymentUrl, accessToken, paymentSubmissionRequest)
                .configureJwsSignatureProducer(BadJwsSignatureProducer(signatureWithInvalidConsentId)).sendRequest()
        }

        // Then
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
    }

    fun shouldCreateDomesticVrp_throwsInvalidDetachedJws_detachedJwsHasDifferentAmountThanTheBodyTest() {
        // Given
        val consentRequest = OBDomesticVrpConsentRequestTestDataFactory.aValidOBDomesticVRPConsentRequest()
        val (consent, accessToken) = createDomesticVrpConsentsApi.createDomesticVrpConsentAndAuthorize(
            consentRequest
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        val patchedConsent = getPatchedConsent(consent)
        val paymentSubmissionRequest = createPaymentRequest(patchedConsent)

        val initialAmount = patchedConsent.data.controlParameters.maximumIndividualAmount.amount;
        patchedConsent.data.controlParameters.maximumIndividualAmount.amount = "12312132233"
        val paymentSubmissionInvalidAmount = createPaymentRequest(patchedConsent)

        val signatureWithInvalidAmount = DefaultJwsSignatureProducer(tppResource.tpp).createDetachedSignature(
            defaultMapper.writeValueAsString(paymentSubmissionInvalidAmount)
        )

        patchedConsent.data.controlParameters.maximumIndividualAmount.amount = initialAmount

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            paymentApiClient.buildSubmitPaymentRequest(createPaymentUrl, accessToken, paymentSubmissionRequest)
                .configureJwsSignatureProducer(BadJwsSignatureProducer(signatureWithInvalidAmount)).sendRequest()
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
    }

    fun shouldCreateDomesticVrpConsent_throwsBadRequestWhenNotSweepingVrpTypeTest() {
        // Given
        val consentRequest = OBDomesticVrpConsentRequestTestDataFactory.aValidOBDomesticVRPConsentRequest()
        consentRequest.data.controlParameters.vrPType = listOf("UK.OBIE.VRPType.Other", "UK.OBIE.VRPType.Sweeping")
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            createDomesticVrpConsentsApi.createDomesticVrpConsentAndAuthorize(
                consentRequest
            )
        }

        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.BAD_REQUEST)
        assertThat(exception.message.toString()).contains("[Invalid VRP type, only Sweeping payments are supported.]")

    }

    fun shouldCreateDomesticVrp_throwsPolicyValidationErrorTest() {
        // Given
        val consentRequest = OBDomesticVrpConsentRequestTestDataFactory.aValidOBDomesticVRPConsentRequest()
        val (consent, accessToken) = createDomesticVrpConsentsApi.createDomesticVrpConsentAndAuthorize(
            consentRequest
        )

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        val patchedConsent = getPatchedConsent(consent)
        patchedConsent.data.initiation.debtorAccount.name = "fake name"
        val paymentSubmissionRequest = createPaymentRequest(patchedConsent)

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            paymentApiClient.buildSubmitPaymentRequest(createPaymentUrl, accessToken, paymentSubmissionRequest)
                .configureDefaultJwsSignatureProducer()
                .configureJwsSignatureProducer(DefaultJwsSignatureProducer(tppResource.tpp)).sendRequest()
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
    }

    fun submitPayment(consentRequest: OBDomesticVRPConsentRequest): OBDomesticVRPResponse {
        val (consent, authorizationToken) = createDomesticVrpConsentsApi.createDomesticVrpConsentAndAuthorize(
            consentRequest
        )
        return submitPayment(consent, authorizationToken)
    }

    fun submitPayment(
        consentResponse: OBDomesticVRPConsentResponse,
        authorizationToken: AccessToken
    ): OBDomesticVRPResponse {
        val patchedConsent = getPatchedConsent(consentResponse)
        return submitPaymentForPatchedConsent(patchedConsent, authorizationToken)
    }

    private fun getPatchedConsent(consent: OBDomesticVRPConsentResponse): OBDomesticVRPConsentResponse {
        return createDomesticVrpConsentsApi.getPatchedConsent(consent)
    }

    private fun submitPaymentForPatchedConsent(
        patchedConsent: OBDomesticVRPConsentResponse,
        authorizationToken: AccessToken
    ): OBDomesticVRPResponse {

        val paymentSubmissionRequest = createPaymentRequest(patchedConsent)
        // If the CreditorAccount was not specified in the consent, the CreditorAccount must be specified in the instruction
        if (patchedConsent.data.initiation.creditorAccount == null && paymentSubmissionRequest.data.instruction.creditorAccount == null) {
            paymentSubmissionRequest.data.instruction.creditorAccount(
                OBDomesticVrpCommonTestDataFactory.aValidOBCashAccountCreditor3()
            )
        }
        return paymentApiClient.submitPayment(
            createPaymentUrl,
            authorizationToken,
            paymentSubmissionRequest
        )
    }

    private fun createPaymentRequest(patchedConsent: OBDomesticVRPConsentResponse): OBDomesticVRPRequest {
        return OBDomesticVRPRequest().data(
            OBDomesticVRPRequestData()
                .consentId(patchedConsent.data.consentId)
                .initiation(patchedConsent.data.initiation)
                .instruction(PaymentFactory.buildVrpInstruction(patchedConsent))
        ).risk(patchedConsent.risk)
    }
}
