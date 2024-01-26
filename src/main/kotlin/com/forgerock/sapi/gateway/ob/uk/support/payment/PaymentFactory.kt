package com.forgerock.sapi.gateway.ob.uk.support.payment

import com.forgerock.sapi.gateway.ob.uk.support.general.GeneralFactory.Companion.urlSubstituted
import com.google.common.base.Preconditions
import org.apache.commons.io.FileUtils
import org.assertj.core.api.Assertions.assertThat
import uk.org.openbanking.datamodel.common.OBSupplementaryData1
import uk.org.openbanking.datamodel.payment.*
import uk.org.openbanking.datamodel.vrp.OBDomesticVRPInitiation
import uk.org.openbanking.testsupport.payment.OBWriteFileConsentTestDataFactory
import java.io.File
import java.io.StringReader
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.xml.bind.JAXB

/**
 * Generate common OB payment URLs
 */
class PaymentFactory {

    object FilePaths {
        const val XML_FILE_PATH = "/com/forgerock/sapi/gateway/ob/uk/payment/file/UK_OBIE_pain_001_001_08.xml"
        const val JSON_FILE_PATH = "/com/forgerock/sapi/gateway/ob/uk/payment/file/UK_OBIE_PaymentInitiation_3_1.json"
    }

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

        fun urlWithFilePaymentSubmitFileId(url: String, filePaymentId: String) =
            urlSubstituted(url, mapOf("ConsentId" to filePaymentId))

        fun urlWithDomesticVrpPaymentId(url: String, domesticVrpPaymentId: String) =
            urlSubstituted(url, mapOf("DomesticVRPId" to domesticVrpPaymentId))

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

        fun getFileAsString(filePath: String): String {
            try {
                return FileUtils.readFileToString(
                    File(object {}.javaClass.getResource(filePath)?.file.toString()),
                    StandardCharsets.UTF_8
                )
            } catch (e: NullPointerException) {
                throw AssertionError("Could not load file: $filePath")
            }
        }

        fun createOBWriteFileConsent3WithFileInfo(fileContent: String, fileType: String): OBWriteFileConsent3 {
            val document = JAXB.unmarshal(
                StringReader(fileContent),
                com.forgerock.sapi.gateway.ob.uk.generated.xml.model.pain00100108.Document::class.java
            )
            val numberOfTransactions = document.cstmrCdtTrfInitn.grpHdr.nbOfTxs.toString()
            val fileHashString = computeSHA256FullHash(fileContent)
            val controlSum = document.cstmrCdtTrfInitn.grpHdr.ctrlSum
            return OBWriteFileConsentTestDataFactory.aValidOBWriteFileConsent3(
                fileType,
                fileHashString,
                numberOfTransactions,
                controlSum
            )
        }

        fun createJsonOBWriteFileConsent3WithFileInfo(
            fileHash: String,
            controlSum: String,
            numberOfPayments: String,
            fileType: String
        ): OBWriteFileConsent3 {

            return OBWriteFileConsentTestDataFactory.aValidOBWriteFileConsent3(
                fileType,
                fileHash,
                numberOfPayments,
                controlSum.toBigDecimal()
            )
        }

        fun createOBWriteFileConsent3WithMandatoryFieldsAndFileInfo(
            fileContent: String,
            fileType: String
        ): OBWriteFileConsent3 {
            val document = JAXB.unmarshal(
                StringReader(fileContent),
                com.forgerock.sapi.gateway.ob.uk.generated.xml.model.pain00100108.Document::class.java
            )
            val fileHashString = computeSHA256FullHash(fileContent)
            return OBWriteFileConsentTestDataFactory.aValidOBWriteFileConsent3MandatoryFields(fileType, fileHashString)
        }

        fun copyOBWriteDomestic2DataInitiation(initiation: OBWriteDomestic2DataInitiation): OBWriteDomestic2DataInitiation {
            val copy = OBWriteDomestic2DataInitiation()
                .instructionIdentification(initiation.instructionIdentification)
                .endToEndIdentification(initiation.endToEndIdentification)
                .localInstrument(initiation.localInstrument)
                .debtorAccount(initiation.debtorAccount)
                .creditorAccount(initiation.creditorAccount)
                .creditorPostalAddress(initiation.creditorPostalAddress)
                .remittanceInformation(initiation.remittanceInformation)
                .supplementaryData(initiation.supplementaryData)
                .instructedAmount(initiation.instructedAmount)

            assertThat(copy.equals(initiation)).isTrue
            return copy

        }

