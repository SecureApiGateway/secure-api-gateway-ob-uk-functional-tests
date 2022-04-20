package com.forgerock.securebanking.tests.functional.deprecated.registration


import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.forgerock.securebanking.framework.data.RegistrationRequest
import com.forgerock.securebanking.framework.data.RegistrationResponse
import com.forgerock.securebanking.framework.data.Tpp
import com.forgerock.securebanking.framework.http.fuel.initFuelAsNewTpp
import com.forgerock.securebanking.framework.http.fuel.responseObject
import com.forgerock.securebanking.framework.utils.GsonUtils
import com.forgerock.securebanking.support.discovery.asDiscovery
import com.forgerock.securebanking.support.loadRsaPrivateKey
import com.forgerock.securebanking.support.registration.unregisterTpp
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Headers.Companion.CONTENT_TYPE
import com.github.kittinunf.fuel.core.isSuccessful
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ForgerockDirectoryDynamicRegistrationTest {

    private lateinit var tpp: Tpp

    @BeforeEach
    fun setup() {
        this.tpp = initFuelAsNewTpp()
    }

    @Test
    fun shouldUnregisterWhenUsingForgerockTransportKeys() {
        // Given
        val (signedRegistrationRequest, _) = signRegistrationRequest()
        val registerResponse = register(signedRegistrationRequest)

        // When
        val (_, response, _) = unregisterTpp(registerResponse.registration_access_token)

        // Then
        assertThat(response.statusCode).isEqualTo(200)
    }

    @Test
    fun shouldRegisterWhenUsingForgerockTransportKeys() {
        // Given
        val (signedRegistrationRequest, registrationRequest) = signRegistrationRequest()

        // When
        val (_, response, result) = Fuel.post(asDiscovery.registration_endpoint!!)
            .body(signedRegistrationRequest)
            .header(CONTENT_TYPE, "application/jwt")
            .responseObject<RegistrationResponse>()

        // Then
        assertThat(response.statusCode).isEqualTo(201)
        assertThat(result.get().client_id).isNotNull()
        assertThat(result.get().redirect_uris).containsExactly(*registrationRequest.redirect_uris.toTypedArray())
        unregisterTpp(result.get().registration_access_token)
    }

    fun register(signedRegistrationRequest: String): RegistrationResponse {
        val (_, response, result) = Fuel.post(asDiscovery.registration_endpoint!!)
            .body(signedRegistrationRequest)
            .header(CONTENT_TYPE, "application/jwt")
            .responseObject<RegistrationResponse>()
        if (!response.isSuccessful) throw AssertionError("Could not register", result.component2())
        return result.get()
    }

    private fun signRegistrationRequest(): Pair<String, RegistrationRequest> {
        val registrationRequest =
            RegistrationRequest(software_statement = tpp.generateSsa(), iss = tpp.softwareStatement.id)
        val key = loadRsaPrivateKey(tpp.signingKey)
        val signedRegistrationRequest = Jwts.builder()
            .setHeaderParam("kid", tpp.signingKid)
            .setPayload(GsonUtils.gson.toJson(registrationRequest))
            .signWith(key, SignatureAlgorithm.PS256)
            .compact()
        return Pair(signedRegistrationRequest, registrationRequest)
    }
}
