package com.forgerock.securebanking.framework.configuration

import com.forgerock.securebanking.support.registerDirectoryUser
import com.forgerock.securebanking.support.registerPSU
import com.forgerock.securebanking.tests.functional.deprecated.directory.UserRegistrationRequest

val RS_SERVER = System.getenv("rsServer") ?: "https://rs.dev.forgerock.financial"
val PLATFORM_SERVER = System.getenv("platformServer") ?: "https://iam.dev.forgerock.financial"
val RCS_SERVER = System.getenv("rcsServer") ?: "https://rcs.dev.forgerock.financial"
val IG_SERVER = System.getenv("igServer") ?: "https://obdemo.dev.forgerock.financial"

val PSU_PASSWORD = System.getenv("userPassword") ?: "Passw0rd@1"
val PSU_USERNAME = System.getenv("username") ?: "username"

val OB_TPP_OB_EIDAS_TEST_SIGNING_KID =
    System.getenv("eidasTestSigningKid") ?: "2yNjPOCjpO8rcKg6_lVtWzAQR0U"
val OB_TPP_PRE_EIDAS_SIGNING_KID = System.getenv("preEidasTestSigningKid") ?: "RmQ-EmViYPKXYyGCVnfuMo6ggXE"

val COOKIE_NAME = System.getenv("cookieName") ?: "iPlanetDirectoryPro"

val psu: UserRegistrationRequest by lazy { registerPSU() }
val directoryUser: UserRegistrationRequest by lazy { registerDirectoryUser() }
