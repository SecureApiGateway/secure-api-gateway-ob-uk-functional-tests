package com.forgerock.securebanking.tests.functional.deprecated.directory

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.forgerock.securebanking.framework.configuration.directoryUser
import com.forgerock.securebanking.support.directory.createSoftwareStatement
import com.forgerock.securebanking.support.directory.getEncryptionKid
import com.forgerock.securebanking.support.directory.getSigningKid
import com.forgerock.securebanking.support.directory.getTransportKid
import com.forgerock.securebanking.support.login
import com.github.kittinunf.fuel.Fuel
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SoftwareStatementTest {

    private lateinit var sessionToken: String

    @BeforeAll
    fun setUp() {
        this.sessionToken = login(directoryUser.user.userName, directoryUser.user.password)
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
        val (_, response, result) = Fuel.get("https://service.directory.DOMAIN/api/software-statement/${softwareStatement.id}/application/${transportKid}/download/privateCert")
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
        val (_, response, result) = Fuel.get("https://service.directory.DOMAIN/api/software-statement/${softwareStatement.id}/application/${transportKid}/download/publicCert")
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
        val (_, response, result) = Fuel.get("https://service.directory.DOMAIN/api/software-statement/${softwareStatement.id}/application/${transportKid}/download/privateCert")
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
        val (_, response, result) = Fuel.get("https://service.directory.DOMAIN/api/software-statement/${softwareStatement.id}/application/${transportKid}/download/publicCert")
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
        val (_, response, result) = Fuel.get("https://service.directory.DOMAIN/api/software-statement/${softwareStatement.id}/application/${transportKid}/download/privateCert")
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
        val (_, response, result) = Fuel.get("https://service.directory.DOMAIN/api/software-statement/${softwareStatement.id}/application/${transportKid}/download/publicCert")
            .header("Cookie", "obri-session=$sessionToken")
            .responseString()

        // Then
        assertThat(response.statusCode).isEqualTo(200)
        assertThat(result.get()).isNotNull()
        assertThat(result.get()).contains("BEGIN CERTIFICATE")
    }

}
