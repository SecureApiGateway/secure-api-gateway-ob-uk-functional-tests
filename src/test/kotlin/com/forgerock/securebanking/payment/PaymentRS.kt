package com.forgerock.securebanking.payment

import com.fasterxml.jackson.module.kotlin.readValue
import com.forgerock.openbanking.common.model.version.OBVersion
import com.forgerock.openbanking.common.model.version.OBVersion.v3_1_2
import com.forgerock.openbanking.common.model.version.OBVersion.v3_1_4
import com.forgerock.openbanking.constants.OpenBankingConstants
import com.forgerock.openbanking.jwt.model.CreateDetachedJwtResponse
import com.forgerock.openbanking.jwt.model.SigningRequest
import com.forgerock.securebanking.*
import com.forgerock.securebanking.discovery.asDiscovery
import com.forgerock.securebanking.discovery.rsDiscovery
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.HttpException
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.core.isSuccessful
import com.google.gson.GsonBuilder
import com.nimbusds.jose.JWSObject
import com.nimbusds.jose.Payload
import com.nimbusds.jose.crypto.RSASSAVerifier
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import java.util.*
import java.util.regex.Pattern

/**
 * Generic RS client methods for payment tests
 */
class PaymentRS {

    object GrantTypes {
        const val CLIENT_CREDENTIALS = "client_credentials"
        const val AUTHORIZATION_CODE = "authorization_code"
    }

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
        version: OBVersion = v3_1_2
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

    fun submitFilePayment(
        consentFileUrl: String,
        file: String,
        mediaType: String,
        tpp: Tpp,
        version: OBVersion = v3_1_2
    ): Response {
        try {
            val accessToken = getAccessToken(tpp)
            val detachedJwt = getDetachedJws(file, tpp, version)
            val (_, consentResponse, r) = Fuel.post(consentFileUrl)
                .jsonBody<String>(file)
                .defaultHeaders(accessToken)
                .header("Content-Type", mediaType)
                .header("x-jws-signature", detachedJwt)
                .responseString()
            if (!consentResponse.isSuccessful) {
                throw AssertionError("Could not create consent with: ${String(consentResponse.data)}", r.component2())
            }
            return consentResponse
        } catch (e: HttpException) {
            println(e)
            throw e
        }
    }

