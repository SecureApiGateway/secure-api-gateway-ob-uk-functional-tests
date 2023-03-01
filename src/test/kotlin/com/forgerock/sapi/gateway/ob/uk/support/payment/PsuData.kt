package com.forgerock.sapi.gateway.ob.uk.support.payment

import com.forgerock.sapi.gateway.framework.configuration.PSU_DEBTOR_ACCOUNT_IDENTIFICATION
import com.forgerock.sapi.gateway.framework.configuration.PSU_USERNAME

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
