package com.forgerock.openbanking.bank

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isIn
import assertk.assertions.isNotNull
import com.forgerock.openbanking.DOMAIN
import com.forgerock.openbanking.authenticatePSU
import com.forgerock.openbanking.checkSession
import com.forgerock.openbanking.registerPSU
import com.github.kittinunf.fuel.Fuel
import org.junit.jupiter.api.Test

class PsuDataTest {

    @Test
    fun updatePsuData() {
        // Given
        val psu = registerPSU()
        val ssoCode = authenticatePSU("gotoUrl", psu.input.user.username, psu.input.user.userPassword)
        val statusCode = checkSession(ssoCode)
        assertThat(statusCode).isEqualTo(200)
        // When
        val (_, dataResponse, dataResult) = Fuel.post("https://matls.service.bank.$DOMAIN/api/data/user/generate")
                .header("Cookie", "obri-session=${ssoCode.tokenId}")
                .responseString()

        // Then
        assertThat(dataResponse.statusCode).isIn(201, 200)
        assertThat(dataResult.get()).isNotNull()
    }
}