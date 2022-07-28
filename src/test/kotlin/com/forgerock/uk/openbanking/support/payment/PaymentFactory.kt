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
            val domesticPayment = OBDomestic2()
                .instructionIdentification(initiation.instructionIdentification)
                .endToEndIdentification(initiation.endToEndIdentification)
                .localInstrument(initiation.localInstrument)
                .creditorPostalAddress(initiation.creditorPostalAddress)
                .supplementaryData(initiation.supplementaryData)

            if (initiation.instructedAmount != null) {
                domesticPayment.instructedAmount(
                    OBActiveOrHistoricCurrencyAndAmount()
                        .amount(initiation.instructedAmount?.amount)
                        .currency(initiation.instructedAmount?.currency)
                )
            }

            if (initiation.debtorAccount != null) {
                domesticPayment.debtorAccount(
                    OBCashAccount3()
                        .schemeName(initiation.debtorAccount?.schemeName)
                        .identification(initiation.debtorAccount?.identification)
                        .name(initiation.debtorAccount?.name)
                        .secondaryIdentification(initiation.debtorAccount?.secondaryIdentification)
                )
            }

            if (initiation.creditorAccount != null) {
                domesticPayment.creditorAccount(
                    OBCashAccount3()
                        .schemeName(initiation.creditorAccount?.schemeName)
                        .identification(initiation.creditorAccount?.identification)
                        .name(initiation.creditorAccount?.name)
                        .secondaryIdentification(initiation.creditorAccount?.secondaryIdentification)
                )
            }

            if (initiation.remittanceInformation != null) {
                domesticPayment.remittanceInformation(
                    OBRemittanceInformation1()
                        .unstructured(initiation.remittanceInformation?.unstructured)
                        .reference(initiation.remittanceInformation?.reference)
                )
            }

            return domesticPayment
        }

        fun mapOBDomesticScheduled2ToOBWriteDomesticScheduled2DataInitiation(initiation: OBDomesticScheduled2): OBWriteDomesticScheduled2DataInitiation? {
            val domesticScheduledPayment = OBWriteDomesticScheduled2DataInitiation()
                .instructionIdentification(initiation.instructionIdentification)
                .endToEndIdentification(initiation.endToEndIdentification)
                .localInstrument(initiation.localInstrument)
                .requestedExecutionDateTime(initiation.requestedExecutionDateTime)
                .creditorPostalAddress(initiation.creditorPostalAddress)
                .supplementaryData(initiation.supplementaryData)

            if (initiation.instructedAmount != null) {
                domesticScheduledPayment.instructedAmount(
                    OBWriteDomestic2DataInitiationInstructedAmount()
                        .amount(initiation.instructedAmount?.amount)
                        .currency(initiation.instructedAmount?.currency)
                )
            }

            if (initiation.debtorAccount != null) {
                domesticScheduledPayment.debtorAccount(
                    OBWriteDomestic2DataInitiationDebtorAccount()
                        .schemeName(initiation.debtorAccount?.schemeName)
                        .identification(initiation.debtorAccount?.identification)
                        .name(initiation.debtorAccount?.name)
                        .secondaryIdentification(initiation.debtorAccount?.secondaryIdentification)
                )
            }

            if (initiation.creditorAccount != null) {
                domesticScheduledPayment.creditorAccount(
                    OBWriteDomestic2DataInitiationCreditorAccount()
                        .schemeName(initiation.creditorAccount?.schemeName)
                        .identification(initiation.creditorAccount?.identification)
                        .name(initiation.creditorAccount?.name)
                        .secondaryIdentification(initiation.creditorAccount?.secondaryIdentification)
                )
            }

            if (initiation.remittanceInformation != null) {
                domesticScheduledPayment.remittanceInformation(
                    OBWriteDomestic2DataInitiationRemittanceInformation()
                        .unstructured(initiation.remittanceInformation?.unstructured)
                        .reference(initiation.remittanceInformation?.reference)
                )
            }

            return domesticScheduledPayment
        }

        fun mapOBDomesticStandingOrder3ToOBWriteDomesticStandingOrder3DataInitiation(initiation: OBDomesticStandingOrder3): OBWriteDomesticStandingOrder3DataInitiation? {
            val standingOrder = OBWriteDomesticStandingOrder3DataInitiation()
                .frequency(initiation.frequency)
                .reference(initiation.reference)
                .numberOfPayments(initiation.numberOfPayments)
                .firstPaymentDateTime(initiation.firstPaymentDateTime)
                .recurringPaymentDateTime(initiation.recurringPaymentDateTime)
                .finalPaymentDateTime(initiation.finalPaymentDateTime)
                .supplementaryData(initiation.supplementaryData)

            if (initiation.firstPaymentAmount != null) {
                standingOrder.firstPaymentAmount(
                    OBWriteDomesticStandingOrder3DataInitiationFirstPaymentAmount()
                        .amount(initiation.firstPaymentAmount?.amount)
                        .currency(initiation.firstPaymentAmount?.currency)
                )
            }

            if (initiation.recurringPaymentAmount != null) {
                standingOrder.recurringPaymentAmount(
                    OBWriteDomesticStandingOrder3DataInitiationRecurringPaymentAmount()
                        .amount(initiation.recurringPaymentAmount?.amount)
                        .currency(initiation.recurringPaymentAmount?.currency)
                )
            }

            if (initiation.finalPaymentAmount != null) {
                standingOrder.finalPaymentAmount(
                    OBWriteDomesticStandingOrder3DataInitiationFinalPaymentAmount()
                        .amount(initiation.finalPaymentAmount?.amount)
                        .currency(initiation.finalPaymentAmount?.currency)
                )
            }

            if (initiation.debtorAccount != null) {
                standingOrder.debtorAccount(
                    OBWriteDomesticStandingOrder3DataInitiationDebtorAccount()
                        .schemeName(initiation.debtorAccount?.schemeName)
                        .identification(initiation.debtorAccount?.identification)
                        .name(initiation.debtorAccount?.name)
                        .secondaryIdentification(initiation.debtorAccount?.secondaryIdentification)
                )
            }

            if (initiation.creditorAccount != null) {
                standingOrder.creditorAccount(
                    OBWriteDomesticStandingOrder3DataInitiationCreditorAccount()
                        .schemeName(initiation.creditorAccount?.schemeName)
                        .identification(initiation.creditorAccount?.identification)
                        .name(initiation.creditorAccount?.name)
                        .secondaryIdentification(initiation.creditorAccount?.secondaryIdentification)
                )
            }

            return standingOrder
        }

        fun copyOBWriteDomesticScheduled2DataInitiation(initiation: OBWriteDomesticScheduled2DataInitiation): OBWriteDomesticScheduled2DataInitiation {
            val domesticScheduledPayment = OBWriteDomesticScheduled2DataInitiation()
                .instructionIdentification(initiation.instructionIdentification)
                .endToEndIdentification(initiation.endToEndIdentification)
                .localInstrument(initiation.localInstrument)
                .requestedExecutionDateTime(initiation.requestedExecutionDateTime)
                .debtorAccount(initiation.debtorAccount)
                .creditorAccount(initiation.creditorAccount)
                .creditorPostalAddress(initiation.creditorPostalAddress)
                .remittanceInformation(initiation.remittanceInformation)
                .supplementaryData(initiation.supplementaryData)

            if (initiation.instructedAmount != null) {
                domesticScheduledPayment.instructedAmount(
                    OBWriteDomestic2DataInitiationInstructedAmount()
                        .amount(initiation.instructedAmount.amount)
                        .currency(initiation.instructedAmount.currency)
                )
            }

            return domesticScheduledPayment
        }

        fun copyOBWriteDomesticStandingOrder3DataInitiation(initiation: OBWriteDomesticStandingOrder3DataInitiation): OBWriteDomesticStandingOrder3DataInitiation {
            val standingOrder = OBWriteDomesticStandingOrder3DataInitiation()
                .frequency(initiation.frequency)
                .reference(initiation.reference)
                .numberOfPayments(initiation.numberOfPayments)
                .debtorAccount(initiation.debtorAccount)
                .creditorAccount(initiation.creditorAccount)
                .firstPaymentDateTime(initiation.firstPaymentDateTime)
                .recurringPaymentDateTime(initiation.recurringPaymentDateTime)
                .finalPaymentDateTime(initiation.finalPaymentDateTime)
                .supplementaryData(initiation.supplementaryData)

            if (initiation.firstPaymentAmount != null) {
                standingOrder.firstPaymentAmount(
                    OBWriteDomesticStandingOrder3DataInitiationFirstPaymentAmount()
                        .amount(initiation.firstPaymentAmount.amount)
                        .currency(initiation.firstPaymentAmount.currency)
                )
            }

            if (initiation.recurringPaymentAmount != null) {
                standingOrder.recurringPaymentAmount(
                    OBWriteDomesticStandingOrder3DataInitiationRecurringPaymentAmount()
                        .amount(initiation.recurringPaymentAmount.amount)
                        .currency(initiation.recurringPaymentAmount.currency)
                )
            }

            if (initiation.finalPaymentAmount != null) {
                standingOrder.finalPaymentAmount(
                    OBWriteDomesticStandingOrder3DataInitiationFinalPaymentAmount()
                        .amount(initiation.finalPaymentAmount.amount)
                        .currency(initiation.finalPaymentAmount.currency)
                )
            }

            return standingOrder
        }

        fun mapOBWriteInternational2DataInitiationToOBInternational2(initiation: OBWriteInternational2DataInitiation): OBInternational2 {
            val internationalPayment = OBInternational2()
                .instructionIdentification(initiation.instructionIdentification)
                .endToEndIdentification(initiation.endToEndIdentification)
                .localInstrument(initiation.localInstrument)
                .instructionPriority(initiation.instructionPriority)
                .purpose(initiation.purpose)
                .chargeBearer(initiation.chargeBearer)
                .currencyOfTransfer(initiation.currencyOfTransfer)
                .supplementaryData(initiation.supplementaryData)

            if (initiation.exchangeRateInformation != null) {
                internationalPayment.exchangeRateInformation(
                    OBExchangeRate1()
                        .unitCurrency(initiation.exchangeRateInformation?.unitCurrency)
                        .exchangeRate(initiation.exchangeRateInformation?.exchangeRate)
                        .rateType(initiation.exchangeRateInformation?.rateType)
                        .contractIdentification(initiation.exchangeRateInformation?.contractIdentification)
                )
            }

            if (initiation.debtorAccount != null) {
                internationalPayment.debtorAccount(
                    OBCashAccount3()
                        .schemeName(initiation.debtorAccount?.schemeName)
                        .identification(initiation.debtorAccount?.identification)
                        .name(initiation.debtorAccount?.name)
                        .secondaryIdentification(initiation.debtorAccount?.secondaryIdentification)
                )
            }

            if (initiation.creditor != null) {
                internationalPayment.creditor(
                    OBPartyIdentification43()
                        .name(initiation.creditor?.name)
                        .postalAddress(initiation.creditor?.postalAddress)
                )
            }

            if (initiation.creditorAgent != null) {
                internationalPayment.creditorAgent(
                    OBBranchAndFinancialInstitutionIdentification3()
                        .schemeName(initiation.creditorAgent?.schemeName)
                        .identification(initiation.creditorAgent?.identification)
                        .name(initiation.creditorAgent?.name)
                        .postalAddress(initiation.creditorAgent?.postalAddress)
                )
            }

            if (initiation.creditorAccount != null) {
                internationalPayment.creditorAccount(
                    OBCashAccount3()
                        .schemeName(initiation.creditorAccount?.schemeName)
                        .identification(initiation.creditorAccount?.identification)
                        .name(initiation.creditorAccount?.name)
                        .secondaryIdentification(initiation.creditorAccount?.secondaryIdentification)
                )
            }

            if (initiation.remittanceInformation != null) {
                internationalPayment.remittanceInformation(
                    OBRemittanceInformation1()
                        .unstructured(initiation.remittanceInformation?.unstructured)
                        .reference(initiation.remittanceInformation?.reference)
                )
            }

            return internationalPayment
        }

        fun copyOBWriteInternational3DataInitiation(initiation: OBWriteInternational3DataInitiation): OBWriteInternational3DataInitiation {
            val internationalPayment = OBWriteInternational3DataInitiation()
                .instructionIdentification(initiation.instructionIdentification)
                .endToEndIdentification(initiation.endToEndIdentification)
                .localInstrument(initiation.localInstrument)
                .instructionPriority(initiation.instructionPriority)
                .purpose(initiation.purpose)
                .extendedPurpose(initiation.extendedPurpose)
                .chargeBearer(initiation.chargeBearer)
                .currencyOfTransfer(initiation.currencyOfTransfer)
                .destinationCountryCode(initiation.destinationCountryCode)
                .debtorAccount(initiation.debtorAccount)
                .creditor(initiation.creditor)
                .creditorAgent(initiation.creditorAgent)
                .creditorAccount(initiation.creditorAccount)
                .remittanceInformation(initiation.remittanceInformation)
                .supplementaryData(initiation.supplementaryData)



            if (initiation.exchangeRateInformation != null) {
                internationalPayment.exchangeRateInformation(
                    OBWriteInternational3DataInitiationExchangeRateInformation()
                        .unitCurrency(initiation.exchangeRateInformation?.unitCurrency)
                        .exchangeRate(initiation.exchangeRateInformation?.exchangeRate)
                        .rateType(initiation.exchangeRateInformation?.rateType)
                        .contractIdentification(initiation.exchangeRateInformation?.contractIdentification)
                )
            }

            return internationalPayment
        }

        fun mapOBWriteInternationalScheduledConsentResponse6DataInitiationToOBWriteInternationalScheduled3DataInitiation(
            initiation: OBWriteInternationalScheduledConsentResponse6DataInitiation
        ): OBWriteInternationalScheduled3DataInitiation {
            val internationalScheduledPayment = OBWriteInternationalScheduled3DataInitiation()
                .instructionIdentification(initiation.instructionIdentification)
                .endToEndIdentification(initiation.endToEndIdentification)
                .localInstrument(initiation.localInstrument)
                .instructionPriority(initiation.instructionPriority)
                .extendedPurpose(initiation.extendedPurpose)
                .requestedExecutionDateTime(initiation.requestedExecutionDateTime)
                .destinationCountryCode(initiation.destinationCountryCode)
                .purpose(initiation.purpose)
                .chargeBearer(initiation.chargeBearer)
                .currencyOfTransfer(initiation.currencyOfTransfer)
                .supplementaryData(initiation.supplementaryData)

            if (initiation.instructedAmount != null) {
                internationalScheduledPayment.instructedAmount(
                    OBWriteDomestic2DataInitiationInstructedAmount()
                        .amount(initiation.instructedAmount?.amount)
                        .currency(initiation.instructedAmount?.currency)
                )
            }

            if (initiation.exchangeRateInformation != null) {
                internationalScheduledPayment.exchangeRateInformation(
                    OBWriteInternational3DataInitiationExchangeRateInformation()
                        .unitCurrency(initiation.exchangeRateInformation?.unitCurrency)
                        .exchangeRate(initiation.exchangeRateInformation?.exchangeRate)
                        .rateType(initiation.exchangeRateInformation?.rateType)
                        .contractIdentification(initiation.exchangeRateInformation?.contractIdentification)
                )
            }

            if (initiation.debtorAccount != null) {
                internationalScheduledPayment.debtorAccount(
                    OBWriteDomestic2DataInitiationDebtorAccount()
                        .schemeName(initiation.debtorAccount?.schemeName)
                        .identification(initiation.debtorAccount?.identification)
                        .name(initiation.debtorAccount?.name)
                        .secondaryIdentification(initiation.debtorAccount?.secondaryIdentification)
                )
            }

            if (initiation.creditor != null) {
                internationalScheduledPayment.creditor(
                    OBWriteInternational3DataInitiationCreditor()
                        .name(initiation.creditor?.name)
                        .postalAddress(initiation.creditor?.postalAddress)
                )
            }

            if (initiation.creditorAgent != null) {
                internationalScheduledPayment.creditorAgent(
                    OBWriteInternational3DataInitiationCreditorAgent()
                        .schemeName(initiation.creditorAgent?.schemeName)
                        .identification(initiation.creditorAgent?.identification)
                        .name(initiation.creditorAgent?.name)
                        .postalAddress(initiation.creditorAgent?.postalAddress)
                )
            }

            if (initiation.creditorAccount != null) {
                internationalScheduledPayment.creditorAccount(
                    OBWriteDomestic2DataInitiationCreditorAccount()
                        .schemeName(initiation.creditorAccount?.schemeName)
                        .identification(initiation.creditorAccount?.identification)
                        .name(initiation.creditorAccount?.name)
                        .secondaryIdentification(initiation.creditorAccount?.secondaryIdentification)
                )
            }

            if (initiation.remittanceInformation != null) {
                internationalScheduledPayment.remittanceInformation(
                    OBWriteDomestic2DataInitiationRemittanceInformation()
                        .unstructured(initiation.remittanceInformation?.unstructured)
                        .reference(initiation.remittanceInformation?.reference)
                )
            }

            return internationalScheduledPayment
        }

        fun mapOBWriteInternationalScheduled2DataInitiationToOBWriteDataInternationalScheduled2(initiation: OBWriteInternationalScheduled2DataInitiation): OBInternationalScheduled2 {
            val internationalScheduledPayment = OBInternationalScheduled2()
                .instructionIdentification(initiation.instructionIdentification)
                .endToEndIdentification(initiation.endToEndIdentification)
                .localInstrument(initiation.localInstrument)
                .instructionPriority(initiation.instructionPriority)
                .purpose(initiation.purpose)
                .chargeBearer(initiation.chargeBearer)
                .currencyOfTransfer(initiation.currencyOfTransfer)
                .requestedExecutionDateTime(initiation.requestedExecutionDateTime)
                .supplementaryData(initiation.supplementaryData)


            if (initiation.instructedAmount != null) {
                internationalScheduledPayment.instructedAmount(
                    OBActiveOrHistoricCurrencyAndAmount()
                        .amount(initiation.instructedAmount?.amount)
                        .currency(initiation.instructedAmount?.currency)
                )
            }

            if (initiation.exchangeRateInformation != null) {
                internationalScheduledPayment.exchangeRateInformation(
                    OBExchangeRate1()
                        .unitCurrency(initiation.exchangeRateInformation?.unitCurrency)
                        .exchangeRate(initiation.exchangeRateInformation?.exchangeRate)
                        .rateType(initiation.exchangeRateInformation?.rateType)
                        .contractIdentification(initiation.exchangeRateInformation?.contractIdentification)
                )
            }

            if (initiation.debtorAccount != null) {
                internationalScheduledPayment.debtorAccount(
                    OBCashAccount3()
                        .schemeName(initiation.debtorAccount?.schemeName)
                        .identification(initiation.debtorAccount?.identification)
                        .name(initiation.debtorAccount?.name)
                        .secondaryIdentification(initiation.debtorAccount?.secondaryIdentification)
                )
            }

            if (initiation.creditor != null) {
                internationalScheduledPayment.creditor(
                    OBPartyIdentification43()
                        .name(initiation.creditor?.name)
                        .postalAddress(initiation.creditor?.postalAddress)
                )
            }

            if (initiation.creditorAgent != null) {
                internationalScheduledPayment.creditorAgent(
                    OBBranchAndFinancialInstitutionIdentification3()
                        .schemeName(initiation.creditorAgent?.schemeName)
                        .identification(initiation.creditorAgent?.identification)
                        .name(initiation.creditorAgent?.name)
                        .postalAddress(initiation.creditorAgent?.postalAddress)
                )
            }

            if (initiation.creditorAccount != null) {
                internationalScheduledPayment.creditorAccount(
                    OBCashAccount3()
                        .schemeName(initiation.creditorAccount?.schemeName)
                        .identification(initiation.creditorAccount?.identification)
                        .name(initiation.creditorAccount?.name)
                        .secondaryIdentification(initiation.creditorAccount?.secondaryIdentification)
                )
            }

            if (initiation.remittanceInformation != null) {
                internationalScheduledPayment.remittanceInformation(
                    OBRemittanceInformation1()
                        .unstructured(initiation.remittanceInformation?.unstructured)
                        .reference(initiation.remittanceInformation?.reference)
                )
            }

            return internationalScheduledPayment
        }
    }
}
