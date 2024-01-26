package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.international.payments.consents.api.v3_1_8

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
import com.forgerock.sapi.gateway.ob.uk.framework.consent.payment.OBWriteInternationalConsent5Factory
import com.forgerock.sapi.gateway.ob.uk.support.discovery.getPaymentsApiLinks
import com.forgerock.sapi.gateway.ob.uk.support.payment.*
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.github.kittinunf.fuel.core.FuelError
import org.assertj.core.api.Assertions
import uk.org.openbanking.datamodel.payment.OBExchangeRateType
import uk.org.openbanking.datamodel.payment.OBWriteDomestic2DataInitiationDebtorAccount
import uk.org.openbanking.datamodel.payment.OBWriteInternationalConsent5
import uk.org.openbanking.datamodel.payment.OBWriteInternationalConsentResponse6
import java.math.BigDecimal
import java.util.*

class CreateInternationalPaymentsConsents(val version: OBVersion, val tppResource: CreateTppCallback.TppResource) {

    private val paymentLinks = getPaymentsApiLinks(version)
    private val paymentApiClient = tppResource.tpp.paymentApiClient
    private val consentFactory = ConsentFactoryRegistryHolder.consentFactoryRegistry.getConsentFactory(
        OBWriteInternationalConsent5Factory::class.java
    )

    fun createInternationalPaymentsConsents() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val consent = createInternationalPaymentConsent(consentRequest)

        // Then
        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()
    }

    fun createInternationalPaymentsConsents_SameIdempotencyKeyMultipleRequestTest() {
        // Given
        val consentRequest = consentFactory.createConsent()
        val idempotencyKey = UUID.randomUUID().toString()

        // When
        // first request
        val consentResponse1 = paymentApiClient.newPostRequestBuilder(
            paymentLinks.CreateInternationalPaymentConsent,
            tppResource.tpp.getClientCredentialsAccessToken(defaultPaymentScopesForAccessToken),
            consentRequest
        ).addIdempotencyKeyHeader(idempotencyKey).sendRequest<OBWriteInternationalConsentResponse6>()
        // second request with the same idempotencyKey
        val consentResponse2 = paymentApiClient.newPostRequestBuilder(
            paymentLinks.CreateInternationalPaymentConsent,
            tppResource.tpp.getClientCredentialsAccessToken(defaultPaymentScopesForAccessToken),
            consentRequest
        ).addIdempotencyKeyHeader(idempotencyKey).sendRequest<OBWriteInternationalConsentResponse6>()

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

    fun createInternationalPaymentsConsents_NoIdempotencyKey_throwsBadRequestTest() {
        // Given
        val consentRequest = consentFactory.createConsent()

        // when
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            paymentApiClient.newPostRequestBuilder(
                paymentLinks.CreateInternationalPaymentConsent,
                tppResource.tpp.getClientCredentialsAccessToken(defaultPaymentScopesForAccessToken),
                consentRequest
            ).deleteIdempotencyKeyHeader().sendRequest<OBWriteInternationalConsentResponse6>()
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat((exception.cause as FuelError).response.body()).isNotNull()
        assertThat(exception.message.toString()).contains("\"Errors\":[{\"ErrorCode\":\"UK.OBIE.Header.Missing\",\"Message\":\"Required request header 'x-idempotency-key' for method parameter type String is not present")
    }

    fun createInternationalPaymentsConsents_withDebtorAccountTest() {
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

        val consent = createInternationalPaymentConsent(consentRequest)

        // Then
        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()
    }

    fun createInternationalPaymentsConsents_throwsInvalidDebtorAccountTest() {
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
            createInternationalPaymentConsent(consentRequest)
        }

        // Then
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
        assertThat((exception.cause as FuelError).response.body()).isNotNull()
        assertThat(exception.message.toString()).contains("Invalid debtor account")
    }

    fun createInternationalPaymentsConsents_mandatoryFieldsTest() {
        // Given
        val consentRequest =
            consentFactory.createConsentWithOnlyMandatoryFieldsPopulated()
        val consent = createInternationalPaymentConsent(consentRequest)

        // Then
        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.risk).isNotNull()
        assertThat(consentRequest.data.initiation.exchangeRateInformation).isNull() // ExchangeRateInformation omitted from request initiation
        assertThat(consent.data.initiation.exchangeRateInformation).isNull() // Verify response initiation is unchanged

        // Validate ASPSP has generated ExchangeRateInformation in the response data section
        val exchangeRateInformation = consent.data.exchangeRateInformation
        assertThat(exchangeRateInformation).isNotNull()
        assertThat(exchangeRateInformation.rateType).isEqualTo(OBExchangeRateType.INDICATIVE)
        assertThat(exchangeRateInformation.unitCurrency).isEqualTo(consentRequest.data.initiation.instructedAmount.currency)
        assertThat(exchangeRateInformation.exchangeRate).isGreaterThan(BigDecimal.ZERO)
    }

    fun shouldCreateInternationalPaymentConsents_throwsSendInvalidFormatDetachedJwsTest() {
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

    fun shouldCreateInternationalPaymentConsents_throwsNoDetachedJwsTest() {
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

    fun shouldCreateInternationalPaymentConsents_throwsNotPermittedB64HeaderAddedInTheDetachedJwsTest() {
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

    fun shouldCreateInternationalPaymentConsents_throwsSendInvalidKidDetachedJwsTest() {
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

    fun shouldCreateInternationalPaymentConsents_throwsRejectedConsent_Test() {
        // Given
        val consentRequest = consentFactory.createConsent()

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            createInternationalPaymentConsentAndReject(
                consentRequest
            )
        }

        // Then
        assertThat(exception.message.toString()).contains(com.forgerock.sapi.gateway.ob.uk.framework.errors.CONSENT_NOT_AUTHORISED)

    }

    fun createInternationalPaymentConsent(consentRequest: OBWriteInternationalConsent5): OBWriteInternationalConsentResponse6 {
        return buildCreateConsentRequest(consentRequest).sendRequest()
    }

    private fun buildCreateConsentRequest(
        consent: OBWriteInternationalConsent5
    ) = paymentApiClient.newPostRequestBuilder(
        paymentLinks.CreateInternationalPaymentConsent,
        tppResource.tpp.getClientCredentialsAccessToken(defaultPaymentScopesForAccessToken),
        consent
    )

    fun createInternationalPaymentConsentAndAuthorize(consentRequest: OBWriteInternationalConsent5): Pair<OBWriteInternationalConsentResponse6, AccessToken> {
        val consent = createInternationalPaymentConsent(consentRequest)
        val accessTokenAuthorizationCode = PaymentAS().authorizeConsent(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        return consent to accessTokenAuthorizationCode
    }


    fun createInternationalPaymentConsentAndReject(consentRequest: OBWriteInternationalConsent5): Pair<OBWriteInternationalConsentResponse6, AccessToken> {
        val consent = createInternationalPaymentConsent(consentRequest)
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