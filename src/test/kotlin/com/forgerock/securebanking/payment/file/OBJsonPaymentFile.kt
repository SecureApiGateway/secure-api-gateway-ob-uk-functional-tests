package com.forgerock.securebanking.payment.file;

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid
import javax.validation.constraints.NotNull

@JsonInclude(JsonInclude.Include.NON_EMPTY)
class OBJsonPaymentFile {

    @JsonProperty("Data")
    @NotNull
    @Valid
    lateinit var data: OBWriteJsonPaymentFile

    fun data(data: OBWriteJsonPaymentFile): OBJsonPaymentFile {
        this.data = data
        return this
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private fun toIndentedString(o: Any?): String {
        return o?.toString()?.replace("\n", "\n    ") ?: "null"
    }

    fun getNumberOfTransactions(): String {
        return data.domesticPayments.size.toString()
    }

    fun getControlSum(): String {
        return this.data.getTotalInstructedAmount()
    }
}
