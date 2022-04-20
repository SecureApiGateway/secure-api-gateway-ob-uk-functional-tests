package com.forgerock.securebanking.support.payment

import com.fasterxml.jackson.module.kotlin.readValue
import com.forgerock.openbanking.common.model.version.OBVersion
import com.forgerock.openbanking.common.model.version.OBVersion.v3_1_4
import com.forgerock.openbanking.common.model.version.OBVersion.v3_1_8
import com.forgerock.openbanking.constants.OpenBankingConstants
import com.forgerock.openbanking.jwt.model.CreateDetachedJwtResponse
import com.forgerock.openbanking.jwt.model.SigningRequest
import com.forgerock.securebanking.framework.constants.INVALID_FORMAT_DETACHED_JWS
import com.forgerock.securebanking.framework.constants.REDIRECT_URI
import com.forgerock.securebanking.framework.constants.TAN
import com.forgerock.securebanking.framework.data.AccessToken
import com.forgerock.securebanking.framework.data.ClientCredentialData
import com.forgerock.securebanking.framework.data.Tpp
import com.forgerock.securebanking.framework.http.fuel.defaultMapper
import com.forgerock.securebanking.framework.http.fuel.jsonBody
import com.forgerock.securebanking.framework.http.fuel.responseObject
import com.forgerock.securebanking.framework.signature.signPayload
import com.forgerock.securebanking.framework.signature.signPayloadSubmitPayment
import com.forgerock.securebanking.framework.utils.GsonUtils
import com.forgerock.securebanking.support.discovery.asDiscovery
import com.forgerock.securebanking.support.discovery.rsDiscovery
import com.forgerock.securebanking.support.general.GeneralAS.Companion.CLIENT_ASSERTION_TYPE
import com.forgerock.securebanking.support.general.GeneralAS.GrantTypes.CLIENT_CREDENTIALS
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.HttpException
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.core.isSuccessful
import com.nimbusds.jose.JWSObject
import java.util.*

/**
 * Generic RS client methods for payment tests
 */
class PaymentRS {
    inline fun <reified T : Any> consent(
        consentUrl: String,
        consentRequest: Any,
        tpp: Tpp,
        version: OBVersion = v3_1_8,
        detachedJwt: String = ""
    ): T {
        try {
            val accessToken = getAccessToken(tpp).access_token
            val (_, consentResponse, result) = Fuel.post(consentUrl)
                .jsonBody(consentRequest)
                .header("Authorization", "Bearer $accessToken")
                .header("x-fapi-financial-id", rsDiscovery.Data.FinancialId ?: "")
                .header("x-jws-signature", detachedJwt)
                .responseObject<T>()
            if (!consentResponse.isSuccessful) {
                throw AssertionError(
                    "Could not create the consent: \n" + result.component2()?.errorData?.toString(Charsets.UTF_8),
                    result.component2()
                )
            }

            if (consentResponse.header("x-jws-signature").isNullOrEmpty()) {
                throw AssertionError(
                    "The response should have 'x-jws-signature' header for the consent : ${result.get()}"
                )
            }
            //TODO: Uncomment the function when the issue https://github.com/SecureBankingAccessToolkit/SecureBankingAccessToolkit/issues/293 is implemented
//            verifyDetachedJws(
//                consentResponse.header("x-jws-signature").first(),
//                defaultMapper.writeValueAsString(result.get()),
//                version,
//                tpp
//            )

            GsonUtils.gson.toJson(result.get())
            return result.get()
        } catch (e: HttpException) {
            println(e)
            throw e
        }
    }

    inline fun <reified T : Any> consentNoDetachedJwt(
        consentUrl: String,
        consentRequest: Any,
        tpp: Tpp,
        version: OBVersion = v3_1_8
    ): T {
        try {
            val accessToken = getAccessToken(tpp).access_token
            val (_, consentResponse, r) = Fuel.post(consentUrl)
                .jsonBody(consentRequest)
                .header("Authorization", "Bearer $accessToken")
                .header("x-fapi-financial-id", rsDiscovery.Data.FinancialId ?: "")
                .responseObject<T>()
            if (!consentResponse.isSuccessful) {
                throw AssertionError(
                    "Could not create the consent: \n" + r.component2()?.errorData?.toString(Charsets.UTF_8),
                    r.component2()
                )
            }

            return r.get()
        } catch (e: HttpException) {
            println(e)
            throw e
        }
    }

