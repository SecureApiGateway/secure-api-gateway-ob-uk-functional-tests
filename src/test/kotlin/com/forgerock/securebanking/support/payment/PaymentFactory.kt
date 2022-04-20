package com.forgerock.securebanking.support.payment

import com.forgerock.securebanking.support.general.GeneralFactory.Companion.urlSubstituted
import com.google.common.base.Preconditions
import uk.org.openbanking.datamodel.account.OBCashAccount3
import uk.org.openbanking.datamodel.payment.OBActiveOrHistoricCurrencyAndAmount
import uk.org.openbanking.datamodel.payment.OBDomestic2
import uk.org.openbanking.datamodel.payment.OBRemittanceInformation1
import uk.org.openbanking.datamodel.payment.OBWriteDomestic2DataInitiation
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

/**
 * Generate common OB payment URLs
 */
class PaymentFactory {
    companion object {
        fun urlWithConsentId(url: String, consentId: String) =
            urlSubstituted(url, mapOf("ConsentId" to consentId))

        fun urlWithDomesticPaymentId(url: String, domesticPaymentId: String) =
            urlSubstituted(url, mapOf("DomesticPaymentId" to domesticPaymentId))

        fun urlWithDomesticScheduledPaymentId(url: String, domesticScheduledPaymentId: String) =
            urlSubstituted(url, mapOf("DomesticScheduledPaymentId" to domesticScheduledPaymentId))

        fun urlWithDomesticStandingOrderId(url: String, domesticStandingOrderId: String) =
            urlSubstituted(url, mapOf("DomesticStandingOrderId" to domesticStandingOrderId))

        fun urlWithInternationalPaymentId(url: String, internationalPaymentId: String) =
            urlSubstituted(url, mapOf("InternationalPaymentId" to internationalPaymentId))

        fun urlWithInternationalScheduledPaymentId(url: String, internationalScheduledPaymentId: String) =
            urlSubstituted(url, mapOf("InternationalScheduledPaymentId" to internationalScheduledPaymentId))

        fun urlWithInternationalStandingOrderPaymentId(url: String, internationalStandingOrderPaymentId: String) =
            urlSubstituted(url, mapOf("InternationalStandingOrderPaymentId" to internationalStandingOrderPaymentId))

        fun urlWithFilePaymentId(url: String, filePaymentId: String) =
            urlSubstituted(url, mapOf("FilePaymentId" to filePaymentId))

        fun computeSHA256FullHash(contentToEncode: String): String {
            Preconditions.checkNotNull(contentToEncode, "Cannot hash null")
            try {
                val digest = MessageDigest.getInstance("SHA-256")
                val hash = digest.digest(contentToEncode.toByteArray(StandardCharsets.UTF_8))
                return Base64.getEncoder().encodeToString(hash)
            } catch (ex: NoSuchAlgorithmException) {
                throw IllegalStateException("Unknown algorithm for file hash: SHA-256")
            }
        }

        fun mapOBWriteDomestic2DataInitiationToOBDomestic2(initiation: OBWriteDomestic2DataInitiation): OBDomestic2 {
            return OBDomestic2()
                .instructionIdentification(initiation.instructionIdentification)
                .endToEndIdentification(initiation.endToEndIdentification)
                .localInstrument(initiation.localInstrument)
                .instructedAmount(
                    OBActiveOrHistoricCurrencyAndAmount()
                        .amount(initiation.instructedAmount?.amount)
                        .currency(initiation.instructedAmount?.currency)
                )
                .debtorAccount(
                    OBCashAccount3()
                        .schemeName(initiation.debtorAccount?.schemeName)
                        .identification(initiation.debtorAccount?.identification)
                        .name(initiation.debtorAccount?.name)
                        .secondaryIdentification(initiation.debtorAccount?.secondaryIdentification)
                )
                .creditorAccount(
                    OBCashAccount3()
                        .schemeName(initiation.creditorAccount?.schemeName)
                        .identification(initiation.creditorAccount?.identification)
                        .name(initiation.creditorAccount?.name)
                        .secondaryIdentification(initiation.creditorAccount?.secondaryIdentification)
                )
                .creditorPostalAddress(initiation.creditorPostalAddress)
                .remittanceInformation(
                    OBRemittanceInformation1()
                        .unstructured(initiation.remittanceInformation?.unstructured)
                        .reference(initiation.remittanceInformation?.reference)
                )
                .supplementaryData(initiation.supplementaryData)
        }
    }
}
