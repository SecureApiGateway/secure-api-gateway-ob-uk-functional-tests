package com.forgerock.securebanking.event.callback

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import com.forgerock.openbanking.common.model.version.OBVersion
import com.forgerock.securebanking.Tpp
import com.forgerock.securebanking.event.Events
import com.forgerock.securebanking.event.EventsDataFactory
import com.forgerock.securebanking.initFuelAsNewTpp
import com.forgerock.securebanking.junit.EnabledIfVersion
import com.github.kittinunf.fuel.core.FuelError
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.event.OBCallbackUrlResponse1

/**
 * Callback URL API
 *
 * https://openbanking.atlassian.net/wiki/spaces/DZ/pages/1077806714/Callback+URL+API+-+v3.1.2
 *
 * - A TPP must only create a callback on one version
 */
@Tags(Tag("eventTest"))
class UpdateCallbackUrlsTest {

    private lateinit var tpp: Tpp
    private val events = Events()

    @BeforeEach
    fun setup() {
        this.tpp = initFuelAsNewTpp().apply { dynamicRegistration() }
    }

    @EnabledIfVersion(
        type = "events",
        apiVersion = "v3.0",
        operations = ["CreateCallbackUrl"],
        apis = ["callback-urls"]
    )
    @Test
    // Note, the use of @JvmName is necessary to avoid `File Name Too Long` errors when compiling on Linux with it's
    // 255 byte filename limit.
    @JvmName("shouldUpdateTppCallbackURL_v3_0")
    fun shouldUpdate_v3_0() {
        // Given
        val credentials = events.clientCredentialsAuthentication(tpp)
        val version = OBVersion.v3_0
        val createRequest = EventsDataFactory.aCallbackUrlRequest(version)
        val createResponse =
            events.submitPost<OBCallbackUrlResponse1>(url(version), createRequest, credentials, tpp, version)
        val callbackUrlId = createResponse.data.callbackUrlId

        // When
        val updateResponse = events.submitPut<OBCallbackUrlResponse1>(
            url(version),
            callbackUrlId,
            createResponse,
            credentials,
            tpp,
            version
        )

        // Then
        assertThat(updateResponse.data.callbackUrlId).contains(createResponse.data.callbackUrlId)
        assertThat(updateResponse.data.url).contains(createRequest.data.url)
        assertThat(updateResponse.data.version).contains(createRequest.data.version)
        assertThat(updateResponse.links).isNull()
        assertThat(updateResponse.meta).isNull()

    }

    @EnabledIfVersion(
        type = "events",
        apiVersion = "v3.1",
        operations = ["CreateCallbackUrl"],
        apis = ["callback-urls"]
    )
    @Test
    @JvmName("shouldUpdateTppCallbackURL_v3_1")
    fun shouldUpdate_v3_1() {
        // Given
        val credentials = events.clientCredentialsAuthentication(tpp)
        val version = OBVersion.v3_1
        val createRequest = EventsDataFactory.aCallbackUrlRequest(version)
        val createResponse =
            events.submitPost<OBCallbackUrlResponse1>(url(version), createRequest, credentials, tpp, version)
        val callbackUrlId = createResponse.data.callbackUrlId

        // When
        val updateResponse = events.submitPut<OBCallbackUrlResponse1>(
            url(version),
            callbackUrlId,
            createResponse,
            credentials,
            tpp,
            version
        )

        // Then
        assertThat(updateResponse.data.callbackUrlId).contains(createResponse.data.callbackUrlId)
        assertThat(updateResponse.data.url).contains(createRequest.data.url)
        assertThat(updateResponse.data.version).contains(createRequest.data.version)
        assertThat(updateResponse.links).isNotNull()
        assertThat(updateResponse.meta).isNotNull()
    }

    @EnabledIfVersion(
        type = "events",
        apiVersion = "v3.1",
        postCreateVersion = "v3.0",
        operations = ["CreateCallbackUrl"],
        apis = ["callback-urls"]
    )
    @Test
    @JvmName("shouldFailUpdateTppCallbackURL_viaOlderVersion_v3_0")
    fun failUpdate_v3_1_via_v3_0() {
        // Given
        val credentials = events.clientCredentialsAuthentication(tpp)
        var version = OBVersion.v3_1
        val createRequest = EventsDataFactory.aCallbackUrlRequest(version)
        val createResponse =
            events.submitPost<OBCallbackUrlResponse1>(url(version), createRequest, credentials, tpp, version)
        val callbackUrlId = createResponse.data.callbackUrlId
        version = OBVersion.v3_0

        // When
        val exception = assertThrows(AssertionError::class.java) {
            events.submitPut<OBCallbackUrlResponse1>(
                url(version),
                callbackUrlId,
                createResponse,
                credentials,
                tpp,
                version
            )
        }
        assertThat(exception.message.toString()).contains("can't be update via an older API version")
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(409)
    }

