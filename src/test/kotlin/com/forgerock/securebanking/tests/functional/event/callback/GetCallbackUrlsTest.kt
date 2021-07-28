package com.forgerock.securebanking.tests.functional.event.callback

import assertk.assertThat
import assertk.assertions.*
import com.forgerock.openbanking.common.model.version.OBVersion
import com.forgerock.securebanking.framework.data.Tpp
import com.forgerock.securebanking.framework.http.fuel.initFuelAsNewTpp
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.support.event.Events
import com.forgerock.securebanking.support.event.EventsDataFactory.Companion.aCallbackUrlRequest
import com.forgerock.securebanking.support.discovery.rsDiscovery
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.event.OBCallbackUrlResponse1
import uk.org.openbanking.datamodel.event.OBCallbackUrlsResponse1

/**
 * Callback URL API
 *
 * https://openbanking.atlassian.net/wiki/spaces/DZ/pages/1077806714/Callback+URL+API+-+v3.1.2
 *
 * - A TPP must only create a callback on one version
 */
@Tags(Tag("eventTest"))
class GetCallbackUrlsTest {

    private lateinit var tpp: Tpp
    private val events = Events()

    @BeforeEach
    fun setup() {
        this.tpp = initFuelAsNewTpp().apply { dynamicRegistration() }
    }

    @EnabledIfVersion(
        type = "events",
        apiVersion = "v3.0",
        operations = ["CreateCallbackUrl", "GetCallbackUrls"],
        apis = ["callback-urls"]
    )
    @Test
    fun shouldGetTppCallbackURL_v3_0() {
        // Given
        val clientCredentials = events.clientCredentialsAuthentication(tpp)
        val version = OBVersion.v3_0
        val createRequest = aCallbackUrlRequest(version)
        val createResponse = events.submitPost<OBCallbackUrlResponse1>(
            createUrl(version),
            createRequest,
            clientCredentials,
            tpp,
            version
        )

        // When
        val getResponse = events.submitGet<OBCallbackUrlsResponse1>(callbacksUrl(version), clientCredentials)

        // Then
        assertThat(getResponse.data.callbackUrl.toString()).contains(createResponse.data.callbackUrlId)
        assertThat(getResponse.data.callbackUrl.toString()).contains(createRequest.data.url)
        assertThat(getResponse.data.callbackUrl.toString()).contains(createRequest.data.version)
        assertThat(getResponse.links).isNull()
        assertThat(getResponse.meta).isNull()
    }

    @EnabledIfVersion(
        type = "events",
        apiVersion = "v3.0",
        operations = ["GetCallbackUrls"],
        apis = ["callback-urls"]
    )
    @Test
    @JvmName("shouldEmptyGetTppCallbackURL_DoesNotExist_v3_0")
    fun emptyGet_DoesNotExist_v3_0() {
        // Given
        val clientCredentials = events.clientCredentialsAuthentication(tpp)
        val version = OBVersion.v3_0

        // When
        val response = events.submitGet<OBCallbackUrlsResponse1>(callbacksUrl(version), clientCredentials)
        assertThat(response.data.callbackUrl).isEmpty()
    }

    @EnabledIfVersion(
        type = "events",
        apiVersion = "v3.0",
        postCreateVersion = "v3.1",
        operations = ["CreateCallbackUrl", "GetCallbackUrls"],
        apis = ["callback-urls"]
    )
    @Test
    @JvmName("shouldGetTppCallbackURL_v3_0_viaNewerApi_v3_1")
    fun get_v3_0_via_v3_1() {
        // Given
        val clientCredentials = events.clientCredentialsAuthentication(tpp)
        var version = OBVersion.v3_0
        val createRequest = aCallbackUrlRequest(version)
        val createResponse = events.submitPost<OBCallbackUrlResponse1>(
            createUrl(version),
            createRequest,
            clientCredentials,
            tpp,
            version
        )
        version = OBVersion.v3_1

        // When
        val getResponse = events.submitGet<OBCallbackUrlsResponse1>(callbacksUrl(version), clientCredentials)

        // Then
        assertThat(getResponse.data.callbackUrl.toString()).contains(createResponse.data.callbackUrlId)
        assertThat(getResponse.data.callbackUrl.toString()).contains(createRequest.data.url)
        assertThat(getResponse.data.callbackUrl.toString()).contains(createRequest.data.version)
        assertThat(getResponse.links).isNotNull()
        assertThat(getResponse.meta).isNotNull()
    }

