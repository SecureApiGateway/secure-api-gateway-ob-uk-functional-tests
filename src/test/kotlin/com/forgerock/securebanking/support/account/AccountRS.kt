package com.forgerock.securebanking.support.account

import com.forgerock.securebanking.framework.configuration.IG_SERVER
import com.forgerock.securebanking.framework.constants.REDIRECT_URI
import com.forgerock.securebanking.framework.data.AccessToken
import com.forgerock.securebanking.framework.data.ClientCredentialData
import com.forgerock.securebanking.framework.data.Tpp
import com.forgerock.securebanking.framework.http.fuel.jsonBody
import com.forgerock.securebanking.framework.http.fuel.responseObject
import com.forgerock.securebanking.framework.signature.signPayload
import com.forgerock.securebanking.support.discovery.asDiscovery
import com.forgerock.securebanking.support.discovery.rsDiscovery
import com.forgerock.securebanking.support.general.GeneralAS.Companion.CLIENT_ASSERTION_TYPE
import com.forgerock.securebanking.support.general.GeneralAS.GrantTypes.CLIENT_CREDENTIALS
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.ResponseResultOf
import com.github.kittinunf.fuel.core.isSuccessful
import uk.org.openbanking.datamodel.account.OBReadAccount3

class AccountRS {

    fun getAccessToken(tpp: Tpp): AccessToken {
        val requestParameters = ClientCredentialData(
            sub = tpp.registrationResponse.client_id,
            iss = tpp.registrationResponse.client_id,
            aud = asDiscovery.issuer
        )
        val signedPayload = signPayload(requestParameters, tpp.signingKey, tpp.signingKid)

        val body = listOf(
            "grant_type" to CLIENT_CREDENTIALS,
            "redirect_uri" to REDIRECT_URI,
            "client_assertion_type" to CLIENT_ASSERTION_TYPE,
            "scope" to "accounts",
            "client_assertion" to signedPayload
        )

        val (_, accessTokenResponse, result) = Fuel.post(asDiscovery.token_endpoint, body)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .responseObject<AccessToken>()
        if (!accessTokenResponse.isSuccessful) throw AssertionError("Could not get access token", result.component2())
        return result.get()
    }

    inline fun <reified T : Any> consent(consentUrl: String, consentRequest: Any, tpp: Tpp): T {
        val accessToken = getAccessToken(tpp).access_token
        val (_, consentResponse, r) = Fuel.post(consentUrl)
            .jsonBody(consentRequest)
            .header("Authorization", "Bearer $accessToken")
            .header("x-fapi-financial-id", rsDiscovery.Data.FinancialId ?: "")
            .responseObject<T>()
        if (!consentResponse.isSuccessful) throw AssertionError(
            "Could not create consent: ${String(consentResponse.data)}",
            r.component2()
        )
        return r.get()
    }

    inline fun <reified T : Any> getConsent(consentUrl: String, tpp: Tpp): T {
        val accessToken = getAccessToken(tpp).access_token
        val (_, consentResponse, r) = Fuel.get(consentUrl)
            .header("Authorization", "Bearer $accessToken")
            .header("x-fapi-financial-id", rsDiscovery.Data.FinancialId ?: "")
            .responseObject<T>()
        if (!consentResponse.isSuccessful) throw AssertionError(
            "Could not create consent: ${String(consentResponse.data)}",
            r.component2()
        )
        return r.get()
    }

    inline fun <reified T : Any> deleteConsent(consentUrl: String, tpp: Tpp): T {
        val accessToken = getAccessToken(tpp).access_token
        val (_, consentResponse, r) = Fuel.delete(consentUrl)
            .header("Authorization", "Bearer $accessToken")
            .header("x-fapi-financial-id", rsDiscovery.Data.FinancialId ?: "")
            .responseObject<T>()
        if (!consentResponse.isSuccessful) throw AssertionError(
            "Could not create consent: ${String(consentResponse.data)}",
            r.component2()
        )
        return r.get()
    }

    inline fun <reified T : Any> getAccountsData(
        accountDataUrl: String,
        accessToken: AccessToken
    ): T {
        val xObURL = "$IG_SERVER/${accountDataUrl.substring(accountDataUrl.indexOf("rs/") + 3)}"
        val (_, accountResult, r) = Fuel.get(accountDataUrl)
            .header("Authorization", "Bearer ${accessToken.access_token}")
            .header("x-fapi-financial-id", rsDiscovery.Data.FinancialId ?: "")
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
            .header("x-fapi-financial-id", rsDiscovery.Data.FinancialId ?: "")
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
            .header("x-fapi-financial-id", rsDiscovery.Data.FinancialId ?: "")
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
            .header("x-fapi-financial-id", rsDiscovery.Data.FinancialId ?: "")
            .header("Accept", acceptHeaderValue)
            .response()
    }

    fun getFirstAccountId(accountDataUrl: String, accessToken: AccessToken): String {
        val accounts = getAccountsData<OBReadAccount3>(accountDataUrl, accessToken)
        return accounts.data.account[0].accountId
    }

    fun getFirstAccountIdAndPsuId(accountDataUrl: String, accessToken: AccessToken): Pair<String, String> {
        val accounts = getAccountsData<OBReadAccount3>(accountDataUrl, accessToken)

        try {
            val accountId = accounts.data.account[0].accountId
            val psuId = accounts.data.account[0].account[0].name
            return Pair<String, String>(accountId, psuId)
        } catch (e: Exception) {
            throw AssertionError("The accounts response body doesn't have the expected format")
        }
    }
}
