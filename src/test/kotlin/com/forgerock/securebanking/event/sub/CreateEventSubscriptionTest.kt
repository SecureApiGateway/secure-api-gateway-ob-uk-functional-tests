package com.forgerock.securebanking.event.sub

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import com.forgerock.openbanking.common.model.version.OBVersion
import com.forgerock.securebanking.Tpp
import com.forgerock.securebanking.event.Events
import com.forgerock.securebanking.event.EventsDataFactory.Companion.anEventSubscriptionRequest
import com.forgerock.securebanking.initFuelAsNewTpp
import com.forgerock.securebanking.junit.EnabledIfVersion
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
class CreateEventSubscriptionTest {

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
    // Note, the use of @JvmName is necessary to avoid `File Name Too Long` errors when compiling on Linux with it's
    // 255 byte filename limit.
    @JvmName("shouldCreateTppEventSubscription_v3_1_2")
    fun shouldCreate_v3_1_2() {
        // Given
        val credentials = events.clientCredentialsAuthentication(tpp)
        val version = OBVersion.v3_1_2
        val request = anEventSubscriptionRequest(version)

        // When
        val response = events.submitPost<OBEventSubscriptionResponse1>(url(version), request, credentials, tpp, version)

        // Then
        assertThat(response.data.eventSubscriptionId).isNotEmpty()
        assertThat(response.links.self).isEqualTo(url(version))
        assertThat(response.data.callbackUrl).isEqualTo(request.data.callbackUrl)
        assertThat(response.data.version).isEqualTo(request.data.version)
    }

    @EnabledIfVersion(
        type = "events",
        apiVersion = "v3.1.2",
        operations = ["CreateEventSubscription"],
        apis = ["event-subscriptions"]
    )
    @Test
    @JvmName("shouldCreateTppEventSubscription_AlreadyExistForTpp_v3_1_2")
    fun shouldCreate_ExistsForTpp_v3_1_2() {
        // Given
        val credentials = events.clientCredentialsAuthentication(tpp)
        val version = OBVersion.v3_1_2
        val request = anEventSubscriptionRequest(version)
        val response = events.submitPost<OBEventSubscriptionResponse1>(url(version), request, credentials, tpp, version)
        assertThat(response.data.eventSubscriptionId).isNotEmpty()
        assertThat(response.data.callbackUrl).isEqualTo(request.data.callbackUrl)
        assertThat(response.data.version).isEqualTo(request.data.version)

        // When
        val exception = assertThrows(AssertionError::class.java) {
            events.submitPost<OBEventSubscriptionResponse1>(
                url(version),
                request,
                credentials,
                tpp,
                version
            )
        }

        // Then
        assertThat(exception.message.toString()).contains("OBRI.EventSubscription.Exists")
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(409)
    }

    @EnabledIfVersion(
        type = "events",
        apiVersion = "v3.1.6",
        operations = ["CreateEventSubscription"],
        apis = ["event-subscriptions"]
    )
    @Test
    @JvmName("shouldCreateTppEventSubscription_v3_1_6")
    fun shouldCreate_v3_1_6() {
        // Given
        val credentials = events.clientCredentialsAuthentication(tpp)
        val version = OBVersion.v3_1_6
        val request = anEventSubscriptionRequest(version)

        // When
        val response = events.submitPost<OBEventSubscriptionResponse1>(url(version), request, credentials, tpp, version)

        // Then
        assertThat(response.data.eventSubscriptionId).isNotEmpty()
        assertThat(response.links.self).isEqualTo(url(version))
        assertThat(response.data.callbackUrl).isEqualTo(request.data.callbackUrl)
        assertThat(response.data.version).isEqualTo(request.data.version)
    }

    private fun url(version: OBVersion): String {
        return events.getEventSubscriptionsUrl(version)
    }
}
