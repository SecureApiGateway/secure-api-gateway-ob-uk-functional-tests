package com.forgerock.securebanking.funds

import com.forgerock.openbanking.common.model.version.OBVersion
import com.forgerock.openbanking.constants.OpenBankingConstants
import com.forgerock.openbanking.jwt.model.CreateDetachedJwtResponse
import com.forgerock.openbanking.jwt.model.SigningRequest
import com.forgerock.securebanking.DOMAIN
import com.forgerock.securebanking.Tpp
import com.forgerock.securebanking.discovery.asDiscovery
import com.forgerock.securebanking.discovery.rsDiscovery
import com.forgerock.securebanking.jsonBody
import com.forgerock.securebanking.responseObject
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.HttpException
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.core.isSuccessful
import com.google.gson.GsonBuilder
import com.nimbusds.jose.JWSObject
import com.nimbusds.jose.jwk.JWKSet
import java.util.*
import java.util.regex.Pattern

class FundsConfirmationRS {

    companion object {
        private const val TAN = "openbanking.org.uk"
        private val DETACHED_SIGNATURE_PATTERN = Pattern.compile("(.*\\.)(\\..*)")

        private val jwkSet = getJwkSet()

        private fun getJwkSet(): JWKSet {
            val jwks_uri = asDiscovery.jwks_uri;
            val (_, response, result) = Fuel.get(jwks_uri).responseString()
            return if (response.isSuccessful) JWKSet.parse(result.get()) else throw AssertionError(
                "Failed to retrieve JWKs",
                result.component2()
            )
        }
    }

    inline fun <reified T : Any> consent(
        consentUrl: String,
        consentRequest: Any,
        tpp: Tpp,
        version: OBVersion = OBVersion.v3_1_2
    ): T {
        try {
            val accessToken = getAccessToken(tpp)
            val detachedJwt = getDetachedJws(consentRequest, tpp, version)
            val (_, consentResponse, r) = Fuel.post(consentUrl)
                .jsonBody(consentRequest)
                .defaultHeaders(accessToken)
                .header("x-jws-signature", detachedJwt)
                .responseObject<T>()
            if (!consentResponse.isSuccessful) {
                throw AssertionError("Could not create consent with: ${String(consentResponse.data)}", r.component2())
            }
            return r.get()
        } catch (e: HttpException) {
            println(e)
            throw e
        }
    }

    inline fun <reified T : Any> getConsent(consentUrl: String, tpp: Tpp): T {
        val accessToken = getAccessToken(tpp)
        val (_, consentResponse, r) = Fuel.get(consentUrl)
            .defaultHeaders(accessToken)
            .responseObject<T>()
        if (!consentResponse.isSuccessful) throw AssertionError(
            "Could not get consent with with: ${
                String(
                    consentResponse.data
                )
            }", r.component2()
        )
        return r.get()
    }

    inline fun <reified T : Any> submitFundConfirmation(
        fundConfirmationUrl: String,
        fundConfirmationRequest: Any,
        accessToken: com.forgerock.securebanking.AccessToken,
        tpp: Tpp,
        version: OBVersion = OBVersion.v3_1_2
    ): T {
        val (_, response, r) = Fuel.post(fundConfirmationUrl)
            .jsonBody(fundConfirmationRequest)
            .defaultHeaders(accessToken.access_token)
            .header("x-jws-signature", getDetachedJws(fundConfirmationRequest, tpp, version))
            .responseObject<T>()
        if (!response.isSuccessful) throw AssertionError(
            "Could not create payment submission with: ${String(response.data)}",
            r.component2()
        )
        return r.get()
    }

    fun getAccessToken(tpp: Tpp): String {
        val scopes = asDiscovery.scopes_supported.intersect(
            listOf(
                OpenBankingConstants.Scope.ACCOUNTS,
                OpenBankingConstants.Scope.PAYMENTS,
                OpenBankingConstants.Scope.FUNDS_CONFIRMATIONS
            )
        ).joinToString(separator = " ")
        val body = listOf(
            "grant_type" to "client_credentials",
            "scope" to scopes
        )
        val (_, accessTokenResponse, result) = Fuel.post(asDiscovery.token_endpoint, parameters = body)
            .authentication()
            .basic(tpp.registrationResponse.client_id, tpp.registrationResponse.client_secret!!)
            .responseObject<com.forgerock.securebanking.AccessToken>()
        if (!accessTokenResponse.isSuccessful) throw AssertionError("Could not get access token", result.component2())
        return result.get().access_token
    }

    fun getDetachedJws(body: Any, tpp: Tpp, version: OBVersion): String {
        val softwareStatement = JWSObject.parse(tpp.registrationResponse.software_statement)
        val ssPayload = softwareStatement.payload.toJSONObject()
        val orgId: String = ssPayload["org_id"] as String
        val softwareId: String = ssPayload["software_id"] as String

        val (_, detachedJwtResponse, detachedJwt) = Fuel.post("https://jwkms.$DOMAIN/api/crypto/signPayloadToDetachedJwt")
            .header("issuerId", "$orgId/$softwareId")
            .header("signingRequest", serialisedSigningRequest(version))
            .jsonBody(body)
            .responseObject<CreateDetachedJwtResponse>()
        if (!detachedJwtResponse.isSuccessful) throw AssertionError(
            "Could not get detached JWS",
            detachedJwt.component2()
        )
        return detachedJwt.get().detachedSignature
    }

    fun Request.defaultHeaders(accessToken: String) =
        this
            .header("Authorization", "Bearer $accessToken")
            // x-fapi-financial-id is no longer required in v3.1.2 onwards
            .header("x-fapi-financial-id", rsDiscovery.Data.FinancialId)
            .header("x-idempotency-key", UUID.randomUUID().toString())
            .header("Accept", "application/json")

    private fun serialisedSigningRequest(version: OBVersion): String {
        val claimsBuilder = SigningRequest.CustomHeaderClaims.builder()
            .includeOBIss(true)
            .includeOBIat(true)
            .includeCrit(true)
            .tan(TAN)
        if (version.isBeforeVersion(OBVersion.v3_1_4)) {
            claimsBuilder.includeB64(true)
        }
        val signingRequest = SigningRequest.builder().customHeaderClaims(claimsBuilder.build()).build()
        return GsonBuilder().create().toJson(signingRequest)
    }
}
