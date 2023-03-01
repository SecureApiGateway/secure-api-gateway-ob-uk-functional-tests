package com.forgerock.sapi.gateway.ob.uk.support

import com.forgerock.sapi.gateway.framework.configuration.AM_COOKIE_NAME
import com.forgerock.sapi.gateway.framework.configuration.PLATFORM_SERVER
import com.forgerock.sapi.gateway.framework.configuration.PSU_PASSWORD
import com.forgerock.sapi.gateway.framework.configuration.PSU_USERNAME
import com.forgerock.sapi.gateway.framework.http.fuel.responseObject
import com.forgerock.sapi.gateway.ob.uk.support.registration.UserRegistrationRequest
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.core.isSuccessful
import com.github.kittinunf.fuel.gson.jsonBody
import com.google.gson.Gson
import org.apache.http.client.utils.URIBuilder
import org.apache.http.entity.ContentType
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

private fun initializeUser(): UserRegistrationRequest {
    return UserRegistrationRequest(PSU_USERNAME, PSU_PASSWORD)
}

fun registerDirectoryUser(): UserRegistrationRequest {
    return initializeUser()
}

fun registerPSU(): UserRegistrationRequest {
    return initializeUser()
}

fun login(username: String, password: String): String {
    //val ssoCode = authenticate(realm = "alpha", username, password)
    val ssoCode = authenticateByHttpClient(realm = "alpha", username, password)
    return ssoCode.tokenId
}

@Deprecated("To delete")
private fun initiateOIDCFlow(): String {
    val (_, initiateLoginResponse, initiateLoginResult) = Fuel.get(
        "https://service.directory.DOMAIN/api/user/initiate-login",
        listOf(Pair("originUrl", "/"))
    ).responseString()
    if (!initiateLoginResponse.isSuccessful) throw AssertionError(
        "Failed to initiate login",
        initiateLoginResult.component2()
    )
    return initiateLoginResult.get()
}

fun checkSession(ssoCode: SsoCode): Int {
    val (_, response, _) = Fuel.post("https://am.DOMAIN/json/sessions?_action=getSessionInfo")
        .header("Content-Type", "application/json")
        .header("Accept-API-Version", "protocol=1.0,resource=2.1")
        .header("iPlanetDirectoryPro", ssoCode.tokenId)
        /*
        .response {request, response, result ->
            println(request)
            println(response)
            val (bytes, error) = result
            if (bytes != null) {
                println("[response bytes] ${String(bytes)}")
            }
        }
         */
        .responseString()
    return response.statusCode
}


fun authenticatePSU(gotoUrl: String, username: String, password: String): SsoCode {
    val (_, authenticateInitResponse, authenticateInitResult) = Fuel.post(
        "https://am.DOMAIN/json/realms/root/realms/openbanking/authenticate?goto=${
            URLEncoder.encode(
                gotoUrl,
                "UTF-8"
            )
        }"
    )
        .header("Content-Type", "application/json")
        .header("Accept-API-Version", "protocol=1.0,resource=2.1")
        .responseObject<OpenBankingRealmAuthenticationRequest>()
    if (!authenticateInitResponse.isSuccessful) throw AssertionError(
        "Failed to initiate login",
        authenticateInitResult.component2()
    )
    val routeCookie =
        authenticateInitResponse.header("Set-Cookie").find { it.contains("route") }?.substringAfter("route=").toString()

    // Authenticate with AM and get SSO code
    val authenticationResponsePayload = authenticateInitResult.get()
    authenticationResponsePayload.callbacks.get(0)._id = 0
    authenticationResponsePayload.callbacks.get(0).input.get(0).value = username
    authenticationResponsePayload.callbacks.get(1)._id = 1
    authenticationResponsePayload.callbacks.get(1).input.get(0).value = password


    val (_, authenticateResponse, authenticateResult) = Fuel.post("https://am.DOMAIN/json/realms/root/realms/openbanking/authenticate")
        .header("Content-Type", "application/json")
        .header("Accept-API-Version", "protocol=1.0,resource=2.1")
        .header("Cookie", "route=$routeCookie")
        .jsonBody(authenticationResponsePayload)
        .responseObject<SsoCode>()
    if (!authenticateResponse.isSuccessful) throw AssertionError(
        "Failed to initiate login",
        authenticateResult.component2()
    )
    return authenticateResult.get()
}

