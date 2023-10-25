package com.forgerock.sapi.gateway.ob.uk.framework.consent.payment

import com.forgerock.sapi.gateway.framework.configuration.paymentContextCodeIsRequired
import uk.org.openbanking.datamodel.common.OBExternalPaymentContext1Code
import uk.org.openbanking.datamodel.payment.OBWriteInternationalScheduledConsent5
import uk.org.openbanking.testsupport.payment.OBWriteInternationalScheduledConsentTestDataFactory.aValidOBWriteInternationalScheduledConsent5
import uk.org.openbanking.testsupport.payment.OBWriteInternationalScheduledConsentTestDataFactory.aValidOBWriteInternationalScheduledConsent5MandatoryFields

class DefaultOBWriteInternationalScheduledConsent5Factory: OBWriteInternationalScheduledConsent5Factory {

    override fun createConsent(): OBWriteInternationalScheduledConsent5 {
        return addPaymentContextCodeIfRequired(aValidOBWriteInternationalScheduledConsent5())
    }

    override fun createConsentWithOnlyMandatoryFieldsPopulated(): OBWriteInternationalScheduledConsent5 {
        return addPaymentContextCodeIfRequired(aValidOBWriteInternationalScheduledConsent5MandatoryFields())
    }

    private fun addPaymentContextCodeIfRequired(consent: OBWriteInternationalScheduledConsent5): OBWriteInternationalScheduledConsent5 {
        if (paymentContextCodeIsRequired) {
            consent.risk.paymentContextCode = consent.risk.paymentContextCode ?: OBExternalPaymentContext1Code.OTHER
        }
        return consent
    }
}