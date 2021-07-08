package com.forgerock.securebanking.event.aggregatedpolling

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import com.forgerock.openbanking.common.model.version.OBVersion.v3_1_2
import com.forgerock.openbanking.common.model.version.OBVersion.v3_1_6
import com.forgerock.openbanking.constants.OpenBankingConstants
import com.forgerock.securebanking.discovery.asDiscovery
import com.forgerock.securebanking.event.Events
import com.forgerock.securebanking.initFuelAsNewTpp
import com.forgerock.securebanking.junit.CreateTppCallback
import com.forgerock.securebanking.junit.EnabledIfVersion
import com.github.kittinunf.fuel.core.FuelError
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test

/**
 * Initial Polling:
 *
 * https://openbanking.atlassian.net/wiki/spaces/DZ/pages/1077806765/Aggregated+Polling+-+v3.1.2
 */
@Tags(Tag("eventTest"))
class PollAggregatedPollingTest(val tppResource: CreateTppCallback.TppResource) {

    private val events = Events()

    @EnabledIfVersion(
        type = "events",
        apiVersion = "v3.1.2",
        operations = ["EventAggregatedPolling"],
        apis = ["events"]
    )
    @Test
    @JvmName("shouldAcceptTppPollingRequest_v3_1_2")
    fun acceptPolling_v3_1_2() {
        // Given
        val version = v3_1_2
        val scopes = asDiscovery.scopes_supported.intersect(
            listOf(
                OpenBankingConstants.Scope.OPENID,
                OpenBankingConstants.Scope.EVENT_POLLING
            )
        ).joinToString(separator = " ")
        val clientCredentials = events.clientCredentialsAuthentication(tppResource.tpp, scopes)
        val dataEvents = events.importEvents(tppResource.tpp, clientCredentials, version)
        assertThat(dataEvents.isNotEmpty())

        // When
        val pollResponse = events.pollForEvents(tppResource.tpp, clientCredentials, version)

        // Then
        assertThat(pollResponse.sets).isNotEmpty()
    }

    @EnabledIfVersion(
        type = "events",
        apiVersion = "v3.1.2",
        operations = ["EventAggregatedPolling"],
        apis = ["events"]
    )
    @Test
    @JvmName("shouldAcceptTppPollingRequest_EmptyResponse_v3_1_2")
    fun acceptPolling_EmptyResponse_v3_1_2() {
        // Given
        val tpp = initFuelAsNewTpp().apply { dynamicRegistration() }
        val version = v3_1_2
        val scopes = asDiscovery.scopes_supported.intersect(
            listOf(
                OpenBankingConstants.Scope.OPENID,
                OpenBankingConstants.Scope.EVENT_POLLING
            )
        ).joinToString(separator = " ")
        val clientCredentials = events.clientCredentialsAuthentication(tpp, scopes)

        // When
        val pollResponse = events.pollForEvents(tppResource.tpp, clientCredentials, version)

        // Then
        assertThat(pollResponse.sets).isEmpty()
    }

    @EnabledIfVersion(
        type = "events",
        apiVersion = "v3.1.2",
        operations = ["EventAggregatedPolling"],
        apis = ["events"]
    )
    @Test
    @JvmName("shouldThrowsErrorTppPollingRequest_v3_1_2_wrongScope")
    fun rejectPolling_v3_1_2_wrongScope() {
        // Given
        val tpp = initFuelAsNewTpp().apply { dynamicRegistration() }
        val version = v3_1_2
        val scopes = asDiscovery.scopes_supported.intersect(
            listOf(
                OpenBankingConstants.Scope.OPENID,
                OpenBankingConstants.Scope.ACCOUNTS
            )
        ).joinToString(separator = " ")
        val clientCredentials = events.clientCredentialsAuthentication(tpp, scopes)

        // when
        val exception = assertThrows(AssertionError::class.java) {
            events.pollForEvents(
                tppResource.tpp,
                clientCredentials,
                version
            )
        }

        // Then
        assertThat(exception.message.toString()).contains("OBRI.AccessToken.Invalid")
        assertThat(exception.message.toString()).contains("Invalid access token. Missing scopes: [eventpolling]")
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(403)
    }

    @EnabledIfVersion(
        type = "events",
        apiVersion = "v3.1.6",
        operations = ["EventAggregatedPolling"],
        apis = ["events"]
    )
    @Test
    @JvmName("shouldAcceptTppPollingRequest_v3_1_6")
    fun acceptPolling_v3_1_6() {
        // Given
        val version = v3_1_6
        val scopes = asDiscovery.scopes_supported.intersect(
            listOf(
                OpenBankingConstants.Scope.OPENID,
                OpenBankingConstants.Scope.ACCOUNTS,
                OpenBankingConstants.Scope.FUNDS_CONFIRMATIONS
            )
        ).joinToString(separator = " ")
        val clientCredentials = events.clientCredentialsAuthentication(tppResource.tpp, scopes)
        val dataEvents = events.importEvents(tppResource.tpp, clientCredentials, version)
        assertThat(dataEvents.isNotEmpty())

        // When
        val pollResponse = events.pollForEvents(tppResource.tpp, clientCredentials, version)

        // Then
        assertThat(pollResponse.sets).isNotEmpty()
    }

    @EnabledIfVersion(
        type = "events",
        apiVersion = "v3.1.6",
        operations = ["EventAggregatedPolling"],
        apis = ["events"]
    )
    @Test
    @JvmName("shouldAcceptTppPollingRequest_EmptyResponse_v3_1_6")
    fun acceptPolling_EmptyResponse_v3_1_6() {
        // Given
        val tpp = initFuelAsNewTpp().apply { dynamicRegistration() }
        val version = v3_1_6
        val scopes = asDiscovery.scopes_supported.intersect(
            listOf(
                OpenBankingConstants.Scope.OPENID,
                OpenBankingConstants.Scope.ACCOUNTS,
                OpenBankingConstants.Scope.FUNDS_CONFIRMATIONS
            )
        ).joinToString(separator = " ")
        val clientCredentials = events.clientCredentialsAuthentication(tpp, scopes)

        // When
        val pollResponse = events.pollForEvents(tppResource.tpp, clientCredentials, version)

        // Then
        assertThat(pollResponse.sets).isEmpty()
    }

    @EnabledIfVersion(
        type = "events",
        apiVersion = "v3.1.6",
        operations = ["EventAggregatedPolling"],
        apis = ["events"]
    )
    @Test
    @JvmName("shouldThrowsErrorTppPollingRequest_v3_1_6_wrongScope")
    fun rejectPolling_v3_1_6_wrongScope() {
        // Given
        val tpp = initFuelAsNewTpp().apply { dynamicRegistration() }
        val version = v3_1_6
        val scopes = asDiscovery.scopes_supported.intersect(
            listOf(
                OpenBankingConstants.Scope.OPENID,
                OpenBankingConstants.Scope.EVENT_POLLING
            )
        ).joinToString(separator = " ")
        val clientCredentials = events.clientCredentialsAuthentication(tpp, scopes)

        // when
        val exception = assertThrows(AssertionError::class.java) {
            events.pollForEvents(
                tppResource.tpp,
                clientCredentials,
                version
            )
        }

        // Then
        assertThat(exception.message.toString()).contains("OBRI.AccessToken.Invalid")
        assertThat(exception.message.toString()).contains("Invalid access token. Missing scopes: [accounts, fundsconfirmations]")
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(403)
    }

}