        fun copyOBWriteDomesticStandingOrder3DataInitiation(initiation: OBWriteDomesticStandingOrder3DataInitiation): OBWriteDomesticStandingOrder3DataInitiation {
            val copy = OBWriteDomesticStandingOrder3DataInitiation()
                .frequency(initiation.frequency)
                .reference(initiation.reference)
                .numberOfPayments(initiation.numberOfPayments)
                .firstPaymentDateTime(initiation.firstPaymentDateTime)
                .recurringPaymentDateTime(initiation.recurringPaymentDateTime)
                .finalPaymentDateTime(initiation.finalPaymentDateTime)
                .firstPaymentAmount(initiation.firstPaymentAmount)
                .recurringPaymentAmount(initiation.recurringPaymentAmount)
                .finalPaymentAmount((initiation.finalPaymentAmount))
                .debtorAccount(initiation.debtorAccount)
                .creditorAccount(initiation.creditorAccount)
                .supplementaryData(initiation.supplementaryData)

            assertThat(copy.equals(initiation))
            return copy
        }

        fun copyOBWriteDomesticScheduled2DataInitiation(initiation: OBWriteDomesticScheduled2DataInitiation): OBWriteDomesticScheduled2DataInitiation {
            val copy = OBWriteDomesticScheduled2DataInitiation()
                .instructionIdentification(initiation.instructionIdentification)
                .endToEndIdentification(initiation.endToEndIdentification)
                .localInstrument(initiation.localInstrument)
                .requestedExecutionDateTime(initiation.requestedExecutionDateTime)
                .debtorAccount(initiation.debtorAccount)
                .creditorAccount(initiation.creditorAccount)
                .creditorPostalAddress(initiation.creditorPostalAddress)
                .remittanceInformation(initiation.remittanceInformation)
                .supplementaryData(initiation.supplementaryData)
                .instructedAmount(initiation.instructedAmount)

            assertThat(copy.equals(initiation)).isTrue

            return copy
        }

        fun copyOBWriteInternational3DataInitiation(initiation: OBWriteInternational3DataInitiation): OBWriteInternational3DataInitiation {
            val copy = OBWriteInternational3DataInitiation()
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
                .exchangeRateInformation(initiation.exchangeRateInformation)
                .instructedAmount(initiation.instructedAmount)

            assertThat(copy.equals(initiation)).isTrue
            return copy
        }

