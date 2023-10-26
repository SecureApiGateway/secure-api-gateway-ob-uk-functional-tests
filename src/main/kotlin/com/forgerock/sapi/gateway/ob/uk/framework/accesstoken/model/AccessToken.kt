package com.forgerock.sapi.gateway.ob.uk.framework.accesstoken.model

data class AccessTokenRequest(
        val client_assertion_type: String? =  "urn:ietf:params:oauth:client-assertion-type:jwt-bearer",
        val grant_type: String? = "client_credentials",
        val client_id: String? = com.forgerock.sapi.gateway.ob.uk.framework.configuration.OB_SOFTWARE_ID,
        val client_assertion: String,
        val scope: String? = com.forgerock.sapi.gateway.ob.uk.framework.configuration.SCOPES_TPP
)

data class AccessTokenResponse(
    val access_token: String,
    val token_type: String,
    val expires_in: Int
)
