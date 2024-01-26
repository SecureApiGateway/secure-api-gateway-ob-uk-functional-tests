package com.forgerock.sapi.gateway.ob.uk.support.funds

import com.forgerock.sapi.gateway.ob.uk.support.general.GeneralFactory
import com.forgerock.sapi.gateway.ob.uk.support.payment.PsuData
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import uk.org.openbanking.datamodel.fund.OBFundsConfirmationConsent1
import uk.org.openbanking.datamodel.fund.OBFundsConfirmationConsent1Data
import uk.org.openbanking.datamodel.fund.OBFundsConfirmationConsent1DataDebtorAccount

class FundsConfirmationConsentFactory {

    companion object {

        fun obFundsConfirmationConsent1(): OBFundsConfirmationConsent1 {
            val debtorAccount = PsuData().getDebtorAccount()
            return OBFundsConfirmationConsent1()
                    .data(
                            OBFundsConfirmationConsent1Data()
                                    .expirationDateTime(DateTime.now().plusMonths(5).withZone(DateTimeZone.UTC))
                                    .debtorAccount(
                                        OBFundsConfirmationConsent1DataDebtorAccount()
                                                    .identification(debtorAccount?.Identification)
                                                    .name(debtorAccount?.Name)
                                                    .schemeName(debtorAccount?.SchemeName)
                                                    .secondaryIdentification(debtorAccount?.SecondaryIdentification)
                                    )
                    )
        }

        fun urlWithConsentId(url: String, consentId: String) =
                GeneralFactory.urlSubstituted(url, mapOf("ConsentId" to consentId))
    }
}