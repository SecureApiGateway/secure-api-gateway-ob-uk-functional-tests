package com.forgerock.sapi.gateway.ob.uk.framework.consent

import com.forgerock.sapi.gateway.framework.configuration.OBDomesticVRPConsentRequestFactoryClass
import com.forgerock.sapi.gateway.framework.configuration.OBWriteDomesticConsent4FactoryClass
import com.forgerock.sapi.gateway.framework.configuration.OBWriteDomesticScheduledConsent4Class
import com.forgerock.sapi.gateway.framework.configuration.OBWriteDomesticStandingOrderConsent5Factory

/**
 * Bridges the Java ConsentFactoryRegistry into Kotlin as a singleton.
 */
object ConsentFactoryRegistryHolder {

    val consentFactoryRegistry: ConsentFactoryRegistry =
        ConsentFactoryRegistry(
            listOf(
                OBDomesticVRPConsentRequestFactoryClass,
                OBWriteDomesticConsent4FactoryClass,
                OBWriteDomesticScheduledConsent4Class,
                OBWriteDomesticStandingOrderConsent5Factory
            )
        )

}