package com.forgerock.sapi.gateway.ob.uk.tests.functional.funds.api.v3_1_10

import assertk.assertThat
import assertk.assertions.contains
import com.forgerock.sapi.gateway.framework.data.AccessToken
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.http.fuel.jsonBody
import com.forgerock.sapi.gateway.framework.http.fuel.responseObject
import com.forgerock.sapi.gateway.ob.uk.common.error.ErrorCode
import com.forgerock.sapi.gateway.ob.uk.common.error.OBRIErrorType
import com.forgerock.sapi.gateway.ob.uk.support.discovery.getFundsApiLinks
import com.forgerock.sapi.gateway.ob.uk.support.funds.FundsConfirmationConsentFactory.Companion.obFundsConfirmationConsent1
import com.forgerock.sapi.gateway.ob.uk.support.funds.FundsConfirmationFactory.Companion.obFundsConfirmation1
import com.forgerock.sapi.gateway.ob.uk.tests.functional.funds.consents.api.v3_1_10.FundsConfirmationConsents
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.core.isSuccessful
import org.apache.http.entity.ContentType
import org.assertj.core.api.Assertions
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import uk.org.openbanking.datamodel.fund.OBFundsConfirmation1
import uk.org.openbanking.datamodel.fund.OBFundsConfirmationResponse1

class FundsConfirmations(
        val version: OBVersion,
        val tppResource: CreateTppCallback.TppResource
) {

    private var fundsConfirmationConsentApi = FundsConfirmationConsents(version, tppResource)
    private val fundsConfirmationLinks = getFundsApiLinks(version)
    private val createFundsConfirmationUrl = fundsConfirmationLinks.CreateFundsConfirmation

    fun shouldCreateFundsConfirmationAvailableTrueTest() {
        // Given
        val consentRequest = obFundsConfirmationConsent1()
        val (consentResponse, authorizationToken) = fundsConfirmationConsentApi.createConsentAndGetAccessToken(consentRequest)

        // When
        val result = submitFundsConfirmation(consentResponse.data.consentId, authorizationToken)
        // Then
        genericAssertions(result)
        Assertions.assertThat(result.data.fundsAvailable).isTrue()
    }

    fun shouldCreateFundsConfirmationAvailableFalseTest() {
        // Given
        val consentRequest = obFundsConfirmationConsent1()
        val (consentResponse, authorizationToken) = fundsConfirmationConsentApi.createConsentAndGetAccessToken(consentRequest)

        // When
        val result = submitFundsConfirmationFalse(consentResponse.data.consentId, authorizationToken)
        // Then
        genericAssertions(result)
        Assertions.assertThat(result.data.fundsAvailable).isFalse()
    }

    fun createFundsConfirmation_currencyMismatch_Test() {
        // Given
        val consentRequest = obFundsConfirmationConsent1()
        val (consentResponse, authorizationToken) = fundsConfirmationConsentApi.createConsentAndGetAccessToken(consentRequest)

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            submitFundsConfirmationCurrencyMismatch(consentResponse.data.consentId, authorizationToken)
        }
        // Then
        assertThat(exception.message.toString()).contains(ErrorCode.OBRI_FUNDS_CONFIRMATION_INVALID.value)
    }

    fun createFundsConfirmation_consentExpired_Test() {
        // Given
        val consentRequest = obFundsConfirmationConsent1()
        val expirationDateTime = DateTime.now().minusMinutes(1).withZone(DateTimeZone.UTC)
        consentRequest.data.expirationDateTime(expirationDateTime)
        val (consentResponse, authorizationToken) = fundsConfirmationConsentApi.createConsentAndGetAccessToken(consentRequest)

        // When
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AssertionError::class.java) {
            submitFundsConfirmation(consentResponse.data.consentId, authorizationToken)
        }
        // Then
        assertThat(exception.message.toString()).contains(OBRIErrorType.FUNDS_CONFIRMATION_EXPIRED.toOBError1(expirationDateTime).message)
        assertThat(exception.message.toString()).contains(OBRIErrorType.FUNDS_CONFIRMATION_EXPIRED.code.value)
    }

    private fun genericAssertions(result: OBFundsConfirmationResponse1) {
        Assertions.assertThat(result).isNotNull()
        Assertions.assertThat(result.data).isNotNull()
        Assertions.assertThat(result.data.consentId).isNotEmpty()
        Assertions.assertThat(result.data.fundsConfirmationId).isNotEmpty()
        Assertions.assertThat(result.links.self.toString()).isEqualTo(createFundsConfirmationUrl + "/" + result.data.fundsConfirmationId)
    }

    private fun submitFundsConfirmationFalse(
            consentId: String,
            authorizationToken: AccessToken
    ): OBFundsConfirmationResponse1 {
        val fundsRequest = obFundsConfirmation1(consentId)
        fundsRequest.data.instructedAmount.amount("1000000000.00")
        return submitRequest(
                createFundsConfirmationUrl,
                authorizationToken,
                fundsRequest
        )
    }

    private fun submitFundsConfirmationCurrencyMismatch(
            consentId: String,
            authorizationToken: AccessToken
    ): OBFundsConfirmationResponse1 {
        val fundsRequest = obFundsConfirmation1(consentId)
        fundsRequest.data.instructedAmount.currency("EUR")
        return submitRequest(
                createFundsConfirmationUrl,
                authorizationToken,
                fundsRequest
        )
    }

    private fun submitFundsConfirmation(
            consentId: String,
            authorizationToken: AccessToken
    ): OBFundsConfirmationResponse1 {
        val fundsRequest = obFundsConfirmation1(consentId)
        return submitRequest(
                createFundsConfirmationUrl,
                authorizationToken,
                fundsRequest
        )
    }

    private fun submitRequest(
            url: String, accessToken: AccessToken, body: OBFundsConfirmation1
    ): OBFundsConfirmationResponse1 {
        val request = Fuel.post(url)
                .header(Headers.CONTENT_TYPE to ContentType.APPLICATION_JSON.mimeType)
                .header(Headers.AUTHORIZATION, "Bearer ${accessToken.access_token}")
                .jsonBody(body)

        val (_, response, result) = request.responseObject<OBFundsConfirmationResponse1>()

        if (!response.isSuccessful) {
            var fapiInteractionId = "no id"
            val fapiInteractionIdHeaderVals = response.headers.get("x-fapi-interaction-id")
            if (fapiInteractionIdHeaderVals.isNotEmpty()) {
                fapiInteractionId = fapiInteractionIdHeaderVals.first()
            }

            throw AssertionError(
                    "API call: " + request.method + " " + request.url + " returned an error response:\n"
                            + result.component2()?.errorData?.toString(Charsets.UTF_8) + "\n x-fapi-interaction-id: "
                            + fapiInteractionId,
                    result.component2()
            )
        }
        return result.get()
    }
}