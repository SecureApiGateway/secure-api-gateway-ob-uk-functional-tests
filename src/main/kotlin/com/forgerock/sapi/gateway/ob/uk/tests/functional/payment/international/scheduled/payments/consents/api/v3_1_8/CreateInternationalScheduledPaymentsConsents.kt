package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.scheduled.payments.consents.api.v3_1_8

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThan
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import com.forgerock.sapi.gateway.framework.conditions.Status
import com.forgerock.sapi.gateway.framework.configuration.psu
import com.forgerock.sapi.gateway.framework.data.AccessToken
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.framework.consent.ConsentFactoryRegistryHolder
import com.forgerock.sapi.gateway.ob.uk.framework.consent.payment.OBWriteInternationalScheduledConsent5Factory
import com.forgerock.sapi.gateway.ob.uk.support.discovery.getPaymentsApiLinks
import com.forgerock.sapi.gateway.ob.uk.support.payment.*
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.github.kittinunf.fuel.core.FuelError
import org.assertj.core.api.Assertions
import org.joda.time.DateTime
import uk.org.openbanking.datamodel.payment.OBExchangeRateType
import uk.org.openbanking.datamodel.payment.OBWriteDomestic2DataInitiationDebtorAccount
import uk.org.openbanking.datamodel.payment.OBWriteInternationalScheduledConsent5
import uk.org.openbanking.datamodel.payment.OBWriteInternationalScheduledConsentResponse6
import java.math.BigDecimal
import java.util.*

class CreateInternationalScheduledPaymentsConsents(
    val version: OBVersion,
    val tppResource: CreateTppCallback.TppResource
) {
    private val paymentLinks = getPaymentsApiLinks(version)
    private val paymentApiClient = tppResource.tpp.paymentApiClient
    private val consentFactory = ConsentFactoryRegistryHolder.consentFactoryRegistry.getConsentFactory(
        OBWriteInternationalScheduledConsent5Factory::class.java
    )

    fun createInternationalScheduledPaymentsConsentsTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val consent = createInternationalScheduledPaymentConsent(consentRequest)

        // Then
        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()
    }

    fun createInternationalScheduledPaymentsConsents_SameIdempotencyKeyMultipleRequestTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val idempotencyKey = UUID.randomUUID().toString()

        // When
        // first request
        val consentResponse1 = paymentApiClient.newPostRequestBuilder(
            paymentLinks.CreateInternationalScheduledPaymentConsent,
            tppResource.tpp.getClientCredentialsAccessToken(defaultPaymentScopesForAccessToken),
            consentRequest
        ).addIdempotencyKeyHeader(idempotencyKey).sendRequest<OBWriteInternationalScheduledConsentResponse6>()
        // second request with the same idempotencyKey
        val consentResponse2 = paymentApiClient.newPostRequestBuilder(
            paymentLinks.CreateInternationalScheduledPaymentConsent,
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

    fun createInternationalScheduledPaymentsConsents_NoIdempotencyKey_throwsBadRequestTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        // when
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            paymentApiClient.newPostRequestBuilder(
                paymentLinks.CreateInternationalScheduledPaymentConsent,
                tppResource.tpp.getClientCredentialsAccessToken(defaultPaymentScopesForAccessToken),
                consentRequest
            ).deleteIdempotencyKeyHeader().sendRequest<OBWriteInternationalScheduledConsentResponse6>()
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat((exception.cause as FuelError).response.body()).isNotNull()
        assertThat(exception.message.toString()).contains("\"Errors\":[{\"ErrorCode\":\"UK.OBIE.Header.Missing\",\"Message\":\"Required request header 'x-idempotency-key' for method parameter type String is not present")
    }

    fun createInternationalScheduledPaymentsConsents_withDebtorAccountTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
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
        val consentRequest = consentFactory.createConsent()
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
        val consentRequest = consentFactory.createConsentWithOnlyMandatoryFieldsPopulated()
        val consent = createInternationalScheduledPaymentConsent(consentRequest)

        // Then
        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()

        assertThat(consentRequest.data.initiation.exchangeRateInformation).isNull() // ExchangeRateInformation omitted from request
        assertThat(consent.data.initiation.exchangeRateInformation).isNull() // Verify response initiation is unchanged

        // Validate ASPSP has generated ExchangeRateInformation in the response data section
        val exchangeRateInformation = consent.data.exchangeRateInformation
        assertThat(exchangeRateInformation).isNotNull()
        assertThat(exchangeRateInformation.rateType).isEqualTo(OBExchangeRateType.INDICATIVE)
        assertThat(exchangeRateInformation.unitCurrency).isEqualTo(consentRequest.data.initiation.instructedAmount.currency)
        assertThat(exchangeRateInformation.exchangeRate).isGreaterThan(BigDecimal.ZERO)

    }

    fun shouldCreateInternationalScheduledPaymentConsents_throwsSendInvalidFormatDetachedJws_Test() {
        // Given
        val consentRequest = consentFactory.createConsent()

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
        val consentRequest = consentFactory.createConsent()

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
        val consentRequest = consentFactory.createConsent()

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
        val consentRequest = consentFactory.createConsent()

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
        val consentRequest = consentFactory.createConsent()
        consentRequest.data.initiation.requestedExecutionDateTime = DateTime.now().minusDays(1)
        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            createInternationalScheduledPaymentConsent(consentRequest)
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.REQUESTED_EXECUTION_TIME_MUST_BE_IN_FUTURE)
    }

    fun shouldCreateInternationalScheduledPaymentConsents_throwsRejectedConsent_Test() {
        // Given
        val consentRequest = consentFactory.createConsent()

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
}