package com.forgerock.sapi.gateway.ob.uk.support.event

import com.forgerock.sapi.gateway.framework.configuration.MTLS_SERVER
import com.forgerock.sapi.gateway.framework.data.AccessToken
import com.forgerock.sapi.gateway.framework.data.Tpp
import com.forgerock.sapi.gateway.framework.http.fuel.jsonBody
import com.forgerock.sapi.gateway.framework.http.fuel.responseObject
import com.forgerock.sapi.gateway.ob.uk.common.datamodel.event.FREventMessages
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.isSuccessful
import uk.org.openbanking.datamodel.event.OBEventPolling1

const val EVENT_ADMIN_API_SCOPES = "openid payments accounts fundsconfirmations"

class EventNotificationRS {

    fun getClientCredentialsAdminAccessToken(tpp: Tpp): AccessToken {
        return tpp.getClientCredentialsAccessToken(EVENT_ADMIN_API_SCOPES)
    }

    fun getClientCredentialsAccessToken(tpp: Tpp, scopes: List<String>): AccessToken {
        return tpp.getClientCredentialsAccessToken(scopes.joinToString(separator = " "))
    }

    fun getEventsAdminAPIUrl(): String {
        return "${MTLS_SERVER}/rs/admin/data/events"
    }

    inline fun <reified T : Any> importDataEvent(
            eventRequest: EventsDataFactory.Companion.FRDataEvent, accessToken: String
    ): T {
        val (_, response, result) = Fuel.post(getEventsAdminAPIUrl())
                .jsonBody(eventRequest)
                .header("Authorization", "Bearer $accessToken")
                .responseObject<T>()
        if (!response.isSuccessful) throw AssertionError(
                "Could not import event: ${String(response.data)}",
                result.component2()
        )
        return result.get()
    }

    inline fun <reified T : Any> exportEvents(apiClientId: String, adminAccessToken: String): T {
        val (_, response, result) = Fuel.get(getEventsAdminAPIUrl() + "?apiClientId=$apiClientId")
                .header("Authorization", "Bearer $adminAccessToken")
                .responseObject<T>()
        if (!response.isSuccessful) throw AssertionError(
                "Could not export events: ${String(response.data)}",
                result.component2()
        )
        return result.get()
    }

    fun deleteImportedEvents(events: FREventMessages, adminAccessToken: String) {
        val apiClientId = events.apiClientId
        events.obEventNotification1List.forEach { event ->
            deleteEvent(event.jti, apiClientId, adminAccessToken)
        }
    }

    private fun deleteEvent(jti: String, apiClientId: String, adminAccessToken: String) {
        val (_, consentResponse, r) = Fuel.delete(
                getEventsAdminAPIUrl() +
                        "?apiClientId=$apiClientId" +
                        "&jti=$jti"
        )
                .header("Authorization", "Bearer $adminAccessToken")
                .responseObject<Void>()
        if (!consentResponse.isSuccessful) throw AssertionError(
                "Could not delete event: ${String(consentResponse.data)}",
                r.component2()
        )
    }

    inline fun <reified T : Any> postAggregatedPolling(
            aggregatedPollingUrl: String,
            aggregatedPollingRequest: OBEventPolling1,
            tpp: Tpp,
            scopes: List<String> = listOf("payments")
    ): T {
        val accessToken = getClientCredentialsAccessToken(tpp, scopes).access_token
        val (_, consentResponse, r) = Fuel.post(aggregatedPollingUrl)
                .jsonBody(aggregatedPollingRequest)
                .header("Authorization", "Bearer $accessToken")
                .header("x-api-client-id", tpp.registrationResponse.client_id)
                .responseObject<T>()
        if (!consentResponse.isSuccessful) throw AssertionError(
                "Error polling events: ${String(consentResponse.data)}",
                r.component2()
        )
        return r.get()
    }
}