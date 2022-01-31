package com.forgerock.securebanking.support.directory

import com.forgerock.securebanking.framework.configuration.DOMAIN
import com.forgerock.securebanking.framework.constants.OB_DEMO
import com.forgerock.securebanking.framework.data.Application
import com.forgerock.securebanking.framework.data.SoftwareStatement
import com.forgerock.securebanking.framework.http.fuel.responseObject
import com.forgerock.securebanking.framework.platform.register.Organization
import com.forgerock.uk.openbanking.framework.accesstoken.constants.OB_TPP_EIDAS_SIGNING_KEY_PATH
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.isSuccessful
import com.github.kittinunf.fuel.gson.jsonBody
import com.google.gson.JsonParser.parseString

/**
 * Holds the common functions for Directory that are used by other tests
 */

fun getTransportKid(softwareStatement: SoftwareStatement, sessionToken: String): String? {
    val (_, response, result) = Fuel.get("https://service.directory.$DOMAIN/api/software-statement/${softwareStatement.id}/application")
        .header("Cookie", "obri-session=$sessionToken")
        .responseObject<Application>()
    if (!response.isSuccessful) throw AssertionError("Could not get transport kid")
    return result.component1()?.transportKeys?.keys?.first()
}

fun getSigningKid(softwareStatement: SoftwareStatement, sessionToken: String): String? {
    val (_, response, result) = Fuel.get("https://service.directory.$DOMAIN/api/software-statement/${softwareStatement.id}/application")
        .header("Cookie", "obri-session=$sessionToken")
        .responseObject<Application>()
    if (!response.isSuccessful) throw AssertionError("Could not get signing kid")
    return result.component1()?.keys?.filter { it.value.keyUse == "sig" }?.keys?.first()
}

fun getEncryptionKid(softwareStatement: SoftwareStatement, sessionToken: String): String? {
    val (_, response, result) = Fuel.get("https://service.directory.$DOMAIN/api/software-statement/${softwareStatement.id}/application")
        .header("Cookie", "obri-session=$sessionToken")
        .responseObject<Application>()
    if (!response.isSuccessful) throw AssertionError("Could not get encryption kid")
    return result.component1()?.keys?.filter { it.value.keyUse == "enc" }?.keys?.first()
}

fun createSoftwareStatement(sessionToken: String): SoftwareStatement {
    return SoftwareStatement(
        "ebSqTNqmQXFYz6VtWGXZAa",
        "ebSqTNqmQXFYz6VtWGXZAa",
        "0015800001041REAAY",
        OB_TPP_EIDAS_SIGNING_KEY_PATH,
        "Test",
        listOf(
            "https://www.google.com", "https://localhost",
            "https://www.google.co.uk"
        ),
        listOf(
            "DATA",
            "AISP",
            "CBPII",
            "PISP"
        ),
        "Active"
    )
}

fun generateSoftwareStatement(sessionToken: String): String {
    val jwkMsKey = issueCertificate()
    val tlsCert = getTLSCertFromJWKs(jwkMsKey)
    var requestBody = GenerateSoftwareStatementRequestBody(
        "softwareid", "Acme Application",
        "11111111", "https://myapp/tos",
        "Acme test application", "https://www.google.com",
        "https://myapp/policy", "https://acme-music.com/wp-content/uploads/2020/07/acme.png",
        listOf(
            "DATA",
            "AISP",
            "CBPII",
            "PISP"
        ), jwkMsKey
    )
    val (_, response, result) = Fuel.post("$OB_DEMO/jwkms/apiclient/getssa")
        .jsonBody(requestBody)
        .header("Content-Type", "application/json")
        .responseString()

    if (!response.isSuccessful) throw AssertionError("Could not create a new software statement")
    return result.get()
}

fun issueCertificate(): String {
    val organization = Organization()
    val (_, response, result) = Fuel.post("$OB_DEMO/jwkms/apiclient/issuecert")
        .jsonBody(organization)
        .header("Content-Type", "application/json")
        .responseString()
    if (!response.isSuccessful) throw AssertionError("Cannot issue certificate", result.component2())
    return result.component1().toString()
}


fun getTLSCertFromJWKs(jwkMsKey: String): String {
    val body = parseString(jwkMsKey).asJsonObject
    val (_, response, result) = Fuel.post("$OB_DEMO/jwkms/apiclient/gettlscert")
        .jsonBody(body)
        .header("Content-Type", "application/json")
        .responseString()
    if (!response.isSuccessful) throw AssertionError("Cannot get the TLS Certificate from JWKms", result.component2())
    return result.component1().toString()
}

data class GenerateSoftwareStatementRequestBody(
    val software_id: String,
    val software_client_name: String,
    val software_client_id: String,
    val software_tos_uri: String,
    val software_client_description: String,
    val software_redirect_uris: String,
    val software_policy_uri: String,
    val software_logo_uri: String,
    val software_roles: List<String>,
    val software_jwks: String
)
