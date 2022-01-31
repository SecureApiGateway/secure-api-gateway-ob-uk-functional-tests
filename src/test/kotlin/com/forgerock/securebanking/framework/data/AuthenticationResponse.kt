package com.forgerock.securebanking.framework.data

data class AuthenticationResponse(
    val tokenId: String,
    val successUrl: String,
    val realm: String
)