    fun submitCSVFilePayment(
        consentFileUrl: String,
        file: String,
        mediaType: String,
        tpp: Tpp,
        version: OBVersion = v3_1_2
    ): Response {
        try {
            val accessToken = getAccessToken(tpp)
            val detachedJwt = getDetachedJws(file, tpp, version)
            val (_, consentResponse, r) = Fuel.post(consentFileUrl)
                .jsonBody<String>(file)
                .defaultHeaders(accessToken)
                .header("Content-Type", mediaType)
                .header("x-jws-signature", detachedJwt)
                .responseString()
            if (!consentResponse.isSuccessful) {
                throw AssertionError("Could not create consent with: ${String(consentResponse.data)}", r.component2())
            }
            return consentResponse
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

    inline fun <reified T : Any> submitPayment(
        paymentUrl: String,
        paymentRequest: Any,
        accessToken: com.forgerock.securebanking.AccessToken,
        tpp: Tpp,
        version: OBVersion = v3_1_2
    ): T {
        val (_, response, r) = Fuel.post(paymentUrl)
            .jsonBody(paymentRequest)
            .defaultHeaders(accessToken.access_token)
            .header("x-jws-signature", getDetachedJws(paymentRequest, tpp, version))
            .responseObject<T>()
        if (!response.isSuccessful) throw AssertionError(
            "Could not create payment submission with: ${String(response.data)}",
            r.component2()
        )
        return r.get()
    }

    inline fun <reified T : Any> getPayment(
        url: String,
        paymentId: String,
        accessToken: com.forgerock.securebanking.AccessToken,
        version: OBVersion = v3_1_2
    ): T {
        return getCall("$url/$paymentId", accessToken, version);
    }

    inline fun <reified T : Any> getFundsConfirmation(
        url: String,
        accessToken: com.forgerock.securebanking.AccessToken,
        version: OBVersion = v3_1_2
    ): T {
        return getCall(url, accessToken, version);
    }

    inline fun <reified T : Any> consentRequest_InvalidDetachedJws(
        consentUrl: String,
        consentRequest: Any,
        tpp: Tpp
    ): Response {
        val accessToken = getAccessToken(tpp)
        val (_, consentResponse, _) = Fuel.post(consentUrl)
            .jsonBody(consentRequest)
            .defaultHeaders(accessToken)
            .header("x-jws-signature", "invalid-jwt")
            .responseObject<T>()
        return consentResponse
    }

    inline fun <reified T : Any> consentRequest_DetachedJwsMissingB64Claim(
        consentUrl: String,
        consentRequest: Any,
        tpp: Tpp
    ): Response {
        val accessToken = getAccessToken(tpp)
        val detachedJwt =
            getDetachedJws(consentRequest, tpp, v3_1_4) // v3.1.4 onwards should not contain the b64 header
        val (_, consentResponse, _) = Fuel.post(consentUrl)
            .jsonBody(consentRequest)
            .defaultHeaders(accessToken)
            .header("x-jws-signature", detachedJwt)
            .responseObject<T>()
        return consentResponse
    }

    inline fun <reified T : Any> submitPayment_InvalidDetachedJws(
        paymentUrl: String,
        paymentRequest: Any,
        accessToken: com.forgerock.securebanking.AccessToken
    ): Response {
        val (_, response, _) = Fuel.post(paymentUrl)
            .jsonBody(paymentRequest)
            .defaultHeaders(accessToken.access_token)
            .header("x-jws-signature", "invalid-jws")
            .responseObject<T>()
        return response
    }

    inline fun <reified T> getCall(
        url: String,
        accessToken: com.forgerock.securebanking.AccessToken,
        version: OBVersion = v3_1_2
    ): T {
        val (_, response, result) = Fuel.get("$url")
            .header("Authorization", "Bearer ${accessToken.access_token}")
            .header("x-fapi-financial-id", rsDiscovery.Data.FinancialId)
            .responseString()
        if (!response.isSuccessful) throw AssertionError(
            "Could not get funds confirmation submission with: ${
                String(
                    response.data
                )
            }", result.component2()
        )

        verifyDetachedJws(response.header("x-jws-signature").first(), Payload(result.get()), version)

        return defaultMapper.readValue(result.get())
    }

    fun getAccessToken(tpp: Tpp): String {
        val scopes = asDiscovery.scopes_supported.intersect(
            listOf(
                OpenBankingConstants.Scope.ACCOUNTS,
                OpenBankingConstants.Scope.PAYMENTS
            )
        ).joinToString(separator = " ")
        val body = listOf(
            "grant_type" to GrantTypes.CLIENT_CREDENTIALS,
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

    fun verifyDetachedJws(detachedJws: String, payload: Payload, version: OBVersion) {
        val isDetached = DETACHED_SIGNATURE_PATTERN.matcher(detachedJws).find()
        val parsedJWSObject = if (isDetached) JWSObject.parse(detachedJws, payload) else JWSObject.parse(detachedJws)
        val jwk = jwkSet.getKeyByKeyId(parsedJWSObject.header.keyID)
        val rsaPublicKey = (jwk as RSAKey).toRSAPublicKey()
        val verifier = RSASSAVerifier(rsaPublicKey, getCriticalHeaders(version))
        if (!parsedJWSObject.verify(verifier)) {
            throw AssertionError("Could not validate detached JWT: $detachedJws")
        }
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
        if (version.isBeforeVersion(v3_1_4)) {
            claimsBuilder.includeB64(true)
        }
        val signingRequest = SigningRequest.builder().customHeaderClaims(claimsBuilder.build()).build()
        return GsonBuilder().create().toJson(signingRequest)
    }

    private fun getCriticalHeaders(version: OBVersion): Set<String> {
        val criticalHeaders = mutableSetOf<String>()
        if (version.isBeforeVersion(v3_1_4)) {
            criticalHeaders.add("b64")
        }
        criticalHeaders.add("http://openbanking.org.uk/iat")
        criticalHeaders.add("http://openbanking.org.uk/iss")
        criticalHeaders.add("http://openbanking.org.uk/tan")
        return criticalHeaders
    }
}
