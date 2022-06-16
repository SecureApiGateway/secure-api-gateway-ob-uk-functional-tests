package com.forgerock.uk.openbanking.support.payment

import com.forgerock.uk.openbanking.support.general.GeneralFactory.Companion.urlSubstituted
import com.google.common.base.Preconditions
import uk.org.openbanking.datamodel.common.OBActiveOrHistoricCurrencyAndAmount
import uk.org.openbanking.datamodel.common.OBCashAccount3
import uk.org.openbanking.datamodel.payment.*
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

        fun mapOBDomesticScheduled2ToOBWriteDomesticScheduled2DataInitiation(initiation: OBDomesticScheduled2): OBWriteDomesticScheduled2DataInitiation? {
            return OBWriteDomesticScheduled2DataInitiation()
                .instructionIdentification(initiation.instructionIdentification)
                .endToEndIdentification(initiation.endToEndIdentification)
                .localInstrument(initiation.localInstrument)
                .requestedExecutionDateTime(initiation.requestedExecutionDateTime)
                .instructedAmount(
                    OBWriteDomestic2DataInitiationInstructedAmount()
                        .amount(initiation.instructedAmount?.amount)
                        .currency(initiation.instructedAmount?.currency)
                )
                .debtorAccount(
                    OBWriteDomestic2DataInitiationDebtorAccount()
                        .schemeName(initiation.debtorAccount?.schemeName)
                        .identification(initiation.debtorAccount?.identification)
                        .name(initiation.debtorAccount?.name)
                        .secondaryIdentification(initiation.debtorAccount?.secondaryIdentification)
                )
                .creditorAccount(
                    OBWriteDomestic2DataInitiationCreditorAccount()
                        .schemeName(initiation.creditorAccount?.schemeName)
                        .identification(initiation.creditorAccount?.identification)
                        .name(initiation.creditorAccount?.name)
                        .secondaryIdentification(initiation.creditorAccount?.secondaryIdentification)
                )
                .creditorPostalAddress(initiation.creditorPostalAddress)
                .remittanceInformation(
                    OBWriteDomestic2DataInitiationRemittanceInformation()
                        .unstructured(initiation.remittanceInformation?.unstructured)
                        .reference(initiation.remittanceInformation?.reference)
                )
                .supplementaryData(initiation.supplementaryData)
        }

        fun copyOBWriteDomesticScheduled2DataInitiation(initiation: OBWriteDomesticScheduled2DataInitiation): OBWriteDomesticScheduled2DataInitiation {
            return OBWriteDomesticScheduled2DataInitiation()
                .instructionIdentification(initiation.instructionIdentification)
                .endToEndIdentification(initiation.endToEndIdentification)
                .localInstrument(initiation.localInstrument)
                .requestedExecutionDateTime(initiation.requestedExecutionDateTime)
                .instructedAmount(
                    OBWriteDomestic2DataInitiationInstructedAmount()
                        .amount(initiation.instructedAmount.amount)
                        .currency(initiation.instructedAmount.currency)
                )
                .debtorAccount(initiation.debtorAccount)
                .creditorAccount(initiation.creditorAccount)
                .creditorPostalAddress(initiation.creditorPostalAddress)
                .remittanceInformation(initiation.remittanceInformation)
                .supplementaryData(initiation.supplementaryData)
        }
    }
}
