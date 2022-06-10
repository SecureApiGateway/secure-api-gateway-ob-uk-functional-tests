package com.forgerock.uk.openbanking.support.discovery

import com.forgerock.securebanking.framework.configuration.IG_SERVER
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
    var issuer: String,
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
    var token_endpoint: String,
    val token_endpoint_auth_methods_supported: List<String>,
    val token_endpoint_auth_signing_alg_values_supported: List<String>,
    val userinfo_encryption_alg_values_supported: List<String>,
    val userinfo_encryption_enc_values_supported: List<String>,
    val userinfo_endpoint: String,
    val userinfo_signing_alg_values_supported: List<String>,
    val version: String,
)

val asDiscovery by lazy { getAsConfiguration() }

private fun getAsConfiguration(): AsDiscovery {
    val (_, response, result) = Fuel.get("$IG_SERVER/am/oauth2/realms/root/realms/alpha/.well-known/openid-configuration")
        .responseObject<AsDiscovery>()
    if (!response.isSuccessful) throw AssertionError("Failed to load As Discovery", result.component2())

    //TODO: change the access token route to go to iam.dev.forgerock.financial
    val asDiscovery = result.get()
    return asDiscovery
}
