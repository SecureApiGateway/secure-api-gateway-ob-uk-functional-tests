package com.forgerock.securebanking.framework.data

import com.forgerock.securebanking.framework.configuration.OB_TPP_EIDAS_SIGNING_KEY_PATH
import com.forgerock.securebanking.framework.configuration.OB_TPP_OB_EIDAS_TEST_SIGNING_KID
import com.forgerock.securebanking.framework.http.fuel.responseObject
import com.forgerock.securebanking.framework.signature.signPayload
import com.forgerock.securebanking.framework.utils.GsonUtils
import com.forgerock.securebanking.framework.utils.FileUtils
import com.forgerock.uk.openbanking.support.discovery.asDiscovery
import com.forgerock.uk.openbanking.support.getRsaPrivateKey
import com.forgerock.uk.openbanking.support.registration.registerTpp
import com.forgerock.uk.openbanking.support.registration.unregisterTpp
import com.forgerock.uk.openbanking.framework.accesstoken.model.AccessTokenRequest
import com.forgerock.uk.openbanking.framework.accesstoken.model.AccessTokenResponse
import com.forgerock.uk.openbanking.framework.accesstoken.model.ClaimsTest
import com.forgerock.uk.openbanking.framework.configuration.OB_ORGANISATION_ID
import com.forgerock.uk.openbanking.framework.configuration.OB_SOFTWARE_ID
import com.forgerock.uk.openbanking.framework.configuration.SSA_MATLS_URL_SANDBOX
import com.forgerock.uk.openbanking.framework.configuration.TOKEN_URL_SANDBOX
import com.forgerock.securebanking.framework.configuration.REDIRECT_URI
import com.forgerock.uk.openbanking.support.general.GeneralAS.Companion
import com.forgerock.uk.openbanking.support.general.GeneralAS.GrantTypes
import com.forgerock.uk.openbanking.support.payment.PaymentApiClient
import com.forgerock.uk.openbanking.support.registration.UserRegistrationRequest
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.core.isSuccessful
import com.github.kittinunf.result.Result
import com.nimbusds.jose.JOSEObjectType
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.Payload
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.io.File
import java.util.*


data class Tpp(
    val sessionToken: String, val directoryUser: UserRegistrationRequest,
    val softwareStatement: SoftwareStatement, val privateCert: String,
    val publicCert: String, val signingKid: String, val signingKeyPath: String
) {

    var signingKey = getRsaPrivateKey(signingKeyPath)!!
    var paymentApiClient = PaymentApiClient(this)
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

    private fun getSSA(accessToken: String): String {
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
            iss = OB_SOFTWARE_ID
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
        return Jwts.builder()
            .setHeaderParam("kid", signingKid)
            .setPayload(GsonUtils.gson.toJson(registrationRequest))
            .signWith(signingKey, SignatureAlgorithm.forName(asDiscovery.request_object_signing_alg_values_supported[0]))
            .compact()
    }

    private fun register(signedRegistrationRequest: String): RegistrationResponse {
        return registerTpp(signedRegistrationRequest)
    }

    private fun getJWS(): SignedJWT? {
        if (File(OB_TPP_EIDAS_SIGNING_KEY_PATH).exists()) {
            val detachedPayload = Payload(GsonUtils.gson.toJson(ClaimsTest()))

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
                        fileReader = FileUtils().getFileReader(OB_TPP_EIDAS_SIGNING_KEY_PATH)
                    )
                )
            )

            return signedJWT
        } else {
            return null
        }
    }

    // TODO: Refactor acquireAccessToken and getClientCredentialsAccessToken into one reusable method for getting access tokens
    private fun acquireAccessToken(jws: SignedJWT): AccessTokenResponse {
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

    fun getClientCredentialsAccessToken(scopes: String): AccessToken {
        val requestParameters = ClientCredentialData(
            sub = registrationResponse.client_id,
            iss = registrationResponse.client_id,
            aud = asDiscovery.issuer
        )
        val signedPayload = signPayload(requestParameters, signingKey, signingKid)
        val body = listOf(
            "grant_type" to GrantTypes.CLIENT_CREDENTIALS,
            "redirect_uri" to REDIRECT_URI,
            "client_assertion_type" to Companion.CLIENT_ASSERTION_TYPE,
            "scope" to scopes,
            "client_assertion" to signedPayload
        )
        val (_, accessTokenResponse, result) = Fuel.post(asDiscovery.token_endpoint, parameters = body)
            .authentication()
            .basic(registrationResponse.client_id, registrationResponse.client_secret!!)
            .responseObject<AccessToken>()
        if (!accessTokenResponse.isSuccessful) throw AssertionError(
            "Could not get access token: \n" + result.component2()?.errorData?.toString(
                Charsets.UTF_8
            ), result.component2()
        )
        return result.get()
    }
}
