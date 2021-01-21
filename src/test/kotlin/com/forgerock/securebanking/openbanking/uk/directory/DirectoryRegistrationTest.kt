package com.forgerock.openbanking.directory

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.forgerock.openbanking.DOMAIN
import com.forgerock.openbanking.initFuel
import com.forgerock.openbanking.registerDirectoryUser
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.gson.jsonBody
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import java.util.*

data class UserRegistrationRequest(val input: Input) {
    constructor(username: String, userPassword: String) : this(Input(User(username, userPassword)))
}
data class Input(val user: User)
data class User(val username: String, val userPassword: String)

@Tags(Tag("directoryTest"))
class DirectoryRegistrationTest {

    @Test
    fun shouldRegisterNewUser() {
        // Given
        val directoryUser = UserRegistrationRequest("fortest_" + UUID.randomUUID(), "password")

        // When
        val (_, response, _) = Fuel.post("https://as.aspsp.$DOMAIN/json/realms/root/realms/auth/selfservice/userRegistration?_action=submitRequirements")
                .header("Accept-API-Version", "protocol=1.0,resource=1.0")
                .jsonBody(directoryUser)
                .responseString()

        // Then
        assertThat(response.statusCode).isEqualTo(200)
    }

    @Test
    fun shouldAuthenticateWithNewUser() {
        // Given
        val directoryUser = registerDirectoryUser()

        // When
        FuelManager.instance.reset()  // Force non mtls
        initFuel()
        val (_, response, _) = Fuel.post("https://service.directory.$DOMAIN/api/user/authenticate", listOf(Pair("username", directoryUser.input.user.username), Pair("password", directoryUser.input.user.userPassword)))
                .responseString()

        // Then
        assertThat(response.statusCode).isEqualTo(200)
    }

}