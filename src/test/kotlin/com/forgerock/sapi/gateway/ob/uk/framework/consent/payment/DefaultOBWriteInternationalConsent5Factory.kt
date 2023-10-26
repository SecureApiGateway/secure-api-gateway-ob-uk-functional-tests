package com.forgerock.sapi.gateway.ob.uk.framework.consent.payment

import com.forgerock.sapi.gateway.framework.configuration.requirePaymentContextCode
import uk.org.openbanking.datamodel.common.OBExternalPaymentContext1Code
import uk.org.openbanking.datamodel.payment.OBWriteInternationalConsent5
import uk.org.openbanking.testsupport.payment.OBWriteInternationalConsentTestDataFactory.aValidOBWriteInternationalConsent5
import uk.org.openbanking.testsupport.payment.OBWriteInternationalConsentTestDataFactory.aValidOBWriteInternationalConsent5MandatoryFields

/**
 * Default factory implementation which is used by the functional tests OOTB.
 * Delegates creating consents to the static factory: OBWriteInternationalConsentTestDataFactory
 */
class DefaultOBWriteInternationalConsent5Factory: OBWriteInternationalConsent5Factory {

    override fun createConsent(): OBWriteInternationalConsent5 {
        return addPaymentContextCodeIfRequired(aValidOBWriteInternationalConsent5())
    }

    override fun createConsentWithOnlyMandatoryFieldsPopulated(): OBWriteInternationalConsent5 {
        return addPaymentContextCodeIfRequired(aValidOBWriteInternationalConsent5MandatoryFields())
    }

    private fun addPaymentContextCodeIfRequired(consent: OBWriteInternationalConsent5): OBWriteInternationalConsent5 {
        if (requirePaymentContextCode) {
            consent.risk.paymentContextCode = consent.risk.paymentContextCode ?: OBExternalPaymentContext1Code.OTHER
        }
        return consent
    }
    
}