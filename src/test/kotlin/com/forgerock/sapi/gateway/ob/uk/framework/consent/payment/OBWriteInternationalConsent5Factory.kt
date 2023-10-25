package com.forgerock.sapi.gateway.ob.uk.framework.consent.payment

import uk.org.openbanking.datamodel.payment.OBWriteInternationalConsent5

interface OBWriteInternationalConsent5Factory {

    fun createConsent(): OBWriteInternationalConsent5

    fun createConsentWithOnlyMandatoryFieldsPopulated(): OBWriteInternationalConsent5
    
}