private fun authenticateByHttpClient(realm: String, username: String, password: String): SsoCode {
    val client = HttpClient.newBuilder().build();
    val request = HttpRequest.newBuilder()
        .uri(URI.create("$PLATFORM_SERVER/am/json/realms/root/realms/$realm/authenticate"))
        .header("X-OpenAM-Username", username)
        .header("X-OpenAM-Password", password)
        .header("Accept-API-Version", "resource=2.1, protocol=1.0")
        .POST(HttpRequest.BodyPublishers.ofString(""))
        .build()
    val response = client.send(request, HttpResponse.BodyHandlers.ofString());
    val gson = Gson()
    return gson.fromJson(response.body(), SsoCode::class.java)
}

@Deprecated("To delete")
private fun authenticate(realm: String, username: String, password: String): SsoCode {
    var request = Fuel.post("$PLATFORM_SERVER/am/json/realms/root/realms/$realm/authenticate")
    request = request
        .header("X-OpenAM-Username", username)
        .header("X-OpenAM-Password", password)
        .header("Accept-API-Version", "resource=2.1, protocol=1.0")
        .header(Headers.CONTENT_TYPE, ContentType.DEFAULT_TEXT)
        .header("My-header", "200")
        .header(Headers.CONTENT_LENGTH, 0)
    /*
    curl -i -X POST https://openam-forgerock-securebankingaccelerato.forgeblocks.com/am/json/realms/root/realms/alpha/authenticate \
    -H 'X-OpenAM-Password:0penBanking!' -H "My-header:200" -H "x-obri-analytics-enabled:false" -H "X-OpenAM-Username:psu" \
    -H "Accept-API-Version:resource=2.1, protocol=1.0" -H "Content-Type:text/plain; charset=ISO-8859-1" -H "Content-Length:0"
     */
    val (_, response, result) = request.responseString()
    if (!response.isSuccessful) throw AssertionError(
        "Failed to get callbacks to Registration Journey",
        result.component2()
    )
    val gson = Gson()
    return gson.fromJson(result.component1(), SsoCode::class.java)
}

@Deprecated("To delete")
private fun exchangeCode(gotoUrl: String, ssoCode: SsoCode): String? {
    val (_, gotoResponse, _) = Fuel.get(gotoUrl)
        .header("Cookie", "amlbcookie=01; $AM_COOKIE_NAME=${ssoCode.tokenId}")
        .allowRedirects(false)
        .responseString()
    val redirect = gotoResponse.headers.get("Location").first()
    val code = URIBuilder(redirect).queryParams.find { it.name.equals("code") }
        ?: throw AssertionError("Failed to get exchange code from url ${redirect}")
    return code.value
}

@Deprecated("To delete")
private fun getObriSessionToken(gotoUrl: String, code: String?): String {
    val state = URIBuilder(gotoUrl).queryParams.find { it.name.equals("state") }
    val (_, loginResponse, loginResult) = Fuel.post("https://service.directory.DOMAIN/api/user/login")
        .jsonBody(mapOf("code" to code, "state" to state?.value))
        .header("Cookie", "OIDC_ORIGIN_URL=/")
        .responseString()
    if (!loginResponse.isSuccessful) throw AssertionError("Failed to log in", loginResult.component2())
    val obriSession =
        loginResponse.header("Set-Cookie").find { it.contains("obri-session") }?.substringAfter("obri-session=")
            .toString()
    return obriSession
}

@Deprecated("To delete")
private fun initUser(obriSession: String) {
    val (_, initResponse, initResult) = Fuel.get("https://service.directory.DOMAIN/api/user/")
        .header("Cookie", "obri-session=${obriSession}")
        .responseString()
    if (!initResponse.isSuccessful) throw AssertionError("Failed to initialise user", initResult.component2())
}

@Deprecated("To delete")
data class AuthenticationRequest(
    val authId: String,
    val callbacks: List<Callback>,
    val header: String = "",
    val stage: String,
    val template: String
) {
    data class Callback(
        val input: List<Input>,
        val output: List<Output>,
        val type: String
    ) {
        data class Input(
            val name: String,
            var value: String
        )

        data class Output(
            val name: String,
            val value: String
        )
    }
}

data class OpenBankingRealmAuthenticationRequest(
    val authId: String,
    val callbacks: List<Callback>
) {
    data class Callback(
        val input: List<Input>,
        val output: List<Output>,
        val type: String,
        var _id: Int
    ) {
        data class Input(
            val name: String,
            var value: String
        )

        data class Output(
            val name: String,
            val value: String
        )
    }
}

data class SsoCode(
    val realm: String,
    val successUrl: String,
    val tokenId: String
)
