package com.forgerock.securebanking.framework.configuration

import com.forgerock.securebanking.support.registerDirectoryUser
import com.forgerock.securebanking.support.registerPSU
import com.forgerock.securebanking.tests.functional.directory.UserRegistrationRequest

val DOMAIN = System.getenv("DOMAIN") ?: "dev.forgerock.financial"
val OB_DEMO_DOMAIN =
    if (System.getenv("DOMAIN") != null) "obdemo." + System.getenv("DOMAIN") else "obdemo.dev.forgerock.financial"

val USER_PASSWORD = System.getenv("USER_PASSWORD") ?: "Passw0rd@1"

val ADMIN_USERNAME = System.getenv("ADMIN_USERNAME") ?: "ADMIN_USERNAME_DEFAULT"
val ADMIN_PASSWORD = System.getenv("ADMIN_PASSWORD") ?: "ADMIN_PASSWORD_DEFAULT"

val OB_TPP_OB_EIDAS_TEST_SIGNING_KID = System.getenv("OB_TPP_OB_EIDAS_TEST_SIGNING_KID") ?: "2yNjPOCjpO8rcKg6_lVtWzAQR0U"
val OB_TPP_PRE_EIDAS_SIGNING_KID = System.getenv("OB_TPP_PRE_EIDAS_SIGNING_KID") ?: "RmQ-EmViYPKXYyGCVnfuMo6ggXE"

val psu: UserRegistrationRequest by lazy { registerPSU() }
val directoryUser: UserRegistrationRequest by lazy { registerDirectoryUser() }
