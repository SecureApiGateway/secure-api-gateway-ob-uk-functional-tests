package com.forgerock.securebanking.event

import com.forgerock.openbanking.common.model.openbanking.persistence.event.FREventNotification
import com.forgerock.openbanking.common.model.version.OBVersion
import com.forgerock.openbanking.constants.OpenBankingConstants
import com.forgerock.openbanking.jwt.model.CreateDetachedJwtResponse
import com.forgerock.openbanking.jwt.model.SigningRequest
import com.forgerock.securebanking.DOMAIN
import com.forgerock.securebanking.Tpp
import com.forgerock.securebanking.discovery.asDiscovery
import com.forgerock.securebanking.discovery.rsDiscovery
import com.forgerock.securebanking.event.EventsDataFactory.Companion.anEventDataRequest
import com.forgerock.securebanking.jsonBody
import com.forgerock.securebanking.responseObject
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.core.isSuccessful
import com.google.gson.GsonBuilder
import com.nimbusds.jose.JWSObject
import uk.org.openbanking.datamodel.event.OBEventPolling1
import uk.org.openbanking.datamodel.event.OBEventPollingResponse1
import java.util.*

class Events {

    companion object {
        private const val TAN = "openbanking.org.uk"
    }

    fun importEvents(
        tpp: Tpp,
        clientCredentials: com.forgerock.securebanking.AccessToken,
        version: OBVersion
    ): Collection<FREventNotification> {
        // call bespoke data api to import events
        val createEventData = createEventData<Collection<FREventNotification>>(tpp, clientCredentials, version)
        return createEventData;
    }

    fun pollForEvents(
        tpp: Tpp,
        clientCredentials: com.forgerock.securebanking.AccessToken,
        version: OBVersion
    ): OBEventPollingResponse1 {
        val url = getPollingUrl(version)
        val request = OBEventPolling1().returnImmediately(true)
        return submitPost(url, request, clientCredentials, tpp, version)
    }

    fun sendAcknowledgement(
        tpp: Tpp,
        clientCredentials: com.forgerock.securebanking.AccessToken,
        request: OBEventPolling1,
        version: OBVersion
    ): OBEventPollingResponse1 {
        val url = getPollingUrl(version)
        return submitPost(url, request, clientCredentials, tpp, version)
    }

    private inline fun <reified T : Any> createEventData(
        tpp: Tpp, accessToken: com.forgerock.securebanking.AccessToken, version: OBVersion =
            OBVersion.v3_1_2
    ): T {
        val frDataEventRequest = anEventDataRequest(tpp, version)
        val (_, response, r) = Fuel.post("https://matls.service.bank.$DOMAIN/api/data/events")
            .jsonBody(frDataEventRequest)
            .defaultHeaders(accessToken.access_token)
            .header("x-jws-signature", getDetachedJws(frDataEventRequest, tpp, version))
            .responseObject<T>()
        if (!response.isSuccessful) throw AssertionError(
            "Could not create event data URL with: ${String(response.data)}",
            r.component2()
        )
        return r.get()
    }

    inline fun <reified T : Any> submitPost(
        eventNotificationUrl: String,
        eventRequest: Any,
        accessToken: com.forgerock.securebanking.AccessToken,
        tpp: Tpp,
        version: OBVersion = OBVersion.v3_1_2
    ): T {
        val (_, response, r) = Fuel.post(eventNotificationUrl)
            .jsonBody(eventRequest)
            .defaultHeaders(accessToken.access_token)
            .header("x-jws-signature", getDetachedJws(eventRequest, tpp, version))
            .responseObject<T>()
        if (!response.isSuccessful) throw AssertionError(
            "Could not create Callback URL with: ${String(response.data)}",
            r.component2()
        )
        return r.get()
    }

    inline fun <reified T : Any> submitGet(
        eventNotificationUrl: String,
        accessToken: com.forgerock.securebanking.AccessToken
    ): T {
        val (_, response, r) = Fuel.get(eventNotificationUrl)
            .defaultHeaders(accessToken.access_token)
            .responseObject<T>()
        if (!response.isSuccessful) throw AssertionError(
            "Could not Get Callback URLS with: ${String(response.data)}",
            r.component2()
        )
        return r.get()
    }

    inline fun <reified T : Any> submitPut(
        eventNotificationUrl: String,
        id: String,
        eventRequest: Any,
        accessToken: com.forgerock.securebanking.AccessToken,
        tpp: Tpp,
        version: OBVersion = OBVersion.v3_1_2
    ): T {
        val (_, response, r) = Fuel.put("$eventNotificationUrl/$id")
            .jsonBody(eventRequest)
            .defaultHeaders(accessToken.access_token)
            .header("x-jws-signature", getDetachedJws(eventRequest, tpp, version))
            .responseObject<T>()
        if (!response.isSuccessful) throw AssertionError(
            "Could not amend Callback URL with: ${String(response.data)}",
            r.component2()
        )
        return r.get()
    }

