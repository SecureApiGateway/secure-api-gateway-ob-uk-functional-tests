package com.forgerock.securebanking.tests.functional.directory

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.forgerock.securebanking.framework.configuration.DOMAIN
import com.forgerock.securebanking.framework.http.fuel.initFuel
import com.forgerock.securebanking.support.registerDirectoryUser
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.gson.jsonBody
import com.google.gson.Gson
import com.nimbusds.jose.shaded.json.JSONObject
import org.junit.jupiter.api.Test
import java.util.*

data class UserRegistrationRequest(val user: User) {
    constructor(userName: String, password: String, givenName: String, sn: String, mail: String) : this(User(userName, password, givenName, sn, mail))
}

data class User(val userName: String, val password: String, val givenName: String, val sn: String, val mail: String)

class DirectoryRegistrationTest {

    //TODO
    @Test
    fun shouldRegisterNewUser() {
        // Given
        val directoryUser = UserRegistrationRequest("fortest_" + UUID.randomUUID(), "Password@1", "givenName", "sn", "mail@forgerock.com")

        // When
        val (_, response, _) = Fuel.post("https://as.aspsp.$DOMAIN/json/realms/root/realms/auth/selfservice/userRegistration?_action=submitRequirements")
            .header("Accept-API-Version", "protocol=1.0,resource=1.0")
            .jsonBody(directoryUser)
            .responseString()

        // Then
        assertThat(response.statusCode).isEqualTo(200)
    }

    //TODO
    @Test
    fun shouldAuthenticateWithNewUser() {
        // Given
        val directoryUser = registerDirectoryUser()

        // When
        FuelManager.instance.reset()  // Force non mtls
        initFuel()
        val (_, response, _) = Fuel.post(
            "https://service.directory.$DOMAIN/api/user/authenticate",
            listOf(
                Pair("username", directoryUser.user.userName),
                Pair("password", directoryUser.user.password)
            )
        )
            .responseString()

        // Then
        assertThat(response.statusCode).isEqualTo(200)
    }

}
