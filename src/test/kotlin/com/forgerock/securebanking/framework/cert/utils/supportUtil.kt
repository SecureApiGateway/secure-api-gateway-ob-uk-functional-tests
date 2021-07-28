package com.forgerock.securebanking.framework.cert.utils

import com.forgerock.securebanking.framework.http.fuel.initFuel
import com.forgerock.securebanking.framework.platform.register.Organization
import java.io.File
import java.nio.file.attribute.PosixFilePermission

data class SupportValues(
    val PATH_EIDAS_RESOURCES: String = "/com/forgerock/securebanking/platform/eidas",
    val PATH_TO_STORE_EIDAS: String = "src/test/resources/${PATH_EIDAS_RESOURCES}",
    val PRIVATE_KEY: String = "PRIVATE KEY",
    val PUBLIC_KEY: String = "PUBLIC KEY",
    val CERTIFICATE: String = "CERTIFICATE",
    val PRIVATE_PREFIX: String = "-private",
    val PUBLIC_PREFIX: String = "-public",
    val fileName: String = Organization().org_name,
    val privatePemKeyClient: String = "${fileName}${PRIVATE_PREFIX}.key",
    val publicPemKeyClient: String = "${fileName}${PUBLIC_PREFIX}.key",
    val certificatePemClient: String = "${fileName}.pem",
    val ssaJwtFile: String = "${fileName}.jwt",
    val ssaJwsFile: String = "${fileName}.jws",
    val filePermissions: Set<PosixFilePermission> = setOf(
        PosixFilePermission.OWNER_EXECUTE,
        PosixFilePermission.OWNER_READ,
        PosixFilePermission.OWNER_WRITE
    )
)

val support = SupportValues()

fun initSSLClient() {

    val privatePemStream = object {}.javaClass.getResourceAsStream("${support.PATH_EIDAS_RESOURCES}/${support.certificatePemClient}")
    val publicPemStream = object {}.javaClass.getResourceAsStream("${support.PATH_EIDAS_RESOURCES}/${support.privatePemKeyClient}")
    initFuel(privatePemStream, publicPemStream)
}

fun deleteFileIfExist(file: File) {
    if (file.exists()) {
        println("deleting existing file $file to create a new one.")
        file.delete()
    }
}
