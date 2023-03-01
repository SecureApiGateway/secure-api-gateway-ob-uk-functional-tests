package com.forgerock.sapi.gateway.framework.data

import com.forgerock.sapi.gateway.ob.uk.support.discovery.asDiscovery
import java.util.*

data class ClientCredentialData(
    val aud: String = asDiscovery.issuer,
    val jti: UUID = UUID.randomUUID(),
    val iss: String,
    val sub: String,
    val exp: Long = System.currentTimeMillis() / 1000 + 600,
    val iat: Long = System.currentTimeMillis() / 1000,
    val nbf: Long = System.currentTimeMillis() / 1000
)
