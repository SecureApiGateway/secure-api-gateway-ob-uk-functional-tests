package com.forgerock.securebanking.framework.configuration

import com.forgerock.securebanking.support.registerDirectoryUser
import com.forgerock.securebanking.support.registerPSU
import com.forgerock.securebanking.tests.functional.directory.UserRegistrationRequest

val DOMAIN = System.getenv("DOMAIN") ?: "dev.forgerock.financial"
val OB_DEMO_DOMAIN =
    if (System.getenv("DOMAIN") != null) "obdemo." + System.getenv("DOMAIN") else "obdemo.dev.forgerock.financial"

val PSU_PASSWORD = System.getenv("PSU_PASSWORD") ?: "Passw0rd@1"
val PSU_USERNAME = System.getenv("PSU_USERNAME") ?: "username"

val OB_TPP_OB_EIDAS_TEST_SIGNING_KID =
    System.getenv("OB_TPP_OB_EIDAS_TEST_SIGNING_KID") ?: "2yNjPOCjpO8rcKg6_lVtWzAQR0U"
val OB_TPP_PRE_EIDAS_SIGNING_KID = System.getenv("OB_TPP_PRE_EIDAS_SIGNING_KID") ?: "RmQ-EmViYPKXYyGCVnfuMo6ggXE"

val psu: UserRegistrationRequest by lazy { registerPSU() }
val directoryUser: UserRegistrationRequest by lazy { registerDirectoryUser() }
