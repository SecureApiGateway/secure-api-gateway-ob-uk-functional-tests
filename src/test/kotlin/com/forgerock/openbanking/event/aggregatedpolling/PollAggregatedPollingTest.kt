package com.forgerock.openbanking.event.aggregatedpolling

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isNotEmpty
import com.forgerock.openbanking.common.model.openbanking.persistence.event.FREventNotification
import com.forgerock.openbanking.common.model.version.OBVersion.v3_1_2
import com.forgerock.openbanking.common.model.version.OBVersion.v3_1_6
import com.forgerock.openbanking.event.Events
import com.forgerock.openbanking.initFuelAsNewTpp
import com.forgerock.openbanking.junit.CreateTppCallback
import com.forgerock.openbanking.junit.EnabledIfOpenBankingVersion
import org.junit.jupiter.api.Test

/**
 * Initial Polling:
 *
 * https://openbanking.atlassian.net/wiki/spaces/DZ/pages/1077806765/Aggregated+Polling+-+v3.1.2
 */
class PollAggregatedPollingTest(val tppResource: CreateTppCallback.TppResource) {

    private val events = Events()

    @EnabledIfOpenBankingVersion(type = "events", version = "v3.1.2", apis = ["events"])
    @Test
    fun shouldAcceptTppPollingRequest_v3_1_2() {
        // Given
        val version = v3_1_2
        val clientCredentials = events.clientCredentialsAuthentication(tppResource.tpp)
        val dataEvents = events.importEvents(tppResource.tpp, clientCredentials, version)
        assertThat(dataEvents.isNotEmpty())

        // When
        val pollResponse = events.pollForEvents(tppResource.tpp, clientCredentials, version)

        // Then
        assertThat(pollResponse.sets).isNotEmpty()
    }

    @EnabledIfOpenBankingVersion(type = "events", version = "v3.1.2", apis = ["events"])
    @Test
    fun shouldAcceptTppPollingRequest_EmptyResponse_v3_1_2() {
        // Given
        val tpp = initFuelAsNewTpp().apply { dynamicRegistration() }
        val version = v3_1_2
        val clientCredentials = events.clientCredentialsAuthentication(tpp)

        // When
        val pollResponse = events.pollForEvents(tppResource.tpp, clientCredentials, version)

        // Then
        assertThat(pollResponse.sets).isEmpty()
    }

    @EnabledIfOpenBankingVersion(type = "events", version = "v3.1.6", apis = ["events"])
    @Test
    fun shouldAcceptTppPollingRequest_v3_1_6() {
        // Given
        val version = v3_1_6
        val clientCredentials = events.clientCredentialsAuthentication(tppResource.tpp)
        val dataEvents = events.importEvents(tppResource.tpp, clientCredentials, version)
        assertThat(dataEvents.isNotEmpty())

        // When
        val pollResponse = events.pollForEvents(tppResource.tpp, clientCredentials, version)

        // Then
        assertThat(pollResponse.sets).isNotEmpty()
    }

    @EnabledIfOpenBankingVersion(type = "events", version = "v3.1.6", apis = ["events"])
    @Test
    fun shouldAcceptTppPollingRequest_EmptyResponse_v3_1_6() {
        // Given
        val tpp = initFuelAsNewTpp().apply { dynamicRegistration() }
        val version = v3_1_6
        val clientCredentials = events.clientCredentialsAuthentication(tpp)

        // When
        val pollResponse = events.pollForEvents(tppResource.tpp, clientCredentials, version)

        // Then
        assertThat(pollResponse.sets).isEmpty()
    }

}