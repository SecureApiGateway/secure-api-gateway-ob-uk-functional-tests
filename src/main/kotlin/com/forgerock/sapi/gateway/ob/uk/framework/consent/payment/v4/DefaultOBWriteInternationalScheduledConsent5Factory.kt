package com.forgerock.sapi.gateway.ob.uk.framework.consent.payment.v4

import com.forgerock.sapi.gateway.framework.configuration.requirePaymentContextCode
import uk.org.openbanking.datamodel.v4.common.OBRisk1PaymentContextCode
import uk.org.openbanking.datamodel.v4.payment.OBWriteInternationalScheduledConsent5
import uk.org.openbanking.testsupport.v4.payment.OBWriteInternationalScheduledConsentTestDataFactory.aValidOBWriteInternationalScheduledConsent5
import uk.org.openbanking.testsupport.v4.payment.OBWriteInternationalScheduledConsentTestDataFactory.aValidOBWriteInternationalScheduledConsent5MandatoryFields

/**
 * Default factory implementation which is used by the functional tests OOTB.
 * Delegates creating consents to the static factory: OBWriteInternationalScheduledConsentTestDataFactory
 */
class DefaultOBWriteInternationalScheduledConsent5Factory: OBWriteInternationalScheduledConsent5Factory {

    override fun createConsent(): OBWriteInternationalScheduledConsent5 {
        return addPaymentContextCodeIfRequired(aValidOBWriteInternationalScheduledConsent5())
    }

    override fun createConsentWithOnlyMandatoryFieldsPopulated(): OBWriteInternationalScheduledConsent5 {
        return addPaymentContextCodeIfRequired(aValidOBWriteInternationalScheduledConsent5MandatoryFields())
    }

    private fun addPaymentContextCodeIfRequired(consent: OBWriteInternationalScheduledConsent5): OBWriteInternationalScheduledConsent5 {
        if (requirePaymentContextCode) {
            consent.risk.paymentContextCode = consent.risk.paymentContextCode ?: OBRisk1PaymentContextCode.BILLINGGOODSANDSERVICESINADVANCE
        }
        return consent
    }
}