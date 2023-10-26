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
        // When
        val consentResponse = createFilePaymentsConsentsApi.createFilePaymentConsent(consentRequest)
        // Then
        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)
    }

    fun shouldGetFilePaymentsConsents_withoutOptionalDebtorAccountTest() {
        // Given
        val fileContent = PaymentFactory.getFileAsString(PaymentFactory.FilePaths.XML_FILE_PATH)
        val consentRequest = PaymentFactory.createOBWriteFileConsent3WithFileInfo(
            fileContent,
            PaymentFileType.UK_OBIE_PAIN_001_001_008.type
        )
        consentRequest.data.initiation.debtorAccount(null)
        // when
        val consentResponse = createFilePaymentsConsentsApi.createFilePaymentConsent(consentRequest)
        // Then
        assertThat(consentResponse).isNotNull()
        assertThat(consentResponse.data).isNotNull()
        assertThat(consentResponse.data.consentId).isNotEmpty()
        Assertions.assertThat(consentResponse.data.status.toString()).`is`(Status.consentCondition)
        assertThat(consentResponse.data.initiation.debtorAccount).isNull()
    }

}