package com.forgerock.uk.openbanking.support.payment

import com.forgerock.securebanking.common.openbanking.uk.forgerock.datamodel.common.FRAccountIdentifier
import com.forgerock.securebanking.framework.configuration.AM_COOKIE_NAME
import com.forgerock.securebanking.framework.configuration.RCS_SERVER
import com.forgerock.uk.openbanking.framework.constants.REDIRECT_URI
import com.forgerock.securebanking.framework.data.AccessToken
import com.forgerock.securebanking.framework.data.RegistrationResponse
import com.forgerock.securebanking.framework.data.RequestParameters
import com.forgerock.securebanking.framework.data.Tpp
import com.forgerock.securebanking.framework.http.fuel.jsonBody
import com.forgerock.securebanking.framework.http.fuel.responseObject
import com.forgerock.securebanking.framework.signature.signPayload
import com.forgerock.securebanking.openbanking.uk.common.api.meta.OBConstants
import com.forgerock.uk.openbanking.support.discovery.asDiscovery
import com.forgerock.uk.openbanking.support.general.GeneralAS
import com.forgerock.uk.openbanking.support.registration.UserRegistrationRequest
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.core.isSuccessful
import com.google.gson.Gson
import com.google.gson.JsonParser


/**
 * Generic AS client methods for payment tests
 */
class PaymentAS : GeneralAS() {

    data class SendConsentDecisionRequestBody(
        val consentJwt: String,
        val decision: String,
        val debtorAccount: FRAccountIdentifier
    )

    fun getAccessToken(
        consentId: String,
        registrationResponse: RegistrationResponse,
        psu: UserRegistrationRequest,
        tpp: Tpp
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
        val consentDetails = getConsentDetails(consentRequest)
        val debtorAccount = getDebtorAccountFromConsentDetails(consentDetails)
        val consentDecisionResponse = sendConsentDecision(consentRequest, debtorAccount)
        val authCode = getAuthCode(consentDecisionResponse.consentJwt, consentDecisionResponse.redirectUri, cookie)
        return exchangeCode(registrationResponse, tpp, authCode)
    }

    private fun getDebtorAccountFromConsentDetails(consentDetails: String): FRAccountIdentifier {
        try {
            val str = JsonParser().parse(consentDetails).asJsonObject
            val accounts = str.getAsJsonArray("accounts")
            val account =
                accounts[0].asJsonObject.get("account").asJsonObject.get("accounts").asJsonArray.get(0).asJsonObject

            val gson = Gson()
            return gson.fromJson(account, FRAccountIdentifier::class.java)
        } catch (e: Exception) {
            throw AssertionError(
                "The response body doesn't have the expected format"
            )
        }
    }

    private fun sendConsentDecision(
        consentRequest: String,
        consentedAccount: FRAccountIdentifier
    ): SendConsentDecisionResponseBody {
        val body = SendConsentDecisionRequestBody(consentRequest, "Authorised", consentedAccount)
        val (_, response, result) = Fuel.post("$RCS_SERVER/api/rcs/consent/decision/")
            .jsonBody(body)
            .responseObject<SendConsentDecisionResponseBody>()
        if (!response.isSuccessful) throw AssertionError(
            "Could not send consent decision",
            result.component2()
        )
        return result.get()
    }
}
