package com.forgerock.sapi.gateway.ob.uk.framework.consent

import com.forgerock.sapi.gateway.ob.uk.framework.consent.payment.OBDomesticVRPConsentRequestFactory
import uk.org.openbanking.datamodel.common.OBExternalPaymentContext1Code
import uk.org.openbanking.datamodel.vrp.OBDomesticVRPConsentRequest
import uk.org.openbanking.testsupport.vrp.OBDomesticVrpConsentRequestTestDataFactory

/**
 * Example impl that a customer might provide
 */
// TODO Move this to another repo...
class CustomOBDomesticVRPConsentRequestFactory: OBDomesticVRPConsentRequestFactory {

    override fun createConsent(): OBDomesticVRPConsentRequest {
        val consent =
            OBDomesticVrpConsentRequestTestDataFactory.aValidOBDomesticVRPConsentRequest()
        setValidPaymentContextCode(consent)
        return consent
    }

    override fun createConsentWithOnlyMandatoryFieldsPopulated(): OBDomesticVRPConsentRequest {
        val consent =
            OBDomesticVrpConsentRequestTestDataFactory.aValidOBDomesticVRPConsentRequestMandatoryFields()
        setValidPaymentContextCode(consent)
        return consent
    }

    private fun setValidPaymentContextCode(consent: OBDomesticVRPConsentRequest) {
        consent.risk.paymentContextCode = OBExternalPaymentContext1Code.PARTYTOPARTY
    }
}