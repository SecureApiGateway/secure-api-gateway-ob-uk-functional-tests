package com.forgerock.sapi.gateway.ob.uk.support.onboarding

import com.forgerock.sapi.gateway.framework.cert.loadRsaPrivateKey
import com.forgerock.sapi.gateway.framework.data.RequestParameters
import com.forgerock.sapi.gateway.framework.http.fuel.initFuel
import com.forgerock.sapi.gateway.framework.signature.signPayload
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths


class ManualOnboardingConfig {
    var clientId: String = ""
    var signingKeyId: String = ""
    var consentId: String = ""

    constructor() : super() {}

    constructor(clientId: String, signingKeyId: String, consentId: String) {
        this.clientId = clientId
        this.signingKeyId = signingKeyId
        this.consentId = consentId
    }
}

/**
 * INSTRUCTIONS:
 * set an environment variable to the path of the directory from which the signing key and the config will be read.
 * The environment variable must be called OBRI-SIGNING-KEY-PATH. In that directory place the signing key to be used
 * (downloaded from OB Sandbox Directory) and a config file containing a JSON representation of the
 * ManualOnboardingConfig object specified above.
 *
 */
fun main() {
    initFuel()
    val ssaDirectory = System.getenv("OBRI-SIGNING-KEY-PATH") ?: "/tmp/ob-ssa-dir"
    val gson = Gson()
    val configFilePath = "$ssaDirectory/config.json"
    val bufferedReader: BufferedReader = File(configFilePath).bufferedReader()
    val configString = bufferedReader.use { it.readText() }
    val manualOnboardingConfig = gson.fromJson(configString, ManualOnboardingConfig::class.java)

    //val ssaPath : String = "$ssaDirectory/ssa.jwt"
    val signingKeyPath = "$ssaDirectory/signing.key"

    signClaims(
        signingKeyPath,
        manualOnboardingConfig.signingKeyId,
        manualOnboardingConfig.consentId,
        manualOnboardingConfig.clientId
    )
}

//fun signRegistrationRequest(ssaPath: String, signingKeyPath: String): Pair<String, ManualRegistrationRequest> {
//    val ssa = Files.readString(Paths.get(ssaPath))
//    val registrationRequest = ManualRegistrationRequest(software_statement = ssa)
//    println("Registration request is \n$registrationRequest")
//    val privateKey = Files.readString(Paths.get(signingKeyPath))
//    val key = loadRsaPrivateKey(privateKey)
//    val signedRegistrationRequest = Jwts.builder()
//            .setHeaderParam("kid", OB_TPP_TEST_KID)
//            .setPayload(GsonUtils.gson.toJson(registrationRequest))
//            .signWith(key, SignatureAlgorithm.forName("RS256"))
//            .compact()
//    return Pair(signedRegistrationRequest, registrationRequest)
//}

fun signClaims(/*ssaPath : String,*/ signingKeyPath: String, signingKid: String, consentId: String, clientId: String):
        String {
    //val ssa = Files.readString(Paths.get(ssaPath))
    val signingKey = Files.readString(Paths.get(signingKeyPath))

    val acr = RequestParameters.Claims.IdToken.Acr(true, "urn:openbanking:psd2:sca")
    val intentId = RequestParameters.Claims.IdToken.OpenbankingIntentId(true, consentId)
    val idToken = RequestParameters.Claims.IdToken(acr, intentId)

    val userIntentId = RequestParameters.Claims.Userinfo.OpenbankingIntentId(true, consentId)
    val userInfo = RequestParameters.Claims.Userinfo(userIntentId)

    val claims = RequestParameters.Claims(idToken, userInfo)
    val requestParameters = RequestParameters(claims = claims, client_id = clientId, iss = clientId)
    println("Request Parameters is \n$requestParameters")
    val signedPayload = signPayload(requestParameters, loadRsaPrivateKey(signingKey)!!, signingKid)
    println("Signed Request Parameters: \n$signedPayload")

    return signedPayload
}
