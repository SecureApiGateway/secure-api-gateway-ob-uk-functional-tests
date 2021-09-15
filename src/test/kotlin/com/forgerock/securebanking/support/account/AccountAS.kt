package com.forgerock.securebanking.support.account

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

class AccountAS {

    fun headlessAuthentication(
        consentId: String,
        registrationResponse: RegistrationResponse,
        psu: UserRegistrationRequest,
        tpp: Tpp
    ): AccessToken {
        val idToken = RequestParameters.Claims.IdToken(
            RequestParameters.Claims.IdToken.Acr(true, "urn:openbanking:psd2:ca"),
            RequestParameters.Claims.IdToken.OpenbankingIntentId(true, consentId)
        )
        val userInfo =
            RequestParameters.Claims.Userinfo(RequestParameters.Claims.Userinfo.OpenbankingIntentId(true, consentId))
        val claims = RequestParameters.Claims(idToken, userInfo)
        val requestParameters = RequestParameters(
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
            "scope" to "openid accounts",
            "username" to psu.input.user.username,
            "password" to psu.input.user.userPassword
        )
        val (_, response, result) = Fuel.post(asDiscovery.token_endpoint, parameters = headlessForm)
            .header("X_HEADLESS_AUTH_ENABLE", true)
            .header("X_HEADLESS_AUTH_USERNAME", psu.input.user.username)
            .header("X_HEADLESS_AUTH_PASSWORD", psu.input.user.username)
            .authentication()
            .basic(tpp.registrationResponse.client_id, tpp.registrationResponse.client_secret!!)
            .responseObject<AccessToken>()
        if (!response.isSuccessful) throw AssertionError("Could not headless authenticate", result.component2())
        return result.get()
    }
}
