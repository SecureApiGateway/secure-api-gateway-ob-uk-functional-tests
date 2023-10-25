package com.forgerock.sapi.gateway.ob.uk.framework.consent.payment

import uk.org.openbanking.datamodel.payment.OBWriteDomesticScheduledConsent4

interface OBWriteDomesticScheduledConsent4Factory {

    fun createConsent(): OBWriteDomesticScheduledConsent4
}