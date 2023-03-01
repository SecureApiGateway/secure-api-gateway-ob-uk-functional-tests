package com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.file.payments.api.v3_1_8

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import com.forgerock.sapi.gateway.framework.conditions.Status
import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.support.payment.PaymentFactory
import com.forgerock.sapi.gateway.ob.uk.support.payment.PaymentFileType
import com.forgerock.sapi.gateway.ob.uk.tests.functional.payment.file.payments.consents.api.v3_1_8.CreateFilePaymentsConsents
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import org.assertj.core.api.Assertions

class GetFilePaymentsConsents(val version: OBVersion, val tppResource: CreateTppCallback.TppResource) {

    private val createFilePaymentsConsentsApi = CreateFilePaymentsConsents(version, tppResource)

    fun shouldGetFilePaymentsConsents() {
        // Given
        val fileContent = PaymentFactory.getFileAsString(PaymentFactory.FilePaths.XML_FILE_PATH)
        val consentRequest = PaymentFactory.createOBWriteFileConsent3WithFileInfo(
            fileContent,
            PaymentFileType.UK_OBIE_PAIN_001_001_008.type
        )
        val consent = createFilePaymentsConsentsApi.createFilePaymentConsent(consentRequest)

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)

        // When
        val result = createFilePaymentsConsentsApi.getPatchedConsent(consent)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data).isEqualTo(consent.data)
        assertThat(result.data.initiation.fileHash).isEqualTo(consent.data.initiation.fileHash)
        assertThat(result.data.initiation.fileReference).isEqualTo(consent.data.initiation.fileReference)
        assertThat(result.data.initiation.fileType).isEqualTo(consent.data.initiation.fileType)
    }

    fun shouldGetFilePaymentsConsents_withoutOptionalDebtorAccountTest() {
        // Given
        val fileContent = PaymentFactory.getFileAsString(PaymentFactory.FilePaths.XML_FILE_PATH)
        val consentRequest = PaymentFactory.createOBWriteFileConsent3WithFileInfo(
            fileContent,
            PaymentFileType.UK_OBIE_PAIN_001_001_008.type
        )
        consentRequest.data.initiation.debtorAccount(null)

        val consent = createFilePaymentsConsentsApi.createFilePaymentConsent(consentRequest)

        assertThat(consent).isNotNull()
        assertThat(consent.data).isNotNull()
        assertThat(consent.data.consentId).isNotEmpty()
        Assertions.assertThat(consent.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consent.data.initiation.debtorAccount).isNull()

        // When
        val result = createFilePaymentsConsentsApi.getPatchedConsent(consent)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.data).isNotNull()
        assertThat(result.data).isEqualTo(consent.data)
        assertThat(result.data.initiation.debtorAccount).isNull()
    }

}