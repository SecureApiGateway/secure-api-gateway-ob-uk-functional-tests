package com.forgerock.sapi.gateway.ob.uk.support.payment

import com.fasterxml.jackson.module.kotlin.readValue
import com.forgerock.sapi.gateway.framework.data.AccessToken
import com.forgerock.sapi.gateway.framework.data.Tpp
import com.forgerock.sapi.gateway.framework.http.fuel.defaultMapper
import com.forgerock.sapi.gateway.framework.http.fuel.jsonBody
import com.forgerock.sapi.gateway.framework.http.fuel.responseObject
import com.forgerock.sapi.gateway.ob.uk.support.discovery.asDiscovery
import com.forgerock.sapi.gateway.ob.uk.support.discovery.rsDiscovery
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBConstants
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion.v3_1_8
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.HttpException
import com.github.kittinunf.fuel.core.isSuccessful
import java.util.*

/**
 * Generic RS client methods for payment tests
 *
 * @deprecated  - com.forgerock.uk.openbanking.support.payment.PaymentApiClient should be used instead, methods are being
 * migrated over to the new class as needed.
 */
@Deprecated("com.forgerock.uk.openbanking.support.payment.PaymentApiClient should be used instead")
class PaymentRS {
    inline fun <reified T : Any> consent(
            consentUrl: String,
            consentRequest: Any,
            tpp: Tpp,
            version: OBVersion = v3_1_8,
            detachedJwt: String = ""
    ): T {
        try {
            val accessToken = getClientCredentialsAccessToken(tpp).access_token
            val (_, consentResponse, result) = Fuel.post(consentUrl)
                .jsonBody(consentRequest)
                .header("Authorization", "Bearer $accessToken")
                .header("x-jws-signature", detachedJwt)
                .responseObject<T>()
            if (!consentResponse.isSuccessful) {
                throw AssertionError(
                    "Could not create the consent: \n" + result.component2()?.errorData?.toString(Charsets.UTF_8),
                    result.component2()
                )
            }

            if (consentResponse.header("x-jws-signature").isNullOrEmpty()) {
                throw AssertionError(
                    "The response should have 'x-jws-signature' header for the consent : ${result.get()}"
                )
            }
            //TODO: Uncomment the function when the issue https://github.com/SecureBankingAccessToolkit/SecureBankingAccessToolkit/issues/293 is implemented
//            verifyDetachedJws(
//                consentResponse.header("x-jws-signature").first(),
//                defaultMapper.writeValueAsString(result.get()),
//                version,
//                tpp
//            )
            return result.get()
        } catch (e: HttpException) {
            println(e)
            throw e
        }
    }

    inline fun <reified T : Any> consentNoDetachedJwt(
            consentUrl: String,
            consentRequest: Any,
            tpp: Tpp,
            version: OBVersion = v3_1_8
    ): T {
        try {
            val accessToken = getClientCredentialsAccessToken(tpp).access_token
            val (_, consentResponse, r) = Fuel.post(consentUrl)
                .jsonBody(consentRequest)
                .header("Authorization", "Bearer $accessToken")
                .responseObject<T>()
            if (!consentResponse.isSuccessful) {
                throw AssertionError(
                    "Could not create the consent: \n" + r.component2()?.errorData?.toString(Charsets.UTF_8),
                    r.component2()
                )
            }

            return r.get()
        } catch (e: HttpException) {
            println(e)
            throw e
        }
    }

    inline fun <reified T : Any> getConsent(consentUrl: String, tpp: Tpp, version: OBVersion = v3_1_8): T {
        val accessToken = getClientCredentialsAccessToken(tpp).access_token
        val (_, consentResponse, result) = Fuel.get(consentUrl)
            .header("Authorization", "Bearer $accessToken")
            .responseObject<T>()
        if (!consentResponse.isSuccessful) throw AssertionError(
            "Could not get the consent: \n" + result.component2()?.errorData?.toString(Charsets.UTF_8),
            result.component2()
        )

        if (consentResponse.header("x-jws-signature").isNullOrEmpty()) {
            throw AssertionError(
                "The response should have 'x-jws-signature' header for the consent : ${result.get()}"
            )
        }

        //TODO: Uncomment the function when the issue https://github.com/SecureBankingAccessToolkit/SecureBankingAccessToolkit/issues/293 is implemented
//        verifyDetachedJws(
//            consentResponse.header("x-jws-signature").first(),
//            defaultMapper.writeValueAsString(result.get()),
//            version,
//            tpp
//        )

        return result.get()
    }

