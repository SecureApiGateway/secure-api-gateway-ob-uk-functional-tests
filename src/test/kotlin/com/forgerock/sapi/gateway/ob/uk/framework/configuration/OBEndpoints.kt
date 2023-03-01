package com.forgerock.sapi.gateway.ob.uk.framework.configuration

/* Sandbox directory api endpoints */
val TOKEN_URL_SANDBOX = System.getenv("tokenUrlSandbox") ?: "https://matls-sso.openbankingtest.org.uk/as/token.oauth2"
val TEST_URL_SANDBOX = System.getenv("testUrlSandbox") ?: "https://matls-api.openbankingtest.org.uk/scim/v2/OBAccountPaymentServiceProviders/"
val AUDIENCE_SANDBOX = System.getenv("audienceSandbox") ?: "https://matls-sso.openbankingtest.org.uk/as/token.oauth2"
val SSA_MATLS_LEGACY_URL_SANDBOX = System.getenv("ssaMatlsLegacyUrlSandbox") ?: "https://matls-ssaapi.openbankingtest.org.uk/api/v1rc2/tpp/{org_id}/ssa/{software_id}"
val SSA_MATLS_URL_SANDBOX = System.getenv("ssaMatlsUrlSandbox") ?: "https://matls-dirapi.openbankingtest.org.uk/organisation/tpp/{org_id}/software-statement/{software_id}/software-statement-assertion"
/* Directory api endpoints */
val TOKEN_URL = System.getenv("tokenUrl") ?: "https://matls-sso.openbanking.org.uk/as/token.oauth2"
val AUDIENCE = System.getenv("audience") ?: "https://matls-sso.openbanking.org.uk/as/token.oauth2"
val TEST_URL = System.getenv("testUrl") ?: "https://matls-api.openbanking.org.uk/scim/v2/OBAccountPaymentServiceProviders/"
val SSA_MATLS_LEGACY_URL = System.getenv("ssaMatlsLegacyUrl") ?: "https://matls-ssaapi.openbanking.org.uk/api/v1rc2/tpp/{org_id}/ssa/{software_id}"
val SSA_MATLS_URL = System.getenv("ssaMatlsUrl") ?: "https://matls-dirapi.openbanking.org.uk/organisation/tpp/{org_id}/software-statement/{software_id}/software-statement-assertion"
