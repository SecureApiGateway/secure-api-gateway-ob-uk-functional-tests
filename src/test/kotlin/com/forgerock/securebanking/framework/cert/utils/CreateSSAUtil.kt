package com.forgerock.securebanking.framework.cert.utils

import com.forgerock.securebanking.framework.constants.OB_DEMO
import com.forgerock.securebanking.framework.platform.register.SSAClaims
import com.forgerock.securebanking.framework.platform.register.SoftwareStatementAssertion
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.core.isSuccessful
import com.google.gson.GsonBuilder
import java.io.File
import java.nio.charset.Charset
import java.nio.file.Files

var jwt: String = "empty"

fun main() {
    createSSAJwtFile()
    signSSAClaims()
}

private fun getSSAJwt(): String {
    initSSLClient()
    val getSSARequest = GsonBuilder().create().toJson(SoftwareStatementAssertion())
    val ssaURL = "$OB_DEMO/jwkms/apiclient/getssa"
    val (_, ssaResult, r) = Fuel.post(ssaURL)
        .header(Headers.CONTENT_TYPE, "application/json")
        .body(getSSARequest)
        .response()
    if (!ssaResult.isSuccessful) throw AssertionError(
        "Could not get requested the SSA JWT data from ${ssaURL}: ${
            String(
                ssaResult.data
            )
        }", r.component2()
    )
    return String(r.get())
}


private fun createSSAJwtFile() {
    val fileSSAJwt = File("${support.PATH_TO_STORE_EIDAS}/${support.ssaJwtFile}")
    jwt = getSSAJwt()
    writeFile(fileSSAJwt, jwt)
}

private fun signSSAClaims() {
    val fileSSAJws = File("${support.PATH_TO_STORE_EIDAS}/${support.ssaJwsFile}")
    deleteFileIfExist(fileSSAJws)
    writeFile(fileSSAJws, getSignedSSAJwtClaims())
}

private fun writeFile(file: File, content: String) {
    deleteFileIfExist(file)
    file.writeText(content, Charset.defaultCharset())
    Files.setPosixFilePermissions(file.toPath(), support.filePermissions)
}

private fun getSignedSSAJwtClaims(): String {
    initSSLClient()
    val getSSAClaimsRequest = GsonBuilder().create().toJson(SSAClaims(software_statement = jwt))
    val singSSAURL = "$OB_DEMO/jwkms/apiclient/signclaims"
    val (_, ssaResult, r) = Fuel.post(singSSAURL)
        .header(Headers.CONTENT_TYPE, "application/json")
        .body(getSSAClaimsRequest)
        .response()
    if (!ssaResult.isSuccessful) throw AssertionError(
        "Could not get signed SSA Claims data from ${singSSAURL}: ${
            String(
                ssaResult.data
            )
        }", r.component2()
    )
    return String(r.get())
}
