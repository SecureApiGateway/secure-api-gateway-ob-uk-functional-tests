package com.forgerock.securebanking.framework.utils

import com.forgerock.securebanking.framework.configuration.ADMIN_PASSWORD
import com.forgerock.securebanking.framework.configuration.ADMIN_USERNAME
import com.forgerock.securebanking.framework.constants.IAM
import com.forgerock.securebanking.framework.data.AuthenticationResponse
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.isStatusRedirection
import com.github.kittinunf.fuel.core.isSuccessful
import com.google.gson.Gson

//IDM Client Credentials
const val CLIENT_ID_KEY = "client_id"
const val CLIENT_ID = "idmAdminClient"

const val COOKIE_NAME = "iPlanetDirectoryPro"
const val REDIRECT_URI_KEY = "redirect_uri"
const val RESPONSE_TYPE_KEY = "response_type"
const val RESPONSE_TYPE = "code"
const val SCOPE_KEY = "scope"
const val SCOPE = "fr:idm:*"
const val CODE_CHALLENGE_KEY = "code_challenge"
const val CODE_CHALLENGE = "gX2yL78GGlz3QHsQZKPf96twOmUBKxn1-IXPd5_EHdA"
const val CODE_CHALLENGE_METHOD_KEY = "code_challenge_method"
const val CODE_CHALLENGE_METHOD = "S256"
const val CODE_KEY = "code"
const val CODE_VERIFIER_KEY = "code_verifier"
const val CODE_VERIFIER = "codeverifier"
const val GRANT_TYPE_KEY = "grant_type"
const val GRANT_TYPE = "authorization_code"

data class GetIDMAdminTokenSuccessResponse(
    val access_token: String,
    val scope: String,
    val token_type: String,
    val Bearer: String
)

fun adminAuthentication(): String {
    val (_, response, result) = Fuel.post("$IAM/am/json/realms/root/authenticate")
        .header("Accept-API-Version", "protocol=1.0,resource=2.1")
        .header("X-OpenAM-Username", ADMIN_USERNAME)
        .header("X-OpenAM-Password", ADMIN_PASSWORD)
        .responseString()

    if (!response.isSuccessful) throw AssertionError("Admin authentication failed", result.component2())
    val gson = Gson()
    val authenticationResponse: AuthenticationResponse = gson.fromJson(result.component1(), AuthenticationResponse::class.java)
    return authenticationResponse.tokenId
}

fun getIDMAdminAuthCode(cookie: String): String {
    val (_, response, result) = Fuel.get(
        "$IAM/am/oauth2/authorize",
        listOf(
            REDIRECT_URI_KEY to "$IAM/platform/appAuthHelperRedirect.html",
            CLIENT_ID_KEY to CLIENT_ID,
            RESPONSE_TYPE_KEY to RESPONSE_TYPE,
            SCOPE_KEY to SCOPE,
            CODE_CHALLENGE_KEY to CODE_CHALLENGE,
            CODE_CHALLENGE_METHOD_KEY to CODE_CHALLENGE_METHOD
        )
    )
        .allowRedirects(false)
        .header("cookie", "$COOKIE_NAME=$cookie")
        .useHttpCache(true)
        .responseString()

    if (!response.isStatusRedirection ) throw AssertionError("Failed to get IDM Admin Auth Code", result.component2())
    val location = response.header("Location")
    if (location.isEmpty() || !location.toString().contains("code=")) throw AssertionError("Failed to get IDM Admin Auth Code", result.component2())
    return location.toString().split("code=")[1].split("&")[0]
}

fun getIDMAdminToken(cookie: String, code: String): String {
    val (_, response, result) = Fuel.post(
        "$IAM/am/oauth2/access_token",
        listOf(
            REDIRECT_URI_KEY to "$IAM/platform/appAuthHelperRedirect.html",
            CLIENT_ID_KEY to CLIENT_ID,
            CODE_KEY to code,
            CODE_VERIFIER_KEY to CODE_VERIFIER,
            GRANT_TYPE_KEY to GRANT_TYPE
        )
    )
        .header("cookie", "$COOKIE_NAME=$cookie")
        .header("Content-Type", "application/x-www-form-urlencoded")
        .responseString()

    if (!response.isSuccessful) throw AssertionError("Failed to get IDM Admin Auth Code", result.component2())
    val gson = Gson()
    val getIDMAdminTokenSuccessResponse: GetIDMAdminTokenSuccessResponse =
        gson.fromJson(result.component1(), GetIDMAdminTokenSuccessResponse::class.java)
    return getIDMAdminTokenSuccessResponse.access_token
}

