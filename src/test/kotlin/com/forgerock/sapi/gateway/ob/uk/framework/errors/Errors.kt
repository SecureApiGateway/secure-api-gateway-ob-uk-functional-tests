package com.forgerock.sapi.gateway.ob.uk.framework.errors

//ERROR MESSAGES
const val SIGNATURE_VALIDATION_FAILED = "Signature validation failed"
const val NO_DETACHED_JWS = "No detached signature header on inbound request"
const val INVALID_FORMAT_DETACHED_JWS_ERROR = "Wrong number of dots on inbound detached signature"
const val PAYMENT_SUBMISSION_ALREADY_EXISTS = "Payment submission already exists."
const val B64_HEADER_NOT_PERMITTED = "B64 header not permitted in JWT header after v3.1.3"
const val UNAUTHORIZED = "Unauthorized"
const val INVALID_CONSENT_STATUS = "UK.OBIE.Resource.InvalidConsentStatus"
const val PAYMENT_ACTION_FOR_AUTHORISED_CONSENT_ERROR = "Action can only be performed on consents with status: Authorised. Currently, the consent is: Consumed"
const val INVALID_DETACHED_JWS_ERROR = "Could not validate detached JWS -"
const val INVALID_FREQUENCY_VALUE = "Invalid frequency value in the request payload."
const val REQUEST_EXECUTION_TIME_IN_THE_PAST = "Invalid RequestedExecutionDateTime value in the request payload."
const val REQUESTED_EXECUTION_TIME_MUST_BE_IN_FUTURE = "Your data request is invalid: reason RequestedExecutionDateTime must be in the future"
const val LOCATION_HEADER_ERROR = "Location header contains an error"
const val LOCATION_HEADER_NOT_EXISTS = "Location header doesn't exist"
const val CONSENT_NOT_AUTHORISED = "Resource Owner did not authorize the request"
const val BAD_REQUEST = "Bad Request"
const val INVALID_RISK = "Payment invalid. Payment risk received doesn't match the risk payment request"

