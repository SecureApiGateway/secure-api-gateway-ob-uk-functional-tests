package com.forgerock.securebanking.directory

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.forgerock.securebanking.DOMAIN
import com.forgerock.securebanking.directoryUser
import com.forgerock.securebanking.login
import com.github.kittinunf.fuel.Fuel
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance


data class SoftwareStatement(
    val applicationId: String,
    val software_id: String,
    val org_id: String,
    val id: String,
    val mode: String,
    val redirectUris: List<Any>,
    val roles: List<String>,
    val status: String
)

data class JwkMsKey(val keyUse: String)
data class Application(val transportKeys: Map<String, JwkMsKey>, val keys: Map<String, JwkMsKey>)

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SoftwareStatementTest {

    private lateinit var sessionToken: String

    @BeforeAll
    fun setUp() {
        this.sessionToken = login(directoryUser.input.user.username, directoryUser.input.user.userPassword)
    }

    @Test
    fun shouldCreateSoftwareStatement() {
        // Given a session

        // When
        val softwareStatement = createSoftwareStatement(sessionToken)

        // Then
        assertThat(softwareStatement.id).isNotNull()
    }

    @Test
    fun shouldDownloadTransportPrivateCert() {
        // Given
        val softwareStatement = createSoftwareStatement(sessionToken)
        val transportKid = getTransportKid(softwareStatement, sessionToken)

        // When
        val (_, response, result) = Fuel.get("https://service.directory.$DOMAIN/api/software-statement/${softwareStatement.id}/application/${transportKid}/download/privateCert")
            .header("Cookie", "obri-session=$sessionToken")
            .responseString()

        // Then
        assertThat(response.statusCode).isEqualTo(200)
        assertThat(result.get()).isNotNull()
        assertThat(result.get()).contains("PRIVATE")
    }

    @Test
    fun shouldDownloadTransportPublicCert() {
        // Given
        val softwareStatement = createSoftwareStatement(sessionToken)
        val transportKid = getTransportKid(softwareStatement, sessionToken)

        // When
        val (_, response, result) = Fuel.get("https://service.directory.$DOMAIN/api/software-statement/${softwareStatement.id}/application/${transportKid}/download/publicCert")
            .header("Cookie", "obri-session=$sessionToken")
            .responseString()

        // Then
        assertThat(response.statusCode).isEqualTo(200)
        assertThat(result.get()).isNotNull()
        assertThat(result.get()).contains("BEGIN CERTIFICATE")
    }

    @Test
    fun shouldDownloadSigningPrivateCert() {
        // Given
        val softwareStatement = createSoftwareStatement(sessionToken)
        val transportKid = getSigningKid(softwareStatement, sessionToken)

        // When
        val (_, response, result) = Fuel.get("https://service.directory.$DOMAIN/api/software-statement/${softwareStatement.id}/application/${transportKid}/download/privateCert")
            .header("Cookie", "obri-session=$sessionToken")
            .responseString()

        // Then
        assertThat(response.statusCode).isEqualTo(200)
        assertThat(result.get()).isNotNull()
        assertThat(result.get()).contains("PRIVATE")
    }

    @Test
    fun shouldDownloadSigningPublicCert() {
        // Given
        val softwareStatement = createSoftwareStatement(sessionToken)
        val transportKid = getSigningKid(softwareStatement, sessionToken)

        // When
        val (_, response, result) = Fuel.get("https://service.directory.$DOMAIN/api/software-statement/${softwareStatement.id}/application/${transportKid}/download/publicCert")
            .header("Cookie", "obri-session=$sessionToken")
            .responseString()

        // Then
        assertThat(response.statusCode).isEqualTo(200)
        assertThat(result.get()).isNotNull()
        assertThat(result.get()).contains("BEGIN CERTIFICATE")
    }

    @Test
    fun shouldDownloadEncryptionPrivateCert() {
        // Given
        val softwareStatement = createSoftwareStatement(sessionToken)
        val transportKid = getEncryptionKid(softwareStatement, sessionToken)

        // When
        val (_, response, result) = Fuel.get("https://service.directory.$DOMAIN/api/software-statement/${softwareStatement.id}/application/${transportKid}/download/privateCert")
            .header("Cookie", "obri-session=$sessionToken")
            .responseString()

        // Then
        assertThat(response.statusCode).isEqualTo(200)
        assertThat(result.get()).isNotNull()
        assertThat(result.get()).contains("PRIVATE")
    }

    @Test
    fun shouldDownloadEncryptionPublicCert() {
        // Given
        val softwareStatement = createSoftwareStatement(sessionToken)
        val transportKid = getEncryptionKid(softwareStatement, sessionToken)

        // When
        val (_, response, result) = Fuel.get("https://service.directory.$DOMAIN/api/software-statement/${softwareStatement.id}/application/${transportKid}/download/publicCert")
            .header("Cookie", "obri-session=$sessionToken")
            .responseString()

        // Then
        assertThat(response.statusCode).isEqualTo(200)
        assertThat(result.get()).isNotNull()
        assertThat(result.get()).contains("BEGIN CERTIFICATE")
    }

}
