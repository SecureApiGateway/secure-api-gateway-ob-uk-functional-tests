package com.forgerock.sapi.gateway.ob.uk.support.funds.v4

import uk.org.openbanking.datamodel.v4.fund.OBFundsConfirmation1
import uk.org.openbanking.datamodel.v4.fund.OBFundsConfirmationData1
import uk.org.openbanking.datamodel.v4.common.OBActiveOrHistoricCurrencyAndAmount

class FundsConfirmationFactory {
    companion object {

        fun obFundsConfirmation1(consentId: String): OBFundsConfirmation1 {
            return OBFundsConfirmation1()
                    .data(
                        OBFundsConfirmationData1()
                                    .consentId(consentId)
                                    .reference("funds-reference-01")
                                    .instructedAmount(
                                        OBActiveOrHistoricCurrencyAndAmount()
                                                    .amount("20.00")
                                                    .currency("GBP")
                                    )
                    )
        }

    }
}