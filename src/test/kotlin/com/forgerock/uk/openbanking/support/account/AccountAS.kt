package com.forgerock.uk.openbanking.support.account

import com.forgerock.securebanking.framework.configuration.AM_COOKIE_NAME
import com.forgerock.securebanking.framework.configuration.RCS_SERVER
import com.forgerock.securebanking.framework.data.AccessToken
import com.forgerock.securebanking.framework.data.RegistrationResponse
import com.forgerock.securebanking.framework.data.RequestParameters
import com.forgerock.securebanking.framework.data.Tpp
import com.forgerock.securebanking.framework.http.fuel.jsonBody
import com.forgerock.securebanking.framework.http.fuel.responseObject
import com.forgerock.securebanking.framework.signature.signPayload
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBConstants
import com.forgerock.securebanking.framework.configuration.REDIRECT_URI
import com.forgerock.uk.openbanking.support.discovery.asDiscovery
import com.forgerock.uk.openbanking.support.general.GeneralAS
import com.forgerock.uk.openbanking.support.registration.UserRegistrationRequest
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.core.isSuccessful
import com.google.gson.JsonParser


/**
 * Generic AS client methods for accounts tests
 */
class AccountAS : GeneralAS() {

    data class SendConsentDecisionRequestBody(
        val consentJwt: String,
        val decision: String,
        val accountIds: List<String>
    )

    fun getAccessToken(
        consentId: String,
        registrationResponse: RegistrationResponse,
        psu: UserRegistrationRequest,
        tpp: Tpp
    ): AccessToken {
        val authenticationURL = generateAuthenticationURL(
            consentId, registrationResponse, psu, tpp, asDiscovery.scopes_supported.intersect(
                listOf(
                    OBConstants.Scope.OPENID,
                    OBConstants.Scope.ACCOUNTS
                )
            ).joinToString(separator = " ")
        )
        val response = authenticateByHttpClient(authenticationURL, psu)
        val authorizeURL = response.successUrl
        val cookie = "$AM_COOKIE_NAME=${response.tokenId}"
        val consentRequest = continueAuthorize(authorizeURL, cookie)
        val consentDetails = getConsentDetails(consentRequest)
        val accountsIds = getAccountsIdsFromConsentDetails(consentDetails)
        val consentDecisionResponse = sendConsentDecision(consentRequest, accountsIds)
        val authCode = getAuthCode(consentDecisionResponse.consentJwt, consentDecisionResponse.redirectUri, cookie)
        return exchangeCode(registrationResponse, tpp, authCode)
    }

    private fun getAccountsIdsFromConsentDetails(consentDetails: String): ArrayList<String> {
        try {
            val str = JsonParser().parse(consentDetails).asJsonObject
            val accountsIds = ArrayList<String>()
            val accounts = str.getAsJsonArray("accounts")
            for (account in accounts) {
                val id = account.asJsonObject.get("id").asString
                accountsIds.add(id)
            }
            return accountsIds
        } catch (e: Exception) {
            throw AssertionError(
                "The response body doesn't have the expected format"
            )
        }
    }

    private fun sendConsentDecision(
        consentRequest: String,
        consentedAccount: ArrayList<String>
    ): SendConsentDecisionResponseBody {
        val body = SendConsentDecisionRequestBody(consentRequest, "Authorised", consentedAccount.toList())
        val (_, response, result) = Fuel.post("$RCS_SERVER/api/rcs/consent/decision/")
            .jsonBody(body)
            .responseObject<SendConsentDecisionResponseBody>()
        if (!response.isSuccessful) throw AssertionError(
            "Could not send consent decision",
            result.component2()
        )
        return result.get()
    }

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
            "username" to psu.user.userName,
            "password" to psu.user.password
        )

        val (_, response, result) = Fuel.post(asDiscovery.token_endpoint, parameters = headlessForm)
            .header("X_HEADLESS_AUTH_ENABLE", true)
            .header("X_HEADLESS_AUTH_USERNAME", psu.user.userName)
            .header("X_HEADLESS_AUTH_PASSWORD", psu.user.password)
            .authentication()
            .basic(tpp.registrationResponse.client_id, tpp.registrationResponse.client_secret!!)
            .responseObject<AccessToken>()
        if (!response.isSuccessful) throw AssertionError("Could not headless authenticate", result.component2())
        return result.get()
    }
}
