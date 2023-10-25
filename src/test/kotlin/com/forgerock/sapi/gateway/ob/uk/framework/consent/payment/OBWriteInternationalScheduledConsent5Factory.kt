package com.forgerock.sapi.gateway.ob.uk.framework.consent.payment

import uk.org.openbanking.datamodel.payment.OBWriteInternationalScheduledConsent5

/**
 * Factory that creates OBWriteInternationalScheduledConsent5 objects
 */
interface OBWriteInternationalScheduledConsent5Factory {

    /**
     * Creates a valid consent request object that can be created in the test facility bank system
     */
    fun createConsent(): OBWriteInternationalScheduledConsent5

    /**
     * Similar to createConsent, except this method only populates fields that are mandatory in the validation logic.
     */
    fun createConsentWithOnlyMandatoryFieldsPopulated(): OBWriteInternationalScheduledConsent5

}