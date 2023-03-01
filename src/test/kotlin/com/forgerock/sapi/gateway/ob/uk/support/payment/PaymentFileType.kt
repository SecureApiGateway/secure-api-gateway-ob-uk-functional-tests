package com.forgerock.sapi.gateway.ob.uk.support.payment

enum class PaymentFileType(var type: String, var mediaType: String, var description: String) {
    UK_OBIE_PAYMENT_INITIATION_V3_0("UK.OBIE.PaymentInitiation.3.0", "application/json", "Json file version 3.0"),
    UK_OBIE_PAYMENT_INITIATION_V3_1("UK.OBIE.PaymentInitiation.3.1", "application/json", "Json file version 3.1"),
    UK_OBIE_PAIN_001_001_008("UK.OBIE.pain.001.001.08", "text/xml", "xml file ISO pain.001.001.08")
}
