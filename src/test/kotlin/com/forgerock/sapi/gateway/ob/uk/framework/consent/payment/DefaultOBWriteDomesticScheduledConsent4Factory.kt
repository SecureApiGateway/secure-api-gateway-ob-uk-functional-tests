package com.forgerock.sapi.gateway.ob.uk.framework.consent.payment

import com.forgerock.sapi.gateway.framework.configuration.paymentContextCodeIsRequired
import uk.org.openbanking.datamodel.common.OBExternalPaymentContext1Code
import uk.org.openbanking.datamodel.payment.OBWriteDomesticScheduledConsent4
import uk.org.openbanking.testsupport.payment.OBWriteDomesticScheduledConsentTestDataFactory

/**
 * Default factory implementation which is used by the functional tests OOTB.
 * Delegates creating consents to the static factory: OBWriteDomesticScheduledConsentTestDataFactory
 */
class DefaultOBWriteDomesticScheduledConsent4Factory: OBWriteDomesticScheduledConsent4Factory {

    override fun createConsent(): OBWriteDomesticScheduledConsent4 {
        val consent =
            OBWriteDomesticScheduledConsentTestDataFactory.aValidOBWriteDomesticScheduledConsent4()
        if (paymentContextCodeIsRequired) {
            consent.risk.paymentContextCode = consent.risk.paymentContextCode ?: OBExternalPaymentContext1Code.OTHER
        }
        return consent
    }
}