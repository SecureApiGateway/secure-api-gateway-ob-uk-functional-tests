package com.forgerock.sapi.gateway.ob.uk.framework.consent.payment

import com.forgerock.sapi.gateway.framework.configuration.requirePaymentContextCode
import uk.org.openbanking.datamodel.v3.common.OBExternalPaymentContext1Code
import uk.org.openbanking.datamodel.v3.payment.OBWriteDomesticConsent4
import uk.org.openbanking.testsupport.v3.payment.OBWriteDomesticConsentTestDataFactory

/**
 * Default factory implementation which is used by the functional tests OOTB.
 * Delegates creating consents to the static factory: OBWriteDomesticConsentTestDataFactory
 */
class DefaultOBWriteDomesticConsent4Factory: OBWriteDomesticConsent4Factory {

    override fun createConsent(): OBWriteDomesticConsent4 {
        val consent = OBWriteDomesticConsentTestDataFactory.aValidOBWriteDomesticConsent4()
        if (requirePaymentContextCode) {
            consent.risk.paymentContextCode = consent.risk.paymentContextCode ?: OBExternalPaymentContext1Code.OTHER
        }
        return consent
    }
}