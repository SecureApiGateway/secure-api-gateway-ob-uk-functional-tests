package com.forgerock.openbanking.payment.file

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.openbanking.Status
import com.forgerock.openbanking.common.model.version.OBVersion.v3_1_4
import com.forgerock.openbanking.common.model.version.OBVersion.v3_1_6
import com.forgerock.openbanking.defaultMapper
import com.forgerock.openbanking.discovery.payment3_1
import com.forgerock.openbanking.discovery.payment3_1_2
import com.forgerock.openbanking.discovery.payment3_1_4
import com.forgerock.openbanking.discovery.payment3_1_6
import com.forgerock.openbanking.junit.CreateTppCallback
import com.forgerock.openbanking.junit.EnabledIfOpenBankingVersion
import com.forgerock.openbanking.payment.PaymentAS
import com.forgerock.openbanking.payment.PaymentFactory
import com.forgerock.openbanking.payment.PaymentFileType
import com.forgerock.openbanking.payment.PaymentRS
import com.forgerock.openbanking.psu
import org.apache.commons.io.FileUtils
import org.assertj.core.api.Assertions
import org.joda.time.DateTime
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.payment.*
import uk.org.openbanking.datamodel.service.converter.payment.OBFileConverter
import uk.org.openbanking.datamodel.service.converter.payment.OBFileConverter.toOBFile2
import uk.org.openbanking.testsupport.payment.OBAccountTestDataFactory.aValidOBCashAccount3
import uk.org.openbanking.testsupport.payment.OBAccountTestDataFactory.aValidOBWriteDomestic2DataInitiationDebtorAccount
import uk.org.openbanking.testsupport.payment.OBAmountTestDataFactory.aValidOBActiveOrHistoricCurrencyAndAmount
import uk.org.openbanking.testsupport.payment.OBPostalAddress6TestDataFactory.aValidOBPostalAddress6
import uk.org.openbanking.testsupport.payment.OBRemittanceInformationTestDataFactory.aValidOBRemittanceInformation1
import uk.org.openbanking.testsupport.payment.OBRemittanceInformationTestDataFactory.aValidOBWriteDomestic2DataInitiationRemittanceInformation
import java.io.File
import java.io.StringReader
import java.math.BigDecimal
import java.nio.charset.StandardCharsets
import javax.xml.bind.JAXB

