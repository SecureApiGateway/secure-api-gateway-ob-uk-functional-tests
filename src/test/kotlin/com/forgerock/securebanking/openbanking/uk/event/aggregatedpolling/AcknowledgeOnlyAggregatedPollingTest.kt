package com.forgerock.openbanking.event.aggregatedpolling

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isNotNull
import com.forgerock.openbanking.common.model.openbanking.persistence.event.FREventNotification
import com.forgerock.openbanking.common.model.version.OBVersion
import com.forgerock.openbanking.event.Events
import com.forgerock.openbanking.junit.CreateTppCallback
import com.forgerock.openbanking.junit.EnabledIfOpenBankingVersion
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.event.OBEventPolling1
import uk.org.openbanking.datamodel.event.OBEventPollingResponse1

/**
 * Acknowledge Only:
 *
 * https://openbanking.atlassian.net/wiki/spaces/DZ/pages/1077806765/Aggregated+Polling+-+v3.1.2
 */
@Tags(Tag("eventTest"))
class AcknowledgeOnlyAggregatedPollingTest(val tppResource: CreateTppCallback.TppResource) {

    private val events = Events()

    @EnabledIfOpenBankingVersion(type = "events", version = "v3.1.2", apis = ["events"])
    @Test
    fun shouldAcceptAcknowledgeOnlyReceiptFromTpp_v3_1_2() {
        // Given
        val version = OBVersion.v3_1_2
        val clientCredentials = events.clientCredentialsAuthentication(tppResource.tpp)
        val dataEvents = events.importEvents(tppResource.tpp, clientCredentials, version)
        assertThat(dataEvents.isNotEmpty())
        val pollResponse = events.pollForEvents(tppResource.tpp, clientCredentials, version)
        val acknowledgeOnlyRequest = acknowledgeOnlyRequest(pollResponse)

        // When
        val submissionResp = events.sendAcknowledgement(tppResource.tpp, clientCredentials, acknowledgeOnlyRequest, version)

        // Then
        // TODO - improve this assertion
        assertThat(submissionResp.sets).isNotNull()
        assertThat(submissionResp.sets).isEmpty()
    }

    @EnabledIfOpenBankingVersion(type = "events", version = "v3.1.6", apis = ["events"])
    @Test
    fun shouldAcceptAcknowledgeOnlyReceiptFromTpp_v3_1_6() {
        // Given
        val version = OBVersion.v3_1_6
        val clientCredentials = events.clientCredentialsAuthentication(tppResource.tpp)
        val dataEvents = events.importEvents(tppResource.tpp, clientCredentials, version)
        assertThat(dataEvents.isNotEmpty())
        val pollResponse = events.pollForEvents(tppResource.tpp, clientCredentials, version)
        val acknowledgeOnlyRequest = acknowledgeOnlyRequest(pollResponse)

        // When
        val submissionResp = events.sendAcknowledgement(tppResource.tpp, clientCredentials, acknowledgeOnlyRequest, version)

        // Then
        // TODO - improve this assertion
        assertThat(submissionResp.sets).isNotNull()
        assertThat(submissionResp.sets).isEmpty()
    }

    private fun acknowledgeOnlyRequest(pollResponse: OBEventPollingResponse1): OBEventPolling1 {
        return OBEventPolling1()
                .maxEvents(0)
                .addAckItem(pollResponse.sets.entries.random().key)
    }

}