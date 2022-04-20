package com.forgerock.securebanking.tests.functional.deprecated.directory

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.forgerock.securebanking.framework.configuration.PLATFORM_SERVER
import com.forgerock.securebanking.framework.http.fuel.initFuel
import com.forgerock.securebanking.support.registerDirectoryUser
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import org.junit.jupiter.api.Test

data class UserRegistrationRequest(val user: User) {
    constructor(userName: String, password: String, uid: String) : this(User(userName, password, uid))
    constructor(userName: String, password: String) : this(User(userName, password,null))
}

data class User(val userName: String, val password: String, var uid: String?)

class DirectoryRegistrationTest {

    @Test
    fun shouldAuthenticateWithNewUser() {
        // Given
        val directoryUser = registerDirectoryUser()

        // When
        FuelManager.instance.reset()  // Force non mtls
        initFuel()
        val (_, response, _) = Fuel.post(
            "$PLATFORM_SERVER/am/XUI/?realm=root&authIndexType=service&authIndexValue=Login",
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
