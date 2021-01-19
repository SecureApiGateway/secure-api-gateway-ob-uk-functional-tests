package com.forgerock.openbanking.directory

import com.forgerock.openbanking.DOMAIN
import com.forgerock.openbanking.responseObject
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
    return result.component1()?.keys?.filter { it.value.keyUse.equals("sig") }?.keys?.first()
}

fun getEncryptionKid(softwareStatement: SoftwareStatement, sessionToken: String): String? {
    val (_, response, result) = Fuel.get("https://service.directory.$DOMAIN/api/software-statement/${softwareStatement.id}/application")
            .header("Cookie", "obri-session=$sessionToken")
            .responseObject<Application>()
    if (!response.isSuccessful) throw AssertionError("Could not get encryption kid")
    return result.component1()?.keys?.filter { it.value.keyUse.equals("enc") }?.keys?.first()
}

fun createSoftwareStatement(sessionToken: String): SoftwareStatement {
    val (_, response, result) = Fuel.post("https://service.directory.$DOMAIN/api/software-statement/")
            .jsonBody(Object())
            .header("Cookie", "obri-session=$sessionToken")
            .responseObject<SoftwareStatement>()
    if (!response.isSuccessful) throw AssertionError("Could not create new software statement")
    return result.get()
}
