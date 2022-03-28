package com.forgerock.securebanking.tests.functional.bank

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.forgerock.securebanking.framework.http.fuel.initFuel
import com.forgerock.securebanking.support.authenticatePSU
import com.forgerock.securebanking.support.checkSession
import com.forgerock.securebanking.support.registerPSU
import com.forgerock.securebanking.tests.functional.directory.UserRegistrationRequest
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
        val psu = UserRegistrationRequest("fortest_" + UUID.randomUUID(), "Password@1")

        // When
        val (_, response, _) = Fuel.post("https://as.aspsp.DOMAIN/json/realms/root/realms/openbanking/selfservice/userRegistration?_action=submitRequirements")
            .header("Accept-API-Version", "resource=1.0, protocol=1.0")
            .header("Content-Type", "application/json")
            .jsonBody(psu)
            .responseString()

        // Then
        assertThat(response.statusCode).isEqualTo(200)
    }

    @Test
    fun shouldAuthenticateWithNewUser() {
        // Given
        val psu = registerPSU()
        val ssoCode = authenticatePSU("gotoUrl", psu.user.userName, psu.user.password)

        // When
        val statusCode = checkSession(ssoCode)

        // Then
        assertThat(statusCode).isEqualTo(200)
    }

}