    inline fun <reified T : Any> getConsent(consentUrl: String, tpp: Tpp, version: OBVersion = v3_1_8): T {
        val accessToken = getAccessToken(tpp).access_token
        val (_, consentResponse, result) = Fuel.get(consentUrl)
            .header("Authorization", "Bearer $accessToken")
            .header("x-fapi-financial-id", rsDiscovery.Data.FinancialId ?: "")
            .responseObject<T>()
        if (!consentResponse.isSuccessful) throw AssertionError(
            "Could not create the consent: \n" + result.component2()?.errorData?.toString(Charsets.UTF_8),
            result.component2()
        )

        if (consentResponse.header("x-jws-signature").isNullOrEmpty()) {
            throw AssertionError(
                "The response should have 'x-jws-signature' header for the consent : ${result.get()}"
            )
        }

        //TODO: Uncomment the function when the issue https://github.com/SecureBankingAccessToolkit/SecureBankingAccessToolkit/issues/293 is implemented
//        verifyDetachedJws(
//            consentResponse.header("x-jws-signature").first(),
//            defaultMapper.writeValueAsString(result.get()),
//            version,
//            tpp
//        )

        return result.get()
    }

    inline fun <reified T : Any> submitPayment(
        paymentUrl: String,
        paymentRequest: Any,
        accessToken: AccessToken,
        signedPayload: String,
        tpp: Tpp,
        version: OBVersion = v3_1_8
    ): T {
        val (_, response, result) = Fuel.post(paymentUrl)
            .jsonBody(paymentRequest)
            .header("Authorization", "Bearer ${accessToken.access_token}")
            .header("x-jws-signature", signedPayload)
            .header("x-idempotency-key", UUID.randomUUID())
            .header("x-fapi-financial-id", rsDiscovery.Data.FinancialId ?: "")
            .responseObject<T>()
        if (!response.isSuccessful) throw AssertionError(
            "Could not create the payment submission: \n" + result.component2()?.errorData?.toString(Charsets.UTF_8),
            result.component2()
        )

        if (response.header("x-jws-signature").isNullOrEmpty()) {
            throw AssertionError(
                "The response should have 'x-jws-signature' header for the consent : ${result.get()}"
            )
        }

        //TODO: Uncomment the function when the issue https://github.com/SecureBankingAccessToolkit/SecureBankingAccessToolkit/issues/293 is implemented
//        verifyDetachedJws(
//            response.header("x-jws-signature").first(),
//            defaultMapper.writeValueAsString(result.get()),
//            version,
//            tpp
//        )

        return result.get()
    }

    inline fun <reified T : Any> submitPaymentNoDetachedJws(
        paymentUrl: String,
        paymentRequest: Any,
        accessToken: AccessToken
    ): T {
        val (_, response, r) = Fuel.post(paymentUrl)
            .jsonBody(paymentRequest)
            .header("Authorization", "Bearer ${accessToken.access_token}")
            .header("x-idempotency-key", UUID.randomUUID())
            .header("x-fapi-financial-id", rsDiscovery.Data.FinancialId ?: "")
            .responseObject<T>()
        if (!response.isSuccessful) throw AssertionError(
            "Could not create the payment submission: \n" + r.component2()?.errorData?.toString(Charsets.UTF_8),
            r.component2()
        )
        return r.get()
    }

    inline fun <reified T : Any> getPayment(
        url: String,
        accessToken: AccessToken,
        tpp: Tpp,
        version: OBVersion = v3_1_8
    ): T {
        return getCall(url, accessToken, tpp, version)
    }