    fun submitDelete(
        eventNotificationUrl: String,
        id: String,
        accessToken: com.forgerock.securebanking.AccessToken
    ): Response {
        val (_, response, r) = Fuel.delete("$eventNotificationUrl/$id")
            .defaultHeaders(accessToken.access_token)
            .responseObject<Response>()
        if (!response.isSuccessful) throw AssertionError(
            "Could not delete Callback URL with: ${String(response.data)}",
            r.component2()
        )
        return response
    }

    fun clientCredentialsAuthentication(tpp: Tpp): com.forgerock.securebanking.AccessToken {
        val scopes = asDiscovery.scopes_supported.intersect(
            listOf(
                OpenBankingConstants.Scope.OPENID,
                OpenBankingConstants.Scope.ACCOUNTS,
                OpenBankingConstants.Scope.PAYMENTS
            )
        ).joinToString(separator = " ")
        val clientCredentialsForm = listOf("grant_type" to "client_credentials", "scope" to scopes)
        val (_, response, result) = Fuel.post(asDiscovery.token_endpoint, parameters = clientCredentialsForm)
            .authentication()
            .basic(tpp.registrationResponse.client_id, tpp.registrationResponse.client_secret!!)
            .responseObject<com.forgerock.securebanking.AccessToken>()
        if (!response.isSuccessful) throw AssertionError("Could not authenticate", result.component2())
        return result.get()
    }

    fun clientCredentialsAuthentication(tpp: Tpp, scopes: String): com.forgerock.securebanking.AccessToken {
        val clientCredentialsForm = listOf("grant_type" to "client_credentials", "scope" to scopes)
        val (_, response, result) = Fuel.post(asDiscovery.token_endpoint, parameters = clientCredentialsForm)
            .authentication()
            .basic(tpp.registrationResponse.client_id, tpp.registrationResponse.client_secret!!)
            .responseObject<com.forgerock.securebanking.AccessToken>()
        if (!response.isSuccessful) throw AssertionError("Could not authenticate", result.component2())
        return result.get()
    }

    fun Request.defaultHeaders(accessToken: String) =
        this
            .header("Authorization", "Bearer $accessToken")
            // x-fapi-financial-id is no longer required in v3.1.2 onwards
            .header("x-fapi-financial-id", rsDiscovery.Data.FinancialId)
            .header("x-idempotency-key", UUID.randomUUID().toString())
            .header("Accept", "application/json")

    fun getDetachedJws(body: Any, tpp: Tpp, version: OBVersion): String {
        val softwareStatement = JWSObject.parse(tpp.registrationResponse.software_statement)
        val ssPayload = softwareStatement.payload.toJSONObject()
        val orgId: String = ssPayload["org_id"] as String
        val softwareId: String = ssPayload["software_id"] as String

        val (_, detachedJwtResponse, detachedJwt) = Fuel.post("https://jwkms.$DOMAIN/api/crypto/signPayloadToDetachedJwt")
            .header("issuerId", "$orgId/$softwareId")
            .header("signingRequest", serialisedSigningRequest(version))
            .jsonBody(body)
            .responseObject<CreateDetachedJwtResponse>()
        if (!detachedJwtResponse.isSuccessful) throw AssertionError(
            "Could not get detached JWS",
            detachedJwt.component2()
        )
        return detachedJwt.get().detachedSignature
    }

    fun getCreateCallbackUrl(version: OBVersion): String {
        val eventNotification = rsDiscovery.Data.EventNotificationAPI
            ?.first { it.Version == version.canonicalName }
            ?: throw IllegalStateException("Unable to get CreateCallbackUrl URL for version $version")
        return eventNotification.Links.links.CreateCallbackUrl
    }

    fun getEventSubscriptionsUrl(version: OBVersion): String {
        val eventNotification = rsDiscovery.Data.EventNotificationAPI
            ?.first { it.Version == version.canonicalName }
            ?: throw IllegalStateException("Unable to get CreateEventSubscription URL for version $version")
        return eventNotification.Links.links.CreateEventSubscription
    }

    private fun getPollingUrl(version: OBVersion): String {
        val eventNotification = rsDiscovery.Data.EventNotificationAPI
            ?.first { it.Version == version.canonicalName }
            ?: throw IllegalStateException("Unable to get EventAggregatedPolling URL for version $version")
        return eventNotification.Links.links.EventAggregatedPolling
    }

    private fun serialisedSigningRequest(version: OBVersion): String {
        val claimsBuilder = SigningRequest.CustomHeaderClaims.builder()
            .includeOBIss(true)
            .includeOBIat(true)
            .includeCrit(true)
            .tan(TAN)
        if (version.isBeforeVersion(OBVersion.v3_1_4)) {
            claimsBuilder.includeB64(true)
        }
        val signingRequest = SigningRequest.builder().customHeaderClaims(claimsBuilder.build()).build()
        return GsonBuilder().create().toJson(signingRequest)
    }
}
