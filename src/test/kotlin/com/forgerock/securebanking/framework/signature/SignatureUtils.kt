package com.forgerock.securebanking.framework.signature

import com.forgerock.securebanking.support.loadRsaPrivateKey
import com.google.gson.GsonBuilder
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm


fun signPayload(payload: Any, signingKey: String, signingKid: String?): String {
    val serialisedPayload = GsonBuilder().create().toJson(payload)
    val key = loadRsaPrivateKey(signingKey)
    return Jwts.builder()
        .setHeaderParam("kid", signingKid)
        .setPayload(serialisedPayload)
        .signWith(key, SignatureAlgorithm.PS256)
        .compact()
}
