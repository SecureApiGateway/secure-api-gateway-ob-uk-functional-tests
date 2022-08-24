package com.forgerock.uk.openbanking.support.payment

import com.forgerock.securebanking.framework.data.AccessToken
import com.forgerock.securebanking.framework.data.Tpp
import com.forgerock.securebanking.framework.http.fuel.defaultMapper
import com.forgerock.securebanking.framework.http.fuel.responseObject
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBConstants
import com.forgerock.uk.openbanking.support.discovery.rsDiscovery
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.isSuccessful
import java.util.*

val defaultPaymentScopesForAccessToken =
    com.forgerock.uk.openbanking.support.discovery.asDiscovery.scopes_supported.intersect(
        listOf(
            OBConstants.Scope.OPENID,
            OBConstants.Scope.ACCOUNTS,
            OBConstants.Scope.PAYMENTS
        )
    ).joinToString(separator = " ")

/**
 * Rest Client used to call payment API endpoints
 */
class PaymentApiClient(val tpp: Tpp) {

    inner class PaymentApiRequestBuilder(val request: Request) {

        var jwsSignatureProducer: JwsSignatureProducer? = null
        var jsonBody: String? = null

        fun configureJwsSignatureProducer(jwsSignatureProducer: JwsSignatureProducer?): PaymentApiRequestBuilder {
            this.jwsSignatureProducer = jwsSignatureProducer
            return this
        }

        fun configureDefaultJwsSignatureProducer(): PaymentApiRequestBuilder {
            jwsSignatureProducer = DefaultJwsSignatureProducer(tpp)
            return this
        }

        fun addBody(body: Any): PaymentApiRequestBuilder {
            // store the body string in a field so we can compute a signature later (if required)
            jsonBody = defaultMapper.writeValueAsString(body)
            request.body(jsonBody!!)
            return this
        }

        fun addAuthorization(accessToken: AccessToken): PaymentApiRequestBuilder {
            request.header("Authorization", "Bearer ${accessToken.access_token}")
            return this
        }

        fun addIdempotencyKeyHeader(postRequest: Request, idempotencyKey: String? = null): PaymentApiRequestBuilder {
            postRequest.header("x-idempotency-key", idempotencyKey ?: UUID.randomUUID())
            return this
        }

        inline fun <reified T : Any> sendRequest(): T {
            if (jwsSignatureProducer != null && jsonBody != null) {
                val detachedSignature = jwsSignatureProducer?.createDetachedSignature(jsonBody!!)
                if (detachedSignature != null) {
                    request.header("x-jws-signature", detachedSignature)
                }
            }

            // TODO x-fapi-financial-id is not necessary anymore, only add it for the legacy versions which need it
            request.header("x-fapi-financial-id", rsDiscovery.Data.FinancialId ?: "")
            val (_, response, result) = request.responseObject<T>()

            if (!response.isSuccessful) {
                throw AssertionError(
                    "Could not create the consent: \n" + result.component2()?.errorData?.toString(Charsets.UTF_8),
                    result.component2()
                )
            }

            if (response.header("x-jws-signature").isNullOrEmpty()) {
                throw AssertionError(
                    "The response should have 'x-jws-signature' header for the consent : ${result.get()}"
                )
            }
            return result.get()
        }
    }

    fun createEmptyPostRequest(url: String): PaymentApiRequestBuilder {
        return PaymentApiRequestBuilder(Fuel.post(url))
    }

    fun createDefaultPostRequest(
        url: String,
        accessToken: AccessToken,
        body: Any
    ) = createEmptyPostRequest(url).addAuthorization(accessToken).addBody(body).configureDefaultJwsSignatureProducer()

    fun createGetRequest(url: String): PaymentApiRequestBuilder {
        return PaymentApiRequestBuilder(Fuel.get(url))
    }

    /**
     * Submits a HTTP GET request using default configuration
     */
    inline fun <reified T : Any> sendGetRequest(url: String, accessToken: AccessToken): T {
        return createGetRequest(url).addAuthorization(accessToken).sendRequest()
    }

    /**
     * Submits a HTTP Post request using default configuration
     */
    inline fun <reified T : Any> sendPostRequest(url: String, accessToken: AccessToken, body: Any): T {
        return createDefaultPostRequest(url, accessToken, body).sendRequest()
    }
}
