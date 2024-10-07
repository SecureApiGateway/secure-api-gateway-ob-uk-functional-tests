package com.forgerock.sapi.gateway.ob.uk.framework.consent.payment

import uk.org.openbanking.datamodel.v4.payment.OBWriteDomesticScheduledConsent4

/**
 * Factory that creates OBWriteDomesticScheduledConsent4 objects
 */
interface OBWriteDomesticScheduledConsent4FactoryV4 {

    /**
     * Creates a valid consent request object that can be created in the test facility bank system
     */
    fun createConsent(): OBWriteDomesticScheduledConsent4
}