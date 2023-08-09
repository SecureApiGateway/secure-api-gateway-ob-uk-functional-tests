package com.forgerock.sapi.gateway.ob.uk.support.funds

import com.forgerock.sapi.gateway.framework.data.AccessToken
import com.forgerock.sapi.gateway.framework.data.Tpp
import com.forgerock.sapi.gateway.framework.http.fuel.jsonBody
import com.forgerock.sapi.gateway.framework.http.fuel.responseObject
import com.forgerock.sapi.gateway.ob.uk.support.account.HTTP_STATUS_CODE_NO_CONTENT
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.isSuccessful

class FundsConfirmationRS {

    fun getClientCredentialsAccessToken(tpp: Tpp): AccessToken {
        return tpp.getClientCredentialsAccessToken("fundsconfirmations")
    }

    inline fun <reified T : Any> consent(consentUrl: String, consentRequest: Any, tpp: Tpp): T {
        val accessToken = getClientCredentialsAccessToken(tpp).access_token
        val (_, consentResponse, r) = Fuel.post(consentUrl)
                .jsonBody(consentRequest)
                .header("Authorization", "Bearer $accessToken")
                .responseObject<T>()
        if (!consentResponse.isSuccessful) throw AssertionError(
                "Could not create consent: ${String(consentResponse.data)}",
                r.component2()
        )
        return r.get()
    }

    inline fun deleteConsent(consentUrl: String, tpp: Tpp) {
        val accessToken = getClientCredentialsAccessToken(tpp).access_token
        val (_, consentResponse, r) = Fuel.delete(consentUrl)
                .header("Authorization", "Bearer $accessToken")
                .response()
        if (consentResponse.statusCode != HTTP_STATUS_CODE_NO_CONTENT) throw AssertionError(
                "Failed to delete consent, expected HTTP 204 response, got response: ${consentResponse.statusCode}",
                r.component2()
        )
        if (r.get().isNotEmpty()) {
            throw AssertionError("Failed to delete consent, expected empty response body")
        }
    }

    inline fun <reified T : Any> getConsent(consentUrl: String, tpp: Tpp): T {
        val accessToken = getClientCredentialsAccessToken(tpp).access_token
        val (_, consentResponse, r) = Fuel.get(consentUrl)
                .header("Authorization", "Bearer $accessToken")
                .responseObject<T>()
        if (!consentResponse.isSuccessful) throw AssertionError(
                "Could not get consent: ${String(consentResponse.data)}",
                r.component2()
        )
        return r.get()
    }
}