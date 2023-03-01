package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.access.consents

import com.forgerock.sapi.gateway.framework.data.AccessToken
import uk.org.openbanking.datamodel.account.OBExternalPermissions1Code
import uk.org.openbanking.datamodel.account.OBReadConsentResponse1

interface AccountAccessConsentApi {
    fun createConsent(permissions: List<OBExternalPermissions1Code>): OBReadConsentResponse1
    fun createConsentAndGetAccessToken(permissions: List<OBExternalPermissions1Code>): Pair<OBReadConsentResponse1, AccessToken>
    fun deleteConsent(consentId: String)
    fun getConsent(consentId: String): OBReadConsentResponse1
}