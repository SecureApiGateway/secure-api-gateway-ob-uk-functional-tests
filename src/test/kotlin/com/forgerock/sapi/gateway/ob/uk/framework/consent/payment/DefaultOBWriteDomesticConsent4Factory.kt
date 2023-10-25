package com.forgerock.sapi.gateway.ob.uk.framework.consent.payment

import com.forgerock.sapi.gateway.framework.configuration.paymentContextCodeIsRequired
import uk.org.openbanking.datamodel.common.OBExternalPaymentContext1Code
import uk.org.openbanking.datamodel.payment.OBWriteDomesticConsent4
import uk.org.openbanking.testsupport.payment.OBWriteDomesticConsentTestDataFactory

/**
 * Default factory implementation which is used by the functional tests OOTB.
 * Delegates creating consents to the static factory: OBWriteDomesticConsentTestDataFactory
 */
class DefaultOBWriteDomesticConsent4Factory: OBWriteDomesticConsent4Factory {

    override fun createConsent(): OBWriteDomesticConsent4 {
        val consent = OBWriteDomesticConsentTestDataFactory.aValidOBWriteDomesticConsent4()
        if (paymentContextCodeIsRequired) {
            consent.risk.paymentContextCode = consent.risk.paymentContextCode ?: OBExternalPaymentContext1Code.OTHER
        }
        return consent
    }
}