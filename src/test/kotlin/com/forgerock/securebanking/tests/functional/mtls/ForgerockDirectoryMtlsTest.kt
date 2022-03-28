package com.forgerock.securebanking.tests.functional.mtls

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.containsAll
import assertk.assertions.isEqualTo
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.http.fuel.initFuelAsNewTpp
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.jackson.responseObject
import org.junit.jupiter.api.Test

data class MtlsResponse(
    val authorities: List<String>,
    val issuerId: String
)

class ForgerockDirectoryMtlsTest(val tppResource: CreateTppCallback.TppResource) {

    @Test
    fun shouldBeAnUnregisteredTppWhenAsApiMtlsCheck() {
        // Given
        val tpp = initFuelAsNewTpp()

        // When
        val (_, response, result) = Fuel.get("https://matls.as.aspsp.DOMAIN/open-banking/mtlsTest")
            .responseObject<MtlsResponse>()

        // Then
        assertThat(response.statusCode).isEqualTo(200)
        assertThat(result.get().issuerId).isEqualTo(tpp.softwareStatement.id)
        assertThat(result.get().authorities).contains("UNREGISTERED_TPP")
    }

    @Test
    fun shouldBeAnUnregisteredTppWhenRsApiMtlsCheck() {
        // Given
        val tpp = initFuelAsNewTpp()

        // When
        val (_, response, result) = Fuel.get("https://matls.rs.aspsp.DOMAIN/open-banking/mtlsTest")
            .responseObject<MtlsResponse>()

        // Then
        assertThat(response.statusCode).isEqualTo(200)
        assertThat(result.get().issuerId).isEqualTo(tpp.softwareStatement.id)
        assertThat(result.get().authorities).contains("UNREGISTERED_TPP")
    }

    @Test
    fun shouldBeAnRegisteredTppWhenAsApiMtlsCheck() {
        // Given already registered

        // When
        val (_, response, result) = Fuel.get("https://matls.as.aspsp.DOMAIN/open-banking/mtlsTest")
            .responseObject<MtlsResponse>()

        // Then
        assertThat(response.statusCode).isEqualTo(200)
        assertThat(result.get().issuerId).isEqualTo(tppResource.tpp.registrationResponse.client_id)
        assertThat(result.get().authorities).containsAll("ROLE_PISP", "ROLE_CBPII", "ROLE_AISP", "ROLE_DATA")
    }

    @Test
    fun shouldBeAnRegisteredTppWhenRsApiMtlsCheck() {
        // Given already registered

        // When
        val (_, response, result) = Fuel.get("https://matls.rs.aspsp.DOMAIN/open-banking/mtlsTest")
            .responseObject<MtlsResponse>()

        // Then
        assertThat(response.statusCode).isEqualTo(200)
        assertThat(result.get().issuerId).isEqualTo(tppResource.tpp.registrationResponse.client_id)
        assertThat(result.get().authorities).containsAll("ROLE_PISP", "ROLE_CBPII", "ROLE_AISP", "ROLE_DATA")
    }

    @Test
    fun shouldHaveRoleSoftwareStatementWhenFRDirectoryMtlsCheck() {
        // When
        val (_, response, result) = Fuel.get("https://matls.service.directory.DOMAIN/api/matls/test")
            .responseObject<MtlsResponse>()

        // Then
        assertThat(response.statusCode).isEqualTo(200)
        assertThat(result.get().issuerId).isEqualTo(tppResource.tpp.softwareStatement.id)
        assertThat(result.get().authorities).contains("ROLE_SOFTWARE_STATEMENT")
    }
}
