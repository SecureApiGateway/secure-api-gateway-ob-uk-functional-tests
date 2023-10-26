package com.forgerock.sapi.gateway.framework.data

data class SoftwareStatement(
    val applicationId: String,
    val software_id: String,
    val org_id: String,
    val id: String,
    val mode: String,
    val redirectUris: List<Any>,
    val roles: List<String>,
    val status: String
)
