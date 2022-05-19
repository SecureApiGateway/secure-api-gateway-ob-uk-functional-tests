package com.forgerock.securebanking.tests.functional.deprecated.event.aggregatedpolling

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.forgerock.openbanking.common.model.version.OBVersion
import com.forgerock.openbanking.constants.OpenBankingConstants
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.support.discovery.asDiscovery
import com.forgerock.securebanking.support.event.Events
import com.github.kittinunf.fuel.core.FuelError
import org.junit.jupiter.api.Assertions.assertThrows
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

    @EnabledIfVersion(
        type = "events",
        apiVersion = "v3.1.2",
        operations = ["EventAggregatedPolling"],
        apis = ["events"]
    )
    @Test
    @JvmName("shouldAcceptAcknowledgeOnlyReceiptFromTpp_v3_1_2")
    fun acceptAcknowledgeOnly_v3_1_2() {
        // Given
        val version = OBVersion.v3_1_2
        val scopes = asDiscovery.scopes_supported.intersect(
            listOf(
                OpenBankingConstants.Scope.OPENID,
                OpenBankingConstants.Scope.EVENT_POLLING
            )
        ).joinToString(separator = " ")
        val clientCredentials = events.clientCredentialsAuthentication(tppResource.tpp, scopes)
        val dataEvents = events.importEvents(tppResource.tpp, clientCredentials, version)
        assertThat(dataEvents.isNotEmpty())
        val pollResponse = events.pollForEvents(tppResource.tpp, clientCredentials, version)
        val acknowledgeOnlyRequest = acknowledgeOnlyRequest(pollResponse)

        // When
        val submissionResp =
            events.sendAcknowledgement(tppResource.tpp, clientCredentials, acknowledgeOnlyRequest, version)

        // Then
        // TODO - improve this assertion
        assertThat(submissionResp.sets).isNotNull()
        assertThat(submissionResp.sets).isEmpty()
    }

    @EnabledIfVersion(
        type = "events",
        apiVersion = "v3.1.2",
        operations = ["EventAggregatedPolling"],
        apis = ["events"]
    )
    @Test
    @JvmName("exceptionAcknowledgeOnlyReceiptFromTpp_v3_1_2_wrongScope")
    fun exceptionAcknowledgeOnly_v3_1_2_wrongScope() {
        // Given
        val version = OBVersion.v3_1_2
        val scopes = asDiscovery.scopes_supported.intersect(
            listOf(
                OpenBankingConstants.Scope.OPENID,
                OpenBankingConstants.Scope.ACCOUNTS
            )
        ).joinToString(separator = " ")
        val clientCredentials = events.clientCredentialsAuthentication(tppResource.tpp, scopes)
        val dataEvents = events.importEvents(tppResource.tpp, clientCredentials, version)
        assertThat(dataEvents.isNotEmpty())

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
    @JvmName("shouldAcceptAcknowledgeOnlyReceiptFromTpp_v3_1_6")
    fun acceptAcknowledgeOnly_v3_1_6() {
        // Given
        val version = OBVersion.v3_1_6
        val clientCredentials = events.clientCredentialsAuthentication(tppResource.tpp)
        val dataEvents = events.importEvents(tppResource.tpp, clientCredentials, version)
        assertThat(dataEvents.isNotEmpty())
        val pollResponse = events.pollForEvents(tppResource.tpp, clientCredentials, version)
        val acknowledgeOnlyRequest = acknowledgeOnlyRequest(pollResponse)

        // When
        val submissionResp =
            events.sendAcknowledgement(tppResource.tpp, clientCredentials, acknowledgeOnlyRequest, version)

        // Then
        // TODO - improve this assertion
        assertThat(submissionResp.sets).isNotNull()
        assertThat(submissionResp.sets).isEmpty()
    }

    @EnabledIfVersion(
        type = "events",
        apiVersion = "v3.1.6",
        operations = ["EventAggregatedPolling"],
        apis = ["events"]
    )
    @Test
    @JvmName("exceptionAcknowledgeOnlyReceiptFromTpp_v3_1_6_wrongScope")
    fun exceptionAcknowledgeOnly_v3_1_6_wrongScope() {
        // Given
        val version = OBVersion.v3_1_6
        val scopes = asDiscovery.scopes_supported.intersect(
            listOf(
                OpenBankingConstants.Scope.OPENID,
                OpenBankingConstants.Scope.EVENT_POLLING
            )
        ).joinToString(separator = " ")
        val clientCredentials = events.clientCredentialsAuthentication(tppResource.tpp, scopes)
        val dataEvents = events.importEvents(tppResource.tpp, clientCredentials, version)
        assertThat(dataEvents.isNotEmpty())

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

    private fun acknowledgeOnlyRequest(pollResponse: OBEventPollingResponse1): OBEventPolling1 {
        return OBEventPolling1()
            .maxEvents(0)
            .addAckItem(pollResponse.sets.entries.random().key)
    }

}
