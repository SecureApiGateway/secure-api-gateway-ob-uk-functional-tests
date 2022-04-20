/* ************************************************* */
/* default profile                                   */
/* ************************************************* */
// servers
val environment by extra("dev")
val rsServer by extra("https://rs.$environment.forgerock.financial")
val platformServer by extra("https://iam.dev.forgerock.financial")
val cookieName by extra("iPlanetDirectoryPro")
val rcsServer by extra("https://rcs.$environment.forgerock.financial")
val igServer by extra("https://obdemo.$environment.forgerock.financial")
//User's Password
val userPassword by extra("0penBanking!")
val username by extra("psu4test")

// Kid's
val eidasTestSigningKid by extra("2yNjPOCjpO8rcKg6_lVtWzAQR0U")
val preEidasTestSigningKid by extra("RmQ-EmViYPKXYyGCVnfuMo6ggXE")
val aspspJwtSignerKid by extra("R3MviZ4QUPEDJm7RS3Mw")