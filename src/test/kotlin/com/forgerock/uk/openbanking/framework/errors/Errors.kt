package com.forgerock.uk.openbanking.framework.errors

//ERROR MESSAGES
const val SIGNATURE_VALIDATION_FAILED = "Signature validation failed"
const val NO_DETACHED_JWS = "No detached signature header on inbound request"
const val INVALID_FORMAT_DETACHED_JWS_ERROR = "Wrong number of dots on inbound detached signature"
const val PAYMENT_SUBMISSION_ALREADY_EXISTS = "Payment submission already exists."
const val B64_HEADER_NOT_PERMITTED = "B64 header not permitted in JWT header after v3.1.3"
const val UNAUTHORIZED = "Unauthorized"
const val INVALID_DETACHED_JWS_ERROR = "Could not validate detached JWS -"
