package com.forgerock.sapi.gateway.ob.uk.support.funds.v4

import uk.org.openbanking.datamodel.v4.fund.OBFundsConfirmation1
import uk.org.openbanking.datamodel.v4.fund.OBFundsConfirmation1Data
import uk.org.openbanking.datamodel.v4.fund.OBFundsConfirmation1DataInstructedAmount

class FundsConfirmationFactory {
    companion object {

        fun obFundsConfirmation1(consentId: String): OBFundsConfirmation1 {
            return OBFundsConfirmation1()
                    .data(
                        OBFundsConfirmation1Data()
                                    .consentId(consentId)
                                    .reference("funds-reference-01")
                                    .instructedAmount(
                                        OBFundsConfirmation1DataInstructedAmount()
                                                    .amount("20.00")
                                                    .currency("GBP")
                                    )
                    )
        }

    }
}