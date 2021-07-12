package com.forgerock.securebanking.account

import uk.org.openbanking.datamodel.account.OBExternalPermissions1Code
import uk.org.openbanking.datamodel.account.OBReadConsent1
import uk.org.openbanking.datamodel.account.OBReadData1
import uk.org.openbanking.datamodel.account.OBRisk2

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
        fun urlSubstituted(url: String, replaceable: Map<String, String>): String {
            var replaced = url
            for (replace in replaceable) replaced = replaced.replace("{${replace.key}}", replace.value)
            return replaced
        }
    }
}
