package com.forgerock.securebanking.support.directory

import com.forgerock.securebanking.framework.configuration.DOMAIN
import com.forgerock.securebanking.framework.data.Application
import com.forgerock.securebanking.framework.data.SoftwareStatement
import com.forgerock.securebanking.framework.http.fuel.responseObject
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.isSuccessful
import com.github.kittinunf.fuel.gson.jsonBody

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
    val (_, response, result) = Fuel.post("https://service.directory.$DOMAIN/api/software-statement/")
        .jsonBody(Object())
        .header("Cookie", "obri-session=$sessionToken")
        .responseObject<SoftwareStatement>()
    if (!response.isSuccessful) throw AssertionError("Could not create new software statement")
    return result.get()
}
