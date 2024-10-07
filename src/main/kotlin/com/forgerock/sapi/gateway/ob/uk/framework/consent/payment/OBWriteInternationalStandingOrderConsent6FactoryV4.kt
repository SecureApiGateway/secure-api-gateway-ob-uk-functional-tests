package com.forgerock.sapi.gateway.ob.uk.framework.consent.payment

import uk.org.openbanking.datamodel.v4.payment.OBWriteInternationalStandingOrderConsent6

/**
 * Factory that creates OBWriteInternationalStandingOrderConsent6 objects
 */
interface OBWriteInternationalStandingOrderConsent6FactoryV4 {

    /**
     * Creates a valid consent request object that can be created in the test facility bank system
     */
    fun createConsent(): OBWriteInternationalStandingOrderConsent6

    /**
     * Similar to createConsent, except this method only populates fields that are mandatory in the validation logic.
     */
    fun createConsentWithOnlyMandatoryFieldsPopulated(): OBWriteInternationalStandingOrderConsent6

}