package com.forgerock.sapi.gateway.ob.uk.support.funds

import uk.org.openbanking.datamodel.common.OBActiveOrHistoricCurrencyAndAmount
import uk.org.openbanking.datamodel.fund.OBFundsConfirmation1
import uk.org.openbanking.datamodel.fund.OBFundsConfirmationData1

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