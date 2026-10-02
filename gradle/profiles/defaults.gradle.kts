/* ************************************************* */
/* shared profile defaults                           */
/* ************************************************* */
/*
 * Values shared by every environment profile. Applied BEFORE the selected
 * profile (see build.gradle.kts), which may override any value below —
 * later definitions win, so a profile only needs to set what differs.
 *
 * The OB directory signing kid lives here so that certificate renewal
 * (see README "Set up the certificates for test purposes") updates ONE
 * file instead of every profile.
 */

// OB directory organisation / software statement
val obOrganisationId by extra("0015800001041REAAY")
val obSoftwareId by extra("Y6NjA9TOn3aMm9GaPtLwkp")

// OB directory assigned kids
// Signing kid: assigned by the OB directory when the OBSeal key is registered; changes on renewal
val eidasTestSigningKid by extra("xaNZ98oTeX8RmVvcjoqY2qcY88E")

// OB token scopes
val scopesTpp by extra("ASPSPReadAccess TPPReadAccess AuthoritiesReadAccess")
val scopesAspsp by extra("ASPSPReadAccess TPPReadAll AuthoritiesReadAccess")

// Truststore configuration
val truststorePath by extra("/com/forgerock/sapi/gateway/ob/uk/truststore.jks")
val truststorePassword by extra("changeit")

// Expected path to find the Certificates used for test purposes
val eidasOBSealKey by extra("./certificates/OBSeal.key")
val eidasOBSealPem by extra("./certificates/OBSeal.pem")
val eidasOBWacKey by extra("./certificates/OBWac.key")
val eidasOBWacPem by extra("./certificates/OBWac.pem")

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

// PSU User configuration
// userId needs to be a UUID and match with the value set in the user data initialiser
val userId by extra("4737f9f9-fa0a-4159-bc61-7da31542e624")
val userPassword by extra("0penBanking!")
val username by extra("psu4test")
// The values must match the Identification field for an Account owned by the PSU
val userDebtorAccountIdentification by extra("01233243245676")
val userAccountId by extra("01233243245676")

val redirectUri by extra("https://www.google.co.uk")
