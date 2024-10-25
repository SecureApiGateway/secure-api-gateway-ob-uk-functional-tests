package com.forgerock.sapi.gateway.ob.uk.framework.consent.payment

import com.forgerock.sapi.gateway.framework.configuration.requirePaymentContextCode
import uk.org.openbanking.datamodel.v3.common.OBExternalPaymentContext1Code
import uk.org.openbanking.datamodel.v3.vrp.OBDomesticVRPConsentRequest
import uk.org.openbanking.testsupport.v3.vrp.OBDomesticVrpConsentRequestTestDataFactory.aValidOBDomesticVRPConsentRequest
import uk.org.openbanking.testsupport.v3.vrp.OBDomesticVrpConsentRequestTestDataFactory.aValidOBDomesticVRPConsentRequestMandatoryFields

/**
 * Default factory implementation which is used by the functional tests OOTB.
 * Delegates creating consents to the static factory: OBDomesticVrpConsentRequestTestDataFactory
 */
class DefaultOBDomesticVRPConsentRequestFactory: OBDomesticVRPConsentRequestFactory {

    override fun createConsent(): OBDomesticVRPConsentRequest {
        return addPaymentContextCodeIfRequired(aValidOBDomesticVRPConsentRequest())
    }

    override fun createConsentWithOnlyMandatoryFieldsPopulated(): OBDomesticVRPConsentRequest {
        return addPaymentContextCodeIfRequired(aValidOBDomesticVRPConsentRequestMandatoryFields())
    }

    private fun addPaymentContextCodeIfRequired(consent: OBDomesticVRPConsentRequest): OBDomesticVRPConsentRequest {
        if (requirePaymentContextCode) {
            consent.risk.paymentContextCode = consent.risk.paymentContextCode ?: OBExternalPaymentContext1Code.OTHER
        }
        return consent
    }

}