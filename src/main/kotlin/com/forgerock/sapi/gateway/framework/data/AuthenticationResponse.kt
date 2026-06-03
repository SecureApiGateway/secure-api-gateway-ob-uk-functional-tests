package com.forgerock.sapi.gateway.framework.data

data class AuthenticationResponse(
    val tokenId: String? = null,
    val successUrl: String? = null,
    val realm: String? = null,
    val authId: String? = null,
    val callbacks: List<AuthCallback>? = null
)

data class AuthCallback(
    val type: String,
    val output: List<AuthCallbackValue>? = null,
    val input: List<AuthCallbackValue>? = null
)

data class AuthCallbackValue(
    val name: String,
    val value: String? = null
)
