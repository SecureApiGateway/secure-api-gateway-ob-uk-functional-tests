package com.forgerock.sapi.gateway.ob.uk.support.directory

import com.forgerock.sapi.gateway.framework.configuration.OB_TPP_EIDAS_SIGNING_KEY_PATH
import com.forgerock.sapi.gateway.framework.data.Application
import com.forgerock.sapi.gateway.framework.data.SoftwareStatement
import com.forgerock.sapi.gateway.framework.http.fuel.responseObject
import com.forgerock.sapi.gateway.ob.uk.framework.configuration.OB_ORGANISATION_ID
import com.forgerock.sapi.gateway.ob.uk.framework.configuration.OB_SOFTWARE_ID
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.isSuccessful

/**
 * Holds the common functions for Directory that are used by other tests
 */

fun getTransportKid(softwareStatement: SoftwareStatement, sessionToken: String): String? {
    val (_, response, result) = Fuel.get("https://service.directory.DOMAIN/api/software-statement/${softwareStatement.id}/application")
        .header("Cookie", "obri-session=$sessionToken")
        .responseObject<Application>()
    if (!response.isSuccessful) throw AssertionError("Could not get transport kid")
    return result.component1()?.transportKeys?.keys?.first()
}

fun getSigningKid(softwareStatement: SoftwareStatement, sessionToken: String): String? {
    val (_, response, result) = Fuel.get("https://service.directory.DOMAIN/api/software-statement/${softwareStatement.id}/application")
        .header("Cookie", "obri-session=$sessionToken")
        .responseObject<Application>()
    if (!response.isSuccessful) throw AssertionError("Could not get signing kid")
    return result.component1()?.keys?.filter { it.value.keyUse == "sig" }?.keys?.first()
}

fun getEncryptionKid(softwareStatement: SoftwareStatement, sessionToken: String): String? {
    val (_, response, result) = Fuel.get("https://service.directory.DOMAIN/api/software-statement/${softwareStatement.id}/application")
        .header("Cookie", "obri-session=$sessionToken")
        .responseObject<Application>()
    if (!response.isSuccessful) throw AssertionError("Could not get encryption kid")
    return result.component1()?.keys?.filter { it.value.keyUse == "enc" }?.keys?.first()
}

fun createSoftwareStatement(): SoftwareStatement {
    return SoftwareStatement(
            OB_SOFTWARE_ID,
            OB_SOFTWARE_ID,
            OB_ORGANISATION_ID,
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
