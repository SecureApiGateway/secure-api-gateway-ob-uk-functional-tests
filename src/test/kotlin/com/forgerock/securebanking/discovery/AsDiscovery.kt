package com.forgerock.securebanking.discovery

import com.forgerock.securebanking.DOMAIN
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.isSuccessful
import com.github.kittinunf.fuel.gson.responseObject

data class AsDiscovery(
    val acr_values_supported: List<String>,
    val authorization_endpoint: String,
    val claims_parameter_supported: Boolean,
    val claims_supported: List<String>,
    val grant_types_supported: List<String>,
    val id_token_encryption_alg_values_supported: List<String>,
    val id_token_encryption_enc_values_supported: List<String>,
    val id_token_signing_alg_values_supported: List<String>,
    val introspection_endpoint: String,
    val issuer: String,
    val jwks_uri: String,
    val registration_endpoint: String?,
    val request_object_encryption_alg_values_supported: List<String>,
    val request_object_encryption_enc_values_supported: List<String>,
    val request_object_signing_alg_values_supported: List<String>,
    val request_parameter_supported: Boolean,
    val request_uri_parameter_supported: Boolean,
    val require_request_uri_registration: Boolean,
    val response_types_supported: List<String>,
    val scopes_supported: List<String>,
    val subject_types_supported: List<String>,
    val token_endpoint: String,
    val token_endpoint_auth_methods_supported: List<String>,
    val token_endpoint_auth_signing_alg_values_supported: List<String>,
    val userinfo_encryption_alg_values_supported: List<String>,
    val userinfo_encryption_enc_values_supported: List<String>,
    val userinfo_endpoint: String,
    val userinfo_signing_alg_values_supported: List<String>,
    val version: String
) {
    var registrationEndpoint: String = "https://matls.as.aspsp.$DOMAIN/open-banking/register/"
        get() = if (registration_endpoint == null) "https://matls.as.aspsp.$DOMAIN/open-banking/register/" else registration_endpoint
}

val asDiscovery by lazy { getAsConfiguration() }

private fun getAsConfiguration(): AsDiscovery {
    val (_, response, result) = Fuel.get("https://as.aspsp.$DOMAIN/oauth2/.well-known/openid-configuration")
        .responseObject<AsDiscovery>()
    if (!response.isSuccessful) throw AssertionError("Failed to load As Discovery", result.component2())
    return result.get()
}