    inline fun <reified T : Any> getFundsConfirmation(
        url: String,
        accessToken: AccessToken
    ): T {
        return getCallWithoutDetachedJws(url, accessToken)
    }

    inline fun <reified T> getCall(
        url: String,
        accessToken: AccessToken,
        tpp: Tpp,
        version: OBVersion = v3_1_8
    ): T {
        val (_, response, result) = Fuel.get(url)
            .header("Authorization", "Bearer ${accessToken.access_token}")
            .header("x-fapi-financial-id", rsDiscovery.Data.FinancialId ?: "")
            .responseString()
        if (!response.isSuccessful) throw AssertionError(
            "Error executing the get call: \n" + result.component2()?.errorData?.toString(Charsets.UTF_8),
            result.component2()
        )

        if (response.header("x-jws-signature").isNullOrEmpty()) {
            throw AssertionError(
                "The response should have 'x-jws-signature' header for the consent : ${result.get()}"
            )
        }

        //TODO: Uncomment the function when the issue https://github.com/SecureBankingAccessToolkit/SecureBankingAccessToolkit/issues/293 is implemented
//        verifyDetachedJws(
//            response.header("x-jws-signature").first(),
//            defaultMapper.writeValueAsString(result.get()),
//            version,
//            tpp
//        )

        return defaultMapper.readValue(result.get())
    }

    inline fun <reified T> getCallWithoutDetachedJws(
        url: String,
        accessToken: AccessToken
    ): T {
        val (_, response, result) = Fuel.get(url)
            .header("Authorization", "Bearer ${accessToken.access_token}")
            .header("x-fapi-financial-id", rsDiscovery.Data.FinancialId ?: "")
            .responseString()
        if (!response.isSuccessful) throw AssertionError(
            "Error executing the get call: \n" + result.component2()?.errorData?.toString(Charsets.UTF_8),
            result.component2()
        )

        return defaultMapper.readValue(result.get())
    }

    fun getAccessToken(tpp: Tpp): AccessToken {
        val requestParameters = ClientCredentialData(
            sub = tpp.registrationResponse.client_id,
            iss = tpp.registrationResponse.client_id,
            aud = asDiscovery.issuer
        )
        val signedPayload = signPayload(requestParameters, tpp.signingKey, tpp.signingKid)

        val scopes = asDiscovery.scopes_supported.intersect(
            listOf(
                OpenBankingConstants.Scope.OPENID,
                OpenBankingConstants.Scope.ACCOUNTS,
                OpenBankingConstants.Scope.PAYMENTS
            )
        ).joinToString(separator = " ")
        val body = listOf(
            "grant_type" to CLIENT_CREDENTIALS,
            "redirect_uri" to REDIRECT_URI,
            "client_assertion_type" to CLIENT_ASSERTION_TYPE,
            "scope" to scopes,
            "client_assertion" to signedPayload
        )
        val (_, accessTokenResponse, result) = Fuel.post(asDiscovery.token_endpoint, parameters = body)
            .authentication()
            .basic(tpp.registrationResponse.client_id, tpp.registrationResponse.client_secret!!)
            .responseObject<AccessToken>()
        if (!accessTokenResponse.isSuccessful) throw AssertionError(
            "Could not get access token: \n" + result.component2()?.errorData?.toString(
                Charsets.UTF_8
            ), result.component2()
        )
        return result.get()
    }

    //DEPRECATED
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

    //DEPRECATED
    inline fun <reified T : Any> getPayment(
        url: String,
        paymentId: String,
        accessToken: AccessToken,
        version: OBVersion = v3_1_8
    ): T {
        return getCall("$url/$paymentId", accessToken, version)
    }

