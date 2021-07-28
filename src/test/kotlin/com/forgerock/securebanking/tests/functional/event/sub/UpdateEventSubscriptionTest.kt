package com.forgerock.securebanking.tests.functional.event.sub

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import com.forgerock.openbanking.common.model.version.OBVersion
import com.forgerock.securebanking.framework.data.Tpp
import com.forgerock.securebanking.framework.http.fuel.initFuelAsNewTpp
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.support.event.Events
import com.forgerock.securebanking.support.event.EventsDataFactory
import com.github.kittinunf.fuel.core.FuelError
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
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
@Tags(Tag("eventTest"))
class UpdateEventSubscriptionTest {

    private lateinit var tpp: Tpp
    private val events = Events()

    @BeforeEach
    fun setup() {
        this.tpp = initFuelAsNewTpp().apply { dynamicRegistration() }
    }

    @EnabledIfVersion(
        type = "events",
        apiVersion = "v3.1.2",
        operations = ["CreateEventSubscription"],
        apis = ["event-subscriptions"]
    )
    @Test
    @JvmName("shouldUpdateTppEventSubscription_v3_1_2")
    fun shouldUpdate_v3_1_2() {
        // Given
        val credentials = events.clientCredentialsAuthentication(tpp)
        val version = OBVersion.v3_1_2
        val createRequest = EventsDataFactory.anEventSubscriptionRequest(version)
        val createResponse =
            events.submitPost<OBEventSubscriptionResponse1>(url(version), createRequest, credentials, tpp, version)
        assertThat(createResponse.data.eventSubscriptionId).isNotEmpty()
        assertThat(createResponse.data.callbackUrl).isEqualTo(createRequest.data.callbackUrl)
        assertThat(createResponse.data.version).isEqualTo(createRequest.data.version)
        val subscriptionId = createResponse.data.eventSubscriptionId

        // when
        val updateResponse = events.submitPut<OBEventSubscriptionResponse1>(
            url(version),
            subscriptionId,
            createResponse,
            credentials,
            tpp,
            version
        )

        // Then
        assertThat(subscriptionId).isNotEmpty()
        assertThat(createResponse.data.callbackUrl).isEqualTo(createRequest.data.callbackUrl)
        assertThat(createResponse.data.version).isEqualTo(createRequest.data.version)
        assertThat(updateResponse.links.self).isEqualTo(url(version))
        assertThat(updateResponse.data.eventSubscriptionId).isEqualTo(subscriptionId)
        assertThat(updateResponse.data.callbackUrl).isEqualTo(createResponse.data.callbackUrl)
        assertThat(updateResponse.data.version).isEqualTo(createResponse.data.version)
    }

    @EnabledIfVersion(
        type = "events",
        apiVersion = "v3.1.3",
        postCreateVersion = "v3.1.5",
        operations = ["CreateEventSubscription"],
        apis = ["event-subscriptions"]
    )
    @Test
    // Note, the use of @JvmName is necessary to avoid `File Name Too Long` errors when compiling on Linux with it's
    // 255 byte filename limit.
    @JvmName("shouldUpdateTppEventSubscription_v3_1_3_viaNewerVersion_v3_1_5")
    fun update_v3_1_3_via_v3_1_5() {
        // Given
        val credentials = events.clientCredentialsAuthentication(tpp)
        var version = OBVersion.v3_1_3
        val createRequest = EventsDataFactory.anEventSubscriptionRequest(version)
        val createResponse =
            events.submitPost<OBEventSubscriptionResponse1>(url(version), createRequest, credentials, tpp, version)
        assertThat(createResponse.data.eventSubscriptionId).isNotEmpty()
        assertThat(createResponse.data.callbackUrl).isEqualTo(createRequest.data.callbackUrl)
        assertThat(createResponse.data.version).isEqualTo(createRequest.data.version)
        val subscriptionId = createResponse.data.eventSubscriptionId
        version = OBVersion.v3_1_5


        // when
        val updateResponse = events.submitPut<OBEventSubscriptionResponse1>(
            url(version),
            subscriptionId,
            createResponse,
            credentials,
            tpp,
            version
        )

        // Then
        assertThat(updateResponse.links.self).isEqualTo(url(version))
        assertThat(updateResponse.data.eventSubscriptionId).isEqualTo(subscriptionId)
        assertThat(updateResponse.data.callbackUrl).isEqualTo(createResponse.data.callbackUrl)
        assertThat(updateResponse.data.version).isEqualTo(createResponse.data.version)
    }

