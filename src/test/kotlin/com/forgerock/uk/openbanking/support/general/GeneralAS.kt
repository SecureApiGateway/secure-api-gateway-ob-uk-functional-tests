package com.forgerock.uk.openbanking.support.general

import com.forgerock.securebanking.framework.configuration.IG_SERVER
import com.forgerock.securebanking.framework.configuration.PLATFORM_SERVER
import com.forgerock.securebanking.framework.data.*
import com.forgerock.securebanking.framework.http.fuel.responseObject
import com.forgerock.securebanking.framework.signature.signPayload
import com.forgerock.securebanking.framework.configuration.REDIRECT_URI
import com.forgerock.uk.openbanking.framework.errors.CONSENT_NOT_AUTHORISED
import com.forgerock.uk.openbanking.framework.errors.LOCATION_HEADER_ERROR
import com.forgerock.uk.openbanking.framework.errors.LOCATION_HEADER_NOT_EXISTS
import com.forgerock.uk.openbanking.support.discovery.asDiscovery
import com.forgerock.uk.openbanking.support.registration.UserRegistrationRequest
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.isSuccessful
import com.google.gson.Gson
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


open class GeneralAS {

    object GrantTypes {
        const val CLIENT_CREDENTIALS = "client_credentials"
        const val AUTHORIZATION_CODE = "authorization_code"
    }

    data class SendConsentDecisionResponseBody(val consentJwt: String, val redirectUri: String)

