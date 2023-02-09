package com.forgerock.uk.openbanking.support.payment

import com.forgerock.securebanking.common.openbanking.uk.forgerock.datamodel.account.FRFinancialAccount
import com.forgerock.securebanking.framework.configuration.AM_COOKIE_NAME
import com.forgerock.securebanking.framework.configuration.IG_SERVER
import com.forgerock.securebanking.framework.data.AccessToken
import com.forgerock.securebanking.framework.data.RegistrationResponse
import com.forgerock.securebanking.framework.data.Tpp
import com.forgerock.securebanking.framework.http.fuel.jsonBody
import com.forgerock.securebanking.framework.http.fuel.responseObject
import com.forgerock.securebanking.framework.utils.GsonUtils.Companion.gson
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBConstants
import com.forgerock.uk.openbanking.support.discovery.asDiscovery
import com.forgerock.uk.openbanking.support.general.GeneralAS
import com.forgerock.uk.openbanking.support.registration.UserRegistrationRequest
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.isSuccessful
import com.google.gson.JsonParser


/**
 * Generic AS client methods for payment tests
 */
class PaymentAS : GeneralAS() {

    data class SendConsentDecisionRequestBody(
        val consentJwt: String,
        val decision: String,
        val debtorAccount: FRFinancialAccount
    )

    /**
     * Authorizes a consent and returns an AccessToken with grant_type authorization_code, this token can then be used
     * in subsequent operations on the consent (such as making a payment)
     */
    fun authorizeConsent(
        consentId: String,
        registrationResponse: RegistrationResponse,
        psu: UserRegistrationRequest,
        tpp: Tpp,
        decision: String = "Authorised"
    ): AccessToken {
        val authenticationURL = generateAuthenticationURL(
            consentId, registrationResponse, psu, tpp, asDiscovery.scopes_supported.intersect(
                listOf(
                    OBConstants.Scope.OPENID,
                    OBConstants.Scope.PAYMENTS,
                    OBConstants.Scope.ACCOUNTS
                )
            ).joinToString(separator = " ")
        )
        val response = authenticateByHttpClient(authenticationURL, psu)
        val authorizeURL = response.successUrl
        val cookie = "$AM_COOKIE_NAME=${response.tokenId}"
        val consentRequest = continueAuthorize(authorizeURL, cookie)
        val consentDetails = getConsentDetails(consentRequest, cookie)
        val debtorAccount = getDebtorAccountFromConsentDetails(consentDetails)
        val consentDecisionResponse = sendConsentDecision(consentRequest, debtorAccount, decision)
        val authCode = getAuthCode(consentDecisionResponse.consentJwt, consentDecisionResponse.redirectUri, cookie)
        return exchangeCode(registrationResponse, tpp, authCode)
    }

    private fun getDebtorAccountFromConsentDetails(consentDetails: String): FRFinancialAccount {
        try {
            val str = JsonParser().parse(consentDetails).asJsonObject
            val accounts = str.getAsJsonArray("accounts")
            val account =
                accounts[0].asJsonObject.get("account").asJsonObject

            return gson.fromJson(account, FRFinancialAccount::class.java)
        } catch (e: Exception) {
            throw AssertionError(
                "The response body doesn't have the expected format"
            )
        }
    }

    private fun sendConsentDecision(
        consentRequest: String,
        consentedAccount: FRFinancialAccount,
        decision: String
    ): SendConsentDecisionResponseBody {
        val body = SendConsentDecisionRequestBody(consentRequest, decision, consentedAccount)
        val (_, response, result) = Fuel.post("$IG_SERVER/rcs/api/consent/decision/")
            .jsonBody(body)
            .responseObject<SendConsentDecisionResponseBody>()
        if (!response.isSuccessful) throw AssertionError(
            "Could not send consent decision",
            result.component2()
        )
        return result.get()
    }
}
