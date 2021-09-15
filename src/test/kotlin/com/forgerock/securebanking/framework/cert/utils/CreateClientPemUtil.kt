package com.forgerock.securebanking.framework.cert.utils

import com.forgerock.securebanking.framework.constants.OB_DEMO
import com.forgerock.securebanking.framework.platform.register.Organization
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.core.isSuccessful
import com.google.gson.GsonBuilder
import com.nimbusds.jose.jwk.JWK
import org.apache.commons.io.output.FileWriterWithEncoding
import org.bouncycastle.util.io.pem.PemObject
import org.bouncycastle.util.io.pem.PemWriter
import java.io.File
import java.nio.charset.Charset
import java.nio.file.Files
import java.security.KeyFactory
import java.security.spec.RSAPrivateCrtKeySpec
import java.security.spec.RSAPublicKeySpec

fun main() {
    val jwk: JWK = JWK.parse(getJWK())
    createPrivateKeyFileFromJWK(jwk)
    createPublicKeyFileFromJWK(jwk)
    createPemCertFileFromJWK(jwk)
}

private fun getJWK(): String {
    val getCertsRequest = GsonBuilder().create().toJson(Organization())
    val certsURL = "$OB_DEMO/jwkms/apiclient/getcert"
    val (_, certResult, r) = Fuel.post(certsURL)
        .header(Headers.CONTENT_TYPE, "application/json")
        .body(getCertsRequest)
        .response()
    if (!certResult.isSuccessful) throw AssertionError(
        "Could not get requested certificates data from ${certsURL}: ${
            String(
                certResult.data
            )
        }", r.component2()
    )
    return String(r.get())
}

private fun createPublicKeyFileFromJWK(jwk: JWK) {
    val rsaKey = jwk.toRSAKey()
    val factory = KeyFactory.getInstance(jwk.keyType.value)
    val rsaPublicKeySpec = RSAPublicKeySpec(
        rsaKey.modulus.decodeToBigInteger(), // n
        rsaKey.publicExponent.decodeToBigInteger() //e
    )
    val publicKeySpec = factory.generatePublic(rsaPublicKeySpec)

    writeFile(
        File("${support.PATH_TO_STORE_EIDAS}/${support.publicPemKeyClient}"),
        PemObject("${support.PUBLIC_KEY}", publicKeySpec.encoded)
    )
}

private fun createPrivateKeyFileFromJWK(jwk: JWK) {
    val rsaKey = jwk.toRSAKey()
    val factory = KeyFactory.getInstance(jwk.keyType.value)
    val rsaPrivateCrtKeySpec = RSAPrivateCrtKeySpec(
        rsaKey.modulus.decodeToBigInteger(),  //n
        rsaKey.publicExponent.decodeToBigInteger(),  // e
        rsaKey.privateExponent.decodeToBigInteger(),  // d
        rsaKey.firstPrimeFactor.decodeToBigInteger(),  //p
        rsaKey.secondPrimeFactor.decodeToBigInteger(),  //q
        rsaKey.firstFactorCRTExponent.decodeToBigInteger(),  //pE
        rsaKey.secondFactorCRTExponent.decodeToBigInteger(),  //qE
        rsaKey.firstCRTCoefficient.decodeToBigInteger() //crt coefficient
    )

    val privateKeyCrt = factory.generatePrivate(rsaPrivateCrtKeySpec)

    writeFile(
        File("${support.PATH_TO_STORE_EIDAS}/${support.privatePemKeyClient}"),
        PemObject("${support.PRIVATE_KEY}", privateKeyCrt.encoded)
    )
}

private fun createPemCertFileFromJWK(jwk: JWK) {
    writeFile(
        File("${support.PATH_TO_STORE_EIDAS}/${support.certificatePemClient}"),
        PemObject("${support.CERTIFICATE}", jwk.x509CertChain[0].decode())
    )
}

private fun writeFile(file: File, pemObject: PemObject) {
    deleteFileIfExist(file)
    val pemWriter = PemWriter(FileWriterWithEncoding(file, Charset.defaultCharset()))
    pemWriter.writeObject(pemObject)
    pemWriter.close()
    Files.setPosixFilePermissions(file.toPath(), support.filePermissions)
}