    @EnabledIfVersion(
        type = "events",
        apiVersion = "v3.1.1",
        operations = ["CreateCallbackUrl"],
        apis = ["callback-urls"]
    )
    @Test
    @JvmName("shouldUpdateTppCallbackURL_v3_1_1")
    fun shouldUpdate_v3_1_1() {
        // Given
        val credentials = events.clientCredentialsAuthentication(tpp)
        val version = OBVersion.v3_1_1
        val createRequest = EventsDataFactory.aCallbackUrlRequest(version)
        val createResponse =
            events.submitPost<OBCallbackUrlResponse1>(url(version), createRequest, credentials, tpp, version)
        val callbackUrlId = createResponse.data.callbackUrlId

        // When
        val updateResponse = events.submitPut<OBCallbackUrlResponse1>(
            url(version),
            callbackUrlId,
            createResponse,
            credentials,
            tpp,
            version
        )

        // Then
        assertThat(updateResponse.data.callbackUrlId).contains(createResponse.data.callbackUrlId)
        assertThat(updateResponse.data.url).contains(createRequest.data.url)
        assertThat(updateResponse.data.version).contains(createRequest.data.version)
        assertThat(updateResponse.links).isNotNull()
        assertThat(updateResponse.meta).isNotNull()
    }

    @EnabledIfVersion(
        type = "events",
        apiVersion = "v3.1.2",
        operations = ["CreateCallbackUrl"],
        apis = ["callback-urls"]
    )
    @Test
    @JvmName("shouldUpdateTppCallbackURL_v3_1_2")
    fun shouldUpdate_v3_1_2() {
        // Given
        val credentials = events.clientCredentialsAuthentication(tpp)
        val version = OBVersion.v3_1_2
        val createRequest = EventsDataFactory.aCallbackUrlRequest(version)
        val createResponse =
            events.submitPost<OBCallbackUrlResponse1>(url(version), createRequest, credentials, tpp, version)
        val callbackUrlId = createResponse.data.callbackUrlId

        // When
        val updateResponse = events.submitPut<OBCallbackUrlResponse1>(
            url(version),
            callbackUrlId,
            createResponse,
            credentials,
            tpp,
            version
        )

        // Then
        assertThat(updateResponse.data.callbackUrlId).contains(createResponse.data.callbackUrlId)
        assertThat(updateResponse.data.url).contains(createRequest.data.url)
        assertThat(updateResponse.data.version).contains(createRequest.data.version)
        assertThat(updateResponse.links).isNotNull()
        assertThat(updateResponse.meta).isNotNull()
    }

    @EnabledIfVersion(
        type = "events",
        apiVersion = "v3.1.6",
        operations = ["CreateCallbackUrl"],
        apis = ["callback-urls"]
    )
    @Test
    @JvmName("shouldUpdateTppCallbackURL_v3_1_6")
    fun shouldUpdate_v3_1_6() {
        // Given
        val credentials = events.clientCredentialsAuthentication(tpp)
        val version = OBVersion.v3_1_6
        val createRequest = EventsDataFactory.aCallbackUrlRequest(version)
        val createResponse =
            events.submitPost<OBCallbackUrlResponse1>(url(version), createRequest, credentials, tpp, version)
        val callbackUrlId = createResponse.data.callbackUrlId

        // When
        val updateResponse = events.submitPut<OBCallbackUrlResponse1>(
            url(version),
            callbackUrlId,
            createResponse,
            credentials,
            tpp,
            version
        )

        // Then
        assertThat(updateResponse.data.callbackUrlId).contains(createResponse.data.callbackUrlId)
        assertThat(updateResponse.data.url).contains(createRequest.data.url)
        assertThat(updateResponse.data.version).contains(createRequest.data.version)
        assertThat(updateResponse.links).isNotNull()
        assertThat(updateResponse.meta).isNotNull()
    }

    @EnabledIfVersion(
        type = "events",
        apiVersion = "v3.1.6",
        postCreateVersion = "v3.1.4",
        operations = ["CreateCallbackUrl"],
        apis = ["callback-urls"]
    )
    @Test
    @JvmName("shouldFailUpdateTppCallbackURL_v3_1_6_viaOlderVersion_v3_1_4")
    fun failUpdate_v3_1_6_via_v3_1_4() {
        // Given
        val credentials = events.clientCredentialsAuthentication(tpp)
        var version = OBVersion.v3_1_6
        val createRequest = EventsDataFactory.aCallbackUrlRequest(version)
        val createResponse =
            events.submitPost<OBCallbackUrlResponse1>(url(version), createRequest, credentials, tpp, version)
        val callbackUrlId = createResponse.data.callbackUrlId
        version = OBVersion.v3_1_4

        // When
        val exception = assertThrows(AssertionError::class.java) {
            events.submitPut<OBCallbackUrlResponse1>(
                url(version),
                callbackUrlId,
                createResponse,
                credentials,
                tpp,
                version
            )
        }
        assertThat(exception.message.toString()).contains("can't be update via an older API version")
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(409)
    }

    private fun url(version: OBVersion): String {
        return events.getCreateCallbackUrl(version)
    }
}
