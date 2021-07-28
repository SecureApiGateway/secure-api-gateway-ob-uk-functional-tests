package com.forgerock.securebanking.framework.platform.register

import com.forgerock.securebanking.framework.cert.utils.SupportValues
import com.forgerock.securebanking.framework.cert.utils.initSSLClient
import com.forgerock.securebanking.framework.constants.IAM
import com.forgerock.securebanking.framework.constants.OB_DEMO
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.core.isSuccessful

val support = SupportValues()
fun main() {
    registerApp()
}

fun registerApp() {
    initSSLClient()
    val ssaJWT = String(object {}.javaClass.getResourceAsStream("${support.PATH_EIDAS_RESOURCES}/${support.ssaJwsFile}")
        .readAllBytes()
    )
    val registerURL = "$OB_DEMO/am/oauth2/realms/root/realms/alpha/register"
    val (_, result, r) = Fuel.post(registerURL)
        .header(Headers.CONTENT_TYPE, "application/jwt")
        .body(ssaJWT)
        .response()
    if (!result.isSuccessful) throw AssertionError(
        "Could not get requested certificates data from ${registerURL}: ${
            String(
                result.data
            )
        }", r.component2()
    )
    println(ssaJWT)
}
