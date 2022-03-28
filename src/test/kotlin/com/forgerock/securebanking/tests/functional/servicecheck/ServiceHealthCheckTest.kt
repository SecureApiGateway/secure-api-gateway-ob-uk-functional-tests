package com.forgerock.securebanking.tests.functional.servicecheck

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.forgerock.securebanking.framework.http.fuel.initFuel
import com.github.kittinunf.fuel.Fuel
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag("servicesCheck")
class ServiceHealthCheckTest {

    @BeforeEach
    internal fun setUp() {
        initFuel()
    }

    @Test
    fun getJWKMSState() {
        // When
        val (_, response, result) = Fuel.get("https://jwkms.DOMAIN/external/actuator/health").responseString()
        // Then
        assertThat(response.statusCode).isEqualTo(200)
        assertThat(result.get()).isNotNull()
        assertThat(result.get()).contains("UP")
    }

    @Test
    fun getDirectoryState() {
        // When
        val (_, response, result) = Fuel.get("https://matls.service.directory.DOMAIN/external/actuator/health")
            .responseString()
        // Then
        assertThat(response.statusCode).isEqualTo(200)
        assertThat(result.get()).isNotNull()
        assertThat(result.get()).contains("UP")
    }

    @Test
    fun getAMState() {
        // When
        val (_, response, result) = Fuel.get("https://as.aspsp.DOMAIN/isAlive.jsp").responseString()
        // Then
        assertThat(response.statusCode).isEqualTo(200)
        assertThat(result.get()).isNotNull()
        assertThat(result.get()).contains("Server is ALIVE")
    }

    @Test
    fun getASState() {
        // When
        val (_, response, result) = Fuel.get("https://as.aspsp.DOMAIN/external/actuator/health").responseString()
        // Then
        assertThat(response.statusCode).isEqualTo(200)
        assertThat(result.get()).isNotNull()
        assertThat(result.get()).contains("UP")
    }

    @Test
    fun getRSState() {
        // When
        val (_, response, result) = Fuel.get("https://rs.aspsp.DOMAIN/external/actuator/health").responseString()
        // Then
        assertThat(response.statusCode).isEqualTo(200)
        assertThat(result.get()).isNotNull()
        assertThat(result.get()).contains("UP")
    }

    @Test
    fun getRCSState() {
        // When
        val (_, response, result) = Fuel.get("https://rcs.aspsp.DOMAIN/external/actuator/health").responseString()
        // Then
        assertThat(response.statusCode).isEqualTo(200)
        assertThat(result.get()).isNotNull()
        assertThat(result.get()).contains("UP")
    }

    @Test
    fun getMonitoringState() {
        // When
        val (_, response, result) = Fuel.get("https://monitoring.DOMAIN/external/actuator/health").responseString()
        // Then
        assertThat(response.statusCode).isEqualTo(200)
        assertThat(result.get()).isNotNull()
        assertThat(result.get()).contains("UP")
    }

    @Test
    fun getBankState() {
        // When
        val (_, response, result) = Fuel.get("https://matls.service.bank.DOMAIN/external/actuator/health")
            .responseString()
        // Then
        assertThat(response.statusCode).isEqualTo(200)
        assertThat(result.get()).isNotNull()
        assertThat(result.get()).contains("UP")
    }
}
