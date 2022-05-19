package com.forgerock.securebanking.tests.functional.deprecated.payments.file

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.openbanking.common.model.version.OBVersion.v3_1_4
import com.forgerock.openbanking.common.model.version.OBVersion.v3_1_6
import com.forgerock.securebanking.framework.conditions.Status
import com.forgerock.securebanking.framework.configuration.psu
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.framework.http.fuel.defaultMapper
import com.forgerock.securebanking.support.discovery.payment3_1_2
import com.forgerock.securebanking.support.discovery.payment3_1_4
import com.forgerock.securebanking.support.discovery.payment3_1_6
import com.forgerock.securebanking.support.payment.PaymentAS
import com.forgerock.securebanking.support.payment.PaymentFactory
import com.forgerock.securebanking.support.payment.PaymentFileType
import com.forgerock.securebanking.support.payment.PaymentRS
import org.apache.commons.io.FileUtils
import org.assertj.core.api.Assertions
import org.joda.time.DateTime
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.payment.*
import uk.org.openbanking.datamodel.service.converter.payment.OBFileConverter.toOBWriteFile2DataInitiation
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

class FilePaymentTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateFilePaymentConsent"],
        apis = ["file-payment-consents"]
    )
    @Test
    fun shouldCreateFilePaymentConsent() {
        // Given
        val consentRequest = filePaymentConsentWithAllFields(PaymentFileType.UK_OBIE_PAYMENT_INITIATION_V3_1.type)

        // When
        val consent = PaymentRS().consent<OBWriteFileConsentResponse3>(
            payment3_1_4.Links.links.CreateFilePaymentConsent,
            consentRequest,
            tppResource.tpp,
            v3_1_4
        )
        val getConsentResult = PaymentRS().getConsent<OBWriteFileConsentResponse3>(
            payment3_1_4.Links.links.CreateFilePaymentConsent + "/" + consent.data.consentId,
            tppResource.tpp
        )

        // Then
        assertThat(getConsentResult).isNotNull()
        assertThat(getConsentResult.data.consentId).isNotEmpty()
        Assertions.assertThat(getConsentResult.data.status.toString()).`is`(Status.consentCondition)
        assertThat(getConsentResult.links.self).isEqualTo(payment3_1_4.Links.links.CreateFilePaymentConsent + "/" + consent.data.consentId)
        assertThat(getConsentResult.meta).isNotNull()

    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.2",
        operations = ["CreateFilePaymentConsent", "CreateFilePayment"],
        apis = ["file-payments"]
    )
    @Test
    fun shouldCreateJSONFilePayment_v3_1_2() {
        // Given
        val file = jsonFilePayment()
        val numberOfTransactions = file.getNumberOfTransactions()
        val controlSum = file.getControlSum()
        val fileHash = PaymentFactory.computeSHA256FullHash(defaultMapper.writeValueAsString(file))
        val consent = PaymentRS().consent<OBWriteFileConsentResponse2>(
            payment3_1_2.Links.links.CreateFilePaymentConsent,
            filePaymentConsentWithAllFields(
                PaymentFileType.UK_OBIE_PAYMENT_INITIATION_V3_1.type,
                fileHash,
                numberOfTransactions,
                controlSum
            ),
            tppResource.tpp
        )
        // when
        val submissionFileResp = PaymentRS().submitFilePayment(
            payment3_1_2.Links.links.CreateFilePaymentConsent + "/" + consent.data.consentId + "/file",
            defaultMapper.writeValueAsString(file),
            PaymentFileType.UK_OBIE_PAYMENT_INITIATION_V3_1.mediaType,
            tppResource.tpp
        )
        // then
        assertThat(submissionFileResp.statusCode).isEqualTo(200)
        assertThat(submissionFileResp.responseMessage).isEqualTo("OK")
        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().headlessAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentAS().clientCredentialsAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            tppResource.tpp
        )
        val paymentSubmissionRequest = OBWriteFile2().data(
            OBWriteFile2Data()
                .consentId(consent.data.consentId)
                .initiation(toOBWriteFile2DataInitiation(consent.data.initiation))
        )

        val submissionResp = PaymentRS().submitPayment<OBWriteFileResponse2>(
            payment3_1_2.Links.links.CreateFilePayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            tppResource.tpp
        )

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteFileResponse2>(
            payment3_1_2.Links.links.CreateFilePayment,
            submissionResp.data.filePaymentId,
            accessTokenClientCredentials
        )


        // Then
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.filePaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(Status.paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_2.Links.links.CreateFilePayment + "/" + submissionResp.data.filePaymentId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.6",
        operations = ["CreateFilePaymentConsent", "CreateFilePayment"],
        apis = ["file-payments"]
    )
    @Test
    fun shouldCreateJSONFilePayment_v3_1_6() {
        // Given
        val file = jsonFilePayment()
        val numberOfTransactions = file.getNumberOfTransactions()
        val controlSum = file.getControlSum()
        val fileHash = PaymentFactory.computeSHA256FullHash(defaultMapper.writeValueAsString(file))
        val consent = PaymentRS().consent<OBWriteFileConsentResponse4>(
            payment3_1_6.Links.links.CreateFilePaymentConsent,
            filePaymentConsentWithAllFields(
                PaymentFileType.UK_OBIE_PAYMENT_INITIATION_V3_1.type,
                fileHash,
                numberOfTransactions,
                controlSum
            ),
            tppResource.tpp,
            v3_1_6
        )
        // when
        val submissionFileResp = PaymentRS().submitFilePayment(
            payment3_1_6.Links.links.CreateFilePaymentConsent + "/" + consent.data.consentId + "/file",
            defaultMapper.writeValueAsString(file),
            PaymentFileType.UK_OBIE_PAYMENT_INITIATION_V3_1.mediaType,
            tppResource.tpp,
            v3_1_6
        )
        // then
        assertThat(submissionFileResp.statusCode).isEqualTo(200)
        assertThat(submissionFileResp.responseMessage).isEqualTo("OK")
        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().headlessAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentAS().clientCredentialsAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            tppResource.tpp
        )
        val paymentSubmissionRequest = OBWriteFile2().data(
            OBWriteFile2Data()
                .consentId(consent.data.consentId)
                .initiation(consent.data.initiation)
        )

        val submissionResp = PaymentRS().submitPayment<OBWriteFileResponse3>(
            payment3_1_6.Links.links.CreateFilePayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            tppResource.tpp
        )

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteFileResponse3>(
            payment3_1_6.Links.links.CreateFilePayment,
            submissionResp.data.filePaymentId,
            accessTokenClientCredentials,
            v3_1_6
        )


        // Then
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.filePaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        assertThat(paymentResult.data.debtor.name).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(Status.paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_6.Links.links.CreateFilePayment + "/" + submissionResp.data.filePaymentId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateFilePaymentConsent", "CreateFilePayment"],
        apis = ["file-payments"]
    )
    @Test
    fun shouldCreateXMLFilePayment() {
        // Given
        val obXMLPaymentFileV08String = getXMLFileAsString()
        val document = JAXB.unmarshal(
            StringReader(obXMLPaymentFileV08String),
            com.forgerock.generated.xml.model.pain00100108.Document::class.java
        )
        val numberOfTransactions = document.cstmrCdtTrfInitn.grpHdr.nbOfTxs.toString()
        val controlSum = document.cstmrCdtTrfInitn.grpHdr.ctrlSum.toString()
        val fileHashString = PaymentFactory.computeSHA256FullHash(obXMLPaymentFileV08String)
        val consent = PaymentRS().consent<OBWriteFileConsentResponse2>(
            payment3_1_4.Links.links.CreateFilePaymentConsent,
            filePaymentConsentWithAllFields(
                PaymentFileType.UK_OBIE_PAIN_001_001_008.type,
                fileHashString,
                numberOfTransactions,
                controlSum
            ),
            tppResource.tpp,
            v3_1_4
        )
        // when
        val submissionFileResp = PaymentRS().submitFilePayment(
            payment3_1_4.Links.links.CreateFilePaymentConsent + "/" + consent.data.consentId + "/file",
            obXMLPaymentFileV08String,
            PaymentFileType.UK_OBIE_PAIN_001_001_008.mediaType,
            tppResource.tpp,
            v3_1_4
        )
        // then
        assertThat(submissionFileResp.statusCode).isEqualTo(200)
        assertThat(submissionFileResp.responseMessage).isEqualTo("OK")

        // Payment
        // accessToken to submit payment use the grant type authorization_code
        val accesstokenAuthorizationCode = PaymentAS().headlessAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        // accessToken to get the payment use the grant type client_credentials
        val accesstokenClientCredentials = PaymentAS().clientCredentialsAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            tppResource.tpp
        )
        val paymentSubmissionRequest = OBWriteFile2().data(
            OBWriteFile2Data()
                .consentId(consent.data.consentId)
                .initiation(toOBWriteFile2DataInitiation(consent.data.initiation))
        )

        val submissionResp = PaymentRS().submitPayment<OBWriteFileResponse2>(
            payment3_1_4.Links.links.CreateFilePayment,
            paymentSubmissionRequest,
            accesstokenAuthorizationCode,
            tppResource.tpp
        )
        // When
        val paymentResult = PaymentRS().getPayment<OBWriteFileResponse2>(
            payment3_1_4.Links.links.CreateFilePayment,
            submissionResp.data.filePaymentId,
            accesstokenClientCredentials,
            v3_1_4
        )


        // Then
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.filePaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(Status.paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_4.Links.links.CreateFilePayment + "/" + submissionResp.data.filePaymentId)
        assertThat(paymentResult.meta).isNotNull()
    }

    private fun filePaymentConsentWithAllFields(
        fileType: String,
        fileHash: String = "ZtJD05MXY3WBBzBXk+MjxcJ/ncVs16ue2QnQIZ2NK0c=",
        numberOfTransactions: String = "1",
        controlSum: String = "0"
    ): OBWriteFileConsent3 {
        return OBWriteFileConsent3()
            .data(
                OBWriteFileConsent3Data()
                    .initiation(
                        OBWriteFile2DataInitiation()
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
                    .authorisation(
                        OBWriteDomesticConsent4DataAuthorisation()
                            .authorisationType(OBExternalAuthorisation1Code.SINGLE)
                            .completionDateTime(DateTime().plusDays(1))
                    )
                    .scASupportData(
                        OBSCASupportData1()
                            .requestedSCAExemptionType(OBRequestedSCAExemptionTypeEnum.BILLPAYMENT)
                            .appliedAuthenticationApproach(OBAppliedAuthenticationApproachEnum.CA)
                            .referencePaymentOrderId("REF-payment-099")
                    )
            )
    }


    private fun jsonFilePayment(): OBJsonPaymentFile {
        return OBJsonPaymentFile()
            .data(
                OBWriteJsonPaymentFile()
                    .domesticPayments(
                        listOf(
                            OBDomestic2()
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
        val filePath = "/com/forgerock/securebanking/payment/file/pain.001.001.08.xml"
        try {
            return FileUtils.readFileToString(
                File(object {}.javaClass.getResource(filePath)?.file.toString()),
                StandardCharsets.UTF_8
            )
        } catch (e: NullPointerException) {
            println(e)
            throw AssertionError("Could not load file: $filePath")
        }
    }
}
