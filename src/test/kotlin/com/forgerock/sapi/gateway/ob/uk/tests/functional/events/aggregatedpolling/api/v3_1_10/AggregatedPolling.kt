package com.forgerock.sapi.gateway.ob.uk.tests.functional.events.aggregatedpolling.api.v3_1_10

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.support.discovery.getEventsApiLinks
import com.forgerock.sapi.gateway.ob.uk.support.event.EventNotificationRS
import com.forgerock.sapi.gateway.ob.uk.support.event.EventsDataFactory
import com.forgerock.sapi.gateway.rs.resource.store.datamodel.events.FREventMessage
import com.forgerock.sapi.gateway.rs.resource.store.datamodel.events.FREventMessages
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import org.assertj.core.api.Assertions
import uk.org.openbanking.datamodel.event.OBEventPolling1
import uk.org.openbanking.datamodel.event.OBEventPolling1SetErrs
import uk.org.openbanking.datamodel.event.OBEventPollingResponse1
import kotlin.streams.toList

class AggregatedPolling(
        val version: OBVersion,
        val tppResource: CreateTppCallback.TppResource
) {

    private val eventAggregatedPollingLinks = getEventsApiLinks(version)
    private val eventAggregatedPollingUrl = eventAggregatedPollingLinks.EventAggregatedPolling

    fun shouldInitialPollingTest() {
        // Given
        val dataEventRequest = EventsDataFactory.aValidFRDataEvent(
                listOf(EventsDataFactory.aValidOBEventNotification1(OBVersion.v3_1_10)),
                tppResource.tpp.registrationResponse.client_id
        )
        Assertions.assertThat(dataEventRequest).isNotNull

        val adminAccessToken = EventNotificationRS().getClientCredentialsAdminAccessToken(tppResource.tpp).access_token
        val eventsImported = EventNotificationRS().importDataEvent<FREventMessages>(dataEventRequest, adminAccessToken)
        Assertions.assertThat(eventsImported).isNotNull
        Assertions.assertThat(eventsImported.events.size).isGreaterThan(0)

        val obEventPollingRequest = OBEventPolling1().returnImmediately(true)

        // When
        val obEventPollingResponse = EventNotificationRS().postAggregatedPolling<OBEventPollingResponse1>(
                eventAggregatedPollingUrl,
                obEventPollingRequest,
                tppResource.tpp
        )

        // Then
        Assertions.assertThat(obEventPollingResponse).isNotNull
        Assertions.assertThat(obEventPollingResponse.sets.keys).contains(eventsImported.events[0].jti)
        Assertions.assertThat(obEventPollingResponse.sets.values).contains(eventsImported.events[0].set)

        // Finally
        deleteImportedEvents(eventsImported, adminAccessToken)

    }

    fun shouldAcknowledgeEventTest() {
        // Given
        val dataEventRequest = EventsDataFactory.aValidFRDataEvent(
                listOf(EventsDataFactory.aValidOBEventNotification1(OBVersion.v3_1_10)),
                tppResource.tpp.registrationResponse.client_id
        )

        val adminAccessToken = EventNotificationRS().getClientCredentialsAdminAccessToken(tppResource.tpp).access_token
        val eventsImported = EventNotificationRS().importDataEvent<FREventMessages>(dataEventRequest, adminAccessToken)
        Assertions.assertThat(eventsImported).isNotNull
        Assertions.assertThat(eventsImported.events.size).isGreaterThan(0)

        val jtiList = eventsImported.events
                .stream()
                .map(FREventMessage::getJti)
                .toList()

        val obEventPollingRequest = OBEventPolling1()
        obEventPollingRequest.ack(jtiList)

        // When
        val obEventPollingResponse = EventNotificationRS().postAggregatedPolling<OBEventPollingResponse1>(
                eventAggregatedPollingUrl,
                obEventPollingRequest,
                tppResource.tpp
        )

        // Then
        Assertions.assertThat(obEventPollingResponse).isNotNull
        Assertions.assertThat(obEventPollingResponse.sets.keys).doesNotContainAnyElementsOf(jtiList)

        // Finally
        verifyNonexistentEvents(eventsImported, adminAccessToken)
    }

    fun shouldPollAndAcknowledgeEventTest() {
        // Given
        val obEventNotifications = listOf(
                EventsDataFactory.aValidOBEventNotification1(OBVersion.v3_1_10),
                EventsDataFactory.aValidOBEventNotification1(OBVersion.v3_1_10)
        )
        val dataEventRequest = EventsDataFactory.aValidFRDataEvent(obEventNotifications, tppResource.tpp.registrationResponse.client_id)

        val adminAccessToken = EventNotificationRS().getClientCredentialsAdminAccessToken(tppResource.tpp).access_token
        val eventsImported = EventNotificationRS().importDataEvent<FREventMessages>(dataEventRequest, adminAccessToken)
        Assertions.assertThat(eventsImported).isNotNull
        Assertions.assertThat(eventsImported.events.size).isGreaterThan(1)

        val obEventPollingRequest = OBEventPolling1().returnImmediately(true)
        obEventPollingRequest.ack(listOf(eventsImported.events[0].jti))

        obEventPollingRequest.setErrs(
                mapOf(
                        eventsImported.events[1].jti to
                                OBEventPolling1SetErrs().err("jwtIss").description("Issuer is invalid or could not be verified")
                )
        )

        // When
        val obEventPollingResponse = EventNotificationRS().postAggregatedPolling<OBEventPollingResponse1>(
                eventAggregatedPollingUrl,
                obEventPollingRequest,
                tppResource.tpp
        )

        // Then
        Assertions.assertThat(obEventPollingResponse).isNotNull
        val jtiList = eventsImported.events
                .stream()
                .map(FREventMessage::getJti)
                .toList()
        Assertions.assertThat(obEventPollingResponse.sets.keys).doesNotContainAnyElementsOf(jtiList)

        // Finally
        deleteImportedEvents(eventsImported, adminAccessToken)
    }

    private fun deleteImportedEvents(eventsImported: FREventMessages, adminAccessToken: String) {
        EventNotificationRS().deleteImportedEvents(eventsImported, adminAccessToken)
        verifyNonexistentEvents(eventsImported, adminAccessToken)
    }

    private fun verifyNonexistentEvents(eventsImported: FREventMessages, adminAccessToken: String) {
        val eventMessages = EventNotificationRS().exportEvents<FREventMessages>(
                eventsImported.apiClientId,
                adminAccessToken
        )
        Assertions.assertThat(eventMessages.events).doesNotContainAnyElementsOf(eventsImported.events)
    }
}