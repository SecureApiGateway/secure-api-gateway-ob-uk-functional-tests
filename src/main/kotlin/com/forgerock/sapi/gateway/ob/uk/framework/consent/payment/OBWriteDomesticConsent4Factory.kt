package com.forgerock.sapi.gateway.ob.uk.framework.consent.payment

import uk.org.openbanking.datamodel.payment.OBWriteDomesticConsent4
import uk.org.openbanking.datamodel.vrp.OBDomesticVRPConsentRequest

/**
 * Factory that creates OBWriteDomesticConsent4 objects
 */
interface OBWriteDomesticConsent4Factory {

    /**
     * Creates a valid consent request object that can be created in the test facility bank system
     */
    fun createConsent(): OBWriteDomesticConsent4
}