    @EnabledIfVersion(
        type = "events",
        apiVersion = "v3.1.3",
        postCreateVersion = "v3.1.2",
        operations = ["CreateEventSubscription"],
        apis = ["event-subscriptions"]
    )
    @Test
    @JvmName("shouldFailToUpdateTppEventSubscription_v3_1_3_viaOlderVersion_v3_1_2")
    fun failUpdate_v3_1_3_via_v3_1_2() {
        // Given
        val credentials = events.clientCredentialsAuthentication(tpp)
        var version = OBVersion.v3_1_3
        val createRequest = EventsDataFactory.anEventSubscriptionRequest(version)
        val createResponse =
            events.submitPost<OBEventSubscriptionResponse1>(url(version), createRequest, credentials, tpp, version)
        assertThat(createResponse.data.eventSubscriptionId).isNotEmpty()
        assertThat(createResponse.data.callbackUrl).isEqualTo(createRequest.data.callbackUrl)
        assertThat(createResponse.data.version).isEqualTo(createRequest.data.version)
        val subscriptionId = createResponse.data.eventSubscriptionId
        version = OBVersion.v3_1_2

        // When
        val exception = assertThrows(AssertionError::class.java) {
            events.submitPut<OBEventSubscriptionResponse1>(
                url(version),
                subscriptionId,
                createResponse,
                credentials,
                tpp,
                version
            )
        }
        assertThat(exception.message.toString()).contains("The event subscription can't be update via an older API version")
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(409)
    }

    @EnabledIfVersion(
        type = "events",
        apiVersion = "v3.1.6",
        operations = ["CreateEventSubscription"],
        apis = ["event-subscriptions"]
    )
    @Test
    @JvmName("shouldUpdateTppEventSubscription_v3_1_6")
    fun shouldUpdate_v3_1_6() {
        // Given
        val credentials = events.clientCredentialsAuthentication(tpp)
        val version = OBVersion.v3_1_6
        val createRequest = EventsDataFactory.anEventSubscriptionRequest(version)
        val createResponse =
            events.submitPost<OBEventSubscriptionResponse1>(url(version), createRequest, credentials, tpp, version)
        assertThat(createResponse.data.eventSubscriptionId).isNotEmpty()
        assertThat(createResponse.data.callbackUrl).isEqualTo(createRequest.data.callbackUrl)
        assertThat(createResponse.data.version).isEqualTo(createRequest.data.version)
        val subscriptionId = createResponse.data.eventSubscriptionId

        // when
        val updateResponse = events.submitPut<OBEventSubscriptionResponse1>(
            url(version),
            subscriptionId,
            createResponse,
            credentials,
            tpp,
            version
        )

        // Then
        assertThat(subscriptionId).isNotEmpty()
        assertThat(createResponse.data.callbackUrl).isEqualTo(createRequest.data.callbackUrl)
        assertThat(createResponse.data.version).isEqualTo(createRequest.data.version)
        assertThat(updateResponse.links.self).isEqualTo(url(version))
        assertThat(updateResponse.data.eventSubscriptionId).isEqualTo(subscriptionId)
        assertThat(updateResponse.data.callbackUrl).isEqualTo(createResponse.data.callbackUrl)
        assertThat(updateResponse.data.version).isEqualTo(createResponse.data.version)
    }

    @EnabledIfVersion(
        type = "events",
        apiVersion = "v3.1.6",
        postCreateVersion = "v3.1.3",
        operations = ["CreateEventSubscription"],
        apis = ["event-subscriptions"]
    )
    @Test
    @JvmName("shouldFailToUpdateTppEventSubscription_v3_1_6_viaOlderVersion_v3_1_3")
    fun failUpdate_v3_1_6_via_v3_1_3() {
        // Given
        val credentials = events.clientCredentialsAuthentication(tpp)
        var version = OBVersion.v3_1_6
        val createRequest = EventsDataFactory.anEventSubscriptionRequest(version)
        val createResponse =
            events.submitPost<OBEventSubscriptionResponse1>(url(version), createRequest, credentials, tpp, version)
        assertThat(createResponse.data.eventSubscriptionId).isNotEmpty()
        assertThat(createResponse.data.callbackUrl).isEqualTo(createRequest.data.callbackUrl)
        assertThat(createResponse.data.version).isEqualTo(createRequest.data.version)
        val subscriptionId = createResponse.data.eventSubscriptionId
        version = OBVersion.v3_1_3

        // When
        val exception = assertThrows(AssertionError::class.java) {
            events.submitPut<OBEventSubscriptionResponse1>(
                url(version),
                subscriptionId,
                createResponse,
                credentials,
                tpp,
                version
            )
        }
        assertThat(exception.message.toString()).contains("The event subscription can't be update via an older API version")
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(409)
    }

    private fun url(version: OBVersion): String {
        return events.getEventSubscriptionsUrl(version)
    }
}
