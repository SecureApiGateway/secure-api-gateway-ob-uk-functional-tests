/* ************************************************* */
/* encore-demo profile                               */
/* ************************************************* */
/* Values shared by all environments live in gradle/profiles/defaults.gradle.kts,
   applied before this file; only the environment-specific values are set here. */

// servers - sample sapig-ob-mr1
// as-sapig-ob-mr1.encore.pingidentity.com
val environment by extra("sapig-ob-mr1")
val amCookieName by extra("cfaba3b41129a6e")
val asIGServer by extra("https://as-$environment.encore.pingidentity.com")
val rsIGServer by extra("https://rs-$environment.encore.pingidentity.com")
// non-uniform mtls hostnames provided - e.g. as-mtls-sapig-sapig-ob-mr1.encore.pingidentity.com
val asIGServerMtls by extra("https://as-mtls-sapig-$environment.encore.pingidentity.com")
val rsIGServerMtls by extra("https://rs-mtls-sapig-$environment.encore.pingidentity.com")

// Kid's - encore uses a different ASPSP signing key
val aspspJwtSignerKid by extra("o5xN09cvkzpLplq1mKQ8CsWabYU")
