package com.forgerock.sapi.gateway.ob.uk.framework.consent.payment

import com.forgerock.sapi.gateway.framework.configuration.requirePaymentContextCode
import uk.org.openbanking.datamodel.common.OBExternalPaymentContext1Code
import uk.org.openbanking.datamodel.payment.OBWriteDomesticStandingOrderConsent5
import uk.org.openbanking.testsupport.payment.OBWriteDomesticStandingOrderConsentTestDataFactory.aValidOBWriteDomesticStandingOrderConsent5
import uk.org.openbanking.testsupport.payment.OBWriteDomesticStandingOrderConsentTestDataFactory.aValidOBWriteDomesticStandingOrderConsent5MandatoryFields

/**
 * Default factory implementation which is used by the functional tests OOTB.
 * Delegates creating consents to the static factory: OBWriteDomesticStandingOrderConsentTestDataFactory
 */
class DefaultOBWriteDomesticStandingOrderConsent5Factory: OBWriteDomesticStandingOrderConsent5Factory {

    override fun createConsent(): OBWriteDomesticStandingOrderConsent5 {
        return addPaymentContextCodeIfRequired(aValidOBWriteDomesticStandingOrderConsent5())
    }

    override fun createConsentWithOnlyMandatoryFieldsPopulated(): OBWriteDomesticStandingOrderConsent5 {
        return addPaymentContextCodeIfRequired(aValidOBWriteDomesticStandingOrderConsent5MandatoryFields())
    }

    private fun addPaymentContextCodeIfRequired(consent: OBWriteDomesticStandingOrderConsent5): OBWriteDomesticStandingOrderConsent5 {
        if (requirePaymentContextCode) {
            consent.risk.paymentContextCode = consent.risk.paymentContextCode ?: OBExternalPaymentContext1Code.OTHER
        }
        return consent
    }
}