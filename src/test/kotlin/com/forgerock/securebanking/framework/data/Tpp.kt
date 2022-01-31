package com.forgerock.securebanking.framework.data

import com.forgerock.securebanking.framework.http.fuel.responseObject
import com.forgerock.securebanking.support.discovery.asDiscovery
import com.forgerock.securebanking.support.loadRsaPrivateKey
import com.forgerock.securebanking.support.registration.registerTpp
import com.forgerock.securebanking.support.registration.unregisterTpp
import com.forgerock.securebanking.tests.functional.directory.UserRegistrationRequest
import com.forgerock.uk.openbanking.framework.accesstoken.constants.*
import com.forgerock.uk.openbanking.framework.accesstoken.model.AccessTokenRequest
import com.forgerock.uk.openbanking.framework.accesstoken.model.AccessTokenResponse
import com.forgerock.uk.openbanking.framework.accesstoken.model.ClaimsTest
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.isSuccessful
import com.github.kittinunf.result.Result
import com.google.gson.GsonBuilder
import com.nimbusds.jose.JOSEObjectType
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.Payload
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.io.FileReader
import java.util.*


data class Tpp(
    val sessionToken: String, val directoryUser: UserRegistrationRequest,
    val softwareStatement: SoftwareStatement, val privateCert: String,
    val publicCert: String, val signingKid: String, val signingKey: String
) {

    lateinit var registrationResponse: RegistrationResponse
    lateinit var accessToken: String

    fun generateSsa(): String {
        val jws = getJWS()
        if (jws != null) {
            println(jws.serialize())
            val accessToken: AccessTokenResponse =
                acquireAccessToken(jws)
            println(accessToken)
            val ssa = getSSA(accessToken.access_token)
            println(ssa)
            return ssa
        } else {
            throw AssertionError("Error requesting the signed JWS")
        }
    }

    fun getSSA(accessToken: String): String {
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


    fun dynamicRegistration(
        registrationRequest: RegistrationRequest = RegistrationRequest(
            software_statement = generateSsa(),
            iss = softwareStatement.id
        )
    ): RegistrationResponse {
        val signed = signRegistrationRequest(registrationRequest)
        this.registrationResponse = register(signed)
        return registrationResponse
    }


    fun unregister(): Triple<Request, Response, Result<String, FuelError>> {
        return unregisterTpp(registrationResponse.client_id)
    }

    private fun signRegistrationRequest(registrationRequest: RegistrationRequest): String {
        val key = loadRsaPrivateKey(this.signingKey)
        return Jwts.builder()
            .setHeaderParam("kid", signingKid)
            .setPayload(GsonBuilder().create().toJson(registrationRequest))
            .signWith(key, SignatureAlgorithm.forName(asDiscovery.request_object_signing_alg_values_supported[0]))
            .compact()
    }

    private fun register(signedRegistrationRequest: String): RegistrationResponse {
        return registerTpp(signedRegistrationRequest)
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

            signedJWT.sign(
                RSASSASigner(
                    com.forgerock.securebanking.framework.cert.loadRsaPrivateKey(
                        fileReader = FileReader(
                            signingKeyResource.file
                        )
                    )
                )
            )

            return signedJWT
        } else {
            return null
        }
    }

    fun acquireAccessToken(jws: SignedJWT): AccessTokenResponse {
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
            "Could not get requested access token data from $TOKEN_URL_SANDBOX: ${
                String(
                    certResult.data
                )
            }", r.component2()
        )
        return r.get()
    }
}
