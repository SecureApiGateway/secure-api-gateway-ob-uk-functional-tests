package com.forgerock.sapi.gateway.ob.uk.framework.accesstoken.model

data class Claims(
        val iss: String = com.forgerock.sapi.gateway.ob.uk.framework.configuration.OB_SOFTWARE_ID,
        val sub: String = com.forgerock.sapi.gateway.ob.uk.framework.configuration.OB_SOFTWARE_ID,
        val scope: String = com.forgerock.sapi.gateway.ob.uk.framework.configuration.SCOPES_TPP,
        val aud: String = com.forgerock.sapi.gateway.ob.uk.framework.configuration.AUDIENCE,
        val exp: Long = (System.currentTimeMillis() / 1000) + 180,
)

data class ClaimsTest(
        val iss: String = com.forgerock.sapi.gateway.ob.uk.framework.configuration.OB_SOFTWARE_ID,
        val sub: String = com.forgerock.sapi.gateway.ob.uk.framework.configuration.OB_SOFTWARE_ID,
        val scope: String = com.forgerock.sapi.gateway.ob.uk.framework.configuration.SCOPES_TPP,
        val aud: String = com.forgerock.sapi.gateway.ob.uk.framework.configuration.AUDIENCE_SANDBOX,
        val exp: Long = (System.currentTimeMillis() / 1000) + 180,
)
