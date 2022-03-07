package com.forgerock.securebanking.support.account

import com.forgerock.securebanking.framework.constants.COOKIE_NAME
import com.forgerock.securebanking.framework.constants.IAM
import com.forgerock.securebanking.framework.constants.RCS
import com.forgerock.securebanking.framework.constants.REDIRECT_URI
import com.forgerock.securebanking.framework.data.*
import com.forgerock.securebanking.framework.http.fuel.jsonBody
import com.forgerock.securebanking.framework.http.fuel.responseObject
import com.forgerock.securebanking.framework.signature.signPayload
import com.forgerock.securebanking.support.discovery.asDiscovery
import com.forgerock.securebanking.tests.functional.directory.UserRegistrationRequest
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.core.isSuccessful
import com.google.gson.JsonParser


const val GATEAWAY_ASSERTION =
    "eyJ0eXAiOiJKV1QiLCJraWQiOiJTbzZlSUR2NEozbEE4OEo5cE9jeFlFeExBVk09IiwiYWxnIjoiUFMyNTYifQ.eyJzdWIiOiJiNjg0MTQ2Zi1mYWE2LTQ2NGQtODA0ZS1iMjUyZmFiYTllMzgiLCJjdHMiOiJPQVVUSDJfR1JBTlRfU0VUIiwiYXV0aF9sZXZlbCI6MCwiYXVkaXRUcmFja2luZ0lkIjoiMWIyOWFhN2YtYjExMC00ODI2LTgwNWQtYjgxYzI1YTk0MDYyLTI4Mjg3NzkiLCJzdWJuYW1lIjoiYjY4NDE0NmYtZmFhNi00NjRkLTgwNGUtYjI1MmZhYmE5ZTM4IiwiaXNzIjoiaHR0cHM6Ly9vcGVuYW0tZm9yZ2Vyb2NrLXNlY3VyZWJhbmtpbmdhY2NlbGVyYXRvLmZvcmdlYmxvY2tzLmNvbS9hbS9vYXV0aDIvcmVhbG1zL3Jvb3QvcmVhbG1zL2FscGhhIiwidG9rZW5OYW1lIjoiYWNjZXNzX3Rva2VuIiwidG9rZW5fdHlwZSI6IkJlYXJlciIsImF1dGhHcmFudElkIjoicnhSZlBFZzBoWWZITHJ6Y21VY2VZdjB6YngwLmkxUElpcUhUeEl1Uy1lblozUEJ2SXJUeDM2OCIsIm5vbmNlIjoiMTBkMjYwYmYtYTdkOS00NDRhLTkyZDktN2I3YTVmMDg4MjA4IiwiYXVkIjoiZmUzYzI2ZTEtNzcwZS00ZmY5LThiM2EtMTg2YjhiZGMyNDA0IiwibmJmIjoxNjM2NTM5ODUwLCJncmFudF90eXBlIjoiYXV0aG9yaXphdGlvbl9jb2RlIiwic2NvcGUiOlsib3BlbmlkIiwiYWNjb3VudHMiXSwiYXV0aF90aW1lIjoxNjM2NTM5ODI5LCJjbGFpbXMiOiJ7XCJpZF90b2tlblwiOntcImFjclwiOntcInZhbHVlXCI6XCJ1cm46b3BlbmJhbmtpbmc6cHNkMjpjYVwiLFwiZXNzZW50aWFsXCI6dHJ1ZX0sXCJvcGVuYmFua2luZ19pbnRlbnRfaWRcIjp7XCJ2YWx1ZVwiOlwiQUFDXzM0NWJlMTA2LWMwNTAtNDg4OC1iYjliLWZhZGQwMTkzYTY3Y1wiLFwiZXNzZW50aWFsXCI6dHJ1ZX19fSIsInJlYWxtIjoiL2FscGhhIiwiY25mIjp7Ing1dCNTMjU2IjoickxvQnJmcHJhZVZMeXh1V0szWUgyUFZURVJ6VnhrY1FRelV6bXNIQmZvNCJ9LCJleHAiOjE2MzY4OTk4NTAsImlhdCI6MTYzNjUzOTg1MCwiZXhwaXJlc19pbiI6MzYwMDAwLCJqdGkiOiJyeFJmUEVnMGhZZkhMcnpjbVVjZVl2MHpieDAuTU5pMVhDMll5akw4TW5SakZHU1VCLTctcFdNIn0.Qj-qpvZXfmGVlBdgIYZbqqAvk8wc3-FISaA99o4govcWvlDWeLDEBBx5CS8bnTIe7vG8QuXVtk80qI2FE8XH4H556FQlFPQ-PMAnwpVltraZ4_YoSP_BZ6z8cZDzp8mJgefnLqr_zajZLaj_xxFAP5G1Xbm_IBpJeZvd8RO6hNYCiMJ0chvz-61p1k-vjmJgWsnfjmKtO3b65nP5qUgDp0s2HtiD0dY-f5u2ONsgCzwTdyKNiivYsoloPw-CLRGSnH6rf98eAXnJo7pvR9BGZ1Njn-2McRRMc4kFneNyNOS7BSpfAXm4ivCv31sVzxrd5nv4rnoGdGKXFGY7NdEEUw"
