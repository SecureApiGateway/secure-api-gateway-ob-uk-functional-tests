package com.forgerock.uk.openbanking.support.payment

import com.fasterxml.jackson.annotation.JsonProperty

data class PaymentPayloadDeprecated(
    @JsonProperty("Data")
    val Data: Any,
    @JsonProperty("Risk")
    val Risk: Any
)