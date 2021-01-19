package com.forgerock.openbanking

import com.forgerock.openbanking.directory.UserRegistrationRequest

val DOMAIN = System.getenv("DOMAIN") ?: "master.forgerock.financial"


val psu: UserRegistrationRequest by lazy { registerPSU() }
val directoryUser: UserRegistrationRequest by lazy { registerDirectoryUser() }

const val OB_SOFTWARE_ID = "ebSqTNqmQXFYz6VtWGXZAa"
const val OB_ORGANISATION_ID = "0015800001041REAAY"
const val REDIRECT_URI = "https://google.com"

// Eidas cert related config, i.e. OBWac and OBSeal related config
const val OB_TPP_OB_EIDAS_TEST_SIGNING_KID = "nzCvFqGZpsJNeqWNQc6sfk1KYq0"
const val OB_TPP_EIDAS_SIGNING_KEY = "/com/forgerock/openbanking/ob-eidas/obseal.key"
const val OB_TPP_EIDAS_TRANSPORT_KEY_PATH = "/com/forgerock/openbanking/ob-eidas/obwac.key"
const val OB_TPP_EIDAS_TRANSPORT_PEM_PATH = "/com/forgerock/openbanking/ob-eidas/obwac.pem"
const val OB_TPP_EIDAS_SSA_PATH = "/com/forgerock/openbanking/ob-eidas/ssa.jwt"


// Pre-eIDAS certs related config i.e. OBTransport and OBSigning key config
const val OB_TPP_PRE_EIDAS_SIGNING_KID = "RmQ-EmViYPKXYyGCVnfuMo6ggXE"
const val OB_TPP_PRE_EIDAS_SIGNING_KEY = "/com/forgerock/openbanking/ob-pre-eidas/signing.key"
const val OB_TPP_PRE_EIDAS_TRANSPORT_KEY_PATH = "/com/forgerock/openbanking/ob-pre-eidas/transport.key"
const val OB_TPP_PRE_EIDAS_TRANSPORT_PEM_PATH = "/com/forgerock/openbanking/ob-pre-eidas/transport.pem"
const val OB_TPP_PRE_EIDAS_SSA_PATH = "/com/forgerock/openbanking/ob-pre-eidas/ssa.jwt"