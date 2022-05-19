package com.forgerock.securebanking.framework.signature

import com.forgerock.openbanking.common.model.version.OBVersion
import com.forgerock.securebanking.framework.constants.INVALID_DETACHED_JWS_ERROR
import com.forgerock.securebanking.framework.constants.ISS_CLAIM_VALUE
import com.forgerock.securebanking.framework.constants.TAN
import com.forgerock.securebanking.framework.data.Tpp
import com.forgerock.securebanking.framework.utils.GsonUtils
import com.forgerock.securebanking.support.loadRsaPrivateKey
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.isSuccessful
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.JWSObject
import com.nimbusds.jose.Payload
import com.nimbusds.jose.crypto.RSASSAVerifier
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.bouncycastle.asn1.x500.X500Name
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.math.BigDecimal
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.*
import java.util.regex.Pattern


private val DETACHED_SIGNATURE_PATTERN = Pattern.compile("(.*\\.)(\\..*)")

fun signPayload(payload: Any, signingKey: String, signingKid: String?): String {
    val serialisedPayload = GsonUtils.gson.toJson(payload)
    val key = loadRsaPrivateKey(signingKey)
    return Jwts.builder()
        .setHeaderParam("kid", signingKid)
        .setPayload(serialisedPayload)
        .signWith(key, SignatureAlgorithm.PS256)
        .compact()
}

fun signPayloadSubmitPayment(
    payload: String,
    signingKey: String,
    signingKid: String?,
    versionLowerThan3_1_4: Boolean? = null
): String {
    val key = loadRsaPrivateKey(signingKey)
    val headers = HashMap<String, Any>()
    if (signingKid != null) {
        headers["kid"] = signingKid
    }

    // The -10 was added because the systems have unsynchronised clocks.
    headers["http://openbanking.org.uk/iat"] = System.currentTimeMillis() / 1000 - 10
    headers["http://openbanking.org.uk/iss"] = ISS_CLAIM_VALUE
    headers["http://openbanking.org.uk/tan"] = TAN
    headers["crit"] = listOf(
        "http://openbanking.org.uk/iat",
        "http://openbanking.org.uk/iss",
        "http://openbanking.org.uk/tan"
    )
    headers["typ"] = "JOSE"

    if (versionLowerThan3_1_4 != null && versionLowerThan3_1_4 == true) {
        headers["b64"] = false
        val jws = Jwts.builder()
            .setHeaderParams(headers)
            .setPayload(payload)
            .signWith(key, SignatureAlgorithm.PS256)
            .compact()

        val jwtElements = jws.split(".")
        return jwtElements[0] + "." + "." + jwtElements[2]

    } else {
        return Jwts.builder()
            .setHeaderParams(headers)
            .setPayload(payload)
            .signWith(key, SignatureAlgorithm.PS256)
            .compact()
    }
}

fun signPayloadSubmitPaymentInvalidB64ClaimTrue(
    payload: String,
    signingKey: String,
    signingKid: String?
): String {
    val key = loadRsaPrivateKey(signingKey)
    val headers = HashMap<String, Any>()
    if (signingKid != null) {
        headers["kid"] = signingKid
    }

    // The -10 was added because the systems have unsynchronised clocks.
    headers["http://openbanking.org.uk/iat"] = System.currentTimeMillis() / 1000 - 10
    headers["http://openbanking.org.uk/iss"] = ISS_CLAIM_VALUE
    headers["http://openbanking.org.uk/tan"] = TAN
    headers["crit"] = listOf(
        "http://openbanking.org.uk/iat",
        "http://openbanking.org.uk/iss",
        "http://openbanking.org.uk/tan"
    )
    headers["b64"] = true
    return Jwts.builder()
        .setHeaderParams(headers)
        .setPayload(payload)
        .signWith(key, SignatureAlgorithm.PS256)
        .compact()
}

fun verifyDetachedJws(detachedJws: String, payload: String, version: OBVersion, tpp: Tpp) {
    val isDetached = DETACHED_SIGNATURE_PATTERN.matcher(detachedJws).find()
    val parsedJWSObject =
        if (isDetached) JWSObject.parse(detachedJws, Payload(payload)) else JWSObject.parse(detachedJws)

    // Validate crit claims - If this fails stop the flow, no point in continuing with the signature validation.
    validateCriticalParameters(parsedJWSObject.header, version, tpp)

    // Validate signature
    //TODO: Replace the jwks_uri after the issue https://github.com/SecureBankingAccessToolkit/SecureBankingAccessToolkit/issues/293 is implemented
    val jwk = getJwkSet(tpp.registrationResponse.jwks_uri).getKeyByKeyId(parsedJWSObject.header.keyID)
        ?: throw AssertionError("$INVALID_DETACHED_JWS_ERROR Exception getting the JWK")
    val rsaPublicKey = (jwk as RSAKey).toRSAPublicKey()
    val verifier = RSASSAVerifier(rsaPublicKey, getCriticalHeaders(version))
    if (!parsedJWSObject.verify(verifier)) {
        throw AssertionError("$INVALID_DETACHED_JWS_ERROR Signature validation failed")
    }

    // Rebuild jwt from the response body
    if (!isDetached) {
        val jwtElements = detachedJws.split(".")
        val rebuiltJwt = jwtElements[0] + "." + Base64.getEncoder().withoutPadding()
            .encodeToString(payload.toByteArray()) + "." + jwtElements[2]
        println("The rebuilt JWT : $rebuiltJwt")
        val jwsObject = JWSObject.parse(rebuiltJwt)
        val isValidJws = jwsObject.verify(verifier)
        println("Signature validation result: $isValidJws")

        if (!isValidJws) throw AssertionError("$INVALID_DETACHED_JWS_ERROR Signature validation failed")
    }
}

