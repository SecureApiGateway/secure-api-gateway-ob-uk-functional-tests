package com.forgerock.sapi.gateway.ob.uk.support.payment

import com.forgerock.sapi.gateway.framework.configuration.AM_COOKIE_NAME
import com.forgerock.sapi.gateway.framework.configuration.IG_SERVER
import com.forgerock.sapi.gateway.framework.data.AccessToken
import com.forgerock.sapi.gateway.framework.data.RegistrationResponse
import com.forgerock.sapi.gateway.framework.data.Tpp
import com.forgerock.sapi.gateway.framework.http.fuel.jsonBody
import com.forgerock.sapi.gateway.framework.http.fuel.responseObject
import com.forgerock.sapi.gateway.framework.utils.GsonUtils.Companion.gson
import com.forgerock.sapi.gateway.ob.uk.common.datamodel.account.FRFinancialAccount
import com.forgerock.sapi.gateway.ob.uk.support.discovery.asDiscovery
import com.forgerock.sapi.gateway.ob.uk.support.general.GeneralAS
import com.forgerock.sapi.gateway.ob.uk.support.registration.UserRegistrationRequest
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBConstants
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.isSuccessful
import com.google.gson.JsonParser
import java.util.*


/**
 * Generic AS client methods for payment tests
 */
class PaymentAS : GeneralAS() {

    data class SendConsentDecisionRequestBody(
        val consentJwt: String,
        val decision: String,
        val debtorAccount: FRFinancialAccount,
        val debtorAccountProvided: Boolean
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
        val consentedAccount = getConsentedAccountFromConsentDetails(consentDetails)
        val isDebtorAccountProvided = isDebtorAccountProvided(consentDetails)
        val consentDecisionResponse = sendConsentDecision(
                consentRequest,
                consentedAccount,
                decision,
                isDebtorAccountProvided,
                cookie
        )
        val authCode = getAuthCode(consentDecisionResponse.consentJwt, consentDecisionResponse.redirectUri, cookie)
        return exchangeCode(registrationResponse, tpp, authCode)
    }

    private fun isDebtorAccountProvided(consentDetails: String): Boolean {
        val str = JsonParser.parseString(consentDetails).asJsonObject
        val initiation = str.getAsJsonObject("initiation")
        if(Objects.nonNull(initiation)){
            if(Objects.nonNull(initiation.getAsJsonObject("debtorAccount"))){
                return true
            }
        }
        return false
    }

    private fun getConsentedAccountFromConsentDetails(consentDetails: String): FRFinancialAccount {
        try {
            val str = JsonParser.parseString(consentDetails).asJsonObject
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
        decision: String,
        isDebtorAccountProvided: Boolean,
        cookie: String
    ): SendConsentDecisionResponseBody {
        val body = SendConsentDecisionRequestBody(consentRequest, decision, consentedAccount, isDebtorAccountProvided)
        val (_, response, result) = Fuel.post("$IG_SERVER/rcs/api/consent/decision/")
                                        .header("Cookie", cookie)
                                        .jsonBody(body)
                                        .responseObject<SendConsentDecisionResponseBody>()
        if (!response.isSuccessful) throw AssertionError(
            "Could not send consent decision",
            result.component2()
        )
        return result.get()
    }
}