    @EnabledIfVersion(
        type = "events",
        apiVersion = "v3.1",
        postCreateVersion = "v3.0",
        operations = ["CreateCallbackUrl", "GetCallbackUrls"],
        apis = ["callback-urls"]
    )
    @Test
    @JvmName("shouldBeEmptyGetTppCallbackURL_v3_1_viaOlderVersion_v3_0")
    fun emptyGet_v3_1_via_v3_0() {
        // Given
        val clientCredentials = events.clientCredentialsAuthentication(tpp)
        var version = OBVersion.v3_1
        val createRequest = aCallbackUrlRequest(version)
        events.submitPost<OBCallbackUrlResponse1>(createUrl(version), createRequest, clientCredentials, tpp, version)
        version = OBVersion.v3_0

        // When
        val getResponse = events.submitGet<OBCallbackUrlsResponse1>(callbacksUrl(version), clientCredentials)

        // Then
        assertThat(getResponse.data.callbackUrl).isEmpty()
    }

    @EnabledIfVersion(
        type = "events",
        apiVersion = "v3.1",
        operations = ["CreateCallbackUrl", "GetCallbackUrls"],
        apis = ["callback-urls"]
    )
    @Test
    @JvmName("shouldEmptyGetTppCallbackURL_v3_1")
    fun emptyGet_v3_1() {
        // Given
        val clientCredentials = events.clientCredentialsAuthentication(tpp)
        val version = OBVersion.v3_1
        val createRequest = aCallbackUrlRequest(version)
        val createResponse = events.submitPost<OBCallbackUrlResponse1>(
            createUrl(version),
            createRequest,
            clientCredentials,
            tpp,
            version
        )

        // When
        val getResponse = events.submitGet<OBCallbackUrlsResponse1>(callbacksUrl(version), clientCredentials)

        // Then
        assertThat(getResponse.data.callbackUrl.toString()).contains(createResponse.data.callbackUrlId)
        assertThat(getResponse.data.callbackUrl.toString()).contains(createRequest.data.url)
        assertThat(getResponse.data.callbackUrl.toString()).contains(createRequest.data.version)
        assertThat(getResponse.links).isNotNull()
        assertThat(getResponse.meta).isNotNull()
    }

    @EnabledIfVersion(
        type = "events",
        apiVersion = "v3.1.1",
        operations = ["CreateCallbackUrl", "GetCallbackUrls"],
        apis = ["callback-urls"]
    )
    @Test
    @JvmName("shouldGetTppCallbackURL_v3_1_1")
    fun get_v3_1_1() {
        // Given
        val clientCredentials = events.clientCredentialsAuthentication(tpp)
        val version = OBVersion.v3_1_1
        val createRequest = aCallbackUrlRequest(version)
        val createResponse = events.submitPost<OBCallbackUrlResponse1>(
            createUrl(version),
            createRequest,
            clientCredentials,
            tpp,
            version
        )

        // When
        val getResponse = events.submitGet<OBCallbackUrlsResponse1>(callbacksUrl(version), clientCredentials)

        // Then
        assertThat(getResponse.data.callbackUrl.toString()).contains(createResponse.data.callbackUrlId)
        assertThat(getResponse.data.callbackUrl.toString()).contains(createRequest.data.url)
        assertThat(getResponse.data.callbackUrl.toString()).contains(createRequest.data.version)
        assertThat(getResponse.links).isNotNull()
        assertThat(getResponse.meta).isNotNull()
    }

