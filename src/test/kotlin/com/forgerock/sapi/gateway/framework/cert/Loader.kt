package com.forgerock.sapi.gateway.framework.cert

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo
import org.bouncycastle.crypto.CryptoException
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.PEMKeyPair
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
import org.bouncycastle.util.io.pem.PemReader
import java.io.FileReader
import java.io.StringReader
import java.security.PrivateKey
import java.security.Security


fun loadRsaPrivateKey(pemKey: String): PrivateKey? {
    Security.addProvider(BouncyCastleProvider())

    val pk: PrivateKey? = try {
        val pr = PemReader(StringReader(pemKey))
        val po = pr.readPemObject()
        val pem = PEMParser(StringReader(pemKey))
        if (po.type == "PRIVATE KEY") {
            JcaPEMKeyConverter().getPrivateKey(pem.readObject() as PrivateKeyInfo)
        } else {
            val kp = pem.readObject() as PEMKeyPair
            JcaPEMKeyConverter().getPrivateKey(kp.privateKeyInfo)
        }
    } catch (e: Exception) {
        throw CryptoException("Failed to convert private key bytes", e)
    }
    return pk
}

fun loadRsaPrivateKey(fileReader: FileReader): PrivateKey? {
    val pemKey = fileReader.readText()
    return loadRsaPrivateKey(pemKey)
}
