package com.forgerock.uk.openbanking.support.payment

import com.forgerock.securebanking.framework.configuration.PSU_DEBTOR_ACCOUNT_IDENTIFICATION
import com.forgerock.securebanking.framework.configuration.PSU_USERNAME

/**
 * Data relating to the PSU (end user)
 */
class PsuData {
    fun getDebtorAccount(): FinancialAccount {
        return FinancialAccount(SchemeName = "UK.OBIE.SortCodeAccountNumber",
                                Identification = PSU_DEBTOR_ACCOUNT_IDENTIFICATION,
                                Name = PSU_USERNAME,
                                SecondaryIdentification = null)
    }
}

data class FinancialAccount(
        val SchemeName: String,
        val Identification: String,
        val Name: String,
        val SecondaryIdentification: String?
)
