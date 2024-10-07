package com.forgerock.sapi.gateway.ob.uk.framework.consent.payment

import uk.org.openbanking.datamodel.v4.payment.OBWriteDomesticConsent4

/**
 * Factory that creates OBWriteDomesticConsent4 objects
 */
interface OBWriteDomesticConsent4FactoryV4 {

    /**
     * Creates a valid consent request object that can be created in the test facility bank system
     */
    fun createConsent(): OBWriteDomesticConsent4
}