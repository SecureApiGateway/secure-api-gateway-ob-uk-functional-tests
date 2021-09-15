package com.forgerock.securebanking.framework.configuration

import com.forgerock.securebanking.support.registerDirectoryUser
import com.forgerock.securebanking.support.registerPSU
import com.forgerock.securebanking.tests.functional.directory.UserRegistrationRequest

val DOMAIN = System.getenv("DOMAIN") ?: "dev.forgerock.financial"

val psu: UserRegistrationRequest by lazy { registerPSU() }
val directoryUser: UserRegistrationRequest by lazy { registerDirectoryUser() }
