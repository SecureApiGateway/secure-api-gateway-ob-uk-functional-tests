package com.forgerock.sapi.gateway.ob.uk.support.account

import com.forgerock.sapi.gateway.framework.configuration.IG_SERVER
import com.forgerock.sapi.gateway.framework.data.AccessToken
import com.forgerock.sapi.gateway.framework.data.Tpp
import com.forgerock.sapi.gateway.framework.http.fuel.jsonBody
import com.forgerock.sapi.gateway.framework.http.fuel.responseObject
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.ResponseResultOf
import com.github.kittinunf.fuel.core.isSuccessful
import uk.org.openbanking.datamodel.account.OBReadAccount6

const val HTTP_STATUS_CODE_NO_CONTENT = 204

class AccountRS {

    fun getClientCredentialsAccessToken(tpp: Tpp): AccessToken {
        return tpp.getClientCredentialsAccessToken("accounts")
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

    inline fun <reified T : Any> getAccountsData(
        accountDataUrl: String,
        accessToken: AccessToken
    ): T {
        val xObURL = "$IG_SERVER/${accountDataUrl.substring(accountDataUrl.indexOf("rs/") + 3)}"
        val (_, accountResult, r) = Fuel.get(accountDataUrl)
            .header("Authorization", "Bearer ${accessToken.access_token}")
            .header("x-ob-url", xObURL)
            .responseObject<T>()
        if (!accountResult.isSuccessful) throw AssertionError(
            "Could not get requested account data from ${accountDataUrl}: ${
                String(
                    accountResult.data
                )
            }", r.component2()
        )
        return r.get()
    }

    inline fun <reified T : Any> getAccountsDataEndUser(
        accountDataUrl: String,
        accessToken: AccessToken
    ): T {
        val xObURL = "$IG_SERVER/${accountDataUrl.substring(accountDataUrl.indexOf("rs/") + 3)}"
        val (_, accountResult, r) = Fuel.get(accountDataUrl)
            .header("Authorization", "Bearer ${accessToken.access_token}")
            .header("x-ob-url", xObURL)
            .responseObject<T>()
        if (!accountResult.isSuccessful) throw AssertionError(
            "Could not get requested account data from ${accountDataUrl}: ${
                String(
                    accountResult.data
                )
            }", r.component2()
        )
        return r.get()
    }

    inline fun <reified T : Any> getAccountData(
            accountDataUrl: String,
            accessToken: AccessToken,
            accountId: String
    ): T {
        val xObURL = "$IG_SERVER/${accountDataUrl.substring(accountDataUrl.indexOf("rs/") + 3)}"
        val (_, accountResult, r) = Fuel.get(
            AccountFactory.urlWithAccountId(
                accountDataUrl,
                accountId
            )
        )
            .header("Authorization", "Bearer ${accessToken.access_token}")
            .header("x-ob-url", xObURL)
            .responseObject<T>()
        if (!accountResult.isSuccessful) throw AssertionError(
            "Could not get requested account data from ${accountDataUrl}: ${
                String(
                    accountResult.data
                )
            }", r.component2()
        )
        return r.get()
    }

    fun getAccountStatementFileData(
            accountDataUrl: String,
            accessToken: AccessToken,
            acceptHeaderValue: String
    ):
            ResponseResultOf<ByteArray> {
        return Fuel.get(accountDataUrl)
            .header("Authorization", "Bearer ${accessToken.access_token}")
            .header("Accept", acceptHeaderValue)
            .response()
    }

    fun getFirstAccountId(accountDataUrl: String, accessToken: AccessToken): String {
        val accounts = getAccountsData<OBReadAccount6>(accountDataUrl, accessToken)
        return accounts.data.account[0].accountId
    }

    fun getFirstAccountIdAndPsuId(accountDataUrl: String, accessToken: AccessToken): Pair<String, String> {
        val accounts = getAccountsData<OBReadAccount6>(accountDataUrl, accessToken)

        try {
            val accountId = accounts.data.account[0].accountId
            val psuId = accounts.data.account[0].account[0].name
            return Pair<String, String>(accountId, psuId)
        } catch (e: Exception) {
            throw AssertionError("The accounts response body doesn't have the expected format")
        }
    }
}
