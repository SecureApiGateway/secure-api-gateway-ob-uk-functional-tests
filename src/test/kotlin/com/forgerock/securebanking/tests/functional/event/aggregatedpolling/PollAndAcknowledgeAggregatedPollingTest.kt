package com.forgerock.securebanking.tests.functional.event.aggregatedpolling

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import com.forgerock.openbanking.common.model.version.OBVersion
import com.forgerock.openbanking.constants.OpenBankingConstants
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.support.event.Events
import com.forgerock.securebanking.support.discovery.asDiscovery
import com.github.kittinunf.fuel.core.FuelError
import org.junit.jupiter.api.Assertions.assertThrows
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

    @EnabledIfVersion(
        type = "events",
        apiVersion = "v3.1.2",
        operations = ["EventAggregatedPolling"],
        apis = ["events"]
    )
    @Test
    @JvmName("shouldAcceptTppErrorAcknowledgement_v3_1_2")
    fun errorAcknowledgement_v3_1_2() {
        // Given
        val version = OBVersion.v3_1_2
        val scopes = asDiscovery.scopes_supported.intersect(
            listOf(
                OpenBankingConstants.Scope.OPENID,
                OpenBankingConstants.Scope.EVENT_POLLING
            )
        ).joinToString(separator = " ")
        val clientCredentials = events.clientCredentialsAuthentication(tppResource.tpp, scopes)
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

    @EnabledIfVersion(
        type = "events",
        apiVersion = "v3.1.2",
        operations = ["EventAggregatedPolling"],
        apis = ["events"]
    )
    @Test
    @JvmName("exceptionTppErrorAcknowledgement_v3_1_2_wrongScope")
    fun exceptionAcknowledgement_v3_1_2_wrongScope() {
        // Given
        val version = OBVersion.v3_1_2
        val scopes = asDiscovery.scopes_supported.intersect(
            listOf(
                OpenBankingConstants.Scope.OPENID,
                OpenBankingConstants.Scope.ACCOUNTS
            )
        ).joinToString(separator = " ")
        val clientCredentials = events.clientCredentialsAuthentication(tppResource.tpp, scopes)
        /*
         * Importing two events
         * One acknowledge with error to check that the system returns the event not acknowledge with errors.
         * The only expected event returned from ASPSP it's the event with no error acknowledge.
         */
        val dataEvents = events.importEvents(tppResource.tpp, clientCredentials, version)
        assertThat(dataEvents.isNotEmpty())
        val dataEvents2 = events.importEvents(tppResource.tpp, clientCredentials, version)
        assertThat(dataEvents2.isNotEmpty())

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
    @JvmName("shouldAcceptTppErrorAcknowledgement_v3_1_6")
    fun errorAcknowledgement_v3_1_6() {
        // Given
        val version = OBVersion.v3_1_6
        val scopes = asDiscovery.scopes_supported.intersect(
            listOf(
                OpenBankingConstants.Scope.OPENID,
                OpenBankingConstants.Scope.ACCOUNTS,
                OpenBankingConstants.Scope.FUNDS_CONFIRMATIONS
            )
        ).joinToString(separator = " ")
        val clientCredentials = events.clientCredentialsAuthentication(tppResource.tpp, scopes)
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

    @EnabledIfVersion(
        type = "events",
        apiVersion = "v3.1.6",
        operations = ["EventAggregatedPolling"],
        apis = ["events"]
    )
    @Test
    @JvmName("exceptionTppErrorAcknowledgement_v3_1_6_wrongScope")
    fun exceptionAcknowledgement_v3_1_6_wrongScope() {
        // Given
        val version = OBVersion.v3_1_6
        val scopes = asDiscovery.scopes_supported.intersect(
            listOf(
                OpenBankingConstants.Scope.OPENID,
                OpenBankingConstants.Scope.EVENT_POLLING
            )
        ).joinToString(separator = " ")
        val clientCredentials = events.clientCredentialsAuthentication(tppResource.tpp, scopes)
        /*
         * Importing two events
         * One acknowledge with error to check that the system returns the event not acknowledge with errors.
         * The only expected event returned from ASPSP it's the event with no error acknowledge.
         */
        val dataEvents = events.importEvents(tppResource.tpp, clientCredentials, version)
        assertThat(dataEvents.isNotEmpty())
        val dataEvents2 = events.importEvents(tppResource.tpp, clientCredentials, version)
        assertThat(dataEvents2.isNotEmpty())

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

    private fun errorAcknowledgement(pollResponse: OBEventPollingResponse1, index: Int): OBEventPolling1 {
        val error = OBEventPolling1SetErrs()
            .err("jwtIss")
            .description("Issuer is invalid or could not be verified")
        return OBEventPolling1()
            .maxEvents(1)
            .putSetErrsItem(pollResponse.sets.entries.elementAt(index).key, error)
    }

}
