package com.forgerock.securebanking.tests.functional.payment.file;

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import uk.org.openbanking.datamodel.payment.OBActiveOrHistoricCurrencyAndAmount
import uk.org.openbanking.datamodel.payment.OBDomestic2
import java.math.BigDecimal
import javax.validation.Valid
import javax.validation.constraints.NotNull

@JsonInclude(JsonInclude.Include.NON_EMPTY)
class OBWriteJsonPaymentFile {
    @JsonProperty("DomesticPayments")
    @NotNull
    @Valid
    lateinit var domesticPayments: List<OBDomestic2>

    fun domesticPayments(domesticPayments: List<OBDomestic2>): OBWriteJsonPaymentFile {
        this.domesticPayments = domesticPayments
        return this
    }

    fun getTotalInstructedAmount(): String {
        return domesticPayments.stream().map(OBDomestic2::getInstructedAmount)
            .map(OBActiveOrHistoricCurrencyAndAmount::getAmount)
            .map(String::toBigDecimal)
            .reduce(BigDecimal.ZERO, BigDecimal::add).toPlainString()
    }

}
