package com.forgerock.sapi.gateway.ob.uk.framework.consent

import com.forgerock.sapi.gateway.framework.configuration.OBDomesticVRPConsentRequestFactoryClass
import com.forgerock.sapi.gateway.framework.configuration.OBWriteDomesticConsent4FactoryClass

/**
 * Bridges the Java ConsentFactoryRegistry into Kotlin as a singleton.
 */
object ConsentFactoryRegistryHolder {

    val consentFactoryRegistry: ConsentFactoryRegistry =
        ConsentFactoryRegistry(
            listOf(OBDomesticVRPConsentRequestFactoryClass, OBWriteDomesticConsent4FactoryClass)
        )

}