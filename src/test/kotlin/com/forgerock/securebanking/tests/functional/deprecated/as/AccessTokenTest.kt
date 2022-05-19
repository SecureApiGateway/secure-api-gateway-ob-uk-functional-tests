package com.forgerock.securebanking.tests.functional.deprecated.`as`

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotNull
import com.forgerock.securebanking.framework.data.AccessToken
import com.forgerock.securebanking.framework.data.RegistrationRequest
import com.forgerock.securebanking.framework.http.fuel.initFuelAsNewTpp
import com.forgerock.securebanking.framework.http.fuel.responseObject
import com.forgerock.securebanking.framework.utils.GsonUtils
import com.forgerock.securebanking.support.discovery.asDiscovery
import com.forgerock.securebanking.support.loadRsaPrivateKey
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.authentication
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.junit.jupiter.api.Test
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.*

fun getSubjectDn(publicCert: String): String {
    val factory = CertificateFactory.getInstance(
        "X.509",
        BouncyCastleProvider.PROVIDER_NAME
    )
    val certificate = factory.generateCertificate(publicCert.byteInputStream()) as X509Certificate
    return certificate.subjectDN.name
}

class AccessTokenTest {

    val tokenEndpointsSupported = asDiscovery.token_endpoint_auth_methods_supported

    @Test
    fun shouldGetAccessTokenUsingTlsAuthCert() {
        val supportsTlsClientAuth = this.tokenEndpointsSupported.contains("tls_client_auth")
        if (supportsTlsClientAuth) {
            // Given
            val tpp = initFuelAsNewTpp().apply {
                dynamicRegistration(
                    RegistrationRequest(
                        software_statement = generateSsa(),
                        iss = softwareStatement.id,
                        token_endpoint_auth_method = "tls_client_auth",
                        tls_client_auth_subject_dn = getSubjectDn(publicCert)
                    )
                )
            }

            // When
            val (_, accessTokenResponse, result) = Fuel.post(
                asDiscovery.token_endpoint, parameters = listOf(
                    "grant_type" to "client_credentials",
                    "scope" to "openid accounts",
                    "client_id" to tpp.registrationResponse.client_id
                )
            )
                .responseObject<AccessToken>()

            // Then
            assertThat(accessTokenResponse.statusCode).isEqualTo(200)
            assertThat(result.get().access_token).isNotNull()
        } else {
            assertThat(supportsTlsClientAuth).isFalse()
        }
    }

    @Test
    fun shouldGetAccessTokenUsingClientBasicSecret() {
        // Given
        val tpp = initFuelAsNewTpp().apply { dynamicRegistration() }

        // When
        val (_, accessTokenResponse, result) = Fuel.post(
            asDiscovery.token_endpoint, parameters = listOf(
                "grant_type" to "client_credentials",
                "scope" to "openid accounts"
            )
        )
            .authentication()
            .basic(tpp.registrationResponse.client_id, tpp.registrationResponse.client_secret!!)
            .responseObject<AccessToken>()

        // Then
        assertThat(accessTokenResponse.statusCode).isEqualTo(200)
        assertThat(result.get().access_token).isNotNull()
    }

    @Test
    @JvmName("shouldGetAccessTokenUsingClientBasicSecret_eventpolling_scope")
    fun shouldGetAccessToken_eventpolling_scope() {
        // Given
        val tpp = initFuelAsNewTpp().apply { dynamicRegistration() }

        // When
        val (_, accessTokenResponse, result) = Fuel.post(
            asDiscovery.token_endpoint, parameters = listOf(
                "grant_type" to "client_credentials",
                "scope" to "openid eventpolling"
            )
        )
            .authentication()
            .basic(tpp.registrationResponse.client_id, tpp.registrationResponse.client_secret!!)
            .responseObject<AccessToken>()

        // Then
        assertThat(accessTokenResponse.statusCode).isEqualTo(200)
        assertThat(result.get().access_token).isNotNull()
    }

    @Test
    fun shouldGetAccessTokenUsingPrivateKeyJwt() {
        val supportsPrivateKeyJwt = this.tokenEndpointsSupported.contains("private_key_jwt")
        if (supportsPrivateKeyJwt) {
            // Given
            val tpp = initFuelAsNewTpp().apply {
                dynamicRegistration(
                    RegistrationRequest(
                        software_statement = generateSsa(),
                        iss = softwareStatement.id,
                        token_endpoint_auth_method = "private_key_jwt"
                    )
                )
            }
            val payload = ClientAssertion(
                aud = "https://as.aspsp.DOMAIN/oauth2",
                sub = tpp.registrationResponse.client_id,
                iss = tpp.registrationResponse.client_id,
                exp = (System.currentTimeMillis() / 1000 + 180).toInt(),
                iat = (System.currentTimeMillis() / 1000).toInt(),
                jti = UUID.randomUUID().toString()
            )
            val key = loadRsaPrivateKey(tpp.signingKey)
            val clientAssertion = Jwts.builder()
                .setHeaderParam("kid", tpp.signingKid)
                .setPayload(GsonUtils.gson.toJson(payload))
                .signWith(key, SignatureAlgorithm.forName(tpp.registrationResponse.token_endpoint_auth_signing_alg))
                .compact()

            // When
            val (_, accessTokenResponse, result) = Fuel.post(
                asDiscovery.token_endpoint, parameters = listOf(
                    "grant_type" to "client_credentials",
                    "scope" to "openid accounts",
                    "client_assertion_type" to "urn:ietf:params:oauth:client-assertion-type:jwt-bearer",
                    "client_assertion" to clientAssertion
                )
            )
                .responseObject<AccessToken>()

            // Then
            assertThat(accessTokenResponse.statusCode).isEqualTo(200)
            assertThat(result.get().access_token).isNotNull()
        } else {
            assertThat(supportsPrivateKeyJwt).isFalse()
        }
    }

}

data class ClientAssertion(
    val aud: String,
    val exp: Int,
    val iat: Int,
    val iss: String,
    val jti: String,
    val sub: String
)
