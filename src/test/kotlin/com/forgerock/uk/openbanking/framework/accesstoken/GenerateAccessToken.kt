package com.forgerock.uk.openbanking.framework.accesstoken

import com.forgerock.securebanking.framework.cert.loadRsaPrivateKey
import com.forgerock.securebanking.framework.configuration.OB_TPP_OB_EIDAS_TEST_SIGNING_KID
import com.forgerock.securebanking.framework.http.fuel.initFuel
import com.forgerock.securebanking.framework.http.fuel.responseObject
import com.forgerock.uk.openbanking.framework.accesstoken.constants.*
import com.forgerock.uk.openbanking.framework.accesstoken.model.AccessTokenRequest
import com.forgerock.uk.openbanking.framework.accesstoken.model.AccessTokenResponse
import com.forgerock.uk.openbanking.framework.accesstoken.model.ClaimsTest
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.isSuccessful
import com.google.gson.GsonBuilder
import com.nimbusds.jose.JOSEObjectType
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.Payload
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import java.io.FileReader
import java.util.*

/*
Hello, I'm try to request a new SSA through OB API and I can get the access token and the test
endpoint https://matls-api.openbankingtest.org.uk/scim/v2/participants/ works properly
but not the endpoint to generate a new SSA https://matls-ssaapi.openbankingtest.org.uk/api/v1rc2/tpp/{org_id}/ssa/{software_id}
I'm getting 400 Bad request I have following the guideline
 */
// https://github.com/OpenBankingUK/directory-api-specs

fun main() {
    val jws = getJWS()
    if (jws != null) {
        println(jws.serialize())
        val accessToken: AccessTokenResponse = acquireAccessToken(jws)
        println(accessToken)
        val ssa = getSSA(accessToken.access_token)
        println(ssa)
    } else {
        println("Error requesting the signed JWS")
    }
}

fun getSSA(accessToken: String): String {
    initOBSSLClient()
    val matlsSSAUrl = SSA_MATLS_URL_SANDBOX
        .replace("{org_id}", OB_ORGANISATION_ID, true)
        .replace("{software_id}", OB_SOFTWARE_ID, true)
    // "https://matls-api.openbankingtest.org.uk/scim/v2/participants/" works
    val (_, certResult, r) = Fuel.get(matlsSSAUrl)
        .header("Accept", "application/jws+json")
        .header("Authorization", "Bearer $accessToken")
        .responseString()
    if (!certResult.isSuccessful) throw AssertionError(
        "Could not get requested SSA data from ${matlsSSAUrl}: ${
            String(
                certResult.data
            )
        }", r.component2()
    )
    return r.get()
}

fun getJWS(): SignedJWT? {
    val signingKeyResource = object {}.javaClass.getResource(OB_TPP_EIDAS_SIGNING_KEY_PATH)
    if (signingKeyResource != null) {
        val detachedPayload = Payload(GsonBuilder().create().toJson(ClaimsTest()))

        val jwtClaims = JWTClaimsSet.Builder(JWTClaimsSet.parse(detachedPayload.toJSONObject()))
            .issueTime(Date())
            .jwtID(UUID.randomUUID().toString())
            .build()

        val jwsHeaderBuilder: JWSHeader = JWSHeader.Builder(JWSAlgorithm.RS256)
            .keyID(OB_TPP_OB_EIDAS_TEST_SIGNING_KID)
            .type(JOSEObjectType.JWT).build()

        val signedJWT = SignedJWT(jwsHeaderBuilder, jwtClaims)

        signedJWT.sign(RSASSASigner(loadRsaPrivateKey(fileReader = FileReader(signingKeyResource.file))))

        return signedJWT
    } else {
        return null
    }

}

fun acquireAccessToken(jws: SignedJWT): AccessTokenResponse {
    initOBSSLClient()
    val accessTokenRequest = AccessTokenRequest(client_assertion = jws.serialize())

    val parameters = listOf(
        "client_assertion_type" to accessTokenRequest.client_assertion_type,
        "grant_type" to accessTokenRequest.grant_type,
        "client_id" to accessTokenRequest.client_id,
        "client_assertion" to accessTokenRequest.client_assertion,
        "scope" to accessTokenRequest.scope
    )

    val (_, certResult, r) = Fuel.post(TOKEN_URL_SANDBOX, parameters)
        .responseObject<AccessTokenResponse>()
    if (!certResult.isSuccessful) throw AssertionError(
        "Could not get requested access token data from ${TOKEN_URL_SANDBOX}: ${
            String(
                certResult.data
            )
        }", r.component2()
    )
    return r.get()
}

/*
fun readPrivateKey(): PrivateKey {
    val keyReader = FileReader(object {}.javaClass.getResource("$OB_TPP_EIDAS_SIGNING_KEY_PATH").file)
    val pemParser = PEMParser(keyReader)
    val converter = JcaPEMKeyConverter()
    val privateKeyInfo = PrivateKeyInfo.getInstance(pemParser.readObject())
    return converter.getPrivateKey(privateKeyInfo)
}
*/

fun initOBSSLClient() {
    val privatePemStream = object {}.javaClass.getResourceAsStream(OB_TPP_EIDAS_TRANSPORT_KEY_PATH)
    val certificatePemStream = object {}.javaClass.getResourceAsStream(OB_TPP_EIDAS_TRANSPORT_PEM_PATH)
    initFuel(privatePemStream, certificatePemStream)
}
