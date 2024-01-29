package com.forgerock.sapi.gateway.ob.uk.support.account

import com.forgerock.sapi.gateway.ob.uk.support.general.GeneralFactory.Companion.urlSubstituted
import uk.org.openbanking.datamodel.account.OBReadConsent1
import uk.org.openbanking.datamodel.account.OBReadConsent1Data
import uk.org.openbanking.datamodel.account.OBRisk2
import uk.org.openbanking.datamodel.common.OBExternalPermissions1Code

/**
 * Generate common OB account data and URLs
 */
class AccountFactory {
    companion object {
        fun obReadConsent1(permissions: List<OBExternalPermissions1Code>): OBReadConsent1 {
            return OBReadConsent1().data(OBReadConsent1Data().permissions(permissions))
                                   .risk(OBRisk2())
        }

        fun urlWithAccountId(url: String, accountId: String) = urlSubstituted(url, mapOf("AccountId" to accountId))

        fun urlWithConsentId(url: String, consentId: String) =
            urlSubstituted(url, mapOf("ConsentId" to consentId))
    }
}
