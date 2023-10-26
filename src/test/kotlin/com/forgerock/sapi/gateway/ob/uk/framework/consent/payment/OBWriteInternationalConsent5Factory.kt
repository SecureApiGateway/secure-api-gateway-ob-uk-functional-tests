package com.forgerock.sapi.gateway.ob.uk.framework.consent.payment

import uk.org.openbanking.datamodel.payment.OBWriteInternationalConsent5

/**
 * Factory that creates OBWriteInternationalConsent5 objects
 */
interface OBWriteInternationalConsent5Factory {

    /**
     * Creates a valid consent request object that can be created in the test facility bank system
     */
    fun createConsent(): OBWriteInternationalConsent5

    /**
     * Similar to createConsent, except this method only populates fields that are mandatory in the validation logic.
     */
    fun createConsentWithOnlyMandatoryFieldsPopulated(): OBWriteInternationalConsent5
    
}