    //DEPRECATED
    inline fun <reified T : Any> submitPayment(
        paymentUrl: String,
        paymentRequest: Any,
        accessToken: AccessToken,
        tpp: Tpp
    ): T {
        val signedPayload = signPayloadSubmitPayment(paymentRequest as String, tpp.signingKey, tpp.signingKid)
        val (_, response, r) = Fuel.post(paymentUrl)
            .jsonBody(paymentRequest)
            .header("Authorization", "Bearer ${accessToken.access_token}")
            .header("x-jws-signature", signedPayload)
            .header("x-idempotency-key", UUID.randomUUID())
            .header("x-fapi-financial-id", rsDiscovery.Data.FinancialId ?: "")
            .responseObject<T>()
        if (!response.isSuccessful) throw AssertionError(
            "Could not create the payment submission",
            r.component2()
        )
        return r.get()
    }

    fun submitFilePayment(
        consentFileUrl: String,
        file: String,
        mediaType: String,
        tpp: Tpp,
        version: OBVersion = v3_1_8
    ): Response {
        try {
            val accessToken = getAccessToken(tpp)
            val detachedJwt = getDetachedJws(file, tpp, version)
            val (_, consentResponse, r) = Fuel.post(consentFileUrl)
                .jsonBody(file)
                .defaultHeaders(accessToken.access_token)
                .header("Content-Type", mediaType)
                .header("x-jws-signature", detachedJwt)
                .responseString()
            if (!consentResponse.isSuccessful) {
                throw AssertionError("Could not create the consent", r.component2())
            }
            return consentResponse
        } catch (e: HttpException) {
            println(e)
            throw e
        }
    }

    //DEPRECATED
    inline fun <reified T : Any> submitPayment_InvalidDetachedJws(
        paymentUrl: String,
        paymentRequest: Any,
        accessToken: AccessToken
    ): Response {
        val (_, response, _) = Fuel.post(paymentUrl)
            .jsonBody(paymentRequest)
            .header("Authorization", "Bearer ${accessToken.access_token}")
            .header("x-fapi-financial-id", rsDiscovery.Data.FinancialId ?: "")
            .header("x-jws-signature", INVALID_FORMAT_DETACHED_JWS)
            .responseObject<T>()
        return response
    }

    //DEPRECATED
    inline fun <reified T> getCall(
        url: String,
        accessToken: AccessToken,
        version: OBVersion = v3_1_8
    ): T {
        val (_, response, result) = Fuel.get(url)
            .header("Authorization", "Bearer ${accessToken.access_token}")
            .header("x-fapi-financial-id", rsDiscovery.Data.FinancialId ?: "")
            .responseString()
        if (!response.isSuccessful) throw AssertionError(
            "Error executing the get call", result.component2()
        )
        return defaultMapper.readValue(result.get())
    }

    fun submitCSVFilePayment(
        consentFileUrl: String,
        file: String,
        mediaType: String,
        tpp: Tpp,
        version: OBVersion = v3_1_8
    ): Response {
        try {
            val accessToken = getAccessToken(tpp)
            val detachedJwt = getDetachedJws(file, tpp, version)
            val (_, consentResponse, r) = Fuel.post(consentFileUrl)
                .jsonBody(file)
                .defaultHeaders(accessToken.access_token)
                .header("Content-Type", mediaType)
                .header("x-jws-signature", detachedJwt)
                .responseString()
            if (!consentResponse.isSuccessful) {
                throw AssertionError("Could not create the consent with", r.component2())
            }
            return consentResponse
        } catch (e: HttpException) {
            println(e)
            throw e
        }
    }

    inline fun <reified T : Any> consentRequest_InvalidDetachedJws(
        consentUrl: String,
        consentRequest: Any,
        tpp: Tpp
    ): Response {
        val accessToken = getAccessToken(tpp).access_token
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
            .defaultHeaders(accessToken.access_token)
            .header("x-jws-signature", detachedJwt)
            .responseObject<T>()
        return consentResponse
    }

    fun Request.defaultHeaders(accessToken: String) =
        this
            .header("Authorization", "Bearer $accessToken")
            // x-fapi-financial-id is no longer required in v3.1.2 onwards
            .header("x-fapi-financial-id", rsDiscovery.Data.FinancialId ?: "")
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
        return GsonUtils.gson.toJson(signingRequest)
    }
}