    inline fun <reified T : Any> submitPayment(
            paymentUrl: String,
            paymentRequest: Any,
            accessToken: AccessToken,
            signedPayload: String,
            tpp: Tpp,
            version: OBVersion = v3_1_8
    ): T {
        val detachedJwt = if (signedPayload == com.forgerock.sapi.gateway.ob.uk.framework.constants.INVALID_FORMAT_DETACHED_JWS) {
            com.forgerock.sapi.gateway.ob.uk.framework.constants.INVALID_FORMAT_DETACHED_JWS
        } else {
            val jwtElements = signedPayload.split(".")
            jwtElements[0] + "." + "." + jwtElements[2]
        }

        val (_, response, result) = Fuel.post(paymentUrl)
            .jsonBody(paymentRequest)
            .header("Authorization", "Bearer ${accessToken.access_token}")
            .header("x-jws-signature", detachedJwt)
            .header("x-idempotency-key", UUID.randomUUID())
            .responseObject<T>()
        if (!response.isSuccessful) throw AssertionError(
            "Could not create the payment submission: \n" + result.component2()?.errorData?.toString(Charsets.UTF_8),
            result.component2()
        )

        if (response.header("x-jws-signature").isNullOrEmpty()) {
            throw AssertionError(
                "The response should have 'x-jws-signature' header for the consent : ${result.get()}"
            )
        }

        //TODO: Uncomment the function when the issue https://github.com/SecureBankingAccessToolkit/SecureBankingAccessToolkit/issues/293 is implemented
//        verifyDetachedJws(
//            response.header("x-jws-signature").first(),
//            defaultMapper.writeValueAsString(result.get()),
//            version,
//            tpp
//        )

        return result.get()
    }

    inline fun <reified T : Any> submitPaymentNoDetachedJws(
        paymentUrl: String,
        paymentRequest: Any,
        accessToken: AccessToken
    ): T {
        val (_, response, r) = Fuel.post(paymentUrl)
            .jsonBody(paymentRequest)
            .header("Authorization", "Bearer ${accessToken.access_token}")
            .header("x-idempotency-key", UUID.randomUUID())
            .responseObject<T>()
        if (!response.isSuccessful) throw AssertionError(
            "Could not create the payment submission: \n" + r.component2()?.errorData?.toString(Charsets.UTF_8),
            r.component2()
        )
        return r.get()
    }

    inline fun <reified T : Any> getPayment(
            url: String,
            accessToken: AccessToken,
            tpp: Tpp,
            version: OBVersion = v3_1_8
    ): T {
        return getCall(url, accessToken, tpp, version)
    }

    inline fun <reified T : Any> getFundsConfirmation(
        url: String,
        accessToken: AccessToken
    ): T {
        return getCallWithoutDetachedJws(url, accessToken)
    }

    inline fun <reified T> getCall(
            url: String,
            accessToken: AccessToken,
            tpp: Tpp,
            version: OBVersion = v3_1_8
    ): T {
        val (_, response, result) = Fuel.get(url)
            .header("Authorization", "Bearer ${accessToken.access_token}")
            .responseString()
        if (!response.isSuccessful) throw AssertionError(
            "Error executing the get call: \n" + result.component2()?.errorData?.toString(Charsets.UTF_8),
            result.component2()
        )

        if (response.header("x-jws-signature").isNullOrEmpty()) {
            throw AssertionError(
                "The response should have 'x-jws-signature' header for the consent : ${result.get()}"
            )
        }

        //TODO: Uncomment the function when the issue https://github.com/SecureBankingAccessToolkit/SecureBankingAccessToolkit/issues/293 is implemented
//        verifyDetachedJws(
//            response.header("x-jws-signature").first(),
//            defaultMapper.writeValueAsString(result.get()),
//            version,
//            tpp
//        )

        return defaultMapper.readValue(result.get())
    }

    inline fun <reified T> getCallWithoutDetachedJws(
        url: String,
        accessToken: AccessToken
    ): T {
        val (_, response, result) = Fuel.get(url)
            .header("Authorization", "Bearer ${accessToken.access_token}")
            .responseString()
        if (!response.isSuccessful) throw AssertionError(
            "Error executing the get call: \n" + result.component2()?.errorData?.toString(Charsets.UTF_8),
            result.component2()
        )

        return defaultMapper.readValue(result.get())
    }

    fun getClientCredentialsAccessToken(tpp: Tpp): AccessToken {
        return tpp.getClientCredentialsAccessToken(
            asDiscovery.scopes_supported.intersect(
                listOf(
                    OBConstants.Scope.OPENID,
                    OBConstants.Scope.ACCOUNTS,
                    OBConstants.Scope.PAYMENTS
                )
            ).joinToString(separator = " ")
        )
    }
}
