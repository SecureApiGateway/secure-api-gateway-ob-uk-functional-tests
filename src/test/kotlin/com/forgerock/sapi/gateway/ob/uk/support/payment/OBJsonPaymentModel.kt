package com.forgerock.sapi.gateway.ob.uk.support.payment

import com.fasterxml.jackson.annotation.*
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.module.kotlin.*

val mapper = jacksonObjectMapper().apply {
    propertyNamingStrategy = PropertyNamingStrategy.LOWER_CAMEL_CASE
    setSerializationInclusion(JsonInclude.Include.NON_NULL)
}

data class JsonFilePayment (
    @get:JsonProperty("Data", required=true)@field:JsonProperty("Data", required=true)
    val data: Data
) {
    fun toJson() = mapper.writeValueAsString(this)

    companion object {
        fun fromJson(json: String) = mapper.readValue<JsonFilePayment>(json)
    }
}

data class Data (
    @get:JsonProperty("DomesticPayments", required=true)@field:JsonProperty("DomesticPayments", required=true)
    val domesticPayments: List<DomesticPayment>
)

data class DomesticPayment (
    @get:JsonProperty("InstructionIdentification", required=true)@field:JsonProperty("InstructionIdentification", required=true)
    val instructionIdentification: String,

    @get:JsonProperty("EndToEndIdentification", required=true)@field:JsonProperty("EndToEndIdentification", required=true)
    val endToEndIdentification: String,

    @get:JsonProperty("LocalInstrument")@field:JsonProperty("LocalInstrument")
    val localInstrument: String? = null,

    @get:JsonProperty("InstructedAmount", required=true)@field:JsonProperty("InstructedAmount", required=true)
    val instructedAmount: InstructedAmount,

    @get:JsonProperty("DebtorAccount", required=true)@field:JsonProperty("DebtorAccount", required=true)
    val debtorAccount: DebtorAccount,

    @get:JsonProperty("CreditorAccount", required=true)@field:JsonProperty("CreditorAccount", required=true)
    val creditorAccount: DebtorAccount,

    @get:JsonProperty("CreditorPostalAddress")@field:JsonProperty("CreditorPostalAddress")
    val creditorPostalAddress: CreditorPostalAddress? = null,

    @get:JsonProperty("RemittanceInformation", required=true)@field:JsonProperty("RemittanceInformation", required=true)
    val remittanceInformation: RemittanceInformation
)

data class DebtorAccount (
    @get:JsonProperty("SchemeName", required=true)@field:JsonProperty("SchemeName", required=true)
    val schemeName: String,

    @get:JsonProperty("Identification", required=true)@field:JsonProperty("Identification", required=true)
    val identification: String,

    @get:JsonProperty("Name", required=true)@field:JsonProperty("Name", required=true)
    val name: String
)

data class CreditorPostalAddress (
    @get:JsonProperty("AddressType", required=true)@field:JsonProperty("AddressType", required=true)
    val addressType: String,

    @get:JsonProperty("StreetName", required=true)@field:JsonProperty("StreetName", required=true)
    val streetName: String,

    @get:JsonProperty("BuildingNumber", required=true)@field:JsonProperty("BuildingNumber", required=true)
    val buildingNumber: String,

    @get:JsonProperty("PostCode", required=true)@field:JsonProperty("PostCode", required=true)
    val postCode: String,

    @get:JsonProperty("TownName", required=true)@field:JsonProperty("TownName", required=true)
    val townName: String,

    @get:JsonProperty("Country", required=true)@field:JsonProperty("Country", required=true)
    val country: String
)

data class InstructedAmount (
    @get:JsonProperty("Amount", required=true)@field:JsonProperty("Amount", required=true)
    val amount: String,

    @get:JsonProperty("Currency", required=true)@field:JsonProperty("Currency", required=true)
    val currency: String
)

data class RemittanceInformation (
    @get:JsonProperty("Reference", required=true)@field:JsonProperty("Reference", required=true)
    val reference: String,

    @get:JsonProperty("Unstructured", required=true)@field:JsonProperty("Unstructured", required=true)
    val unstructured: String
)
