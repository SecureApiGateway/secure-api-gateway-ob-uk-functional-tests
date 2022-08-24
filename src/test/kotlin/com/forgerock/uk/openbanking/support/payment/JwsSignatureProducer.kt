package com.forgerock.uk.openbanking.support.payment

import com.forgerock.securebanking.framework.configuration.ISS_CLAIM_VALUE
import com.forgerock.securebanking.framework.data.Tpp
import com.forgerock.uk.openbanking.framework.constants.INVALID_FORMAT_DETACHED_JWS
import com.forgerock.uk.openbanking.framework.constants.INVALID_SIGNING_KID
import com.forgerock.uk.openbanking.framework.constants.TAN
import io.jsonwebtoken.JwtBuilder
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.util.HashMap

interface JwsSignatureProducer {
    fun createDetachedSignature(jsonPayload: String): String
}

/**
 * Default implementation of JwsSignatureProducer, creates detached signatures using the provided Tpp's signingKey.
 * Adds headers to produce a JWS which is compliant with OBIE requirements.
 *
 * A b64 header may be added (it is omitted by default as it must not be supplied in OBIE version>= 3.1.4), if the
 * b64HeaderValue is not null then it is added as either true or false.
 */
open class DefaultJwsSignatureProducer(private val tpp: Tpp, private val b64HeaderValue: Boolean? = null) :
    JwsSignatureProducer {

    private fun addHeaders(jwtBuilder: JwtBuilder) {
        val headers = HashMap<String, Any>()
        headers["kid"] = getKid()
        headers["http://openbanking.org.uk/iat"] = System.currentTimeMillis() / 1000 - 10
        headers["http://openbanking.org.uk/iss"] = ISS_CLAIM_VALUE
        headers["http://openbanking.org.uk/tan"] = TAN
        headers["crit"] = listOf(
            "http://openbanking.org.uk/iat",
            "http://openbanking.org.uk/iss",
            "http://openbanking.org.uk/tan"
        )
        headers["typ"] = "JOSE"
        if (b64HeaderValue != null) {
            headers["b64"] = b64HeaderValue
        }
        jwtBuilder.setHeaderParams(headers)
    }

    protected open fun getKid() = tpp.signingKid

    private fun createSignature(jsonPayload: String): String {
        val jwtBuilder = Jwts.builder()
        addHeaders(jwtBuilder)
        return jwtBuilder.setPayload(jsonPayload)
            .signWith(tpp.signingKey, SignatureAlgorithm.PS256)
            .compact()
    }

    override fun createDetachedSignature(jsonPayload: String): String {
        val signature = createSignature(jsonPayload)

        // Remove the payload portion to produce a detached sig
        val jwtElements = signature.split('.')
        return jwtElements[0] + ".." + jwtElements[2]
    }
}

/**
 * Implementation for testing purposes, produces an invalid detached JWS
 */
class BadJwsSignatureProducer: JwsSignatureProducer {
    override fun createDetachedSignature(jsonPayload: String) = INVALID_FORMAT_DETACHED_JWS
}

/**
 * Implementation for testing purposes, produces a detached JWS with and invalid kid header value
 */
class InvalidKidJwsSignatureProducer(tpp: Tpp, b64HeaderValue: Boolean? = null): DefaultJwsSignatureProducer(tpp, b64HeaderValue) {
    override fun getKid() = INVALID_SIGNING_KID
}
