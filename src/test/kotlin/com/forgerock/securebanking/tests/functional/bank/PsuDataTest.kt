package com.forgerock.securebanking.tests.functional.bank

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isIn
import assertk.assertions.isNotNull
import com.forgerock.securebanking.framework.configuration.DOMAIN
import com.forgerock.securebanking.support.authenticatePSU
import com.forgerock.securebanking.support.checkSession
import com.forgerock.securebanking.support.registerPSU
import com.github.kittinunf.fuel.Fuel
import org.junit.jupiter.api.Test

class PsuDataTest {

    @Test
    fun updatePsuData() {
        // Given
        val psu = registerPSU()
        val ssoCode = authenticatePSU("gotoUrl", psu.user.userName, psu.user.password)
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
