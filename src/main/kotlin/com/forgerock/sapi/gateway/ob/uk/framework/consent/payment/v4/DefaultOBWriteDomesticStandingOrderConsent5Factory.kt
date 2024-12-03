package com.forgerock.sapi.gateway.ob.uk.framework.consent.payment.v4

import com.forgerock.sapi.gateway.framework.configuration.requirePaymentContextCode
import uk.org.openbanking.datamodel.v4.common.OBRisk1PaymentContextCode
import uk.org.openbanking.datamodel.v4.payment.OBWriteDomesticStandingOrderConsent5
import uk.org.openbanking.testsupport.v4.payment.OBWriteDomesticStandingOrderConsentTestDataFactory.aValidOBWriteDomesticStandingOrderConsent5
import uk.org.openbanking.testsupport.v4.payment.OBWriteDomesticStandingOrderConsentTestDataFactory.aValidOBWriteDomesticStandingOrderConsent5MandatoryFields

/**
 * Default factory implementation which is used by the functional tests OOTB.
 * Delegates creating consents to the static factory: OBWriteDomesticStandingOrderConsentTestDataFactory
 */
class DefaultOBWriteDomesticStandingOrderConsent5Factory: OBWriteDomesticStandingOrderConsent5Factory {

    override fun createConsent(): OBWriteDomesticStandingOrderConsent5 {
        return addConsentParameters(aValidOBWriteDomesticStandingOrderConsent5())
    }

    override fun createConsentWithOnlyMandatoryFieldsPopulated(): OBWriteDomesticStandingOrderConsent5 {
        return addConsentParameters(aValidOBWriteDomesticStandingOrderConsent5MandatoryFields())
    }

    private fun addConsentParameters(consent: OBWriteDomesticStandingOrderConsent5): OBWriteDomesticStandingOrderConsent5 {
        if (requirePaymentContextCode) {
            consent.risk.paymentContextCode = consent.risk.paymentContextCode ?: OBRisk1PaymentContextCode.BILLINGGOODSANDSERVICESINADVANCE
        }
        consent.data.initiation.mandateRelatedInformation.frequency.pointInTime("22")
        return consent
    }
}