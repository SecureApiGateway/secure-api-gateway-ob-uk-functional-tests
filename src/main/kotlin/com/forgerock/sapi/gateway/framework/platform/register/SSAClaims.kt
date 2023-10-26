package com.forgerock.sapi.gateway.framework.platform.register

data class SSAClaims(
    val exp: Long? = (System.currentTimeMillis() / 1000) + 180,
    val scope: String? = "openid accounts payments eventpolling",
    val response_types: List<String>? = listOf("code id_token"),
    val redirect_uris: List<String>? = listOf("https://obdemo.dev.forgerock.financial/tpp", "https://tpp.com/callback"),
    val application_type: String? = "web",
    val grant_types: List<String>? = listOf("authorization_code", "refresh_token", "client_credentials"),
    val software_statement: String = "SSA_JWT_SERIALISED",
    val token_endpoint_auth_method: String? = "private_key_jwt",
    val token_endpoint_auth_signing_alg: String? = "PS256",
    val id_token_signed_response_alg: String? = "PS256",
    val request_object_signing_alg: String? = "PS256",
    val request_object_encryption_alg: String? = "RSA-OAEP-256",
    val request_object_encryption_enc: String? = "A128CBC-HS256"
)
