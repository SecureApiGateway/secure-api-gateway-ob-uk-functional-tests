package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.scheduled.payments.consents.api.v3_1_8

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.sapi.gateway.framework.conditions.Status
import com.forgerock.sapi.gateway.framework.configuration.psu
import com.forgerock.sapi.gateway.framework.data.AccessToken
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.support.discovery.getPaymentsApiLinks
import com.forgerock.sapi.gateway.ob.uk.support.payment.*
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.github.kittinunf.fuel.core.FuelError
import org.assertj.core.api.Assertions
import org.joda.time.DateTime
import uk.org.openbanking.datamodel.payment.OBWriteDomestic2DataInitiationDebtorAccount
import uk.org.openbanking.datamodel.payment.OBWriteInternationalScheduledConsent5
import uk.org.openbanking.datamodel.payment.OBWriteInternationalScheduledConsentResponse6
import uk.org.openbanking.testsupport.payment.OBWriteInternationalScheduledConsentTestDataFactory
import java.util.*

class CreateInternationalScheduledPaymentsConsents(
    val version: OBVersion,
    val tppResource: CreateTppCallback.TppResource
) {
    private val paymentLinks = getPaymentsApiLinks(version)
    private val paymentApiClient = tppResource.tpp.paymentApiClient

    fun createInternationalScheduledPaymentsConsentsTest() {
        // Given
        val consentRequest =
            OBWriteInternationalScheduledConsentTestDataFactory.aValidOBWriteInternationalScheduledConsent5()
        val consent = createInternationalScheduledPaymentConsent(consentRequest)

        // Then
        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()
    }

    fun createDomesticPaymentsConsents_SameIdempotencyKeyMultipleRequestTest() {
        // Given
        val consentRequest =
            OBWriteInternationalScheduledConsentTestDataFactory.aValidOBWriteInternationalScheduledConsent5()
        val idempotencyKey = UUID.randomUUID().toString()

        // When
        // first request
        val consentResponse1 = paymentApiClient.newPostRequestBuilder(
            paymentLinks.CreateDomesticPaymentConsent,
            tppResource.tpp.getClientCredentialsAccessToken(defaultPaymentScopesForAccessToken),
            consentRequest
        ).addIdempotencyKeyHeader(idempotencyKey).sendRequest<OBWriteInternationalScheduledConsentResponse6>()
        // second request with the same idempotencyKey
        val consentResponse2 = paymentApiClient.newPostRequestBuilder(
            paymentLinks.CreateDomesticPaymentConsent,
            tppResource.tpp.getClientCredentialsAccessToken(defaultPaymentScopesForAccessToken),
            consentRequest
        ).addIdempotencyKeyHeader(idempotencyKey).sendRequest<OBWriteInternationalScheduledConsentResponse6>()

        // Then
        assertThat(consentResponse1).isNotNull()
        assertThat(consentResponse1.data).isNotNull()
        assertThat(consentResponse1.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse1.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consentResponse1.risk).isNotNull()

        assertThat(consentResponse2).isNotNull()
        assertThat(consentResponse2.data).isNotNull()
        assertThat(consentResponse2.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse2.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consentResponse2.risk).isNotNull()

        assertThat(consentResponse1.data.consentId).equals(consentResponse2.data.consentId)

    }

    fun createDomesticPaymentsConsents_NoIdempotencyKey_throwsBadRequestTest() {
        // Given
        val consentRequest =
            OBWriteInternationalScheduledConsentTestDataFactory.aValidOBWriteInternationalScheduledConsent5()
        // when
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            paymentApiClient.newPostRequestBuilder(
                paymentLinks.CreateDomesticPaymentConsent,
                tppResource.tpp.getClientCredentialsAccessToken(defaultPaymentScopesForAccessToken),
                consentRequest
            ).deleteIdempotencyKeyHeader().sendRequest<OBWriteInternationalScheduledConsentResponse6>()
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat((exception.cause as FuelError).response.body()).isNotNull()
        assertThat(exception.message.toString()).contains("Bad request [Failed to get create the resource, 'x-idempotency-key' header / value expected]")
    }

    fun createInternationalScheduledPaymentsConsents_withDebtorAccountTest() {
        // Given
        val consentRequest =
            OBWriteInternationalScheduledConsentTestDataFactory.aValidOBWriteInternationalScheduledConsent5()
        // optional debtor account
        val debtorAccount = PsuData().getDebtorAccount()
        consentRequest.data.initiation.debtorAccount(
            OBWriteDomestic2DataInitiationDebtorAccount()
                .identification(debtorAccount?.Identification)
                .name(debtorAccount?.Name)
                .schemeName(debtorAccount?.SchemeName)
                .secondaryIdentification(debtorAccount?.SecondaryIdentification)
        )

        val consent = createInternationalScheduledPaymentConsent(consentRequest)

        // Then
        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()
    }

    fun createInternationalScheduledPaymentsConsents_throwsInvalidDebtorAccountTest() {
        // Given
        val consentRequest =
            OBWriteInternationalScheduledConsentTestDataFactory.aValidOBWriteInternationalScheduledConsent5()
        // optional debtor account (wrong debtor account)
        consentRequest.data.initiation.debtorAccount(
            OBWriteDomestic2DataInitiationDebtorAccount()
                .identification("Identification")
                .name("name")
                .schemeName("SchemeName")
                .secondaryIdentification("SecondaryIdentification")
        )

        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            createInternationalScheduledPaymentConsent(consentRequest)
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat((exception.cause as FuelError).response.body()).isNotNull()
        assertThat(exception.message.toString()).contains("Invalid debtor account")
    }

    fun createInternationalScheduledPaymentsConsents_mandatoryFields_Test() {
        // Given
        val consentRequest =
            OBWriteInternationalScheduledConsentTestDataFactory.aValidOBWriteInternationalScheduledConsent5MandatoryFields()
        val consent = createInternationalScheduledPaymentConsent(consentRequest)

        // Then
        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()
    }

    fun shouldCreateInternationalScheduledPaymentConsents_throwsSendInvalidFormatDetachedJws_Test() {
        // Given
        val consentRequest =
            OBWriteInternationalScheduledConsentTestDataFactory.aValidOBWriteInternationalScheduledConsent5()

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            buildCreateConsentRequest(consentRequest).configureJwsSignatureProducer(BadJwsSignatureProducer())
                .sendRequest()
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.INVALID_FORMAT_DETACHED_JWS_ERROR)
    }

    fun shouldCreateInternationalScheduledPaymentConsents_throwsNoDetachedJws_Test() {
        // Given
        val consentRequest =
            OBWriteInternationalScheduledConsentTestDataFactory.aValidOBWriteInternationalScheduledConsent5()

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            buildCreateConsentRequest(consentRequest).configureJwsSignatureProducer(null).sendRequest()
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.NO_DETACHED_JWS)
    }

    fun shouldCreateInternationalScheduledPaymentConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJws_Test() {
        // Given
        val consentRequest =
            OBWriteInternationalScheduledConsentTestDataFactory.aValidOBWriteInternationalScheduledConsent5()

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            buildCreateConsentRequest(consentRequest).configureJwsSignatureProducer(
                DefaultJwsSignatureProducer(
                    tppResource.tpp,
                    false
                )
            ).sendRequest()
        }

        // Then
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
    }

    fun shouldCreateInternationalScheduledPaymentConsents_throwsSendInvalidKidDetachedJws_Test() {
        // Given
        val consentRequest =
            OBWriteInternationalScheduledConsentTestDataFactory.aValidOBWriteInternationalScheduledConsent5()

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            buildCreateConsentRequest(consentRequest).configureJwsSignatureProducer(
                InvalidKidJwsSignatureProducer(
                    tppResource.tpp
                )
            ).sendRequest()
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(401)
        assertThat((exception.cause as FuelError).response.responseMessage).isEqualTo(com.forgerock.sapi.gateway.ob.uk.framework.errors.UNAUTHORIZED)
    }

    fun shouldCreateInternationalScheduledPaymentConsents_throwsRequestExecutionTimeInThePast_Test() {
        // Given
        val consentRequest =
            OBWriteInternationalScheduledConsentTestDataFactory.aValidOBWriteInternationalScheduledConsent5()
        consentRequest.data.initiation.requestedExecutionDateTime = DateTime.now().minusDays(1)
        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            createInternationalScheduledPaymentConsent(consentRequest)
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.REQUEST_EXECUTION_TIME_IN_THE_PAST)
    }

    fun shouldCreateInternationalScheduledPaymentConsents_throwsRejectedConsent_Test() {
        // Given
        val consentRequest =
            OBWriteInternationalScheduledConsentTestDataFactory.aValidOBWriteInternationalScheduledConsent5()

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            createInternationalScheduledPaymentConsentAndReject(
                consentRequest
            )
        }

        // Then
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.CONSENT_NOT_AUTHORISED)
    }

    fun createInternationalScheduledPaymentConsent(consentRequest: OBWriteInternationalScheduledConsent5): OBWriteInternationalScheduledConsentResponse6 {
        return buildCreateConsentRequest(consentRequest).sendRequest()
    }

    private fun buildCreateConsentRequest(
        consent: OBWriteInternationalScheduledConsent5
    ) = paymentApiClient.newPostRequestBuilder(
        paymentLinks.CreateInternationalScheduledPaymentConsent,
        tppResource.tpp.getClientCredentialsAccessToken(defaultPaymentScopesForAccessToken),
        consent
    )

    fun createInternationalScheduledPaymentConsentAndAuthorize(consentRequest: OBWriteInternationalScheduledConsent5): Pair<OBWriteInternationalScheduledConsentResponse6, AccessToken> {
        val consent = createInternationalScheduledPaymentConsent(consentRequest)
        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().authorizeConsent(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        return consent to accessTokenAuthorizationCode
    }

    fun createInternationalScheduledPaymentConsentAndReject(consentRequest: OBWriteInternationalScheduledConsent5): Pair<OBWriteInternationalScheduledConsentResponse6, AccessToken> {
        val consent = createInternationalScheduledPaymentConsent(consentRequest)
        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().authorizeConsent(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp,
            "Rejected"
        )

        return consent to accessTokenAuthorizationCode
    }

    fun getPatchedConsent(consent: OBWriteInternationalScheduledConsentResponse6): OBWriteInternationalScheduledConsentResponse6 {
        val patchedConsent = paymentApiClient.getConsent<OBWriteInternationalScheduledConsentResponse6>(
            paymentLinks.GetInternationalScheduledPaymentConsent,
            consent.data.consentId,
            tppResource.tpp.getClientCredentialsAccessToken(defaultPaymentScopesForAccessToken)
        )
        assertThat(patchedConsent).isNotNull()
        assertThat(patchedConsent.data).isNotNull()
        assertThat(patchedConsent.risk).isNotNull()
        assertThat(patchedConsent.data.consentId).isNotEmpty()
        Assertions.assertThat(patchedConsent.data.status.toString()).`is`(Status.consentCondition)
        return patchedConsent
    }
}