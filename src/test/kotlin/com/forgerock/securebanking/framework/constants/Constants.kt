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

const val SCOPES_TPP = "ASPSPReadAccess TPPReadAccess AuthoritiesReadAccess"
const val SCOPES_ASPSP = "ASPSPReadAccess TPPReadAll AuthoritiesReadAccess"


