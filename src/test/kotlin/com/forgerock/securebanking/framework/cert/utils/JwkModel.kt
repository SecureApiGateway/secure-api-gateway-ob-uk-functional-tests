package com.forgerock.securebanking.framework.cert.utils

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName

data class JwkModel(
    val p: String,
    val kty: String,
    @JsonProperty("x5t#S256")
    val x5t: String,
    val q: String,
    val d: String,
    val e: String,
    val kid: String,
    val x5c: List<String>,
    val qi: String,
    val dp: String,
    val dq: String,
    val n: String
)
