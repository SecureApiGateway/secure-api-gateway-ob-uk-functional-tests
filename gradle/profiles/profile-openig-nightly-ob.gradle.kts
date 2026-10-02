/* ************************************************* */
/* openig-nightly-ob profile                                */
/* ************************************************* */
/* Values shared by all environments live in gradle/profiles/defaults.gradle.kts,
   applied before this file; only the environment-specific values are set here. */

// servers
val environment by extra("openig-nightly-ob")
val amCookieName by extra("iPlanetDirectoryPro")
val asIGServer by extra("https://as-sapig.$environment.forgerock.financial")
val rsIGServer by extra("https://rs-sapig.$environment.forgerock.financial")
  // By default non-mtls hostname is transformed for mtls, but if a non-uniform value is required then provide below
  // val asIGServerMtls by extra("https://my-as-mtls-sapig-$environment...")
  // val rsIGServerMtls by extra("https://my-rs-mtls-sapig-$environment...")

// Kid's
val aspspJwtSignerKid by extra("R3MviZ4QUPEDJm7RS3Mw")

