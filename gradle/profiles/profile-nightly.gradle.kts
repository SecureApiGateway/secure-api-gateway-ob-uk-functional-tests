/* ************************************************* */
/* default profile                                   */
/* ************************************************* */

// Truststore configuration
val truststorePath by extra("/com/forgerock/sapi/gateway/ob/uk/truststore.jks")
val truststorePassword by extra("changeit")
/**
 * OB configuration: Used Create a Software Statement Assertion (SSA) using an API call for Dynamic Registration
 * https://openbanking.atlassian.net/wiki/spaces/DZ/pages/1322979574/Open+Banking+Directory+Usage+-+eIDAS+release+Production+-+v1.9
 */
// OB directory organisation
val obOrganisationId by extra("0015800001041REAAY")
val obSoftwareId by extra("Y6NjA9TOn3aMm9GaPtLwkp")

// OB token scopes
val scopesTpp by extra("ASPSPReadAccess TPPReadAccess AuthoritiesReadAccess")
val scopesAspsp by extra("ASPSPReadAccess TPPReadAll AuthoritiesReadAccess")

/*
 OB Sandbox directory
 */
val obSandboxHostSufix by extra("openbankingtest.org.uk")
val tokenUrlSandbox by extra("https://matls-sso.$obSandboxHostSufix/as/token.oauth2")
val testUrlSandbox by extra("https://matls-api.$obSandboxHostSufix/scim/v2/OBAccountPaymentServiceProviders/")
val audienceSandbox by extra("https://matls-sso.$obSandboxHostSufix/as/token.oauth2")
val ssaMatlsUrlSandbox by extra("https://matls-dirapi.$obSandboxHostSufix/organisation/tpp/{org_id}/software-statement/{software_id}/software-statement-assertion")
val ssaMatlsLegacyUrlSandbox by extra("https://matls-ssaapi.$obSandboxHostSufix/api/v1rc2/tpp/{org_id}/ssa/{software_id}")
/*
 OB Directory api endpoints
 */
val obHostSufix by extra("openbanking.org.uk")
val tokenUrl by extra("https://matls-sso.$obHostSufix/as/token.oauth2")
val audience by extra("https://matls-sso.$obHostSufix/as/token.oauth2")
val testUrl by extra("https://matls-api.$obHostSufix/scim/v2/OBAccountPaymentServiceProviders/")
val ssaMatlsLegacyUrl by extra("https://matls-ssaapi.$obHostSufix/api/v1rc2/tpp/{org_id}/ssa/{software_id}")
val ssaMatlsUrl by extra("https://matls-dirapi.$obHostSufix/organisation/tpp/{org_id}/software-statement/{software_id}/software-statement-assertion")

/**
 * Functional tests configuration
 */
// servers
val environment by extra ("nightly")
val amCookieName by extra("80d4a6cdee74c6e")
val igServer by extra("https://sapig.$environment.forgerock.financial")

// PSU User configuration
// needs to be a UUID and match with the value set in the use data initialiser
val userId by extra ("4737f9f9-fa0a-4159-bc61-7da31542e624")
val userPassword by extra("0penBanking!")
val username by extra("psu4test")
val userDebtorAccountIdentification by extra("01233243245676")
val userAccountId by extra ("01233243245676")

// Kid's
val eidasTestSigningKid by extra("qfL4CT5GrVgoyXNQtUjF5TIVOXA")
val preEidasTestSigningKid by extra("RmQ-EmViYPKXYyGCVnfuMo6ggXE")
val aspspJwtSignerKid by extra("R3MviZ4QUPEDJm7RS3Mw")

// Expected path to find the Certificates used for test purposes
val eidasOBSealKey by extra("./certificates/OBSeal.key")
val eidasOBSealPem by extra("./certificates/OBSeal.pem")
val eidasOBWacKey by extra("./certificates/OBWac.key")
val eidasOBWacPem by extra("./certificates/OBWac.pem")

val redirectUri by extra("https://www.google.co.uk")
