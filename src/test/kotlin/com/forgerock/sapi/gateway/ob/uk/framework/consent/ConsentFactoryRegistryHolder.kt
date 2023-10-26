package com.forgerock.sapi.gateway.ob.uk.framework.consent

import com.forgerock.sapi.gateway.framework.configuration.OBDomesticVRPConsentRequestFactoryClass
import com.forgerock.sapi.gateway.framework.configuration.OBWriteDomesticConsent4FactoryClass
import com.forgerock.sapi.gateway.framework.configuration.OBWriteDomesticScheduledConsent4Class
import com.forgerock.sapi.gateway.framework.configuration.OBWriteDomesticStandingOrderConsent5FactoryClass
import com.forgerock.sapi.gateway.framework.configuration.OBWriteInternationalConsent5FactoryClass
import com.forgerock.sapi.gateway.framework.configuration.OBWriteInternationalScheduledConsent5FactoryClass
import com.forgerock.sapi.gateway.framework.configuration.OBWriteInternationalStandingOrderConsent6FactoryClass

/**
 * Bridges the Java ConsentFactoryRegistry into Kotlin as a singleton.
 *
 * Responsible for creating the registry using the configuration found in Config.kt
 */
object ConsentFactoryRegistryHolder {

    val consentFactoryRegistry: ConsentFactoryRegistry =
        ConsentFactoryRegistry(
            listOf(
                OBDomesticVRPConsentRequestFactoryClass,
                OBWriteDomesticConsent4FactoryClass,
                OBWriteDomesticScheduledConsent4Class,
                OBWriteDomesticStandingOrderConsent5FactoryClass,
                OBWriteInternationalConsent5FactoryClass,
                OBWriteInternationalScheduledConsent5FactoryClass,
                OBWriteInternationalStandingOrderConsent6FactoryClass
            )
        )

}