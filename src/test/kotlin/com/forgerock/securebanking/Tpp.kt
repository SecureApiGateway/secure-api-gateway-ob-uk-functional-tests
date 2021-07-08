package com.forgerock.securebanking

import com.forgerock.securebanking.directory.SoftwareStatement
import com.forgerock.securebanking.directory.UserRegistrationRequest
import com.forgerock.securebanking.discovery.asDiscovery
import com.forgerock.securebanking.onboarding.RegistrationRequest
import com.forgerock.securebanking.onboarding.RegistrationResponse
import com.forgerock.securebanking.onboarding.registerTpp
import com.forgerock.securebanking.onboarding.unregisterTpp
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.isSuccessful
import com.github.kittinunf.result.Result
import com.google.gson.GsonBuilder
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm

data class Tpp(
    val sessionToken: String, val directoryUser: UserRegistrationRequest,
    val softwareStatement: SoftwareStatement, val privateCert: String,
    val publicCert: String, val signingKid: String, val signingKey: String
) {

    lateinit var registrationResponse: RegistrationResponse

    fun generateSsa(): String {
        val (_, response, result) = Fuel.post("https://matls.service.directory.$DOMAIN/api/software-statement/current/ssa")
            .responseString()
        if (!response.isSuccessful) throw AssertionError("Could not get ssa", result.component2())
        return result.get()
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
        return unregisterTpp(registrationResponse.registration_access_token)
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
}
