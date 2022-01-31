package com.forgerock.securebanking.framework.constants

import com.forgerock.uk.openbanking.framework.accesstoken.constants.OB_SOFTWARE_ID
import com.forgerock.uk.openbanking.framework.accesstoken.constants.SCOPES_TPP

data class AccessTokenRequest(
    val client_assertion_type: String? =  "urn:ietf:params:oauth:client-assertion-type:jwt-bearer",
    val grant_type: String? = "client_credentials",
    val client_id: String? = OB_SOFTWARE_ID,
    val client_assertion: String,
    val scope: String? = SCOPES_TPP
)

data class AccessTokenResponse(
    val access_token: String,
    val token_type: String,
    val expires_in: Int
)