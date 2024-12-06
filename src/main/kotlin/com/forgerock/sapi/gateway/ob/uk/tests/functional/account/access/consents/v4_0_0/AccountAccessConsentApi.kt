package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.access.consents.v4_0_0

import com.forgerock.sapi.gateway.framework.data.AccessToken
import uk.org.openbanking.datamodel.v4.account.OBInternalPermissions1Code
import uk.org.openbanking.datamodel.v4.account.OBReadConsentResponse1

interface AccountAccessConsentApi {
    fun createConsent(permissions: List<OBInternalPermissions1Code>): OBReadConsentResponse1
    fun createConsentAndGetAccessToken(permissions: List<OBInternalPermissions1Code>): Pair<OBReadConsentResponse1, AccessToken>
    fun deleteConsent(consentId: String)
    fun getConsent(consentId: String): OBReadConsentResponse1
}