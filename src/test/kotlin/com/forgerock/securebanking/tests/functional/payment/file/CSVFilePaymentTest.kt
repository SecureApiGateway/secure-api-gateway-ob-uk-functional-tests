package com.forgerock.securebanking.tests.functional.payment.file

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.forgerock.openbanking.aspsp.rs.ext.lbg.file.payment.csv.factory.CSVFilePaymentFactory
import com.forgerock.openbanking.aspsp.rs.ext.lbg.file.payment.csv.factory.CSVFilePaymentType
import com.forgerock.openbanking.aspsp.rs.ext.lbg.file.payment.csv.model.CSVCreditIndicatorRow
import com.forgerock.openbanking.aspsp.rs.ext.lbg.file.payment.csv.model.CSVDebitIndicatorSection
import com.forgerock.openbanking.aspsp.rs.ext.lbg.file.payment.csv.model.CSVFilePayment
import com.forgerock.openbanking.aspsp.rs.ext.lbg.file.payment.csv.model.CSVHeaderIndicatorSection
import com.forgerock.openbanking.aspsp.rs.ext.lbg.file.payment.csv.validation.CSVValidation
import com.forgerock.openbanking.common.model.version.OBVersion.v3_1_4
import com.forgerock.openbanking.common.model.version.OBVersion.v3_1_6
import com.forgerock.openbanking.exceptions.OBErrorException
import com.forgerock.securebanking.framework.conditions.Status
import com.forgerock.securebanking.framework.configuration.psu
import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.framework.extensions.junit.EnabledIfVersion
import com.forgerock.securebanking.support.payment.PaymentAS
import com.forgerock.securebanking.support.payment.PaymentFactory
import com.forgerock.securebanking.support.payment.PaymentRS
import com.forgerock.securebanking.support.discovery.payment3_1_2
import com.forgerock.securebanking.support.discovery.payment3_1_4
import com.forgerock.securebanking.support.discovery.payment3_1_6
import org.assertj.core.api.Assertions
import org.joda.time.DateTime
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.payment.*
import uk.org.openbanking.datamodel.service.converter.payment.OBFileConverter.toOBWriteFile2DataInitiation
import uk.org.openbanking.testsupport.payment.OBAccountTestDataFactory.aValidOBWriteDomestic2DataInitiationDebtorAccount
import uk.org.openbanking.testsupport.payment.OBRemittanceInformationTestDataFactory.aValidOBWriteDomestic2DataInitiationRemittanceInformation
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.util.*

