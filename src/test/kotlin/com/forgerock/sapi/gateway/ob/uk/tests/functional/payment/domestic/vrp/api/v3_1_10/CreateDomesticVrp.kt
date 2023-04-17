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
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.vrp.consents.api.v3_1_10.CreateDomesticVrpConsents
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
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
        assertThat(result.links.self.toString()).isEqualTo(createPaymentUrl + "/" + result.data.domesticVRPId)
    }

    fun limitBreachSimulationDomesticVrpPaymentTest() {
        // Given
        val headers = Headers()
        val consentRequest = OBDomesticVrpConsentRequestTestDataFactory.aValidOBDomesticVRPConsentRequest()
        val (consentResponse, authorizationToken) = createDomesticVrpConsentsApi.createDomesticVrpConsentAndAuthorize(
                consentRequest
        )
        val paymentSubmissionRequest = createPaymentRequest(consentResponse)
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
        val (consentResponse, accessToken) = createDomesticVrpConsentsApi.createDomesticVrpConsentAndAuthorize(
                consentRequest
        )

        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = createPaymentRequest(consentResponse)

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
        val (consentResponse, accessToken) = createDomesticVrpConsentsApi.createDomesticVrpConsentAndAuthorize(
                consentRequest
        )

        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = createPaymentRequest(consentResponse)

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
        val (consentResponse, accessToken) = createDomesticVrpConsentsApi.createDomesticVrpConsentAndAuthorize(
                consentRequest
        )

        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = createPaymentRequest(consentResponse)

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
        val (consentResponse, accessToken) = createDomesticVrpConsentsApi.createDomesticVrpConsentAndAuthorize(
                consentRequest
        )

        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = createPaymentRequest(consentResponse)

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
        val (consentResponse, accessToken) = createDomesticVrpConsentsApi.createDomesticVrpConsentAndAuthorize(
                consentRequest
        )

        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = createPaymentRequest(consentResponse)

        consentResponse.data.consentId = com.forgerock.sapi.gateway.ob.uk.framework.constants.INVALID_CONSENT_ID
        val paymentSubmissionWithInvalidConsentId = createPaymentRequest(consentResponse)

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
        val (consentResponse, accessToken) = createDomesticVrpConsentsApi.createDomesticVrpConsentAndAuthorize(
                consentRequest
        )

        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = createPaymentRequest(consentResponse)

        val initialAmount = consentResponse.data.controlParameters.maximumIndividualAmount.amount;
        consentResponse.data.controlParameters.maximumIndividualAmount.amount = "12312132233"
        val paymentSubmissionInvalidAmount = createPaymentRequest(consentResponse)

        val signatureWithInvalidAmount = DefaultJwsSignatureProducer(tppResource.tpp).createDetachedSignature(
                defaultMapper.writeValueAsString(paymentSubmissionInvalidAmount)
        )

        consentResponse.data.controlParameters.maximumIndividualAmount.amount = initialAmount

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
        val (consentResponse, accessToken) = createDomesticVrpConsentsApi.createDomesticVrpConsentAndAuthorize(
                consentRequest
        )

        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

        consentResponse.data.initiation.debtorAccount.name = "fake name"
        val paymentSubmissionRequest = createPaymentRequest(consentResponse)

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            paymentApiClient.buildSubmitPaymentRequest(createPaymentUrl, accessToken, paymentSubmissionRequest)
                    .configureDefaultJwsSignatureProducer()
                    .configureJwsSignatureProducer(DefaultJwsSignatureProducer(tppResource.tpp)).sendRequest()
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.BAD_REQUEST)
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
        return submitPaymentForConsent(consentResponse, authorizationToken)
    }

    private fun submitPaymentForConsent(
            consentResponse: OBDomesticVRPConsentResponse,
            authorizationToken: AccessToken
    ): OBDomesticVRPResponse {

        val paymentSubmissionRequest = createPaymentRequest(consentResponse)
        // If the CreditorAccount was not specified in the consent, the CreditorAccount must be specified in the instruction
        if (consentResponse.data.initiation.creditorAccount == null && paymentSubmissionRequest.data.instruction.creditorAccount == null) {
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

    private fun createPaymentRequest(consentResponse: OBDomesticVRPConsentResponse): OBDomesticVRPRequest {
        return OBDomesticVRPRequest().data(
                OBDomesticVRPRequestData()
                        .consentId(consentResponse.data.consentId)
                        .initiation(consentResponse.data.initiation)
                        .instruction(PaymentFactory.buildVrpInstruction(consentResponse))
        ).risk(consentResponse.risk)
    }
}
