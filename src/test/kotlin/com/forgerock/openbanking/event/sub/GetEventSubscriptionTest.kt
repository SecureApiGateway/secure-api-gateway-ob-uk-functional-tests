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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.event.OBEventSubscriptionResponse1
import uk.org.openbanking.datamodel.event.OBEventSubscriptionsResponse1

/**
 * Event Notification Subscription API
 *
 * https://openbanking.atlassian.net/wiki/spaces/DZ/pages/1077806674/Event+Notification+Subscription+API+-+v3.1.2
 *
 * - Events subscriptions versions >= v3.1.2
 *
 * -A TPP must only create an event-subscription on one version
 */
class GetEventSubscriptionTest {

    private lateinit var tpp: Tpp
    private val events = Events()

    @BeforeEach
    fun setup() {
        this.tpp = initFuelAsNewTpp().apply { dynamicRegistration() }
    }

    @EnabledIfOpenBankingVersion(type = "events", version = "v3.1.2", apis = ["event-subscriptions"])
    @Test
    // Note, the use of @JvmName is necessary to avoid `File Name Too Long` errors when compiling on Linux with it's
    // 255 byte filename limit.
    @JvmName("shouldGetTppEventSubscription_v3_1_2")
    fun shouldGet_v3_1_2() {
        // Given
        val credentials = events.clientCredentialsAuthentication(tpp)
        val version = OBVersion.v3_1_2
        val createRequest = EventsDataFactory.anEventSubscriptionRequest(version)
        val createResponse = events.submitPost<OBEventSubscriptionResponse1>(url(version), createRequest, credentials, tpp, version)

        // when
        val getResponse = events.submitGet<OBEventSubscriptionsResponse1>(url(version), credentials)

        // Then
        assertThat(createResponse.data.eventSubscriptionId).isNotEmpty()
        assertThat(createResponse.data.callbackUrl).isEqualTo(createRequest.data.callbackUrl)
        assertThat(createResponse.data.version).isEqualTo(createRequest.data.version)
        assertThat(getResponse.data.eventSubscription.toString()).contains(createResponse.data.callbackUrl)
        assertThat(getResponse.data.eventSubscription.toString()).contains(createResponse.data.eventSubscriptionId)
        assertThat(getResponse.data.eventSubscription.toString()).contains(createResponse.data.version)
    }

    @EnabledIfOpenBankingVersion(type = "events", version = "v3.1.3", apis = ["callback-urls"])
    @Test
    @JvmName("shouldFailToGetTppEventSubscription_accessedByOlderVersion_v3_1_3")
    fun failGet_calledByOlderVersion_v3_1_3() {
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
        val exception = assertThrows(AssertionError::class.java) { events.submitGet<OBEventSubscriptionsResponse1>(url(version), credentials) }
        assertThat(exception.message.toString()).contains("The event subscription can't be read via an older API version")
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(409)
    }

    @EnabledIfOpenBankingVersion(type = "events", version = "v3.1.6", apis = ["event-subscriptions"])
    @Test
    @JvmName("shouldGetTppEventSubscription_v3_1_6")
    fun shouldGet_v3_1_6() {
        // Given
        val credentials = events.clientCredentialsAuthentication(tpp)
        val version = OBVersion.v3_1_6
        val createRequest = EventsDataFactory.anEventSubscriptionRequest(version)
        val createResponse = events.submitPost<OBEventSubscriptionResponse1>(url(version), createRequest, credentials, tpp, version)

        // when
        val getResponse = events.submitGet<OBEventSubscriptionsResponse1>(url(version), credentials)

        // Then
        assertThat(createResponse.data.eventSubscriptionId).isNotEmpty()
        assertThat(createResponse.data.callbackUrl).isEqualTo(createRequest.data.callbackUrl)
        assertThat(createResponse.data.version).isEqualTo(createRequest.data.version)
        assertThat(getResponse.data.eventSubscription.toString()).contains(createResponse.data.callbackUrl)
        assertThat(getResponse.data.eventSubscription.toString()).contains(createResponse.data.eventSubscriptionId)
        assertThat(getResponse.data.eventSubscription.toString()).contains(createResponse.data.version)
    }

    @EnabledIfOpenBankingVersion(type = "events", version = "v3.1.6", apis = ["callback-urls"])
    @Test
    @JvmName("shouldGetTppEventSubscription_calledByNewerVersion_v3_1_4_v3_1_6")
    fun shouldGet_calledByNewerVersion_v3_1_4_v3_1_6() {
        // Given
        val credentials = events.clientCredentialsAuthentication(tpp)
        var version = OBVersion.v3_1_4
        val createRequest = EventsDataFactory.anEventSubscriptionRequest(version)
        val createResponse = events.submitPost<OBEventSubscriptionResponse1>(url(version), createRequest, credentials, tpp, version)
        assertThat(createResponse.data.eventSubscriptionId).isNotEmpty()
        assertThat(createResponse.data.callbackUrl).isEqualTo(createRequest.data.callbackUrl)
        assertThat(createResponse.data.version).isEqualTo(createRequest.data.version)
        version = OBVersion.v3_1_6

        // When
        val getResponse = events.submitGet<OBEventSubscriptionsResponse1>(url(version), credentials)

        // Then
        assertThat(getResponse.data.eventSubscription.toString()).contains(createResponse.data.callbackUrl)
        assertThat(getResponse.data.eventSubscription.toString()).contains(createResponse.data.eventSubscriptionId)
        assertThat(getResponse.data.eventSubscription.toString()).contains(createResponse.data.version)
    }

    @EnabledIfOpenBankingVersion(type = "events", version = "v3.1.6", apis = ["callback-urls"])
    @Test
    @JvmName("shouldFailToGetTppEventSubscription_accessedByOlderVersion_v3_1_6")
    fun failToGet_calledByOlderVersion_v3_1_6() {
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
        val exception = assertThrows(AssertionError::class.java) { events.submitGet<OBEventSubscriptionsResponse1>(url(version), credentials) }
        assertThat(exception.message.toString()).contains("The event subscription can't be read via an older API version")
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(409)
    }

    private fun url(version: OBVersion): String {
        return events.getEventSubscriptionsUrl(version)
    }
}