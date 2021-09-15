package com.forgerock.securebanking.framework.platform.register

import com.forgerock.securebanking.framework.cert.utils.*
import com.forgerock.securebanking.framework.constants.IAM
import com.forgerock.securebanking.framework.constants.OB_DEMO
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.core.isSuccessful
import java.io.File
import java.nio.charset.Charset
import java.nio.file.Files

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
    createRegisterResponseFile(String(result.data))
    println(String(result.data))
}

private fun createRegisterResponseFile(response: String) {
    val fileRegisterResponse = File("${support.PATH_TO_STORE_EIDAS}/registerResponse.json")
    writeFile(fileRegisterResponse, response)
}

private fun writeFile(file: File, content: String) {
    deleteFileIfExist(file)
    file.writeText(content, Charset.defaultCharset())
    Files.setPosixFilePermissions(file.toPath(), com.forgerock.securebanking.framework.cert.utils.support.filePermissions)
}
