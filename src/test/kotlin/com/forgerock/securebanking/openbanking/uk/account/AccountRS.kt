package com.forgerock.openbanking.account

import com.forgerock.openbanking.AccessToken
import com.forgerock.openbanking.Tpp
import com.forgerock.openbanking.discovery.asDiscovery
import com.forgerock.openbanking.discovery.rsDiscovery
import com.forgerock.openbanking.jsonBody
import com.forgerock.openbanking.responseObject
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.core.isSuccessful
import uk.org.openbanking.datamodel.account.OBReadAccount3
import uk.org.openbanking.datamodel.account.OBReadStatement2

class AccountRS {

    inline fun <reified T : Any> consent(consentUrl: String, consentRequest: Any, tpp: Tpp): T {
        val body = listOf(
                "grant_type" to "client_credentials",
                "scope" to "accounts"
        )
        val (_, accessTokenResponse, result) = Fuel.post(asDiscovery.token_endpoint, parameters = body)
                .authentication()
                .basic(tpp.registrationResponse.client_id, tpp.registrationResponse.client_secret!!)
                .responseObject<AccessToken>()
        if (!accessTokenResponse.isSuccessful) throw AssertionError("Could not get access token", result.component2())

        val (_, consentResponse, r) = Fuel.post(consentUrl)
                .jsonBody(consentRequest)
                .header("Authorization", "Bearer ${result.get().access_token}")
                .header("x-fapi-financial-id", rsDiscovery.Data.FinancialId)
                .responseObject<T>()
        if (!consentResponse.isSuccessful) throw AssertionError("Could not create consent: ${String(consentResponse.data)}", r.component2())
        return r.get()
    }

    inline fun <reified T : Any> getAccountData(accountDataUrl: String, accessToken: AccessToken): T {
        val (_, accountResult, r) = Fuel.get(accountDataUrl)
                .header("Authorization", "Bearer ${accessToken.access_token}")
                .header("x-fapi-financial-id", rsDiscovery.Data.FinancialId)
                .responseObject<T>()
        if (!accountResult.isSuccessful) throw AssertionError("Could not get requested account data from ${accountDataUrl}: ${String(accountResult.data)}", r.component2())
        return r.get()
    }

    fun getFirstAccountId(accountDataUrl: String, accessToken: AccessToken): String {
        val accounts = getAccountData<OBReadAccount3>(accountDataUrl, accessToken)
        return accounts.data.account[0].accountId
    }

    fun getFirstStatementId(statementUrl: String, accessToken: AccessToken): String {
        val statement = getAccountData<OBReadStatement2>(statementUrl, accessToken)
        return statement.data.statement[0].statementId
    }
}