        fun copyOBWriteInternationalScheduled3DataInitiation(initiation: OBWriteInternationalScheduled3DataInitiation): OBWriteInternationalScheduled3DataInitiation {
            val copy = OBWriteInternationalScheduled3DataInitiation()
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
                .exchangeRateInformation(initiation.exchangeRateInformation)
                .requestedExecutionDateTime(initiation.requestedExecutionDateTime)
                .instructedAmount(initiation.instructedAmount)

            assertThat(copy.equals(initiation)).isTrue
            return copy
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

        fun mapOBWriteDomesticStandingOrderConsentResponse6DataInitiationToOBWriteDomesticStandingOrder3DataInitiation(
            inputInitiation: OBWriteDomesticStandingOrderConsentResponse6DataInitiation
        ): OBWriteDomesticStandingOrder3DataInitiation {
            val outputInitiation = OBWriteDomesticStandingOrder3DataInitiation()
                .frequency(inputInitiation.frequency)
                .reference(inputInitiation.reference)
                .numberOfPayments(inputInitiation.numberOfPayments)
                .firstPaymentDateTime(inputInitiation.firstPaymentDateTime)
                .recurringPaymentDateTime(inputInitiation.recurringPaymentDateTime)
                .finalPaymentDateTime(inputInitiation.finalPaymentDateTime)
                .supplementaryData(inputInitiation.supplementaryData)

            if (inputInitiation.firstPaymentAmount != null) {
                outputInitiation.firstPaymentAmount(
                    OBWriteDomesticStandingOrder3DataInitiationFirstPaymentAmount()
                        .amount(inputInitiation.firstPaymentAmount?.amount)
                        .currency(inputInitiation.firstPaymentAmount?.currency)
                )
            }

            if (inputInitiation.recurringPaymentAmount != null) {
                outputInitiation.recurringPaymentAmount(
                    OBWriteDomesticStandingOrder3DataInitiationRecurringPaymentAmount()
                        .amount(inputInitiation.recurringPaymentAmount?.amount)
                        .currency(inputInitiation.recurringPaymentAmount?.currency)
                )
            }

            if (inputInitiation.finalPaymentAmount != null) {
                outputInitiation.finalPaymentAmount(
                    OBWriteDomesticStandingOrder3DataInitiationFinalPaymentAmount()
                        .amount(inputInitiation.finalPaymentAmount?.amount)
                        .currency(inputInitiation.finalPaymentAmount?.currency)
                )
            }

            if (inputInitiation.debtorAccount != null) {
                outputInitiation.debtorAccount(
                    OBWriteDomesticStandingOrder3DataInitiationDebtorAccount()
                        .schemeName(inputInitiation.debtorAccount?.schemeName)
                        .identification(inputInitiation.debtorAccount?.identification)
                        .name(inputInitiation.debtorAccount?.name)
                        .secondaryIdentification(inputInitiation.debtorAccount?.secondaryIdentification)
                )
            }

            if (inputInitiation.creditorAccount != null) {
                outputInitiation.creditorAccount(
                    OBWriteDomesticStandingOrder3DataInitiationCreditorAccount()
                        .schemeName(inputInitiation.creditorAccount?.schemeName)
                        .identification(inputInitiation.creditorAccount?.identification)
                        .name(inputInitiation.creditorAccount?.name)
                        .secondaryIdentification(inputInitiation.creditorAccount?.secondaryIdentification)
                )
            }
            return outputInitiation
        }

        fun mapOBWriteInternationalStandingOrderConsentResponse7DataInitiationToOBWriteInternationalStandingOrder3DataInitiation(
            inputInitiation: OBWriteInternationalStandingOrderConsentResponse7DataInitiation
        ): OBWriteInternationalStandingOrder4DataInitiation? {
            val outputInitiation = OBWriteInternationalStandingOrder4DataInitiation()
                .frequency(inputInitiation.frequency)
                .reference(inputInitiation.reference)
                .numberOfPayments(inputInitiation.numberOfPayments)
                .firstPaymentDateTime(inputInitiation.firstPaymentDateTime)
                .finalPaymentDateTime(inputInitiation.finalPaymentDateTime)
                .supplementaryData(inputInitiation.supplementaryData)

            if (inputInitiation.instructedAmount != null) {
                outputInitiation.instructedAmount(
                    OBWriteDomestic2DataInitiationInstructedAmount()
                        .amount(inputInitiation.instructedAmount?.amount)
                        .currency(inputInitiation.instructedAmount?.currency)
                )
            }

            if (inputInitiation.debtorAccount != null) {
                outputInitiation.debtorAccount(
                    OBWriteDomesticStandingOrder3DataInitiationDebtorAccount()
                        .schemeName(inputInitiation.debtorAccount?.schemeName)
                        .identification(inputInitiation.debtorAccount?.identification)
                        .name(inputInitiation.debtorAccount?.name)
                        .secondaryIdentification(inputInitiation.debtorAccount?.secondaryIdentification)
                )
            }

            if (inputInitiation.creditorAccount != null) {
                outputInitiation.creditorAccount(
                    OBWriteInternationalStandingOrder4DataInitiationCreditorAccount()
                        .schemeName(inputInitiation.creditorAccount?.schemeName)
                        .identification(inputInitiation.creditorAccount?.identification)
                        .name(inputInitiation.creditorAccount?.name)
                        .secondaryIdentification(inputInitiation.creditorAccount?.secondaryIdentification)
                )
            }

            if (inputInitiation.purpose != null) {
                outputInitiation.purpose(inputInitiation.purpose)
            }

            if (inputInitiation.extendedPurpose != null) {
                outputInitiation.extendedPurpose(inputInitiation.extendedPurpose)
            }

            if (inputInitiation.chargeBearer != null) {
                outputInitiation.chargeBearer(inputInitiation.chargeBearer)
            }

            if (inputInitiation.currencyOfTransfer != null) {
                outputInitiation.currencyOfTransfer(inputInitiation.currencyOfTransfer)
            }

            if (inputInitiation.destinationCountryCode != null) {
                outputInitiation.destinationCountryCode(inputInitiation.destinationCountryCode)
            }

            if (inputInitiation.creditor != null) {
                outputInitiation.creditor(
                    OBWriteInternationalScheduledConsentResponse6DataInitiationCreditor()
                        .name(inputInitiation.creditor?.name)
                        .postalAddress(inputInitiation.creditor?.postalAddress)
                )
            }

            if (inputInitiation.creditorAgent != null) {
                outputInitiation.creditorAgent(
                    OBWriteInternationalStandingOrder4DataInitiationCreditorAgent()
                        .schemeName(inputInitiation.creditorAgent?.schemeName)
                        .identification(inputInitiation.creditorAgent?.identification)
                        .name(inputInitiation.creditorAgent?.name)
                        .postalAddress(inputInitiation.creditorAgent?.postalAddress)
                )
            }

            return outputInitiation
        }

        fun mapOBWriteFileConsentResponse4DataInitiationToOBWriteFile2DataInitiation(inputInitiation: OBWriteFile2DataInitiation): OBWriteFile2DataInitiation? {
            val outputInitiation = OBWriteFile2DataInitiation()
                .fileHash(inputInitiation.fileHash)
                .fileReference(inputInitiation.fileReference)
                .fileType(inputInitiation.fileType)
                .numberOfTransactions(inputInitiation.numberOfTransactions)
                .requestedExecutionDateTime(inputInitiation.requestedExecutionDateTime)
                .localInstrument(inputInitiation.localInstrument)
                .controlSum(inputInitiation.controlSum)


            if (inputInitiation.debtorAccount != null) {
                outputInitiation.debtorAccount(
                    OBWriteDomestic2DataInitiationDebtorAccount()
                        .schemeName(inputInitiation.debtorAccount?.schemeName)
                        .identification(inputInitiation.debtorAccount?.identification)
                        .name(inputInitiation.debtorAccount?.name)
                        .secondaryIdentification(inputInitiation.debtorAccount?.secondaryIdentification)
                )
            }

            if (inputInitiation.remittanceInformation != null) {
                outputInitiation.remittanceInformation(
                    OBWriteDomestic2DataInitiationRemittanceInformation()
                        .unstructured(inputInitiation.remittanceInformation?.unstructured)
                        .reference(inputInitiation.remittanceInformation?.reference)
                )
            }

            if (inputInitiation.supplementaryData != null) {
                outputInitiation.supplementaryData(
                    OBSupplementaryData1()
                )
            }

            return outputInitiation
        }

        fun copyOBWriteFile2DataInitiation(initiation: OBWriteFile2DataInitiation): OBWriteFile2DataInitiation? {
            val copy = OBWriteFile2DataInitiation()
                .fileHash(initiation.fileHash)
                .fileReference(initiation.fileReference)
                .fileType(initiation.fileType)
                .numberOfTransactions(initiation.numberOfTransactions)
                .requestedExecutionDateTime(initiation.requestedExecutionDateTime)
                .localInstrument(initiation.localInstrument)
                .controlSum(initiation.controlSum)
                .debtorAccount(initiation.debtorAccount)
                .remittanceInformation(initiation.remittanceInformation)
                .supplementaryData(initiation.supplementaryData)

            assertThat(copy.equals(initiation)).isTrue
            return copy
        }

        fun copyOBWriteInternationalStandingOrder4DataInitiation(initiation: OBWriteInternationalStandingOrder4DataInitiation): OBWriteInternationalStandingOrder4DataInitiation? {
            val copy = OBWriteInternationalStandingOrder4DataInitiation()
                .frequency(initiation.frequency)
                .reference(initiation.reference)
                .numberOfPayments(initiation.numberOfPayments)
                .firstPaymentDateTime(initiation.firstPaymentDateTime)
                .finalPaymentDateTime(initiation.finalPaymentDateTime)
                .supplementaryData(initiation.supplementaryData)
                .instructedAmount(initiation.instructedAmount)
                .debtorAccount(initiation.debtorAccount)
                .creditorAccount(initiation.creditorAccount)
                .purpose(initiation.purpose)
                .extendedPurpose(initiation.extendedPurpose)
                .chargeBearer(initiation.chargeBearer)
                .currencyOfTransfer(initiation.currencyOfTransfer)
                .destinationCountryCode(initiation.destinationCountryCode)
                .creditor(initiation.creditor)
                .creditorAgent(initiation.creditorAgent)

            assertThat(copy.equals(initiation)).isTrue
            return copy
        }

        fun copyOBDomesticVRPInitiation(initiation: OBDomesticVRPInitiation): OBDomesticVRPInitiation {
            val copy = OBDomesticVRPInitiation()
                .creditorAccount(initiation.creditorAccount)
                .creditorPostalAddress(initiation.creditorPostalAddress)
                .debtorAccount(initiation.debtorAccount)
                .remittanceInformation(initiation.remittanceInformation)

            assertThat(copy.equals(initiation)).isTrue
            return copy
        }
    }
}
