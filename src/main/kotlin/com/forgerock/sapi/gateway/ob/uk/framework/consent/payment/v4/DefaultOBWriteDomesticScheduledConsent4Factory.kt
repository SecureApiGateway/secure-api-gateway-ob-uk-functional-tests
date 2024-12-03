package com.forgerock.sapi.gateway.ob.uk.framework.consent.payment.v4

import com.forgerock.sapi.gateway.framework.configuration.requirePaymentContextCode
import uk.org.openbanking.datamodel.v4.common.OBRisk1PaymentContextCode
import uk.org.openbanking.datamodel.v4.payment.OBWriteDomesticScheduledConsent4
import uk.org.openbanking.testsupport.v4.payment.OBWriteDomesticScheduledConsentTestDataFactory

/**
 * Default factory implementation which is used by the functional tests OOTB.
 * Delegates creating consents to the static factory: OBWriteDomesticScheduledConsentTestDataFactory
 */
class DefaultOBWriteDomesticScheduledConsent4Factory: OBWriteDomesticScheduledConsent4Factory {

    override fun createConsent(): OBWriteDomesticScheduledConsent4 {
        val consent =
            OBWriteDomesticScheduledConsentTestDataFactory.aValidOBWriteDomesticScheduledConsent4()
        if (requirePaymentContextCode) {
            consent.risk.paymentContextCode = consent.risk.paymentContextCode ?: OBRisk1PaymentContextCode.BILLINGGOODSANDSERVICESINADVANCE
        }
        return consent
    }
}