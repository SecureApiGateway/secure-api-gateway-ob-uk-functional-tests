package com.forgerock.sapi.gateway.ob.uk.framework.consent.payment

import uk.org.openbanking.datamodel.vrp.OBDomesticVRPConsentRequest
import uk.org.openbanking.testsupport.vrp.OBDomesticVrpConsentRequestTestDataFactory

/**
 * Default factory implementation which is used by the functional tests OOTB.
 * Delegates creating consents to the static factory: OBDomesticVrpConsentRequestTestDataFactory
 */
class DefaultOBDomesticVRPConsentRequestFactory: OBDomesticVRPConsentRequestFactory {

    override fun createConsent(): OBDomesticVRPConsentRequest {
        return OBDomesticVrpConsentRequestTestDataFactory.aValidOBDomesticVRPConsentRequest()
    }

    override fun createConsentWithOnlyMandatoryFieldsPopulated(): OBDomesticVRPConsentRequest {
        return OBDomesticVrpConsentRequestTestDataFactory.aValidOBDomesticVRPConsentRequestMandatoryFields()
    }

}