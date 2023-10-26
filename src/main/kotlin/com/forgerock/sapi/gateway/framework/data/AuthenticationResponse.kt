package com.forgerock.sapi.gateway.framework.data

data class AuthenticationResponse(
    val tokenId: String,
    val successUrl: String,
    val realm: String
)

