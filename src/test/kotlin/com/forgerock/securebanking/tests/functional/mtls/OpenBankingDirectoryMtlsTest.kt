package com.forgerock.securebanking.tests.functional.mtls

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.containsAll
import assertk.assertions.isEqualTo
import com.forgerock.securebanking.framework.constants.OB_SOFTWARE_ID
import com.forgerock.securebanking.framework.constants.OB_TPP_PRE_EIDAS_TRANSPORT_KEY_PATH
import com.forgerock.securebanking.framework.constants.OB_TPP_PRE_EIDAS_TRANSPORT_PEM_PATH
import com.forgerock.securebanking.framework.data.RegistrationResponse
import com.forgerock.securebanking.framework.http.fuel.initFuel
import com.forgerock.securebanking.support.discovery.asDiscovery
import com.forgerock.securebanking.support.registration.signRegistrationRequest
import com.forgerock.securebanking.support.registration.unregisterTpp
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.jackson.responseObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class OpenBankingDirectoryMtlsTest {

    @BeforeEach
    fun setup() {
        initFuel(privatePem = OB_TPP_PRE_EIDAS_TRANSPORT_KEY_PATH, publicPem = OB_TPP_PRE_EIDAS_TRANSPORT_PEM_PATH)
    }

    @Test
    fun shouldBeAnUnregisteredTppWhenAsApiMtlsCheck() {
        // When
        val (_, response, result) = Fuel.get("https://matls.as.aspsp.DOMAIN/open-banking/mtlsTest")
            .responseObject<MtlsResponse>()

        // Then
        assertThat(response.statusCode).isEqualTo(200)
        assertThat(result.get().issuerId).isEqualTo(OB_SOFTWARE_ID)
        assertThat(result.get().authorities).contains("UNREGISTERED_TPP")
    }

    @Test
    fun shouldBeAnUnregisteredTppWhenRsApiMtlsCheck() {
        // When
        val (_, response, result) = Fuel.get("https://matls.rs.aspsp.DOMAIN/open-banking/mtlsTest")
            .responseObject<MtlsResponse>()

        // Then
        assertThat(response.statusCode).isEqualTo(200)
        assertThat(result.get().issuerId).isEqualTo(OB_SOFTWARE_ID)
        assertThat(result.get().authorities).contains("UNREGISTERED_TPP")
    }

    @Test
    fun shouldBeAnRegisteredTppWhenAsApiMtlsCheck() {
        // Given
        val (signedRegistrationRequest, _) = signRegistrationRequest()
        val (_, _, registrationResult) = Fuel.post(asDiscovery.registration_endpoint!!)
            .body(signedRegistrationRequest)
            .header(Headers.CONTENT_TYPE, "application/jwt")
            .responseObject<RegistrationResponse>()

        // When
        val (_, response, result) = Fuel.get("https://matls.as.aspsp.DOMAIN/open-banking/mtlsTest")
            .responseObject<MtlsResponse>()

        // Then
        try {
            assertThat(response.statusCode).isEqualTo(200)
            assertThat(result.get().issuerId).isEqualTo(registrationResult.get().client_id)
            assertThat(result.get().authorities).containsAll("ROLE_PISP", "ROLE_AISP")
        } finally {
            unregisterTpp(registrationResult.get().registration_access_token)
        }
    }

    @Test
    fun shouldBeAnRegisteredTppWhenRsApiMtlsCheck() {
        // Given
        val (signedRegistrationRequest, _) = signRegistrationRequest()
        val (_, _, registrationResult) = Fuel.post(asDiscovery.registration_endpoint!!)
            .body(signedRegistrationRequest)
            .header(Headers.CONTENT_TYPE, "application/jwt")
            .responseObject<RegistrationResponse>()

        // When
        try {
            val (_, response, result) = Fuel.get("https://matls.rs.aspsp.DOMAIN/open-banking/mtlsTest")
                .responseObject<MtlsResponse>()

            // Then
            assertThat(response.statusCode).isEqualTo(200)
            assertThat(result.get().issuerId).isEqualTo(registrationResult.get().client_id)
            assertThat(result.get().authorities).containsAll("ROLE_PISP", "ROLE_AISP")
        } finally {
            unregisterTpp(registrationResult.get().registration_access_token)
        }
    }
}