    /* DEV
    https://iam.dev.forgerock.financial:443/am/oauth2/realms/root/realms/alpha/authorize?
    redirect_uri=https://google.com&response_type=code%20id_token&client_id=e327d05e-a822-4fee-9989-e294af3b586b&state=10d260bf-a7d9-444a-92d9-7b7a5f088208
    &nonce=10d260bf-a7d9-444a-92d9-7b7a5f088208&request=eyJraWQiOiIyeU5qUE9DanBPOHJjS2c2X2xWdFd6QVFSMFUiLCJhbGciOiJQUzI1NiJ9.eyJhdWQiOiJodHRwczovL2lhbS5kZXYuZm9yZ2Vyb2NrLmZpbmFuY2lhbDo0NDMvYW0vb2F1dGgyL3JlYWxtcy9yb290L3JlYWxtcy9hbHBoYSIsImNsYWltcyI6eyJpZF90b2tlbiI6eyJhY3IiOnsiZXNzZW50aWFsIjp0cnVlLCJ2YWx1ZSI6InVybjpvcGVuYmFua2luZzpwc2QyOmNhIn0sIm9wZW5iYW5raW5nX2ludGVudF9pZCI6eyJlc3NlbnRpYWwiOnRydWUsInZhbHVlIjoiQUFDX2UzMDUxOGUyLTEzMmEtNGExOS04MGUwLTY1NGE4Mzg5ZDgwZiJ9fSwidXNlcmluZm8iOnsib3BlbmJhbmtpbmdfaW50ZW50X2lkIjp7ImVzc2VudGlhbCI6dHJ1ZSwidmFsdWUiOiJBQUNfZTMwNTE4ZTItMTMyYS00YTE5LTgwZTAtNjU0YTgzODlkODBmIn19fSwiY2xpZW50X2lkIjoiZTMyN2QwNWUtYTgyMi00ZmVlLTk5ODktZTI5NGFmM2I1ODZiIiwiZXhwIjoxNjQ3MjU5ODgwLCJpYXQiOjE2NDcyNTkyODAsImp0aSI6IjAzM2U3ZmM5LTYxNWItNDQ0NS1hZGMzLTNhNDVjYzI5NTc4NyIsImlzcyI6ImUzMjdkMDVlLWE4MjItNGZlZS05OTg5LWUyOTRhZjNiNTg2YiIsIm5vbmNlIjoiMTBkMjYwYmYtYTdkOS00NDRhLTkyZDktN2I3YTVmMDg4MjA4IiwicmVkaXJlY3RfdXJpIjoiaHR0cHM6Ly9nb29nbGUuY29tIiwicmVzcG9uc2VfdHlwZSI6ImNvZGUgaWRfdG9rZW4iLCJzY29wZSI6Im9wZW5pZCBhY2NvdW50cyIsInN0YXRlIjoiMTBkMjYwYmYtYTdkOS00NDRhLTkyZDktN2I3YTVmMDg4MjA4In0.S8HkY6lujZ2LqIZtwnLS6rhlgLTQJFsB0_4r-C5ZrfykR8XCMA1h6b3PndXPrXXBJMFabOEjF-W6eHptberpwj3tm6K0XF7H2wmt303wDlmm_JcU279c7UC0foVvCB0zf7NGIYh7tLBdzp_OF5nAyBc1iDvw7LRL8-qt0M1P-cyt8qDv6DuT_Hauq0M6PYy0Hgj2VrONFmk_1OmNHDdeyiPWPn2TCvOdzQI08Ev2eHzAqpeUljNPhbXkRJTVRrjmW9EuV0q71-F-uauXGxyIuP28oBZXkUatgJ7MUXTvaiZiffO-oMLVEvVPD2KSm7oYR-OLdTJB4xeOPf964RoBpw
    &scope=openid%20accounts&username=psu&password=0penBanking!&acr=urn%3Aopenbanking%3Apsd2%3Aca&acr_sig=ON47-DQ6NP3jdgkGDx9VeYU4OMd5r8QQyJaf37G7pQ4
     */
    /* nightly
    https://openam-forgerock-securebankingaccelerato.forgeblocks.com:443/am/oauth2/realms/root/realms/alpha/authorize?
    redirect_uri=https://google.com&response_type=code%20id_token&client_id=219f749f-cd39-43b4-a2e1-76fe1fd7953f&state=10d260bf-a7d9-444a-92d9-7b7a5f088208
    &nonce=10d260bf-a7d9-444a-92d9-7b7a5f088208&request=eyJraWQiOiIyeU5qUE9DanBPOHJjS2c2X2xWdFd6QVFSMFUiLCJhbGciOiJQUzI1NiJ9.eyJhdWQiOiJodHRwczovL29wZW5hbS1mb3JnZXJvY2stc2VjdXJlYmFua2luZ2FjY2VsZXJhdG8uZm9yZ2VibG9ja3MuY29tOjQ0My9hbS9vYXV0aDIvcmVhbG1zL3Jvb3QvcmVhbG1zL2FscGhhIiwiY2xhaW1zIjp7ImlkX3Rva2VuIjp7ImFjciI6eyJlc3NlbnRpYWwiOnRydWUsInZhbHVlIjoidXJuOm9wZW5iYW5raW5nOnBzZDI6Y2EifSwib3BlbmJhbmtpbmdfaW50ZW50X2lkIjp7ImVzc2VudGlhbCI6dHJ1ZSwidmFsdWUiOiJBQUNfZTViOTJkNjUtYmMxNy00MWY0LTk0NTctY2ZkMWUwZTU2NmM3In19LCJ1c2VyaW5mbyI6eyJvcGVuYmFua2luZ19pbnRlbnRfaWQiOnsiZXNzZW50aWFsIjp0cnVlLCJ2YWx1ZSI6IkFBQ19lNWI5MmQ2NS1iYzE3LTQxZjQtOTQ1Ny1jZmQxZTBlNTY2YzcifX19LCJjbGllbnRfaWQiOiIyMTlmNzQ5Zi1jZDM5LTQzYjQtYTJlMS03NmZlMWZkNzk1M2YiLCJleHAiOjE2NDcyNjAyMzgsImlhdCI6MTY0NzI1OTYzOCwianRpIjoiMTNhNDBmNGYtZWFmZi00ZDY0LTg2N2YtMWU5ZGQ1ZWYyYjVhIiwiaXNzIjoiMjE5Zjc0OWYtY2QzOS00M2I0LWEyZTEtNzZmZTFmZDc5NTNmIiwibm9uY2UiOiIxMGQyNjBiZi1hN2Q5LTQ0NGEtOTJkOS03YjdhNWYwODgyMDgiLCJyZWRpcmVjdF91cmkiOiJodHRwczovL2dvb2dsZS5jb20iLCJyZXNwb25zZV90eXBlIjoiY29kZSBpZF90b2tlbiIsInNjb3BlIjoib3BlbmlkIGFjY291bnRzIiwic3RhdGUiOiIxMGQyNjBiZi1hN2Q5LTQ0NGEtOTJkOS03YjdhNWYwODgyMDgifQ.i8UU8MrHXhlcl7VBh3nVTIYbOAlvAIJL7ivkgMqrbHLycdGxjw81IwuEssDWZBXf_PFMnDItWpsz_y10u4G4I6n0nq7E6q4PZJlx_bQyJk8LVX98n77Erk1avAZCP6gdbBeeqdi8YhriinSBrUd1GSoR-mgDbWkA99xMAexL9cBarNMSYfmlghuVm7HtXCPc1KvaRALr6y6lS9kN7CG3noXJUwn0SX8FFtfVPHfGdKZvPN-6T7d5ZbtUXg-k_CjR_fUbxBDmBqo9RSQRxOBN-4xLRSzy3H_6cUjI0CEbfyF0dhQfVbZHFgVPPzXv2QgN8hfe13bu1HZuUPAn-693yw
    &scope=openid%20accounts&username=psu&password=0penBanking!&acr=urn%3Aopenbanking%3Apsd2%3Aca&acr_sig=1CV3mNNno7XVBxmVlhU3Q3_1Z5OdpshVpqtVIGcgg3U
     */
    protected fun generateAuthenticationURL(
        consentId: String,
        registrationResponse: RegistrationResponse,
        psu: UserRegistrationRequest,
        tpp: Tpp,
        scopes: String
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
            iss = registrationResponse.client_id,
            scope = scopes
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
            return "$PLATFORM_SERVER/am/json/realms/root/realms/alpha/authenticate$parameters"
        } catch (e: Exception) {
            throw AssertionError("Location header URL doesn't have parameters")
        }

    }

    protected fun authenticateByHttpClient(
        authenticationURL: String,
        psu: UserRegistrationRequest
    ): AuthenticationResponse {
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder()
            .uri(URI.create(authenticationURL))
            .header("X-OpenAM-Username", psu.user.userName)
            .header("X-OpenAM-Password", psu.user.password)
            .header("Accept-API-Version", "resource=2.1, protocol=1.0")
            .POST(HttpRequest.BodyPublishers.ofString(""))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        val gson = Gson()
        return gson.fromJson(response.body(), AuthenticationResponse::class.java)
    }

    protected fun continueAuthorize(authorizeURL: String, cookie: String): String {
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

    protected fun getConsentDetails(consentRequest: String, cookie: String): String {
        val (_, response, result) = Fuel.post("$IG_SERVER/rcs/api/consent/details/")
            .header("Cookie", cookie)
            .body(consentRequest)
            .header("Content-Type", "application/jwt")
            .responseString()
        if (!response.isSuccessful) throw AssertionError(
            "Could not get the consent details",
            result.component2()
        )

        return result.get()
    }

    protected fun getAuthCode(consentResponse: String, authorizeURL: String, cookie: String): String {
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

    protected fun exchangeCode(
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

    private fun getLocationFromHeaders(response: Response): String {
        try {
            var location = response.header("Location").firstOrNull().toString()
            if (location.contains("error_description")) {
                if (location.contains("Resource%20Owner%20did%20not%20authorize%20the%20request")) {
                    throw AssertionError(CONSENT_NOT_AUTHORISED)
                } else {
                    throw AssertionError(LOCATION_HEADER_ERROR)
                }
            }
            return location
        } catch (e: Exception) {
            throw AssertionError(LOCATION_HEADER_NOT_EXISTS)
        }
    }

    companion object {
        const val GATEAWAY_ASSERTION =
            "eyJ0eXAiOiJKV1QiLCJraWQiOiJTbzZlSUR2NEozbEE4OEo5cE9jeFlFeExBVk09IiwiYWxnIjoiUFMyNTYifQ.eyJzdWIiOiJiNjg0MTQ2Zi1mYWE2LTQ2NGQtODA0ZS1iMjUyZmFiYTllMzgiLCJjdHMiOiJPQVVUSDJfR1JBTlRfU0VUIiwiYXV0aF9sZXZlbCI6MCwiYXVkaXRUcmFja2luZ0lkIjoiMWIyOWFhN2YtYjExMC00ODI2LTgwNWQtYjgxYzI1YTk0MDYyLTI4Mjg3NzkiLCJzdWJuYW1lIjoiYjY4NDE0NmYtZmFhNi00NjRkLTgwNGUtYjI1MmZhYmE5ZTM4IiwiaXNzIjoiaHR0cHM6Ly9vcGVuYW0tZm9yZ2Vyb2NrLXNlY3VyZWJhbmtpbmdhY2NlbGVyYXRvLmZvcmdlYmxvY2tzLmNvbS9hbS9vYXV0aDIvcmVhbG1zL3Jvb3QvcmVhbG1zL2FscGhhIiwidG9rZW5OYW1lIjoiYWNjZXNzX3Rva2VuIiwidG9rZW5fdHlwZSI6IkJlYXJlciIsImF1dGhHcmFudElkIjoicnhSZlBFZzBoWWZITHJ6Y21VY2VZdjB6YngwLmkxUElpcUhUeEl1Uy1lblozUEJ2SXJUeDM2OCIsIm5vbmNlIjoiMTBkMjYwYmYtYTdkOS00NDRhLTkyZDktN2I3YTVmMDg4MjA4IiwiYXVkIjoiZmUzYzI2ZTEtNzcwZS00ZmY5LThiM2EtMTg2YjhiZGMyNDA0IiwibmJmIjoxNjM2NTM5ODUwLCJncmFudF90eXBlIjoiYXV0aG9yaXphdGlvbl9jb2RlIiwic2NvcGUiOlsib3BlbmlkIiwiYWNjb3VudHMiXSwiYXV0aF90aW1lIjoxNjM2NTM5ODI5LCJjbGFpbXMiOiJ7XCJpZF90b2tlblwiOntcImFjclwiOntcInZhbHVlXCI6XCJ1cm46b3BlbmJhbmtpbmc6cHNkMjpjYVwiLFwiZXNzZW50aWFsXCI6dHJ1ZX0sXCJvcGVuYmFua2luZ19pbnRlbnRfaWRcIjp7XCJ2YWx1ZVwiOlwiQUFDXzM0NWJlMTA2LWMwNTAtNDg4OC1iYjliLWZhZGQwMTkzYTY3Y1wiLFwiZXNzZW50aWFsXCI6dHJ1ZX19fSIsInJlYWxtIjoiL2FscGhhIiwiY25mIjp7Ing1dCNTMjU2IjoickxvQnJmcHJhZVZMeXh1V0szWUgyUFZURVJ6VnhrY1FRelV6bXNIQmZvNCJ9LCJleHAiOjE2MzY4OTk4NTAsImlhdCI6MTYzNjUzOTg1MCwiZXhwaXJlc19pbiI6MzYwMDAwLCJqdGkiOiJyeFJmUEVnMGhZZkhMcnpjbVVjZVl2MHpieDAuTU5pMVhDMll5akw4TW5SakZHU1VCLTctcFdNIn0.Qj-qpvZXfmGVlBdgIYZbqqAvk8wc3-FISaA99o4govcWvlDWeLDEBBx5CS8bnTIe7vG8QuXVtk80qI2FE8XH4H556FQlFPQ-PMAnwpVltraZ4_YoSP_BZ6z8cZDzp8mJgefnLqr_zajZLaj_xxFAP5G1Xbm_IBpJeZvd8RO6hNYCiMJ0chvz-61p1k-vjmJgWsnfjmKtO3b65nP5qUgDp0s2HtiD0dY-f5u2ONsgCzwTdyKNiivYsoloPw-CLRGSnH6rf98eAXnJo7pvR9BGZ1Njn-2McRRMc4kFneNyNOS7BSpfAXm4ivCv31sVzxrd5nv4rnoGdGKXFGY7NdEEUw"
        const val CLIENT_ASSERTION_TYPE = "urn:ietf:params:oauth:client-assertion-type:jwt-bearer"
    }
}