fun validateCriticalParameters(headers: JWSHeader, version: OBVersion, tpp: Tpp) {
    // alg
    if (headers.algorithm.name != "PS256")
        throw AssertionError("$INVALID_DETACHED_JWS_ERROR Invalid algorithm was used: ${headers.algorithm.name}")

    // typ - Optional header
    if (headers.type != null && headers.type.type != "JOSE")
        throw AssertionError("$INVALID_DETACHED_JWS_ERROR Invalid type detected: ${headers.type}")

    // http://openbanking.org.uk/iat
    val currentTimestamp = System.currentTimeMillis() / 1000
    if (!(headers.customParams["http://openbanking.org.uk/iat"] != null && BigDecimal(headers.customParams["http://openbanking.org.uk/iat"] as Double) < BigDecimal(
            currentTimestamp
        ))
    )
        throw AssertionError("$INVALID_DETACHED_JWS_ERROR Invalid issued at timestamp - value from JWT: ${headers.customParams["http://openbanking.org.uk/iat"]} and current timestamp: $currentTimestamp")

    // http://openbanking.org.uk/tan
    if (!(headers.customParams["http://openbanking.org.uk/tan"] != null && headers.customParams["http://openbanking.org.uk/tan"] == TAN))
        throw AssertionError("$INVALID_DETACHED_JWS_ERROR Invalid trusted anchor found: ${headers.customParams["http://openbanking.org.uk/tan"]} expected: $TAN")

    // http://openbanking.org.uk/iss
    //TODO - http://openbanking.org.uk/iss must be extracted from the OB signing certificate of the ASPSP. Currently we don't have open banking certificates on ASPSP side
    val jwtHeaderSubject = X500Name(headers.customParams["http://openbanking.org.uk/iss"] as String)
    println("Initialized jwtHeaderSubject: $jwtHeaderSubject")
    val routeSubjectDn = X500Name(getSubjectDN(tpp.publicCert))
    println("Initialized routeSubjectDn: $routeSubjectDn")
    if (!(headers.customParams["http://openbanking.org.uk/iss"] != null && jwtHeaderSubject == routeSubjectDn)
    ) throw AssertionError("$INVALID_DETACHED_JWS_ERROR Comparison of subject dns failed")

    // b64
    if (version.isBeforeVersion(OBVersion.v3_1_4) && headers.isBase64URLEncodePayload)
        throw AssertionError("$INVALID_DETACHED_JWS_ERROR B64 header must be false in JWT header before v3.1.4")
    if (!version.isBeforeVersion(OBVersion.v3_1_4) && !headers.isBase64URLEncodePayload)
        throw AssertionError("$INVALID_DETACHED_JWS_ERROR B64 header not permitted in JWT header after v3.1.3")
}

private fun getIssuerDN(certificatePath: String): String {
    val certPem = object {}.javaClass.getResource(certificatePath)
    val certStream: InputStream = ByteArrayInputStream(certPem?.readBytes())
    val cf: CertificateFactory = CertificateFactory.getInstance("X.509")
    val cert: X509Certificate = cf.generateCertificate(certStream) as X509Certificate
    return cert.issuerDN.name
}

private fun getSubjectDN(certificatePath: String): String? {
    val certPem = object {}.javaClass.getResource(certificatePath)
    val certStream: InputStream = ByteArrayInputStream(certPem?.readBytes())
    val cf: CertificateFactory = CertificateFactory.getInstance("X.509")
    val cert: X509Certificate = cf.generateCertificate(certStream) as X509Certificate
    return cert.subjectDN.name
}

private fun getJwkSet(jwksUri: String): JWKSet {
    val (_, response, result) = Fuel.get(jwksUri).responseString()
    return if (response.isSuccessful) JWKSet.parse(result.get()) else throw AssertionError(
        "Failed to retrieve JWKs",
        result.component2()
    )
}

private fun getCriticalHeaders(version: OBVersion): Set<String> {
    val criticalHeaders = mutableSetOf<String>()
    if (version.isBeforeVersion(OBVersion.v3_1_4)) {
        criticalHeaders.add("b64")
    }
    criticalHeaders.add("http://openbanking.org.uk/iat")
    criticalHeaders.add("http://openbanking.org.uk/iss")
    criticalHeaders.add("http://openbanking.org.uk/tan")
    return criticalHeaders
}
