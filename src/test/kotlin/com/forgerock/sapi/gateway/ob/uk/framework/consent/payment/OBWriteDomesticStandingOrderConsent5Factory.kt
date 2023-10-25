package com.forgerock.sapi.gateway.ob.uk.framework.consent.payment

import uk.org.openbanking.datamodel.payment.OBWriteDomesticStandingOrderConsent5

/**
 * Factory that creates OBWriteDomesticStandingOrderConsent5 objects
 */
interface OBWriteDomesticStandingOrderConsent5Factory {

    /**
     * Creates a valid consent request object that can be created in the test facility bank system
     */
    fun createConsent(): OBWriteDomesticStandingOrderConsent5

    /**
     * Similar to createConsent, except this method only populates fields that are mandatory in the validation logic.
     */
    fun createConsentWithOnlyMandatoryFieldsPopulated(): OBWriteDomesticStandingOrderConsent5

}