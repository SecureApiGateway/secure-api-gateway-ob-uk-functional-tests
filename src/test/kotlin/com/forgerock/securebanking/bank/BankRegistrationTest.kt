package com.forgerock.securebanking.bank

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.forgerock.securebanking.*
import com.forgerock.securebanking.directory.UserRegistrationRequest
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.gson.jsonBody
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class BankRegistrationTest {

    @BeforeEach
    fun setup() {
        initFuel()
    }

    @Test
    fun shouldRegisterNewUser() {
        // Given
        val psu = UserRegistrationRequest("fortest_" + UUID.randomUUID(), "password")

        // When
        val (_, response, _) = Fuel.post("https://as.aspsp.$DOMAIN/json/realms/root/realms/openbanking/selfservice/userRegistration?_action=submitRequirements")
            .header("Accept-API-Version", "protocol=1.0,resource=1.0")
            .jsonBody(psu)
            .responseString()

        // Then
        assertThat(response.statusCode).isEqualTo(200)
    }

    @Test
    fun shouldAuthenticateWithNewUser() {
        // Given
        val psu = registerPSU()
        val ssoCode = authenticatePSU("gotoUrl", psu.input.user.username, psu.input.user.userPassword)

        // When
        val statusCode = checkSession(ssoCode)

        // Then
        assertThat(statusCode).isEqualTo(200)
    }

}
