package com.forgerock.sapi.gateway.ob.uk.support.payment

import com.forgerock.sapi.gateway.framework.data.AccessToken
import com.forgerock.sapi.gateway.framework.data.Tpp
import com.forgerock.sapi.gateway.framework.http.fuel.defaultMapper
import com.forgerock.sapi.gateway.framework.http.fuel.responseObject
import com.forgerock.sapi.gateway.ob.uk.support.account.HTTP_STATUS_CODE_NO_CONTENT
import com.forgerock.sapi.gateway.ob.uk.support.discovery.asDiscovery
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBConstants
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.isSuccessful
import org.apache.http.entity.ContentType
import java.util.*

val defaultPaymentScopesForAccessToken =
    asDiscovery.scopes_supported.intersect(
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
            // store the body string in a field, which allows us to compute a JWS signature later (if required)
            jsonBody = defaultMapper.writeValueAsString(body)
            request.header(Headers.CONTENT_TYPE to ContentType.APPLICATION_JSON.mimeType)
            request.body(jsonBody!!)
            return this
        }

        fun addFileBody(body: Any, contentType: String): PaymentApiRequestBuilder {
            // store the body string in a field, which allows us to compute a JWS signature later (if required)
            jsonBody = body.toString()
            request.header(Headers.CONTENT_TYPE to contentType)
            request.body(body.toString()!!)
            return this
        }

        fun addHeaders(headers: Headers): PaymentApiRequestBuilder {
            // add extra headers to request
            request.headers.putAll(headers)
            return this
        }

        fun addAuthorization(accessToken: AccessToken): PaymentApiRequestBuilder {
            request.header(Headers.AUTHORIZATION, "Bearer ${accessToken.access_token}")
            return this
        }

        fun addIdempotencyKeyHeader(idempotencyKey: String? = null): PaymentApiRequestBuilder {
            request.header("x-idempotency-key", idempotencyKey ?: UUID.randomUUID())
            return this
        }

        fun deleteIdempotencyKeyHeader(): PaymentApiRequestBuilder {
            request.headers.remove("x-idempotency-key")
            return this;
        }

        inline fun <reified T : Any> sendRequest(): T {
            if (jwsSignatureProducer != null && jsonBody != null) {
                val detachedSignature = jwsSignatureProducer?.createDetachedSignature(jsonBody!!)
                if (detachedSignature != null) {
                    request.header("x-jws-signature", detachedSignature)
                }
            }


            val (_, response, result) = request.responseObject<T>()

            if (!response.isSuccessful) {
                var fapiInteractionId = "no id"
                val fapiInteractionIdHeaderVals = response.headers.get("x-fapi-interaction-id");
                if(fapiInteractionIdHeaderVals.isNotEmpty()){
                    fapiInteractionId = fapiInteractionIdHeaderVals.first()
                }

                throw AssertionError(
                    "API call: " + request.method + " " + request.url + " returned an error response:\n"
                            + result.component2()?.errorData?.toString(Charsets.UTF_8) + "\n x-fapi-interaction-id: "
                            + fapiInteractionId,
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

        inline fun sendDeleteRequest() {

            val (_, response, result) = request.response()

            if (response.statusCode != HTTP_STATUS_CODE_NO_CONTENT) throw AssertionError(
                "Failed to delete consent, expected HTTP 204 response, got response: ${response.statusCode}",
                result.component2()
            )
            if (result.get().isNotEmpty()) {
                throw AssertionError("Failed to delete consent, expected empty response body")
            }
        }

        inline fun sendFileRequest(contentType: String) {
            if (jwsSignatureProducer != null && jsonBody != null) {
                val detachedSignature = jwsSignatureProducer?.createDetachedSignature(jsonBody!!)
                if (detachedSignature != null) {
                    request.header("x-jws-signature", detachedSignature)
                }
            }

            request.header(Headers.CONTENT_TYPE, contentType)

            val (_, response, result) = request.response()

            if (!response.isSuccessful) {
                throw AssertionError(
                    "API call: " + request.method + " " + request.url + " returned an error response:\n"
                            + result.component2()?.errorData?.toString(Charsets.UTF_8),
                    result.component2()
                )
            }

            if (response.header("x-jws-signature").isNullOrEmpty()) {
                throw AssertionError(
                    "The response should have 'x-jws-signature' header for the consent : ${result.get()}"
                )
            }
        }
    }

    fun newPostRequestBuilder(url: String): PaymentApiRequestBuilder {
        return PaymentApiRequestBuilder(Fuel.post(url))
    }

    fun newGetRequestBuilder(url: String): PaymentApiRequestBuilder {
        return PaymentApiRequestBuilder(Fuel.get(url))
    }

    fun newDeleteRequestBuilder(url: String): PaymentApiRequestBuilder {
        return PaymentApiRequestBuilder(Fuel.delete(url))
    }

    /**
     * Creates a PaymentApiRequestBuilder for HTTP Post request with the following configuration:
     * - authorization token add to header
     * - json body added
     * - detached JWS signature header
     */
    fun newPostRequestBuilder(
            url: String,
            accessToken: AccessToken,
            body: Any
    ) = newPostRequestBuilder(url)
        .addIdempotencyKeyHeader()
        .addAuthorization(accessToken)
        .addBody(body)
        .configureDefaultJwsSignatureProducer()

    fun newFilePostRequestBuilder(
            url: String,
            accessToken: AccessToken,
            body: Any,
            contentType: String
    ) = newPostRequestBuilder(url)
        .addIdempotencyKeyHeader()
        .addAuthorization(accessToken)
        .addFileBody(body, contentType)
        .configureDefaultJwsSignatureProducer()

    /**
     * Submits a HTTP GET request using default configuration
     */
    inline fun <reified T : Any> sendGetRequest(url: String, accessToken: AccessToken): T {
        return newGetRequestBuilder(url).addAuthorization(accessToken).sendRequest()
    }

    /**
     * Submits a HTTP Post request using default configuration
     */
    inline fun <reified T : Any> sendPostRequest(url: String, accessToken: AccessToken, body: Any): T {
        return newPostRequestBuilder(url, accessToken, body).sendRequest()
    }

    /**
     * Submits a HTTP DELETE request using default configuration
     */
    inline fun sendDeleteRequest(url: String, accessToken: AccessToken) {
        return newDeleteRequestBuilder(url).addAuthorization(accessToken).sendDeleteRequest()
    }

    inline fun <reified T : Any> getConsent(url: String, consentId: String, accessToken: AccessToken): T {
        return sendGetRequest(PaymentFactory.urlWithConsentId(url, consentId), accessToken)
    }

    inline fun deleteConsent(url: String, consentId: String, accessToken: AccessToken) {
        return sendDeleteRequest(PaymentFactory.urlWithConsentId(url, consentId), accessToken)
    }

    inline fun <reified T : Any> submitPayment(url: String, accessToken: AccessToken, body: Any): T {
        return buildSubmitPaymentRequest(url, accessToken, body).sendRequest()
    }

    fun buildSubmitPaymentRequest(
            url: String,
            accessToken: AccessToken,
            body: Any
    ) = newPostRequestBuilder(url, accessToken, body)
        .addIdempotencyKeyHeader()

    /**
     * Get an Access Token using the Client Credential grant using the following scopes:
     * - openid
     * - accounts
     * - payments
     */
    fun getClientCredentialsAccessToken(tpp: Tpp): AccessToken {
        return tpp.getClientCredentialsAccessToken(defaultPaymentScopesForAccessToken)
    }
}
