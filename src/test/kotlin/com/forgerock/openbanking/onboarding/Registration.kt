package com.forgerock.openbanking.onboarding

import com.forgerock.openbanking.*
import com.forgerock.openbanking.discovery.asDiscovery
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.jackson.responseObject
import com.github.kittinunf.result.Result
import com.google.gson.GsonBuilder
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.nio.file.Files
import java.nio.file.Paths

fun unregisterTpp(accessToken: String): Triple<Request, Response, Result<String, FuelError>> {
    return Fuel.delete(asDiscovery.registrationEndpoint)
            .header("Authorization", "Bearer $accessToken")
            .responseObject()
}

fun registerTpp(signedRegistrationRequest: String): RegistrationResponse {
    val (_, response, result) = Fuel.post(asDiscovery.registrationEndpoint)
            .body(signedRegistrationRequest)
            .header(Headers.CONTENT_TYPE, "application/jwt")
            .responseObject<RegistrationResponse>()
    if (response.statusCode!=201){
        val xFapiInteractionId = response.headers.get("x-fapi-interaction-id")
        throw AssertionError("Registration failed with ${response.statusCode} : ${String
            (response.data)} x-fapi-interaction-id: ${xFapiInteractionId}", result.component2())
    }
    return result.get()
}

fun signRegistrationRequest(): Pair<String, RegistrationRequest> {
    val ssa = Files.readString(Paths.get(object {}.javaClass.getResource(OB_TPP_PRE_EIDAS_SSA_PATH).toURI()))
    val registrationRequest = RegistrationRequest(software_statement = ssa)
    val privateKey = Files.readString(Paths.get(object {}.javaClass.getResource(OB_TPP_PRE_EIDAS_SIGNING_KEY).toURI()))
    val key = loadRsaPrivateKey(privateKey)
    val signedRegistrationRequest = Jwts.builder()
            .setHeaderParam("kid", OB_TPP_PRE_EIDAS_SIGNING_KID)
            .setPayload(GsonBuilder().create().toJson(registrationRequest))
            .signWith(key, SignatureAlgorithm.forName(asDiscovery.request_object_signing_alg_values_supported[0]))
            .compact()
    return Pair(signedRegistrationRequest, registrationRequest)
}

fun signOBEidasRegistrationRequest(): Pair<String, RegistrationRequest> {
    val ssa = Files.readString(Paths.get(object {}.javaClass.getResource(OB_TPP_EIDAS_SSA_PATH).toURI()))
    val registrationRequest = RegistrationRequest(software_statement = ssa)
    val privateKey = Files.readString(Paths.get(object {}.javaClass.getResource(OB_TPP_EIDAS_SIGNING_KEY).toURI()))
    val key = loadRsaPrivateKey(privateKey)
    val signedRegistrationRequest = Jwts.builder()
        .setHeaderParam("kid", OB_TPP_OB_EIDAS_TEST_SIGNING_KID)
        .setPayload(GsonBuilder().create().toJson(registrationRequest))
        .signWith(key, SignatureAlgorithm.forName(asDiscovery.request_object_signing_alg_values_supported[0]))
        .compact()
    return Pair(signedRegistrationRequest, registrationRequest)
}
