package com.forgerock.sapi.gateway.ob.uk.framework.consent.payment.v4

import uk.org.openbanking.datamodel.v4.vrp.OBDomesticVRPConsentRequest

/**
 * Factory that creates OBDomesticVRPConsentRequest objects
 */
interface OBDomesticVRPConsentRequestFactory {

    /**
     * Creates a valid consent request object that can be created in the test facility bank system
     */
    fun createConsent(): OBDomesticVRPConsentRequest

    /**
     * Similar to createConsent, except this method only populates fields that are mandatory in the validation logic.
     */
    fun createConsentWithOnlyMandatoryFieldsPopulated(): OBDomesticVRPConsentRequest

}