const val CLIENT_ASSERTION_TYPE = "urn:ietf:params:oauth:client-assertion-type:jwt-bearer"

class AccountAS {

    data class SendConsentDecisionRequestBody(
        val consentJwt: String,
        val decision: String,
        val sharedAccounts: List<String>
    )

    data class SendConsentDecisionResponseBody(val consentJwt: String, val redirectUri: String)

    fun getAccessToken(
        consentId: String,
        registrationResponse: RegistrationResponse,
        psu: UserRegistrationRequest,
        tpp: Tpp
    ): AccessToken {
        val authenticationURL = generateAuthenticationURL(consentId, registrationResponse, psu, tpp)
        val response = authenticate(authenticationURL, psu)
        val authorizeURL = response.successUrl
        val cookie = "$COOKIE_NAME=${response.tokenId}"
        val consentRequest = continueAuthorize(authorizeURL, cookie)
        val accountsIds = getConsentDetails(consentRequest)
        val consentDecisionResponse = sendConsentDecision(consentRequest, accountsIds)
        val authCode = getAuthCode(consentDecisionResponse.consentJwt, consentDecisionResponse.redirectUri, cookie)
        return exchangeCode(registrationResponse, tpp, authCode)
    }

    fun generateAuthenticationURL(
        consentId: String,
        registrationResponse: RegistrationResponse,
        psu: UserRegistrationRequest,
        tpp: Tpp
    ): String {
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
        val data = listOf(
            "redirect_uri" to requestParameters.redirect_uri,
            "response_type" to requestParameters.response_type,
            "client_id" to requestParameters.client_id,
            "state" to requestParameters.state,
            "nonce" to requestParameters.nonce,
            "request" to signedPayload,
            "scope" to requestParameters.scope,
            "username" to psu.user.userName,
            "password" to psu.user.password
        )

        val (_, response, result) = Fuel.get(
            asDiscovery.authorization_endpoint,
            parameters = data
        ).allowRedirects(false)
            .responseString()
        if (response.statusCode != 302) throw AssertionError(
            "Could not create authentication URL",
            result.component2()
        )

        try {
            val location = getLocationFromHeaders(response)
            val parameters = location.substring(location.indexOf("?"))
            return "$IAM/am/json/realms/root/realms/alpha/authenticate$parameters"
        } catch (e: Exception) {
            throw AssertionError("Location header URL doesn't have parameters")
        }

    }