    @EnabledIfVersion(
        type = "events",
        apiVersion = "v3.1.6",
        operations = ["CreateCallbackUrl", "GetCallbackUrls"],
        apis = ["callback-urls"]
    )
    @Test
    @JvmName("shouldGetTppCallbackURL_v3_1_6")
    fun get_v3_1_6() {
        // Given
        val clientCredentials = events.clientCredentialsAuthentication(tpp)
        val version = OBVersion.v3_1_6
        val createRequest = aCallbackUrlRequest(version)
        val createResponse = events.submitPost<OBCallbackUrlResponse1>(
            createUrl(version),
            createRequest,
            clientCredentials,
            tpp,
            version
        )

        // When
        val getResponse = events.submitGet<OBCallbackUrlsResponse1>(callbacksUrl(version), clientCredentials)

        // Then
        assertThat(getResponse.data.callbackUrl.toString()).contains(createResponse.data.callbackUrlId)
        assertThat(getResponse.data.callbackUrl.toString()).contains(createRequest.data.url)
        assertThat(getResponse.data.callbackUrl.toString()).contains(createRequest.data.version)
        assertThat(getResponse.links).isNotNull()
        assertThat(getResponse.meta).isNotNull()
    }

    @EnabledIfVersion(
        type = "events",
        apiVersion = "v3.1.6",
        operations = ["GetCallbackUrls"],
        apis = ["callback-urls"]
    )
    @Test
    @JvmName("shouldFailGetTppCallbackURL_DoesNotExist_v3_1_6")
    fun failGet_v3_1_6() {
        // Given
        val clientCredentials = events.clientCredentialsAuthentication(tpp)
        val version = OBVersion.v3_1_6

        // When
        val response = events.submitGet<OBCallbackUrlsResponse1>(callbacksUrl(version), clientCredentials)
        assertThat(response.data.callbackUrl).isNullOrEmpty()
    }

    @EnabledIfVersion(
        type = "events",
        apiVersion = "v3.1.5",
        postCreateVersion = "v3.1.6",
        operations = ["CreateCallbackUrl", "GetCallbackUrls"],
        apis = ["callback-urls"]
    )
    @Test
    @JvmName("shouldGetTppCallbackURL_v3_1_5_viaNewerVersion_v3_1_6")
    fun get_v3_1_5_via_v3_1_6() {
        // Given
        val clientCredentials = events.clientCredentialsAuthentication(tpp)
        var version = OBVersion.v3_1_5
        val createRequest = aCallbackUrlRequest(version)
        val createResponse = events.submitPost<OBCallbackUrlResponse1>(
            createUrl(version),
            createRequest,
            clientCredentials,
            tpp,
            version
        )
        version = OBVersion.v3_1_6

        // When
        val getResponse = events.submitGet<OBCallbackUrlsResponse1>(callbacksUrl(version), clientCredentials)

        // Then
        assertThat(getResponse.data.callbackUrl.toString()).contains(createResponse.data.callbackUrlId)
        assertThat(getResponse.data.callbackUrl.toString()).contains(createRequest.data.url)
        assertThat(getResponse.data.callbackUrl.toString()).contains(createRequest.data.version)
        assertThat(getResponse.links).isNotNull()
        assertThat(getResponse.meta).isNotNull()
    }

    @EnabledIfVersion(
        type = "events",
        apiVersion = "v3.1.6",
        postCreateVersion = "v3.1.5",
        operations = ["CreateCallbackUrl", "GetCallbackUrls"],
        apis = ["callback-urls"]
    )
    @Test
    @JvmName("shouldBeEmptyGetTppCallbackURL_v3_1_6_viaOlderVersion_v3_1_5")
    fun emptyGet_v3_1_6_via_v3_1_5() {
        // Given
        val clientCredentials = events.clientCredentialsAuthentication(tpp)
        var version = OBVersion.v3_1_6
        val createRequest = aCallbackUrlRequest(version)
        events.submitPost<OBCallbackUrlResponse1>(createUrl(version), createRequest, clientCredentials, tpp, version)
        version = OBVersion.v3_1_5

        // When
        val getResponse = events.submitGet<OBCallbackUrlsResponse1>(callbacksUrl(version), clientCredentials)

        // Then
        assertThat(getResponse.data.callbackUrl).isEmpty()
    }

    private fun createUrl(version: OBVersion): String {
        return events.getCreateCallbackUrl(version)
    }

    private fun callbacksUrl(version: OBVersion): String {
        val eventNotification = rsDiscovery.Data.EventNotificationAPI
            ?.first { it.Version == version.canonicalName }
            ?: throw IllegalStateException("Unable to get GetCallbackUrls URL for version $version")
        return eventNotification.Links.links.GetCallbackUrls
    }
}
