package com.forgerock.securebanking.framework.constants

const val OB_SOFTWARE_ID = "ebSqTNqmQXFYz6VtWGXZAa"
const val OB_ORGANISATION_ID = "0015800001041REAAY"
const val REDIRECT_URI = "https://google.com"

// Eidas cert related config, i.e. OBWac and OBSeal related config
const val OB_TPP_EIDAS_SIGNING_KEY = "/com/forgerock/securebanking/ob-eidas/obseal.key"
const val OB_TPP_EIDAS_TRANSPORT_KEY_PATH = "/com/forgerock/securebanking/ob-eidas/obwac.key"
const val OB_TPP_EIDAS_TRANSPORT_PEM_PATH = "/com/forgerock/securebanking/ob-eidas/obwac.pem"
const val OB_TPP_EIDAS_SSA_PATH = "/com/forgerock/securebanking/ob-eidas/ssa.jwt"
const val TRUSTSTORE_PATH = "/com/forgerock/securebanking/truststore.jks"
const val TRUSTSTORE_PASSWORD = "changeit"


// Pre-eIDAS certs related config i.e. OBTransport and OBSigning key config
const val OB_TPP_PRE_EIDAS_SIGNING_KEY = "/com/forgerock/securebanking/ob-pre-eidas/signing.key"
const val OB_TPP_PRE_EIDAS_TRANSPORT_KEY_PATH = "/com/forgerock/securebanking/ob-pre-eidas/transport.key"
const val OB_TPP_PRE_EIDAS_TRANSPORT_PEM_PATH = "/com/forgerock/securebanking/ob-pre-eidas/transport.pem"
const val OB_TPP_PRE_EIDAS_SSA_PATH = "/com/forgerock/securebanking/ob-pre-eidas/ssa.jwt"


//Detached jws constants
const val TAN = "openbanking.org.uk"
const val ISS_CLAIM_VALUE =
    "CN=0015800001041REAAY,organizationIdentifier=PSDGB-OB-Unknown0015800001041REAAY,O=FORGEROCK LIMITED,C=GB"
const val INVALID_CONSENT_ID = "InvalidConsentId"
const val INVALID_FORMAT_DETACHED_JWS = "invalid-format-jws"
const val INVALID_SIGNING_KID = "invalid-signing-kid"

//ERROR MESSAGES
const val SIGNATURE_VALIDATION_FAILED = "Signature validation failed"
const val NO_DETACHED_JWS = "No detached signature header on inbound request"
const val INVALID_FORMAT_DETACHED_JWS_ERROR = "Wrong number of dots on inbound detached signature"
const val PAYMENT_SUBMISSION_ALREADY_EXISTS = "Payment submission already exists."
const val B64_HEADER_NOT_PERMITTED = "B64 header not permitted in JWT header after v3.1.3"
const val UNAUTHORIZED = "Unauthorized"
const val INVALID_DETACHED_JWS_ERROR = "Could not validate detached JWS -"