    fun authenticate(authenticationURL: String, psu: UserRegistrationRequest): AuthenticationResponse {
        val (_, response, result) = Fuel.post(authenticationURL)
            .header("X-OpenAM-Username", psu.user.userName)
            .header("X-OpenAM-Password", psu.user.password)
            .responseObject<AuthenticationResponse>()
        if (!response.isSuccessful) throw AssertionError(
            "Could not authenticate the user with the username: ${psu.user.userName}",
            result.component2()
        )
        if (!response.headers.containsKey("Set-Cookie"))
            throw AssertionError(
                "Could not get "
            )
        return result.get()
    }

    fun continueAuthorize(authorizeURL: String, cookie: String): String {
        val (_, response, result) = Fuel.get(authorizeURL)
            .header("Cookie", cookie)
            .allowRedirects(false)
            .responseString()
        if (response.statusCode != 302) throw AssertionError(
            "Could not continue the authorization",
            result.component2()
        )
        try {
            val location = getLocationFromHeaders(response)
            return location.substring(location.indexOf("=") + 1)
        } catch (e: Exception) {
            throw AssertionError("Location header doesn't contain the Code")
        }
    }

    fun getConsentDetails(consentRequest: String): ArrayList<String> {
        val (_, response, result) = Fuel.post("$RCS/api/rcs/consent/details/")
            .body(consentRequest)
            .header("Content-Type", "application/jwt")
            .responseString()
        if (!response.isSuccessful) throw AssertionError(
            "Could not get the consent details",
            result.component2()
        )
        try {
            val str = JsonParser.parseString(result.get()).asJsonObject
            val accountsIds = ArrayList<String>()
            val accounts = str.getAsJsonArray("accounts")
            for (account in accounts)
            {
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

    fun sendConsentDecision(consentRequest: String, consentedAccount: ArrayList<String>): SendConsentDecisionResponseBody {
        val body = SendConsentDecisionRequestBody(consentRequest, "Authorised", consentedAccount.toList())
        val (_, response, result) = Fuel.post("$RCS/api/rcs/consent/decision/")
            .jsonBody(body)
            .responseObject<SendConsentDecisionResponseBody>()
        if (!response.isSuccessful) throw AssertionError(
            "Could not send consent decision",
            result.component2()
        )
        return result.get()
    }

    fun getAuthCode(consentResponse: String, authorizeURL: String, cookie: String): String {
        val (_, response, result) = Fuel.post(authorizeURL, listOf("consent_response" to consentResponse))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("Cookie", cookie)
            .allowRedirects(false)
            .responseString()
        if (response.statusCode != 302) throw AssertionError(
            "Could not get Auth Code",
            result.component2()
        )

        try {
            val location = getLocationFromHeaders(response)
            return location.substring(location.indexOf("code=") + 5, location.indexOf("&"))
        } catch (e: Exception) {
            throw AssertionError("Location header doesn't contain the Auth Code")
        }

    }

    fun exchangeCode(
        registrationResponse: RegistrationResponse,
        tpp: Tpp, authCode: String
    ): AccessToken {
        val requestParameters = ClientCredentialData(
            sub = registrationResponse.client_id,
            iss = registrationResponse.client_id,
            aud = asDiscovery.issuer
        )
        val signedPayload = signPayload(requestParameters, tpp.signingKey, tpp.signingKid)

        val body = listOf(
            "grant_type" to "authorization_code",
            "code" to authCode,
            "redirect_uri" to REDIRECT_URI,
            "client_assertion_type" to CLIENT_ASSERTION_TYPE,
            "client_assertion" to signedPayload,
            "gateway_assertion" to GATEAWAY_ASSERTION
        )
        val (_, response, result) = Fuel.post(asDiscovery.token_endpoint, body)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .responseObject<AccessToken>()
        if (!response.isSuccessful) throw AssertionError(
            "Could not get the access token",
            result.component2()
        )
        return result.get()
    }

    fun getLocationFromHeaders(response: Response): String {
        try {
            return response.header("Location").firstOrNull().toString()
        } catch (e: Exception) {
            throw AssertionError("Location header doesn't exist")
        }
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
