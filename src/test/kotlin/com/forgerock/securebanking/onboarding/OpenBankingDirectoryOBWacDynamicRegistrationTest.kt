package com.forgerock.securebanking.onboarding

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.forgerock.securebanking.OB_TPP_EIDAS_TRANSPORT_KEY_PATH
import com.forgerock.securebanking.OB_TPP_EIDAS_TRANSPORT_PEM_PATH
import com.forgerock.securebanking.initFuel
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class OpenBankingDirectoryOBWacDynamicRegistrationTest {

    @BeforeEach
    fun setup() {
        initFuel(privatePem = OB_TPP_EIDAS_TRANSPORT_KEY_PATH, publicPem = OB_TPP_EIDAS_TRANSPORT_PEM_PATH)
    }

    @Test
    fun shouldUnregisterWhenUsingOpenBankingTransportKeys() {
        // Given
        val (signedRegistrationRequest, _) = signOBEidasRegistrationRequest()
        val registrationResponse = registerTpp(signedRegistrationRequest)

        try {
            // When
            val (_, response, _) = unregisterTpp(registrationResponse.registration_access_token)

            // Then
            assertThat(response.statusCode).isEqualTo(200)
        } finally {
            unregisterTpp(registrationResponse.registration_access_token)
        }
    }

    @Test
    fun shouldRegisterWhenUsingOpenBankingTransportKeys() {
        // Given
        val (signedRegistrationRequest, registrationRequest) = signOBEidasRegistrationRequest()

        // When
        val registrationResponse = registerTpp(signedRegistrationRequest)

        // Then
        try {
            assertThat(registrationResponse.client_id).isNotNull()
            assertThat(registrationResponse.redirect_uris).containsExactly(*registrationRequest.redirect_uris.toTypedArray())
        } finally {
            unregisterTpp(registrationResponse.registration_access_token)
        }
    }
}
