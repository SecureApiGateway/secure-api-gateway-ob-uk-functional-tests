package com.forgerock.openbanking.event.aggregatedpolling

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.forgerock.openbanking.common.model.version.OBVersion
import com.forgerock.openbanking.event.Events
import com.forgerock.openbanking.junit.CreateTppCallback
import com.forgerock.openbanking.junit.EnabledIfOpenBankingVersion
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.event.OBEventPolling1
import uk.org.openbanking.datamodel.event.OBEventPolling1SetErrs
import uk.org.openbanking.datamodel.event.OBEventPollingResponse1

/**
 * Poll and Acknowledge:
 *
 * https://openbanking.atlassian.net/wiki/spaces/DZ/pages/1077806765/Aggregated+Polling+-+v3.1.2
 */
@Tags(Tag("eventTest"))
class PollAndAcknowledgeAggregatedPollingTest(val tppResource: CreateTppCallback.TppResource) {

    private val events = Events()

    @EnabledIfOpenBankingVersion(type = "events", version = "v3.1.2", apis = ["events"])
    @Test
    fun shouldAcceptTppErrorAcknowledgement_v3_1_2() {
        // Given
        val version = OBVersion.v3_1_2
        val clientCredentials = events.clientCredentialsAuthentication(tppResource.tpp)
        /*
         * Importing two events
         * One acknowledge with error to check that the system returns the event not acknowledge with errors.
         * The only expected event returned from ASPSP it's the event with no error acknowledge.
         */
        val dataEvents = events.importEvents(tppResource.tpp, clientCredentials, version)
        assertThat(dataEvents.isNotEmpty())
        val dataEvents2 = events.importEvents(tppResource.tpp, clientCredentials, version)
        assertThat(dataEvents2.isNotEmpty())
        val pollResponse = events.pollForEvents(tppResource.tpp, clientCredentials, version)
        var eventIndex = 0
        val request = errorAcknowledgement(pollResponse, eventIndex)

        // When
        val submissionResp = events.sendAcknowledgement(tppResource.tpp, clientCredentials, request, version)

        // Then
        // the returned element from ASPSP is the element with no error acknowledge
        assertThat(submissionResp.sets.entries.elementAt(0)).isEqualTo(pollResponse.sets.entries.elementAt(++eventIndex))
    }

    @EnabledIfOpenBankingVersion(type = "events", version = "v3.1.6", apis = ["events"])
    @Test
    fun shouldAcceptTppErrorAcknowledgement_v3_1_6() {
        // Given
        val version = OBVersion.v3_1_6
        val clientCredentials = events.clientCredentialsAuthentication(tppResource.tpp)
        /*
         * Importing two events
         * One acknowledge with error to check that the system returns the event not acknowledge with errors.
         * The only expected event returned from ASPSP it's the event with no error acknowledge.
         */
        val dataEvents = events.importEvents(tppResource.tpp, clientCredentials, version)
        assertThat(dataEvents.isNotEmpty())
        val dataEvents2 = events.importEvents(tppResource.tpp, clientCredentials, version)
        assertThat(dataEvents2.isNotEmpty())
        val pollResponse = events.pollForEvents(tppResource.tpp, clientCredentials, version)
        var eventIndex = 0
        val request = errorAcknowledgement(pollResponse, eventIndex)

        // When
        val submissionResp = events.sendAcknowledgement(tppResource.tpp, clientCredentials, request, version)

        // Then
        // the returned element from ASPSP is the element with no error acknowledge
        assertThat(submissionResp.sets.entries.elementAt(0)).isEqualTo(pollResponse.sets.entries.elementAt(++eventIndex))

    }

    private fun errorAcknowledgement(pollResponse: OBEventPollingResponse1, index: Int): OBEventPolling1 {
        val error = OBEventPolling1SetErrs()
                .err("jwtIss")
                .description("Issuer is invalid or could not be verified")
        return OBEventPolling1()
                .maxEvents(1)
                .putSetErrsItem(pollResponse.sets.entries.elementAt(index).key, error)
    }

}