@Tags(Tag("paymentTest"))
class FilePaymentTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfOpenBankingVersion(type = "payments", version = "v3.1.4", apis = ["file-payment-consents"])
    @Test
    fun shouldCreateFilePaymentConsent() {
        // Given
        val consentRequest = filePaymentConsentWithAllFields(PaymentFileType.UK_OBIE_PAYMENT_INITIATION_V3_1.type)

        // When
        val consent = PaymentRS().consent<OBWriteFileConsentResponse3>(payment3_1_4.Links.links.CreateFilePaymentConsent, consentRequest, tppResource.tpp, v3_1_4)
        val getConsentResult = PaymentRS().getConsent<OBWriteFileConsentResponse3>(payment3_1_4.Links.links.CreateFilePaymentConsent + "/" + consent.data.consentId, tppResource.tpp)

        // Then
        assertThat(getConsentResult).isNotNull()
        assertThat(getConsentResult.data.consentId).isNotEmpty()
        Assertions.assertThat(getConsentResult.data.status.toString()).`is`(Status.consentCondition)
        assertThat(getConsentResult.links.self).isEqualTo(payment3_1_4.Links.links.CreateFilePaymentConsent + "/" + consent.data.consentId)
        assertThat(getConsentResult.meta).isNotNull()

    }

    @EnabledIfOpenBankingVersion(type = "payments", version = "v3.1.2", apis = ["file-payments"])
    @Test
    fun shouldCreateJSONFilePayment_v3_1_2() {
        // Given
        val file = jsonFilePayment()
        val numberOfTransactions = file.getNumberOfTransactions()
        val controlSum = file.getControlSum()
        val fileHash = PaymentFactory.computeSHA256FullHash(defaultMapper.writeValueAsString(file))
        val consent = PaymentRS().consent<OBWriteFileConsentResponse2>(payment3_1_2.Links.links.CreateFilePaymentConsent, filePaymentConsentWithAllFields(PaymentFileType.UK_OBIE_PAYMENT_INITIATION_V3_1.type, fileHash, numberOfTransactions, controlSum), tppResource.tpp)
        // when
        val submissionFileResp = PaymentRS().submitFilePayment(payment3_1_2.Links.links.CreateFilePaymentConsent + "/" + consent.data.consentId + "/file", defaultMapper.writeValueAsString(file), PaymentFileType.UK_OBIE_PAYMENT_INITIATION_V3_1.mediaType, tppResource.tpp)
        // then
        assertThat(submissionFileResp.statusCode).isEqualTo(200)
        assertThat(submissionFileResp.responseMessage).isEqualTo("OK")
        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().headlessAuthentication(consent.data.consentId, tppResource.tpp.registrationResponse, psu, tppResource.tpp)
        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentAS().clientCredentialsAuthentication(consent.data.consentId, tppResource.tpp.registrationResponse, tppResource.tpp)
        val paymentSubmissionRequest = OBWriteFile2().data(
                OBWriteDataFile2()
                        .consentId(consent.data.consentId)
                        .initiation(consent.data.initiation)
        )

        val submissionResp = PaymentRS().submitPayment<OBWriteFileResponse2>(payment3_1_2.Links.links.CreateFilePayment, paymentSubmissionRequest, accessTokenAuthorizationCode, tppResource.tpp)

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteFileResponse2>(payment3_1.Links.links.CreateFilePayment, submissionResp.data.filePaymentId, accessTokenClientCredentials)


        // Then
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.filePaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(Status.paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1.Links.links.CreateFilePayment + "/" + submissionResp.data.filePaymentId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfOpenBankingVersion(type = "payments", version = "v3.1.6", apis = ["file-payments"])
    @Test
    fun shouldCreateJSONFilePayment_v3_1_6() {
        // Given
        val file = jsonFilePayment()
        val numberOfTransactions = file.getNumberOfTransactions()
        val controlSum = file.getControlSum()
        val fileHash = PaymentFactory.computeSHA256FullHash(defaultMapper.writeValueAsString(file))
        val consent = PaymentRS().consent<OBWriteFileConsentResponse4>(payment3_1_6.Links.links.CreateFilePaymentConsent, filePaymentConsentWithAllFields(PaymentFileType.UK_OBIE_PAYMENT_INITIATION_V3_1.type, fileHash, numberOfTransactions, controlSum), tppResource.tpp, v3_1_6)
        // when
        val submissionFileResp = PaymentRS().submitFilePayment(payment3_1_6.Links.links.CreateFilePaymentConsent + "/" + consent.data.consentId + "/file", defaultMapper.writeValueAsString(file), PaymentFileType.UK_OBIE_PAYMENT_INITIATION_V3_1.mediaType, tppResource.tpp, v3_1_6)
        // then
        assertThat(submissionFileResp.statusCode).isEqualTo(200)
        assertThat(submissionFileResp.responseMessage).isEqualTo("OK")
        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().headlessAuthentication(consent.data.consentId, tppResource.tpp.registrationResponse, psu, tppResource.tpp)
        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentAS().clientCredentialsAuthentication(consent.data.consentId, tppResource.tpp.registrationResponse, tppResource.tpp)
        val paymentSubmissionRequest = OBWriteFile2().data(
                OBWriteDataFile2()
                        .consentId(consent.data.consentId)
                        .initiation(toOBFile2(consent.data.initiation))
        )

        val submissionResp = PaymentRS().submitPayment<OBWriteFileResponse3>(payment3_1_6.Links.links.CreateFilePayment, paymentSubmissionRequest, accessTokenAuthorizationCode, tppResource.tpp, v3_1_6)

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteFileResponse3>(payment3_1_6.Links.links.CreateFilePayment, submissionResp.data.filePaymentId, accessTokenClientCredentials, v3_1_6)


        // Then
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.filePaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        assertThat(paymentResult.data.debtor.name).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(Status.paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_6.Links.links.CreateFilePayment + "/" + submissionResp.data.filePaymentId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfOpenBankingVersion(type = "payments", version = "v3.1.4", apis = ["file-payments"])
    @Test
    fun shouldCreateXMLFilePayment() {
        // Given
        val obXMLPaymentFileV08String = getXMLFileAsString()
        val document = JAXB.unmarshal(StringReader(obXMLPaymentFileV08String), com.forgerock.generated.xml.model.pain00100108.Document::class.java)
        val numberOfTransactions = document.cstmrCdtTrfInitn.grpHdr.nbOfTxs.toString()
        val controlSum = document.cstmrCdtTrfInitn.grpHdr.ctrlSum.toString()
        val fileHashString = PaymentFactory.computeSHA256FullHash(obXMLPaymentFileV08String)
        val consent = PaymentRS().consent<OBWriteFileConsentResponse2>(payment3_1_4.Links.links.CreateFilePaymentConsent, filePaymentConsentWithAllFields(PaymentFileType.UK_OBIE_PAIN_001_001_008.type, fileHashString, numberOfTransactions, controlSum), tppResource.tpp, v3_1_4)
        // when
        val submissionFileResp = PaymentRS().submitFilePayment(payment3_1_4.Links.links.CreateFilePaymentConsent + "/" + consent.data.consentId + "/file", obXMLPaymentFileV08String, PaymentFileType.UK_OBIE_PAIN_001_001_008.mediaType, tppResource.tpp, v3_1_4)
        // then
        assertThat(submissionFileResp.statusCode).isEqualTo(200)
        assertThat(submissionFileResp.responseMessage).isEqualTo("OK")

        // Payment
        // accessToken to submit payment use the grant type authorization_code
        val accesstokenAuthorizationCode = PaymentAS().headlessAuthentication(consent.data.consentId, tppResource.tpp.registrationResponse, psu, tppResource.tpp)
        // accessToken to get the payment use the grant type client_credentials
        val accesstokenClientCredentials = PaymentAS().clientCredentialsAuthentication(consent.data.consentId, tppResource.tpp.registrationResponse, tppResource.tpp)
        val paymentSubmissionRequest = OBWriteFile2().data(
                OBWriteDataFile2()
                        .consentId(consent.data.consentId)
                        .initiation(consent.data.initiation)
        )

        val submissionResp = PaymentRS().submitPayment<OBWriteFileResponse2>(payment3_1_4.Links.links.CreateFilePayment, paymentSubmissionRequest, accesstokenAuthorizationCode, tppResource.tpp, v3_1_4)
        // When
        val paymentResult = PaymentRS().getPayment<OBWriteFileResponse2>(payment3_1_4.Links.links.CreateFilePayment, submissionResp.data.filePaymentId, accesstokenClientCredentials, v3_1_4)


        // Then
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.filePaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(Status.paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_4.Links.links.CreateFilePayment + "/" + submissionResp.data.filePaymentId)
        assertThat(paymentResult.meta).isNotNull()
    }

    private fun filePaymentConsentWithAllFields(fileType: String, fileHash: String = "ZtJD05MXY3WBBzBXk+MjxcJ/ncVs16ue2QnQIZ2NK0c=", numberOfTransactions: String = "1", controlSum: String = "0"): OBWriteFileConsent3 {
        return OBWriteFileConsent3()
                .data(OBWriteFileConsent3Data()
                        .initiation(OBWriteFile2DataInitiation()
                                .fileType(fileType)
                                .fileHash(fileHash)
                                .fileReference("file-Example")
                                .numberOfTransactions(numberOfTransactions)
                                .controlSum(BigDecimal(controlSum))
                                .requestedExecutionDateTime(DateTime.now())
                                .localInstrument("File-payment-0099")
                                .debtorAccount(aValidOBWriteDomestic2DataInitiationDebtorAccount())
                                .remittanceInformation(aValidOBWriteDomestic2DataInitiationRemittanceInformation())
                                .supplementaryData(OBSupplementaryData1())
                        )
                        .authorisation(OBWriteDomesticConsent3DataAuthorisation()
                                .authorisationType(OBWriteDomesticConsent3DataAuthorisation.AuthorisationTypeEnum.SINGLE)
                                .completionDateTime(DateTime().plusDays(1)))
                        .scASupportData(OBWriteDomesticConsent3DataSCASupportData()
                                .requestedSCAExemptionType(OBWriteDomesticConsent3DataSCASupportData.RequestedSCAExemptionTypeEnum.BILLPAYMENT)
                                .appliedAuthenticationApproach(OBWriteDomesticConsent3DataSCASupportData.AppliedAuthenticationApproachEnum.CA)
                                .referencePaymentOrderId("REF-payment-099"))
                )
    }


    private fun jsonFilePayment(): OBJsonPaymentFile {
        return OBJsonPaymentFile()
                .data(OBWriteJsonPaymentFile()
                        .domesticPayments(
                                listOf(OBDomestic2()
                                        .instructionIdentification("ANSM099")
                                        .endToEndIdentification("FTEST.2019.0099")
                                        .localInstrument("UK.OBIE.TEST.099")
                                        .instructedAmount(aValidOBActiveOrHistoricCurrencyAndAmount())
                                        .debtorAccount(aValidOBCashAccount3())
                                        .creditorAccount(aValidOBCashAccount3())
                                        .creditorPostalAddress(aValidOBPostalAddress6())
                                        .remittanceInformation(aValidOBRemittanceInformation1())
                                )
                        )
                )
    }

    private fun getXMLFileAsString(): String {
        val filePath = "/com/forgerock/openbanking/payment/file/pain.001.001.08.xml"
        try {
            return FileUtils.readFileToString(File(object {}.javaClass.getResource(filePath)?.file), StandardCharsets.UTF_8)
        } catch (e: NullPointerException) {
            println(e)
            throw AssertionError("Could not load file: ${filePath}")
        }
    }
}