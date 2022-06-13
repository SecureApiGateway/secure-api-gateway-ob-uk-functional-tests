package com.forgerock.securebanking.framework.data

import com.forgerock.securebanking.openbanking.uk.common.api.meta.OBConstants
import com.forgerock.uk.openbanking.framework.constants.REDIRECT_URI
import com.forgerock.uk.openbanking.support.discovery.asDiscovery
import java.util.*

data class RequestParameters(
    val aud: String = asDiscovery.issuer,
    val claims: Claims,
    val client_id: String,
    val exp: Long = System.currentTimeMillis() / 1000 + 600,
    val iat: Long = System.currentTimeMillis() / 1000,
    val jti: UUID = UUID.randomUUID(),
    val iss: String,
    val nonce: String = "10d260bf-a7d9-444a-92d9-7b7a5f088208",
    val redirect_uri: String = REDIRECT_URI,
    val response_type: String = "code id_token",
    val scope: String = asDiscovery.scopes_supported.intersect(
        listOf(
            OBConstants.Scope.OPENID,
            OBConstants.Scope.ACCOUNTS
        )
    ).joinToString(separator = " "),
    val state: String = "10d260bf-a7d9-444a-92d9-7b7a5f088208"
) {
    data class Claims(
        val id_token: IdToken,
        val userinfo: Userinfo
    ) {
        data class IdToken(
            val acr: Acr,
            val openbanking_intent_id: OpenbankingIntentId
        ) {
            data class OpenbankingIntentId(
                val essential: Boolean,
                val value: String
            )

            data class Acr(
                val essential: Boolean,
                val value: String
            )
        }

        data class Userinfo(
            val openbanking_intent_id: OpenbankingIntentId
        ) {
            data class OpenbankingIntentId(
                val essential: Boolean,
                val value: String
            )
        }
    }
}
