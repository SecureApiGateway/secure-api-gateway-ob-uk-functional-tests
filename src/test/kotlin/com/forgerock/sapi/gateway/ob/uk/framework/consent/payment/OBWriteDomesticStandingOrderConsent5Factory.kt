package com.forgerock.sapi.gateway.ob.uk.framework.consent.payment

import uk.org.openbanking.datamodel.payment.OBWriteDomesticStandingOrderConsent5

interface OBWriteDomesticStandingOrderConsent5Factory {

    fun createConsent(): OBWriteDomesticStandingOrderConsent5

    fun createConsentWithOnlyMandatoryFieldsPopulated(): OBWriteDomesticStandingOrderConsent5

}