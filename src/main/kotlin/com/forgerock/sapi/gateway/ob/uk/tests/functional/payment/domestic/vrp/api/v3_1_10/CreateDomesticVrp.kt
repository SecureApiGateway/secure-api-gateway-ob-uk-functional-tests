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
import com.forgerock.sapi.gateway.ob.uk.framework.consent.ConsentFactoryRegistryHolder
import com.forgerock.sapi.gateway.ob.uk.framework.consent.payment.OBDomesticVRPConsentRequestFactory
import com.forgerock.sapi.gateway.ob.uk.support.discovery.getPaymentsApiLinks
import com.forgerock.sapi.gateway.ob.uk.support.payment.*
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.domestic.vrp.consents.api.v3_1_10.CreateDomesticVrpConsents
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Headers
import org.assertj.core.api.Assertions
import uk.org.openbanking.datamodel.vrp.*
import uk.org.openbanking.testsupport.vrp.OBDomesticVrpCommonTestDataFactory
import java.math.BigDecimal
import java.util.UUID


class CreateDomesticVrp(val version: OBVersion, val tppResource: CreateTppCallback.TppResource) {

    private val createDomesticVrpConsentsApi = CreateDomesticVrpConsents(version, tppResource)
    private val paymentApiClient = tppResource.tpp.paymentApiClient
    private val paymentLinks = getPaymentsApiLinks(version)
    private val createPaymentUrl = paymentLinks.CreateDomesticVRPPayment
    private val consentFactory: OBDomesticVRPConsentRequestFactory = ConsentFactoryRegistryHolder.consentFactoryRegistry.getConsentFactory(
        OBDomesticVRPConsentRequestFactory::class.java)

    fun createDomesticVrpPaymentTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val result = submitPayment(consentRequest)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.consentId).isNotEmpty()
        assertThat(result.data.charges).isNotNull().isNotEmpty()
        assertThat(result.links.self.toString()).isEqualTo(createPaymentUrl + "/" + result.data.domesticVRPId)
    }

    fun createDomesticVrpPaymentWithDebtorAccountTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val debtorAccount = PsuData().getDebtorAccount()
        consentRequest.data.initiation.debtorAccount(
            OBCashAccountDebtorWithName()
                .identification(debtorAccount?.Identification)
                .name(debtorAccount?.Name)
                .schemeName(debtorAccount?.SchemeName)
                .secondaryIdentification(debtorAccount?.SecondaryIdentification)
        )
        val result = submitPayment(consentRequest)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.consentId).isNotEmpty()
        assertThat(result.data.charges).isNotNull().isNotEmpty()
        assertThat(result.links.self.toString()).isEqualTo(createPaymentUrl + "/" + result.data.domesticVRPId)
    }

    fun shouldCreateMultiplePaymentsForConsent() {
        val consentRequest = consentFactory.createConsent()
        val (consent, authorizationToken) = createDomesticVrpConsentsApi.createDomesticVrpConsentAndAuthorize(
            consentRequest
        )
        val numPaymentsToCreate = 5
        val paymentIds = HashSet<String>()
        for (i in 1..numPaymentsToCreate) {
            val result = submitPayment(consent.data.consentId, consentRequest, authorizationToken)

            // Then
            assertThat(result).isNotNull()
            assertThat(result.data).isNotNull()
            assertThat(result.data.consentId).isNotEmpty()
            assertThat(result.data.domesticVRPId).isNotEmpty()
            assertThat(result.data.charges).isNotNull().isNotEmpty()
            assertThat(result.links.self.toString()).isEqualTo(createPaymentUrl + "/" + result.data.domesticVRPId)
            paymentIds.add(result.data.domesticVRPId)
        }
        assertThat(paymentIds.size).isEqualTo(numPaymentsToCreate)
    }

    fun limitBreachSimulationDomesticVrpPaymentTest() {
        // Given
        val headers = Headers()
        val consentRequest = consentFactory.createConsent()
        val (consentResponse, authorizationToken) = createDomesticVrpConsentsApi.createDomesticVrpConsentAndAuthorize(
            consentRequest
        )
        val paymentSubmissionRequest = createPaymentRequest(consentResponse.data.consentId, consentRequest)
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
            consentFactory.createConsentWithOnlyMandatoryFieldsPopulated()
        // When
        val result = submitPayment(consentRequest)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data.consentId).isNotEmpty()
    }


    fun shouldCreateDomesticVrp_throwsSendInvalidFormatDetachedJwsTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val (consentResponse, accessToken) = createDomesticVrpConsentsApi.createDomesticVrpConsentAndAuthorize(
            consentRequest
        )

        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = createPaymentRequest(consentResponse.data.consentId, consentRequest)

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
        val consentRequest = consentFactory.createConsent()
        val (consentResponse, accessToken) = createDomesticVrpConsentsApi.createDomesticVrpConsentAndAuthorize(
            consentRequest
        )

        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = createPaymentRequest(consentResponse.data.consentId, consentRequest)

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
        val consentRequest = consentFactory.createConsent()
        val (consentResponse, accessToken) = createDomesticVrpConsentsApi.createDomesticVrpConsentAndAuthorize(
            consentRequest
        )

        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = createPaymentRequest(consentResponse.data.consentId, consentRequest)

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
        val consentRequest = consentFactory.createConsent()
        val (consentResponse, accessToken) = createDomesticVrpConsentsApi.createDomesticVrpConsentAndAuthorize(
            consentRequest
        )

        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = createPaymentRequest(consentResponse.data.consentId, consentRequest)

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
        val consentRequest = consentFactory.createConsent()
        val (consentResponse, accessToken) = createDomesticVrpConsentsApi.createDomesticVrpConsentAndAuthorize(
            consentRequest
        )

        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = createPaymentRequest(consentResponse.data.consentId, consentRequest)

        consentResponse.data.consentId = com.forgerock.sapi.gateway.ob.uk.framework.constants.INVALID_CONSENT_ID
        val paymentSubmissionWithInvalidConsentId = createPaymentRequest(consentResponse.data.consentId, consentRequest)

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
        val consentRequest = consentFactory.createConsent()
        val (consentResponse, accessToken) = createDomesticVrpConsentsApi.createDomesticVrpConsentAndAuthorize(
            consentRequest
        )

        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

        val paymentSubmissionRequest = createPaymentRequest(consentResponse.data.consentId, consentRequest)

        val initialAmount = consentResponse.data.controlParameters.maximumIndividualAmount.amount;
        consentRequest.data.controlParameters.maximumIndividualAmount.amount = "12312132233"
        val paymentSubmissionInvalidAmount = createPaymentRequest(consentResponse.data.consentId, consentRequest)

        val signatureWithInvalidAmount = DefaultJwsSignatureProducer(tppResource.tpp).createDetachedSignature(
            defaultMapper.writeValueAsString(paymentSubmissionInvalidAmount)
        )

        consentRequest.data.controlParameters.maximumIndividualAmount.amount = initialAmount

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
        val consentRequest = consentFactory.createConsent()
        consentRequest.data.controlParameters.vrPType = listOf("UK.OBIE.VRPType.Other", "UK.OBIE.VRPType.Sweeping")
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            createDomesticVrpConsentsApi.createDomesticVrpConsentAndAuthorize(
                consentRequest
            )
        }

        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.BAD_REQUEST)
        assertThat(exception.message.toString()).contains("Your data request is invalid: reason VRPType specified is not supported, only the following types are supported: UK.OBIE.VRPType.Sweeping")

    }

    fun shouldCreateDomesticVrp_throwsPolicyValidationErrorTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val (consentResponse, accessToken) = createDomesticVrpConsentsApi.createDomesticVrpConsentAndAuthorize(
            consentRequest
        )

        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)

        consentRequest.data.initiation.debtorAccount =
            OBCashAccountDebtorWithName()
                .identification("12341325")
                .name("diff name")
                .schemeName("UK.OBIE.SortCodeAccountNumber")

        val paymentSubmissionRequest = createPaymentRequest(consentResponse.data.consentId, consentRequest)

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

    fun shouldFailToCreateVrpWhenMaxIndividualAmountBreachedTest() {
        val consentRequest = consentFactory.createConsent()

        val (consent, authorizationToken) = createDomesticVrpConsentsApi.createDomesticVrpConsentAndAuthorize(
                consentRequest
        )

        val paymentSubmissionRequest = createPaymentRequest(consent.data.consentId, consentRequest)

        // Attempt to create a payment with instructedAmount equal to maximumIndividualAmount + 1
        val instructedAmount = consentRequest.data.controlParameters.maximumIndividualAmount
        instructedAmount.amount = BigDecimal(instructedAmount.amount).plus(BigDecimal.ONE).toString()
        paymentSubmissionRequest.data.instruction.instructedAmount(instructedAmount)

        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            paymentApiClient.submitPayment(
                    createPaymentUrl,
                    authorizationToken,
                    paymentSubmissionRequest)
        }

        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message!!).contains("\"ErrorCode\":\"UK.OBIE.Rules.FailsControlParameters\"")
        assertThat(exception.message!!).contains("\"Message\":\"The field 'InstructedAmount' breaches a limitation set by 'MaximumIndividualAmount'\"")
    }

    fun testCreatingPaymentIsIdempotent() {
        val consentRequest = consentFactory.createConsent()
        val (consent, authorizationToken) = createDomesticVrpConsentsApi.createDomesticVrpConsentAndAuthorize(
            consentRequest
        )
        val paymentSubmissionRequest = createPaymentRequest(consent.data.consentId, consentRequest)

        val idempotencyKey = UUID.randomUUID().toString()
        val firstPaymentResponse: OBDomesticVRPResponse = paymentApiClient.newPostRequestBuilder(createPaymentUrl, authorizationToken, paymentSubmissionRequest)
            .addIdempotencyKeyHeader(idempotencyKey)
            .sendRequest()

        Assertions.assertThat(firstPaymentResponse).isNotNull()
        Assertions.assertThat(firstPaymentResponse.data).isNotNull()
        Assertions.assertThat(firstPaymentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(firstPaymentResponse.links.self.toString()).isEqualTo(createPaymentUrl + "/" + firstPaymentResponse.data.domesticVRPId)

        // Submit again with same key
        val secondPaymentResponse: OBDomesticVRPResponse = paymentApiClient.newPostRequestBuilder(createPaymentUrl, authorizationToken, paymentSubmissionRequest)
            .addIdempotencyKeyHeader(idempotencyKey)
            .sendRequest()

        Assertions.assertThat(secondPaymentResponse).isEqualTo(firstPaymentResponse)
    }

    fun submitPayment(consentRequest: OBDomesticVRPConsentRequest): OBDomesticVRPResponse {
        val (consent, authorizationToken) = createDomesticVrpConsentsApi.createDomesticVrpConsentAndAuthorize(
            consentRequest
        )
        return submitPayment(consent.data.consentId, consentRequest, authorizationToken)
    }

    fun submitPayment(
        consentId: String,
        consentRequest: OBDomesticVRPConsentRequest,
        authorizationToken: AccessToken
    ): OBDomesticVRPResponse {
        return submitPaymentForConsent(consentId, consentRequest, authorizationToken)
    }

    private fun submitPaymentForConsent(
        consentId: String,
        consentRequest: OBDomesticVRPConsentRequest,
        authorizationToken: AccessToken
    ): OBDomesticVRPResponse {

        val paymentSubmissionRequest = createPaymentRequest(consentId, consentRequest)
        // If the CreditorAccount was not specified in the consent, the CreditorAccount must be specified in the instruction
        if (consentRequest.data.initiation.creditorAccount == null && paymentSubmissionRequest.data.instruction.creditorAccount == null) {
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

    private fun createPaymentRequest(
        consentId: String,
        consentRequest: OBDomesticVRPConsentRequest
    ): OBDomesticVRPRequest {
        return OBDomesticVRPRequest().data(
            OBDomesticVRPRequestData()
                    .consentId(consentId)
                    .initiation(PaymentFactory.copyOBDomesticVRPInitiation(consentRequest.data.initiation))
                    .instruction(buildVrpInstruction(consentRequest))
                    .psUAuthenticationMethod(consentRequest.data.controlParameters.psUAuthenticationMethods.first())
        ).risk(consentRequest.risk)
    }

    private fun buildVrpInstruction(consentRequest: OBDomesticVRPConsentRequest): OBDomesticVRPInstruction? {
        val instruction = OBDomesticVRPInstruction().creditorAccount(consentRequest.data.initiation.creditorAccount)
                                                    .instructedAmount(consentRequest.data.controlParameters.maximumIndividualAmount)
                                                    .instructionIdentification(System.nanoTime().toString())
                                                    .endToEndIdentification(System.nanoTime().toString())

        if (consentRequest.data.initiation.creditorPostalAddress != null) {
            instruction.creditorPostalAddress = consentRequest.data.initiation.creditorPostalAddress
        }
        return instruction;
    }
}
