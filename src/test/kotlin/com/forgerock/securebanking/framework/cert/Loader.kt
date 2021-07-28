package com.forgerock.securebanking.framework.cert

import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.PEMReader
import java.io.FileReader
import java.io.Reader
import java.io.StringReader
import java.security.KeyPair
import java.security.PrivateKey
import java.security.Security


fun loadRsaPrivateKey(keyContent: String): PrivateKey? {
    Security.addProvider(BouncyCastleProvider())
    PEMReader(StringReader(keyContent) as Reader).use { pemReader ->
        val `object` = pemReader.readObject()
        return if (`object` is KeyPair)
            `object`.private
        else
            `object` as PrivateKey
    }
}

fun loadRsaPrivateKey(fileReader: FileReader): PrivateKey? {
    Security.addProvider(BouncyCastleProvider())

    PEMReader(StringReader(fileReader.readText()) as Reader).use { pemReader ->
        val `object` = pemReader.readObject()
        return if (`object` is KeyPair)
            `object`.private
        else
            `object` as PrivateKey
    }
}
