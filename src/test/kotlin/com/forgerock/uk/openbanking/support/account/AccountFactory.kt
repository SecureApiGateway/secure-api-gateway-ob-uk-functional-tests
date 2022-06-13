package com.forgerock.uk.openbanking.support.account

import com.forgerock.uk.openbanking.support.general.GeneralFactory.Companion.urlSubstituted
import uk.org.openbanking.datamodel.account.OBExternalPermissions1Code
import uk.org.openbanking.datamodel.account.OBReadConsent1
import uk.org.openbanking.datamodel.account.OBReadData1
import uk.org.openbanking.datamodel.account.OBRisk2

/**
 * Generate common OB account data and URLs
 */
class AccountFactory {
    companion object {
        fun obReadConsent1(permissions: List<OBExternalPermissions1Code>): OBReadConsent1 {
            return OBReadConsent1().data(
                OBReadData1()
                    .permissions(permissions)
            )
                .risk(OBRisk2())
        }

        fun urlWithAccountId(url: String, accountId: String) = urlSubstituted(url, mapOf("AccountId" to accountId))

        fun urlWithConsentId(url: String, consentId: String) =
            urlSubstituted(url, mapOf("ConsentId" to consentId))
    }
}
