package com.forgerock.securebanking

data class AccessToken(
    val access_token: String,
    val expires_in: Int,
    val scope: String,
    val token_type: String
)
