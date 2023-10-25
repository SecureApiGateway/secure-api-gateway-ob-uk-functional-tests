package com.forgerock.sapi.gateway.ob.uk.framework.consent.payment

import uk.org.openbanking.datamodel.payment.OBWriteInternationalStandingOrderConsent6

interface OBWriteInternationalStandingOrderConsent6Factory {

    fun createConsent(): OBWriteInternationalStandingOrderConsent6

    fun createConsentWithOnlyMandatoryFieldsPopulated(): OBWriteInternationalStandingOrderConsent6

}