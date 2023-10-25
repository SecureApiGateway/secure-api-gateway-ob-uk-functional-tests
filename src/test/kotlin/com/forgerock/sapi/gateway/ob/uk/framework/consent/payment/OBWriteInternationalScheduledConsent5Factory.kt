package com.forgerock.sapi.gateway.ob.uk.framework.consent.payment

import uk.org.openbanking.datamodel.payment.OBWriteInternationalScheduledConsent5

interface OBWriteInternationalScheduledConsent5Factory {

    fun createConsent(): OBWriteInternationalScheduledConsent5

    fun createConsentWithOnlyMandatoryFieldsPopulated(): OBWriteInternationalScheduledConsent5

}