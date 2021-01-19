package com.forgerock.openbanking.event.sub

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import com.forgerock.openbanking.Tpp
import com.forgerock.openbanking.common.model.version.OBVersion
import com.forgerock.openbanking.event.Events
import com.forgerock.openbanking.event.EventsDataFactory
import com.forgerock.openbanking.initFuelAsNewTpp
import com.forgerock.openbanking.junit.EnabledIfOpenBankingVersion
import com.github.kittinunf.fuel.core.FuelError
import org.junit.jupiter.api.Assertions.assertThrows
import com.github.kittinunf.fuel.core.isSuccessful
import org.apache.http.HttpStatus
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.event.OBEventSubscriptionResponse1

/**
 * Event Notification Subscription API
 *
 * https://openbanking.atlassian.net/wiki/spaces/DZ/pages/1077806674/Event+Notification+Subscription+API+-+v3.1.2
 *
 * - Events subscriptions versions >= v3.1.2
 *
 * -A TPP must only create an event-subscription on one version
 */
class DeleteEventSubscriptionTest {

    private lateinit var tpp: Tpp
    private val events = Events()

    @BeforeEach
    fun setup() {
        this.tpp = initFuelAsNewTpp().apply { dynamicRegistration() }
    }

    @EnabledIfOpenBankingVersion(type = "events", version = "v3.1.2", apis = ["event-subscriptions"])
    @Test
    fun shouldDeleteTppEventSubscription_v3_1_2() {
        // Given
        val credentials = events.clientCredentialsAuthentication(tpp)
        val version = OBVersion.v3_1_2
        val createRequest = EventsDataFactory.anEventSubscriptionRequest(version)
        val createResponse = events.submitPost<OBEventSubscriptionResponse1>(url(version), createRequest, credentials, tpp, version)
        assertThat(createResponse.data.eventSubscriptionId).isNotEmpty()
        assertThat(createResponse.data.callbackUrl).isEqualTo(createRequest.data.callbackUrl)
        assertThat(createResponse.data.version).isEqualTo(createRequest.data.version)

        // When
        val response = events.submitDelete(url(version), createResponse.data.eventSubscriptionId, credentials)

        // Then
        assertThat(response.isSuccessful)
        assertThat(response.statusCode.dec() == HttpStatus.SC_NO_CONTENT)
    }

    @EnabledIfOpenBankingVersion(type = "events", version = "v3.1.3", apis = ["callback-urls"])
    @Test
    fun shouldFailToDeleteTppEventSubscription_accessedByOlderVersion_v3_1_3() {
        // Given
        val credentials = events.clientCredentialsAuthentication(tpp)
        var version = OBVersion.v3_1_3
        val createRequest = EventsDataFactory.anEventSubscriptionRequest(version)
        val createResponse = events.submitPost<OBEventSubscriptionResponse1>(url(version), createRequest, credentials, tpp, version)
        assertThat(createResponse.data.eventSubscriptionId).isNotEmpty()
        assertThat(createResponse.data.callbackUrl).isEqualTo(createRequest.data.callbackUrl)
        assertThat(createResponse.data.version).isEqualTo(createRequest.data.version)
        version = OBVersion.v3_1_2

        // When
        val exception = assertThrows(AssertionError::class.java) { events.submitDelete(url(version), createResponse.data.eventSubscriptionId, credentials) }
        assertThat(exception.message.toString()).contains("The event subscription can't be delete via an older API version")
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(409)
    }

    @EnabledIfOpenBankingVersion(type = "events", version = "v3.1.3", apis = ["event-subscriptions"])
    @Test
    fun shouldDeleteTppEventSubscription_viaNewerVersion_v3_1_3_v3_1_4() {
        // Given
        val credentials = events.clientCredentialsAuthentication(tpp)
        var version = OBVersion.v3_1_3
        val createRequest = EventsDataFactory.anEventSubscriptionRequest(version)
        val createResponse = events.submitPost<OBEventSubscriptionResponse1>(url(version), createRequest, credentials, tpp, version)
        assertThat(createResponse.data.eventSubscriptionId).isNotEmpty()
        assertThat(createResponse.data.callbackUrl).isEqualTo(createRequest.data.callbackUrl)
        assertThat(createResponse.data.version).isEqualTo(createRequest.data.version)
        version = OBVersion.v3_1_4

        // When
        val response = events.submitDelete(url(version), createResponse.data.eventSubscriptionId, credentials)

        // Then
        assertThat(response.isSuccessful)
        assertThat(response.statusCode.dec() == HttpStatus.SC_NO_CONTENT)
    }

    @EnabledIfOpenBankingVersion(type = "events", version = "v3.1.6", apis = ["event-subscriptions"])
    @Test
    fun shouldDeleteTppEventSubscription_v3_1_6() {
        // Given
        val credentials = events.clientCredentialsAuthentication(tpp)
        val version = OBVersion.v3_1_6
        val createRequest = EventsDataFactory.anEventSubscriptionRequest(version)
        val createResponse = events.submitPost<OBEventSubscriptionResponse1>(url(version), createRequest, credentials, tpp, version)
        assertThat(createResponse.data.eventSubscriptionId).isNotEmpty()
        assertThat(createResponse.data.callbackUrl).isEqualTo(createRequest.data.callbackUrl)
        assertThat(createResponse.data.version).isEqualTo(createRequest.data.version)

        // When
        val response = events.submitDelete(url(version), createResponse.data.eventSubscriptionId, credentials)

        // Then
        assertThat(response.isSuccessful)
        assertThat(response.statusCode.dec() == HttpStatus.SC_NO_CONTENT)
    }

    @EnabledIfOpenBankingVersion(type = "events", version = "v3.1.6", apis = ["callback-urls"])
    @Test
    fun shouldFailToDeleteTppEventSubscription_accessedByOlderVersion_v3_1_6() {
        // Given
        val credentials = events.clientCredentialsAuthentication(tpp)
        var version = OBVersion.v3_1_6
        val createRequest = EventsDataFactory.anEventSubscriptionRequest(version)
        val createResponse = events.submitPost<OBEventSubscriptionResponse1>(url(version), createRequest, credentials, tpp, version)
        assertThat(createResponse.data.eventSubscriptionId).isNotEmpty()
        assertThat(createResponse.data.callbackUrl).isEqualTo(createRequest.data.callbackUrl)
        assertThat(createResponse.data.version).isEqualTo(createRequest.data.version)
        version = OBVersion.v3_1_4

        // When
        val exception = assertThrows(AssertionError::class.java) { events.submitDelete(url(version), createResponse.data.eventSubscriptionId, credentials) }
        assertThat(exception.message.toString()).contains("The event subscription can't be delete via an older API version")
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(409)
    }

    private fun url(version: OBVersion): String {
        return events.getEventSubscriptionsUrl(version)
    }
}