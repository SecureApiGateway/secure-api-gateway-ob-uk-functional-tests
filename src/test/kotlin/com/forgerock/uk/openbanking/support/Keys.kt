package com.forgerock.uk.openbanking.support

import com.forgerock.securebanking.framework.data.SoftwareStatement
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.isSuccessful
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.io.pem.PemObject
import org.bouncycastle.util.io.pem.PemReader
import java.io.FileReader
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.Security
import java.security.spec.PKCS8EncodedKeySpec


/**
 * Load rsa private key in DER format. DER because it's nicer code to parse DER format than PEM
 *
 * @param key path to private key. Defaults to open banking directory signing key
 */
fun loadRsaPrivateKey(key: String): PrivateKey? {
    Security.addProvider(BouncyCastleProvider())
//    PEMReader(StringReader(key) as Reader).use { pemReader ->
//        val `object` = pemReader.readObject()
//        return if (`object` is KeyPair)
//            `object`.private
//        else
//            `object` as PrivateKey
//    }

    val factory: KeyFactory = KeyFactory.getInstance("RSA")

    FileReader(key).use { keyReader ->
        PemReader(keyReader).use { pemReader ->
            val pemObject: PemObject = pemReader.readPemObject()
            val content: ByteArray = pemObject.content
            val privKeySpec = PKCS8EncodedKeySpec(content)
            return factory.generatePrivate(privKeySpec)
        }
    }
}

fun getPrivateCert(softwareStatement: SoftwareStatement?, kid: String?, sessionToken: String): String {
    val (_, response, public) = Fuel.get("https://service.directory.DOMAIN/api/software-statement/${softwareStatement?.id}/application/${kid}/download/privateCert")
        .header("Cookie", "obri-session=$sessionToken")
        .responseString()
    if (!response.isSuccessful) throw AssertionError("Failed to download private cert", public.component2())
    return public.get()
}

fun getPublicCert(softwareStatement: SoftwareStatement?, kid: String?, sessionToken: String): String {
    val (_, response, public) = Fuel.get("https://service.directory.DOMAIN/api/software-statement/${softwareStatement?.id}/application/${kid}/download/publicCert")
        .header("Cookie", "obri-session=$sessionToken")
        .responseString()
    if (!response.isSuccessful) throw AssertionError("Failed to download public cert", public.component2())
    return public.get()
}
