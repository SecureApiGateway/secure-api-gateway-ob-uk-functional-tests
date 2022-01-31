package com.forgerock.securebanking.support.funds

import com.forgerock.openbanking.constants.OpenBankingConstants
import com.forgerock.securebanking.framework.constants.REDIRECT_URI
import com.forgerock.securebanking.framework.data.AccessToken
import com.forgerock.securebanking.framework.data.RegistrationResponse
import com.forgerock.securebanking.framework.data.RequestParameters
import com.forgerock.securebanking.framework.data.Tpp
import com.forgerock.securebanking.framework.http.fuel.responseObject
import com.forgerock.securebanking.framework.signature.signPayload
import com.forgerock.securebanking.support.discovery.asDiscovery
import com.forgerock.securebanking.tests.functional.directory.UserRegistrationRequest
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.core.isSuccessful

class FundsConfirmationsAS {

    fun headlessAuthentication(
        consentId: String,
        registrationResponse: RegistrationResponse,
        psu: UserRegistrationRequest,
        tpp: Tpp
    ): AccessToken {
        val idToken = RequestParameters.Claims.IdToken(
            RequestParameters.Claims.IdToken.Acr(true, "urn:openbanking:psd2:sca"),
            RequestParameters.Claims.IdToken.OpenbankingIntentId(true, consentId)
        )
        val userInfo =
            RequestParameters.Claims.Userinfo(RequestParameters.Claims.Userinfo.OpenbankingIntentId(true, consentId))
        val claims = RequestParameters.Claims(idToken, userInfo)
        val scopes = asDiscovery.scopes_supported.intersect(
            listOf(
                OpenBankingConstants.Scope.OPENID,
                OpenBankingConstants.Scope.ACCOUNTS,
                OpenBankingConstants.Scope.PAYMENTS,
                OpenBankingConstants.Scope.FUNDS_CONFIRMATIONS
            )
        ).joinToString(separator = " ")
        val requestParameters = RequestParameters(
            scope = scopes,
            claims = claims,
            client_id = registrationResponse.client_id,
            iss = registrationResponse.client_id
        )
        val signedPayload = signPayload(requestParameters, tpp.signingKey, tpp.signingKid)
        val headlessForm = listOf(
            "grant_type" to "headless_auth",
            "redirect_uri" to REDIRECT_URI,
            "response_type" to "code id_token",
            "client_id" to registrationResponse.client_id,
            "state" to requestParameters.state,
            "nonce" to requestParameters.nonce,
            "request" to signedPayload,
            "scope" to scopes,
            "username" to psu.user.userName,
            "password" to psu.user.password
        )
        val (_, response, result) = Fuel.post(asDiscovery.token_endpoint, parameters = headlessForm)
            .header("X_HEADLESS_AUTH_ENABLE", true)
            .header("X_HEADLESS_AUTH_USERNAME", psu.user.userName)
            .header("X_HEADLESS_AUTH_PASSWORD", psu.user.userName)
            .authentication()
            .basic(tpp.registrationResponse.client_id, tpp.registrationResponse.client_secret!!)
            .responseObject<AccessToken>()
        if (!response.isSuccessful) throw AssertionError("Could not headless authenticate", result.component2())
        return result.get()
    }

    fun clientCredentialsAuthentication(
        consentId: String,
        registrationResponse: RegistrationResponse,
        tpp: Tpp
    ): AccessToken {
        val idToken = RequestParameters.Claims.IdToken(
            RequestParameters.Claims.IdToken.Acr(true, "urn:openbanking:psd2:sca"),
            RequestParameters.Claims.IdToken.OpenbankingIntentId(true, consentId)
        )
        val userInfo =
            RequestParameters.Claims.Userinfo(RequestParameters.Claims.Userinfo.OpenbankingIntentId(true, consentId))
        val claims = RequestParameters.Claims(idToken, userInfo)
        val scopes = asDiscovery.scopes_supported.intersect(
            listOf(
                OpenBankingConstants.Scope.OPENID,
                OpenBankingConstants.Scope.ACCOUNTS,
                OpenBankingConstants.Scope.PAYMENTS
            )
        ).joinToString(separator = " ")
        val requestParameters = RequestParameters(
            scope = scopes,
            claims = claims,
            client_id = registrationResponse.client_id,
            iss = registrationResponse.client_id
        )
        val signedPayload = signPayload(requestParameters, tpp.signingKey, tpp.signingKid)
        val clientCredentialsForm = listOf(
            "grant_type" to "client_credentials",
            "redirect_uri" to REDIRECT_URI,
            "response_type" to "code id_token",
            "client_id" to registrationResponse.client_id,
            "state" to requestParameters.state,
            "nonce" to requestParameters.nonce,
            "request" to signedPayload,
            "scope" to scopes
        )
        val (_, response, result) = Fuel.post(asDiscovery.token_endpoint, parameters = clientCredentialsForm)
            .authentication()
            .basic(tpp.registrationResponse.client_id, tpp.registrationResponse.client_secret!!)
            .responseObject<AccessToken>()
        if (!response.isSuccessful) throw AssertionError("Could not authenticate", result.component2())
        return result.get()
    }
}
