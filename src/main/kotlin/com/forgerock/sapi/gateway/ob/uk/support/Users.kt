package com.forgerock.sapi.gateway.ob.uk.support

import com.forgerock.sapi.gateway.framework.configuration.PSU_PASSWORD
import com.forgerock.sapi.gateway.framework.configuration.PSU_USERNAME
import com.forgerock.sapi.gateway.ob.uk.support.registration.UserRegistrationRequest

private fun initializeUser(): UserRegistrationRequest {
    return UserRegistrationRequest(PSU_USERNAME, PSU_PASSWORD)
}

fun registerPSU(): UserRegistrationRequest {
    return initializeUser()
}
