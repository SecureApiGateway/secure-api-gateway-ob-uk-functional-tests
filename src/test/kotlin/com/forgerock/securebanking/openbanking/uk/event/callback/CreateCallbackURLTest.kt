package com.forgerock.openbanking.event.callback

import assertk.assertThat
import assertk.assertions.*
import com.forgerock.openbanking.Tpp
import com.forgerock.openbanking.common.model.version.OBVersion
import com.forgerock.openbanking.event.Events
import com.forgerock.openbanking.event.EventsDataFactory.Companion.aCallbackUrlRequest
import com.forgerock.openbanking.initFuelAsNewTpp
import com.forgerock.openbanking.junit.EnabledIfOpenBankingVersion
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
class CreateCallbackURLTest {

    private lateinit var tpp: Tpp
    private val events = Events()

    @BeforeEach
    fun setup() {
        this.tpp = initFuelAsNewTpp().apply { dynamicRegistration() }
    }

    /*
     ****************
     * v3.0 tests
     ****************
     */
    @EnabledIfOpenBankingVersion(type = "events", version = "v3.0", apis = ["callback-urls"])
    @Test
    fun shouldCreateTppCallbackURL_v3_0() {
        // Given
        val credentials = events.clientCredentialsAuthentication(tpp)
        val version = OBVersion.v3_0
        val request = aCallbackUrlRequest(version)

        // When
        val response = events.submitPost<OBCallbackUrlResponse1>(url(version), request, credentials, tpp, version)

        // Then
        assertThat(response.data.callbackUrlId).isNotEmpty()
        assertThat(response.data.url).isEqualTo(request.data.url)
        assertThat(response.data.version).isEqualTo(request.data.version)
        assertThat(response.links).isNull()
        assertThat(response.meta).isNull()


    }

    @EnabledIfOpenBankingVersion(type = "events", version = "v3.0", apis = ["callback-urls"])
    @Test
    // Note, the use of @JvmName is necessary to avoid `File Name Too Long` errors when compiling on Linux with it's
    // 255 byte filename limit.
    @JvmName("shouldFailToCreateTppCallbackURL_AlreadyExistForTpp_v3_0")
    fun failCreateTppCallbkURL_ExistForTpp_v3_0() {
        // Given
        val credentials = events.clientCredentialsAuthentication(tpp)
        val version = OBVersion.v3_0
        val request = aCallbackUrlRequest(version)
        // create a callback url for tpp
        val response = events.submitPost<OBCallbackUrlResponse1>(url(version), request, credentials, tpp, version)
        assertThat(response.data.callbackUrlId).isNotEmpty()
        assertThat(response.data.url).isEqualTo(request.data.url)
        assertThat(response.data.version).isEqualTo(request.data.version)

        // When
        val exception = assertThrows(AssertionError::class.java) { events.submitPost<OBCallbackUrlResponse1>(url(version), request, credentials, tpp, version) }
        assertThat(exception.message).isEqualTo("Could not create Callback URL with: Callback URL already exists")
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(409)
    }

    @EnabledIfOpenBankingVersion(type = "events", version = "v3.0", apis = ["callback-urls"])
    @Test
    @JvmName("shouldFailToCreateTppCallbackURL_GivenVersionMismatch_v3_0")
    fun failCreateTppCallbkURL_VersionMismatch_v3_0() {
        // Given
        val credentials = events.clientCredentialsAuthentication(tpp)
        val version = OBVersion.v3_0
        val request = aCallbackUrlRequest(version)
        request.data.version(OBVersion.v3_1_3.canonicalVersion)

        // When
        val exception = assertThrows(AssertionError::class.java) { events.submitPost<OBCallbackUrlResponse1>(url(version), request, credentials, tpp, version) }
        assertThat(exception.message.toString()).contains("Version on the callback url field https://tpp.domain.test.net/open-banking/v3.0/event-notifications doesn't match with the version value field 3.1.3")
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
    }

    /*
     ****************
     * v3.1.6 tests
     ****************
     */
    @EnabledIfOpenBankingVersion(type = "events", version = "v3.1.6", apis = ["callback-urls"])
    @Test
    fun shouldCreateTppCallbackURL_v3_1_6() {
        // Given
        val credentials = events.clientCredentialsAuthentication(tpp)
        val version = OBVersion.v3_1_6
        val request = aCallbackUrlRequest(version)

        // When
        val response = events.submitPost<OBCallbackUrlResponse1>(url(version), request, credentials, tpp, version)

        // Then
        assertThat(response.data.callbackUrlId).isNotEmpty()
        assertThat(response.data.url).isEqualTo(request.data.url)
        assertThat(response.data.version).isEqualTo(request.data.version)
        assertThat(response.links).isNotNull()
        assertThat(response.meta).isNotNull()
    }

    @EnabledIfOpenBankingVersion(type = "events", version = "v3.1.6", apis = ["callback-urls"])
    @Test
    @JvmName("shouldFailToCreateTppCallbackURL_AlreadyExistForTpp_v3_1_6")
    fun failCreateTppCallbkURL_ExistForTpp_v3_1_6() {
        // Given
        val credentials = events.clientCredentialsAuthentication(tpp)
        val version = OBVersion.v3_1_6
        val request = aCallbackUrlRequest(version)
        // create a callback url for tpp
        val response = events.submitPost<OBCallbackUrlResponse1>(url(version), request, credentials, tpp, version)
        assertThat(response.data.callbackUrlId).isNotEmpty()
        assertThat(response.data.url).isEqualTo(request.data.url)
        assertThat(response.data.version).isEqualTo(request.data.version)

        // When
        val exception = assertThrows(AssertionError::class.java) { events.submitPost<OBCallbackUrlResponse1>(url(version), request, credentials, tpp, version) }
        assertThat(exception.message.toString()).contains("OBRI.EventSubscription.Exists")
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(409)
    }

    @EnabledIfOpenBankingVersion(type = "events", version = "v3.1.6", apis = ["callback-urls"])
    @Test
    @JvmName("shouldFailToCreateTppCallbackURL_GivenVersionMismatch_v3_1_6")
    fun failCreateTppCallbkURL_VersionMismatch_v3_1_6() {
        // Given
        val credentials = events.clientCredentialsAuthentication(tpp)
        val version = OBVersion.v3_1_6
        val request = aCallbackUrlRequest(version)
        request.data.version(OBVersion.v3_1_2.canonicalVersion)

        // When
        val exception = assertThrows(AssertionError::class.java) { events.submitPost<OBCallbackUrlResponse1>(url(version), request, credentials, tpp, version) }
        assertThat(exception.message.toString()).contains("Version on the callback url field https://tpp.domain.test.net/open-banking/v3.1.6/event-notifications doesn't match with the version value field 3.1.2")
        assertThat((exception.cause as FuelError).response.statusCode).isEqualTo(400)
    }

    private fun url(version: OBVersion): String {
        return events.getCreateCallbackUrl(version)
    }
}