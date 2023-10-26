package com.forgerock.sapi.gateway.ob.uk.framework.consent.payment

import com.forgerock.sapi.gateway.framework.configuration.requirePaymentContextCode
import uk.org.openbanking.datamodel.common.OBExternalPaymentContext1Code
import uk.org.openbanking.datamodel.payment.OBWriteInternationalStandingOrderConsent6
import uk.org.openbanking.testsupport.payment.OBWriteInternationalStandingOrderConsentTestDataFactory.aValidOBWriteInternationalStandingOrderConsent6
import uk.org.openbanking.testsupport.payment.OBWriteInternationalStandingOrderConsentTestDataFactory.aValidOBWriteInternationalStandingOrderConsent6MandatoryFields

/**
 * Default factory implementation which is used by the functional tests OOTB.
 * Delegates creating consents to the static factory: OBWriteInternationalStandingOrderConsentTestDataFactory
 */
class DefaultOBWriteInternationalStandingOrderConsent6Factory: OBWriteInternationalStandingOrderConsent6Factory {

    override fun createConsent(): OBWriteInternationalStandingOrderConsent6 {
        return addPaymentContextCodeIfRequired(aValidOBWriteInternationalStandingOrderConsent6())
    }

    override fun createConsentWithOnlyMandatoryFieldsPopulated(): OBWriteInternationalStandingOrderConsent6 {
        return addPaymentContextCodeIfRequired(aValidOBWriteInternationalStandingOrderConsent6MandatoryFields())
    }

    private fun addPaymentContextCodeIfRequired(consent: OBWriteInternationalStandingOrderConsent6): OBWriteInternationalStandingOrderConsent6 {
        if (requirePaymentContextCode) {
            consent.risk.paymentContextCode = consent.risk.paymentContextCode ?: OBExternalPaymentContext1Code.OTHER
        }
        return consent
    }
    
}