@Tag("extension")
class CSVFilePaymentTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateFilePaymentConsent"],
        apis = ["file-payment-consents"]
    )
    @Test
    fun shouldCreateFilePaymentConsentFPS() {
        // Given
        val file = getFile(CSVFilePaymentType.UK_LBG_FPS_BATCH_V10)
        val consentRequest = filePaymentConsentWithAllFields(
            CSVFilePaymentType.UK_LBG_FPS_BATCH_V10.fileType,
            PaymentFactory.computeSHA256FullHash(file.toString())
        )

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
        apiVersion = "v3.1.4",
        operations = ["CreateFilePaymentConsent"],
        apis = ["file-payment-consents"]
    )
    @Test
    fun shouldCreateFilePaymentConsentBULK() {
        // Given
        val file = getFile(CSVFilePaymentType.UK_LBG_BACS_BULK_V10)
        val consentRequest = filePaymentConsentWithAllFields(
            CSVFilePaymentType.UK_LBG_BACS_BULK_V10.fileType,
            PaymentFactory.computeSHA256FullHash(file.toString())
        )

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
    fun shouldCreateCSVFilePaymentFPS_v3_1_2() {
        // Given
        val file = getFile(CSVFilePaymentType.UK_LBG_FPS_BATCH_V10)
        val numberOfTransactions = file.headerIndicatorSection.numCredits
        val controlSum = file.headerIndicatorSection.valueCreditsSum
        val fileHash = PaymentFactory.computeSHA256FullHash(file.toString())

        val consent = PaymentRS().consent<OBWriteFileConsentResponse2>(
            payment3_1_2.Links.links.CreateFilePaymentConsent,
            filePaymentConsentWithAllFields(
                CSVFilePaymentType.UK_LBG_FPS_BATCH_V10.fileType,
                fileHash,
                numberOfTransactions.toString(),
                controlSum.toString()
            ),
            tppResource.tpp
        )
        // when
        val submissionFileResp = PaymentRS().submitCSVFilePayment(
            payment3_1_2.Links.links.CreateFilePaymentConsent + "/" + consent.data.consentId + "/file",
            file.toString(),
            CSVFilePaymentType.UK_LBG_FPS_BATCH_V10.contentType,
            tppResource.tpp
        )
        // then
        assertThat(submissionFileResp.statusCode).isEqualTo(200)
        assertThat(submissionFileResp.responseMessage).isEqualTo("OK")

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
            payment3_1_2.Links.links.CreateFilePayment,
            paymentSubmissionRequest,
            accesstokenAuthorizationCode,
            tppResource.tpp
        )
        // When
        val paymentResult = PaymentRS().getPayment<OBWriteFileResponse2>(
            payment3_1_2.Links.links.CreateFilePayment,
            submissionResp.data.filePaymentId,
            accesstokenClientCredentials
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
        apiVersion = "v3.1.4",
        operations = ["CreateFilePaymentConsent", "CreateFilePayment"],
        apis = ["file-payments"]
    )
    @Test
    fun shouldCreateCSVFilePaymentFPS_v3_1_4() {
        // Given
        val file = getFile(CSVFilePaymentType.UK_LBG_FPS_BATCH_V10)
        val numberOfTransactions = file.headerIndicatorSection.numCredits
        val controlSum = file.headerIndicatorSection.valueCreditsSum
        val fileHash = PaymentFactory.computeSHA256FullHash(file.toString())

        val consent = PaymentRS().consent<OBWriteFileConsentResponse2>(
            payment3_1_4.Links.links.CreateFilePaymentConsent,
            filePaymentConsentWithAllFields(
                CSVFilePaymentType.UK_LBG_FPS_BATCH_V10.fileType,
                fileHash,
                numberOfTransactions.toString(),
                controlSum.toString()
            ),
            tppResource.tpp,
            v3_1_4
        )
        // when
        val submissionFileResp = PaymentRS().submitCSVFilePayment(
            payment3_1_4.Links.links.CreateFilePaymentConsent + "/" + consent.data.consentId + "/file",
            file.toString(),
            CSVFilePaymentType.UK_LBG_FPS_BATCH_V10.contentType,
            tppResource.tpp,
            v3_1_4
        )
        // then
        assertThat(submissionFileResp.statusCode).isEqualTo(200)
        assertThat(submissionFileResp.responseMessage).isEqualTo("OK")

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
            tppResource.tpp,
            v3_1_4
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

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.6",
        operations = ["CreateFilePaymentConsent", "CreateFilePayment"],
        apis = ["file-payments"]
    )
    @Test
    fun shouldCreateCSVFilePaymentFPS_v3_1_6() {
        // Given
        val file = getFile(CSVFilePaymentType.UK_LBG_FPS_BATCH_V10)
        val numberOfTransactions = file.headerIndicatorSection.numCredits
        val controlSum = file.headerIndicatorSection.valueCreditsSum
        val fileHash = PaymentFactory.computeSHA256FullHash(file.toString())

        val consent = PaymentRS().consent<OBWriteFileConsentResponse4>(
            payment3_1_6.Links.links.CreateFilePaymentConsent,
            filePaymentConsentWithAllFields(
                CSVFilePaymentType.UK_LBG_FPS_BATCH_V10.fileType,
                fileHash,
                numberOfTransactions.toString(),
                controlSum.toString()
            ),
            tppResource.tpp,
            v3_1_6
        )
        // when
        val submissionFileResp = PaymentRS().submitCSVFilePayment(
            payment3_1_6.Links.links.CreateFilePaymentConsent + "/" + consent.data.consentId + "/file",
            file.toString(),
            CSVFilePaymentType.UK_LBG_FPS_BATCH_V10.contentType,
            tppResource.tpp,
            v3_1_6
        )
        // then
        assertThat(submissionFileResp.statusCode).isEqualTo(200)
        assertThat(submissionFileResp.responseMessage).isEqualTo("OK")

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
                .initiation(consent.data.initiation)
        )

        val submissionResp = PaymentRS().submitPayment<OBWriteFileResponse3>(
            payment3_1_6.Links.links.CreateFilePayment,
            paymentSubmissionRequest,
            accesstokenAuthorizationCode,
            tppResource.tpp,
            v3_1_6
        )
        // When
        val paymentResult = PaymentRS().getPayment<OBWriteFileResponse3>(
            payment3_1_6.Links.links.CreateFilePayment,
            submissionResp.data.filePaymentId,
            accesstokenClientCredentials,
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
        apiVersion = "v3.1.2",
        operations = ["CreateFilePaymentConsent", "CreateFilePayment"],
        apis = ["file-payments"]
    )
    @Test
    fun shouldCreateCSVFilePaymentBULK_v3_1_2() {
        // Given
        val file = getFile(CSVFilePaymentType.UK_LBG_BACS_BULK_V10)
        val numberOfTransactions = file.headerIndicatorSection.numCredits
        val controlSum = file.headerIndicatorSection.valueCreditsSum
        val fileHash = PaymentFactory.computeSHA256FullHash(file.toString())
        val consent = PaymentRS().consent<OBWriteFileConsentResponse2>(
            payment3_1_2.Links.links.CreateFilePaymentConsent,
            filePaymentConsentWithAllFields(
                CSVFilePaymentType.UK_LBG_BACS_BULK_V10.fileType,
                fileHash,
                numberOfTransactions.toString(),
                controlSum.toString()
            ),
            tppResource.tpp
        )
        // when
        val submissionFileResp = PaymentRS().submitCSVFilePayment(
            payment3_1_2.Links.links.CreateFilePaymentConsent + "/" + consent.data.consentId + "/file",
            file.toString(),
            CSVFilePaymentType.UK_LBG_BACS_BULK_V10.contentType,
            tppResource.tpp
        )
        // then
        assertThat(submissionFileResp.statusCode).isEqualTo(200)
        assertThat(submissionFileResp.responseMessage).isEqualTo("OK")

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
            payment3_1_2.Links.links.CreateFilePayment,
            paymentSubmissionRequest,
            accesstokenAuthorizationCode,
            tppResource.tpp
        )

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteFileResponse2>(
            payment3_1_2.Links.links.CreateFilePayment,
            submissionResp.data.filePaymentId,
            accesstokenClientCredentials
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
    fun shouldCreateCSVFilePaymentBULK_v3_1_6() {
        // Given
        val file = getFile(CSVFilePaymentType.UK_LBG_BACS_BULK_V10)
        val numberOfTransactions = file.headerIndicatorSection.numCredits
        val controlSum = file.headerIndicatorSection.valueCreditsSum
        val fileHash = PaymentFactory.computeSHA256FullHash(file.toString())
        val consent = PaymentRS().consent<OBWriteFileConsentResponse4>(
            payment3_1_6.Links.links.CreateFilePaymentConsent,
            filePaymentConsentWithAllFields(
                CSVFilePaymentType.UK_LBG_BACS_BULK_V10.fileType,
                fileHash,
                numberOfTransactions.toString(),
                controlSum.toString()
            ),
            tppResource.tpp,
            v3_1_6
        )
        // when
        val submissionFileResp = PaymentRS().submitCSVFilePayment(
            payment3_1_6.Links.links.CreateFilePaymentConsent + "/" + consent.data.consentId + "/file",
            file.toString(),
            CSVFilePaymentType.UK_LBG_BACS_BULK_V10.contentType,
            tppResource.tpp,
            v3_1_6
        )
        // then
        assertThat(submissionFileResp.statusCode).isEqualTo(200)
        assertThat(submissionFileResp.responseMessage).isEqualTo("OK")

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
                .initiation(consent.data.initiation)
        )

        val submissionResp = PaymentRS().submitPayment<OBWriteFileResponse3>(
            payment3_1_6.Links.links.CreateFilePayment,
            paymentSubmissionRequest,
            accesstokenAuthorizationCode,
            tppResource.tpp,
            v3_1_6
        )

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteFileResponse3>(
            payment3_1_6.Links.links.CreateFilePayment,
            submissionResp.data.filePaymentId,
            accesstokenClientCredentials,
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

    @Throws(OBErrorException::class)
    private fun getFile(filePaymentType: CSVFilePaymentType): CSVFilePayment {
        val file = CSVFilePaymentFactory.create(filePaymentType)
        file.setHeaderIndicator(
            CSVHeaderIndicatorSection.builder()
                .headerIndicator(CSVHeaderIndicatorSection.HEADER_IND_EXPECTED)
                .fileCreationDate(file.dateTimeFormatter.format(LocalDate.now()))
                .uniqueId("ID001")
                .numCredits(1)
                .valueCreditsSum(BigDecimal(10.10).setScale(2, RoundingMode.CEILING))
                .build()
        )
        file.setDebitIndicator(
            CSVDebitIndicatorSection.builder()
                .debitIndicator(CSVDebitIndicatorSection.DEBIT_IND_EXPECTED)
                .paymentDate(file.dateTimeFormatter.format(LocalDate.now().plusDays(2)))
                .batchReference("Payments")
                .debitAccountDetails("301775-12345678")
                .build()
        )
        val row: MutableList<CSVCreditIndicatorRow> = ArrayList()
        row.add(
            CSVCreditIndicatorRow.builder()
                .creditIndicator(CSVCreditIndicatorRow.CREDIT_IND_EXPECTED)
                .recipientName("Beneficiary name")
                .accNumber("12345678")
                .recipientSortCode("301763")
                .reference("Beneficiary ref.")
                .debitAmount(BigDecimal(10.10).setScale(2, RoundingMode.CEILING))
                .paymentASAP(CSVValidation.PAYMENT_ASAP_VALUES[0])
                .paymentDate("")
                .eToEReference("EtoEReference")
                .build()
        )
        file.creditIndicatorRows = row
        return file
    }

}
