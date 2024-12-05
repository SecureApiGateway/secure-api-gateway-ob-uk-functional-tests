package com.forgerock.sapi.gateway.ob.uk.framework.consent.payment.v4

import com.forgerock.sapi.gateway.framework.configuration.requirePaymentContextCode
import uk.org.openbanking.datamodel.v4.common.OBRisk1PaymentContextCode
import uk.org.openbanking.datamodel.v4.payment.OBWriteInternationalStandingOrderConsent6
import uk.org.openbanking.testsupport.v4.payment.OBWriteInternationalStandingOrderConsentTestDataFactory.aValidOBWriteInternationalStandingOrderConsent6
import uk.org.openbanking.testsupport.v4.payment.OBWriteInternationalStandingOrderConsentTestDataFactory.aValidOBWriteInternationalStandingOrderConsent6MandatoryFields

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
            consent.risk.paymentContextCode = consent.risk.paymentContextCode ?: OBRisk1PaymentContextCode.BILLINGGOODSANDSERVICESINADVANCE
        }
        consent.data.initiation.mandateRelatedInformation.frequency.pointInTime("22")
        return consent
    }
    
}