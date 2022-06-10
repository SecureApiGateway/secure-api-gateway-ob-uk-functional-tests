package com.forgerock.uk.openbanking.support.funds

import com.forgerock.openbanking.jwt.model.CreateDetachedJwtResponse
import com.forgerock.openbanking.jwt.model.SigningRequest
import com.forgerock.securebanking.framework.data.AccessToken
import com.forgerock.securebanking.framework.data.Tpp
import com.forgerock.securebanking.framework.http.fuel.jsonBody
import com.forgerock.securebanking.framework.http.fuel.responseObject
import com.forgerock.securebanking.framework.utils.GsonUtils
import com.forgerock.securebanking.openbanking.uk.common.api.meta.OBConstants
import com.forgerock.securebanking.openbanking.uk.common.api.meta.OBVersion
import com.forgerock.uk.openbanking.support.discovery.asDiscovery
import com.forgerock.uk.openbanking.support.discovery.rsDiscovery
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.HttpException
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.core.isSuccessful
import com.nimbusds.jose.JWSObject
import java.util.*

class FundsConfirmationRS {

    companion object {
        private const val TAN = "openbanking.org.uk"
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
        accessToken: AccessToken,
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
                OBConstants.Scope.ACCOUNTS,
                OBConstants.Scope.PAYMENTS,
                OBConstants.Scope.FUNDS_CONFIRMATIONS
            )
        ).joinToString(separator = " ")
        val body = listOf(
            "grant_type" to "client_credentials",
            "scope" to scopes
        )
        val (_, accessTokenResponse, result) = Fuel.post(asDiscovery.token_endpoint, parameters = body)
            .authentication()
            .basic(tpp.registrationResponse.client_id, tpp.registrationResponse.client_secret!!)
            .responseObject<AccessToken>()
        if (!accessTokenResponse.isSuccessful) throw AssertionError("Could not get access token", result.component2())
        return result.get().access_token
    }

    fun getDetachedJws(body: Any, tpp: Tpp, version: OBVersion): String {
        val softwareStatement = JWSObject.parse(tpp.registrationResponse.software_statement)
        val ssPayload = softwareStatement.payload.toJSONObject()
        val orgId: String = ssPayload["org_id"] as String
        val softwareId: String = ssPayload["software_id"] as String

        val (_, detachedJwtResponse, detachedJwt) = Fuel.post("https://jwkms.DOMAIN/api/crypto/signPayloadToDetachedJwt")
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
            .header("x-fapi-financial-id", rsDiscovery.Data.FinancialId?:"")
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
        return GsonUtils.gson.toJson(signingRequest)
    }
}
