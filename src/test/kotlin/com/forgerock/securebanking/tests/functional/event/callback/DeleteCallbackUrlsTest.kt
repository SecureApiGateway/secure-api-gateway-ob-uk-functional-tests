package com.forgerock.securebanking.tests.functional.event.callback

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import com.forgerock.openbanking.common.model.version.OBVersion
import com.forgerock.securebanking.framework.data.Tpp
import com.forgerock.securebanking.framework.http.fuel.initFuelAsNewTpp
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.support.event.Events
import com.forgerock.securebanking.support.event.EventsDataFactory.Companion.aCallbackUrlRequest
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.isSuccessful
import org.apache.http.HttpStatus
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
class DeleteCallbackUrlsTest {

    private lateinit var tpp: Tpp
    private val events = Events()

    @BeforeEach
    fun setup() {
        this.tpp = initFuelAsNewTpp().apply { dynamicRegistration() }
    }

    @EnabledIfVersion(type = "events", apiVersion = "v3.0", apis = ["callback-urls"])
    @Test
    fun shouldDeleteTppCallbackURL_v3_0() {
        // Given
        val credentials = events.clientCredentialsAuthentication(tpp)
        val version = OBVersion.v3_0
        val createRequest = aCallbackUrlRequest(version)
        val createResponse =
            events.submitPost<OBCallbackUrlResponse1>(url(version), createRequest, credentials, tpp, version)

        // When
        val response = events.submitDelete(url(version), createResponse.data.callbackUrlId, credentials)

        // Then
        assertThat(response.isSuccessful)
        assertThat(response.statusCode.dec() == HttpStatus.SC_NO_CONTENT)
    }

    @EnabledIfVersion(type = "events", apiVersion = "v3.1.1", apis = ["callback-urls"])
    @Test
    fun shouldDeleteTppCallbackURL_v3_1_1() {
        // Given
        val credentials = events.clientCredentialsAuthentication(tpp)
        val version = OBVersion.v3_1_1
        val createRequest = aCallbackUrlRequest(version)
        val createResponse =
            events.submitPost<OBCallbackUrlResponse1>(url(version), createRequest, credentials, tpp, version)

        // When
        val response = events.submitDelete(url(version), createResponse.data.callbackUrlId, credentials)

        // Then
        assertThat(response.isSuccessful)
        assertThat(response.statusCode.dec() == HttpStatus.SC_NO_CONTENT)
    }

    @EnabledIfVersion(
        type = "events",
        apiVersion = "v3.1.1",
        postCreateVersion = "v3.1",
        operations = ["CreateCallbackUrl"],
        apis = ["callback-urls"]
    )
    @Test
    @JvmName("shouldFailDeleteTppCallbackURL_v3_1_1_viaOlderVersion_v3_1")
    fun failDelTppCallbackURL_v3_1_1_via_v3_1() {
        // Given
        val credentials = events.clientCredentialsAuthentication(tpp)
        var version = OBVersion.v3_1_1
        val createRequest = aCallbackUrlRequest(version)
        val createResponse =
            events.submitPost<OBCallbackUrlResponse1>(url(version), createRequest, credentials, tpp, version)
        version = OBVersion.v3_1

        // When
        val exception = assertThrows(AssertionError::class.java) {
            events.submitDelete(
                url(version),
                createResponse.data.callbackUrlId,
                credentials
            )
        }
        assertThat(exception.message.toString()).contains("can't be delete via an older API version")
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(409)
    }

    @EnabledIfVersion(
        type = "events",
        apiVersion = "v3.1.2",
        operations = ["CreateCallbackUrl"],
        apis = ["callback-urls"]
    )
    @Test
    fun shouldDeleteTppCallbackURL_v3_1_2() {
        // Given
        val credentials = events.clientCredentialsAuthentication(tpp)
        val version = OBVersion.v3_1_2
        val createRequest = aCallbackUrlRequest(version)
        val createResponse =
            events.submitPost<OBCallbackUrlResponse1>(url(version), createRequest, credentials, tpp, version)

        // When
        val response = events.submitDelete(url(version), createResponse.data.callbackUrlId, credentials)

        // Then
        assertThat(response.isSuccessful)
        assertThat(response.statusCode.dec() == HttpStatus.SC_NO_CONTENT)
    }

    @EnabledIfVersion(
        type = "events",
        apiVersion = "v3.1.6",
        operations = ["CreateCallbackUrl"],
        apis = ["callback-urls"]
    )
    @Test
    fun shouldDeleteTppCallbackURL_v3_1_6() {
        // Given
        val credentials = events.clientCredentialsAuthentication(tpp)
        val version = OBVersion.v3_1_6
        val createRequest = aCallbackUrlRequest(version)
        val createResponse =
            events.submitPost<OBCallbackUrlResponse1>(url(version), createRequest, credentials, tpp, version)

        // When
        val response = events.submitDelete(url(version), createResponse.data.callbackUrlId, credentials)

        // Then
        assertThat(response.isSuccessful)
        assertThat(response.statusCode.dec() == HttpStatus.SC_NO_CONTENT)
    }

    @EnabledIfVersion(
        type = "events",
        apiVersion = "v3.1.6",
        postCreateVersion = "v3.1.1",
        operations = ["CreateCallbackUrl"],
        apis = ["callback-urls"]
    )
    @Test
    @JvmName("shouldFailDeleteTppCallbackURL_v3_1_6_viaOlderVersion_v3_1_1")
    fun failDelTppCallbackURL_v3_1_6_via_v3_1_1() {
        // Given
        val credentials = events.clientCredentialsAuthentication(tpp)
        var version = OBVersion.v3_1_6
        val createRequest = aCallbackUrlRequest(version)
        val createResponse =
            events.submitPost<OBCallbackUrlResponse1>(url(version), createRequest, credentials, tpp, version)
        version = OBVersion.v3_1_1

        // When
        val exception = assertThrows(AssertionError::class.java) {
            events.submitDelete(
                url(version),
                createResponse.data.callbackUrlId,
                credentials
            )
        }
        assertThat(exception.message.toString()).contains("can't be delete via an older API version")
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(409)
    }


    @EnabledIfVersion(
        type = "events",
        apiVersion = "v3.1.6",
        postCreateVersion = "v3.1.3",
        operations = ["CreateCallbackUrl"],
        apis = ["callback-urls"]
    )
    @Test
    @JvmName("shouldFailDeleteTppCallbackURL_v3_1_6_viaOlderVersion_v3_1_3")
    fun failDelTppCallbackURL_v3_1_6_via_v3_1_3() {
        // Given
        val credentials = events.clientCredentialsAuthentication(tpp)
        var version = OBVersion.v3_1_6
        val createRequest = aCallbackUrlRequest(version)
        val createResponse =
            events.submitPost<OBCallbackUrlResponse1>(url(version), createRequest, credentials, tpp, version)
        version = OBVersion.v3_1_3

        // When
        val exception = assertThrows(AssertionError::class.java) {
            events.submitDelete(
                url(version),
                createResponse.data.callbackUrlId,
                credentials
            )
        }
        assertThat(exception.message.toString()).contains("can't be delete via an older API version")
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(409)
    }

    private fun url(version: OBVersion): String {
        return events.getCreateCallbackUrl(version)
    }

}
