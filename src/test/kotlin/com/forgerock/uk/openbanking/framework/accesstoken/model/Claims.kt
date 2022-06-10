package com.forgerock.uk.openbanking.framework.accesstoken.model

import com.forgerock.uk.openbanking.framework.configuration.AUDIENCE
import com.forgerock.uk.openbanking.framework.configuration.AUDIENCE_SANDBOX
import com.forgerock.uk.openbanking.framework.configuration.OB_SOFTWARE_ID
import com.forgerock.uk.openbanking.framework.configuration.SCOPES_TPP

data class Claims(
    val iss: String = OB_SOFTWARE_ID,
    val sub: String = OB_SOFTWARE_ID,
    val scope: String = SCOPES_TPP,
    val aud: String = AUDIENCE,
    val exp: Long = (System.currentTimeMillis() / 1000) + 180,
)

data class ClaimsTest(
    val iss: String = OB_SOFTWARE_ID,
    val sub: String = OB_SOFTWARE_ID,
    val scope: String = SCOPES_TPP,
    val aud: String = AUDIENCE_SANDBOX,
    val exp: Long = (System.currentTimeMillis() / 1000) + 180,
)
