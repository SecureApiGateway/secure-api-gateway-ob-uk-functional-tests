package com.forgerock.uk.openbanking.tests.functional.account.access.consents

import uk.org.openbanking.datamodel.account.OBExternalPermissions1Code
import uk.org.openbanking.datamodel.account.OBReadConsentResponse1

interface AccountAccessConsentApi {
    fun createConsent(permissions: List<OBExternalPermissions1Code>): OBReadConsentResponse1
    fun deleteConsent(consentId: String): OBReadConsentResponse1
    fun getConsent(consentId: String): OBReadConsentResponse1
}