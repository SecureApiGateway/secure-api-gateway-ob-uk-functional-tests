package com.forgerock.securebanking.framework.platform.register

data class SoftwareStatementAssertion(
    val software_id: String = "Forgerock_org4test_id",
    val software_client_name: String = "Forgerock_org4test application",
    val software_client_id: String = "11111111",
    val software_tos_uri: String = "https://tpp.com/tos.html",
    val software_client_description: String = "Tpp Forgerock_org4test client software for secure banking",
    val software_redirect_uris: List<String> = listOf(
        "https://obdemo.dev.forgerock.financial/tpp",
        "https://tpp.com/callback"
    ),
    val software_policy_uri: String = "https://tpp.com/policy.html",
    val software_logo_uri: String = "https://tpp.com/tpp.png",
    val software_roles: List<String> = listOf("DATA", "AISP", "PISP", "CBPII")
)
