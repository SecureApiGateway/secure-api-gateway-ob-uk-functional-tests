package com.forgerock.sapi.gateway.framework.configuration

import com.forgerock.sapi.gateway.ob.uk.support.registerPSU
import com.forgerock.sapi.gateway.ob.uk.support.registration.UserRegistrationRequest

val PLATFORM_SERVER = System.getenv("platformServer") ?: "https://iam.dev.forgerock.financial"
val IG_SERVER = System.getenv("igServer") ?: "https://sapig.dev.forgerock.financial"
// mtls is a subdomain of the gateway domain
val MTLS_SERVER = IG_SERVER.replace("https://", "https://mtls.")

val TRUSTSTORE_PATH = System.getenv("truststorePath") ?: "/com/forgerock/sapi/gateway/ob/uk/truststore.jks"
val TRUSTSTORE_PASSWORD = System.getenv("truststorePassword") ?: "changeit"

val PSU_USER_ID = System.getenv("userId") ?: "4737f9f9-fa0a-4159-bc61-7da31542e624"
val PSU_PASSWORD = System.getenv("userPassword") ?: "password"
val PSU_USERNAME = System.getenv("username") ?: "username"
// The value must match the Identification field for an Account owned by the PSU
val PSU_DEBTOR_ACCOUNT_IDENTIFICATION = System.getenv("userDebtorAccountIdentification") ?: "01233243245676"
val USER_ACCOUNT_ID = System.getenv("userAccountId") ?: "01233243245676"

val OB_TPP_OB_EIDAS_TEST_SIGNING_KID =
    System.getenv("eidasTestSigningKid") ?: "2yNjPOCjpO8rcKg6_lVtWzAQR0U"
val OB_TPP_PRE_EIDAS_SIGNING_KID = System.getenv("preEidasTestSigningKid") ?: "RmQ-EmViYPKXYyGCVnfuMo6ggXE"

val AM_COOKIE_NAME = System.getenv("amCookieName") ?: "iPlanetDirectoryPro"

val psu: UserRegistrationRequest by lazy { registerPSU() }

// certificates
val OB_TPP_EIDAS_SIGNING_KEY_PATH = System.getenv("eidasOBSealKey") ?: "./certs/OBSeal.key"
val OB_TPP_EIDAS_SIGNING_PEM_PATH = System.getenv("eidasOBSealPem") ?: "./certs/OBSeal.pem"
val OB_TPP_EIDAS_TRANSPORT_KEY_PATH = System.getenv("eidasOBWacKey") ?: "./certs/OBWac.key"
val OB_TPP_EIDAS_TRANSPORT_PEM_PATH = System.getenv("eidasOBWacPem") ?: "./certs/OBWac.pem"

// ISS claim values (expected from client cert)
val COMMON_NAME = System.getenv("commonName") ?: "0015800001041REAAY"
val ORGANIZATION_IDENTIFIER = System.getenv("organizationIdentifier") ?: "PSDGB-OB-Unknown0015800001041REAAY"
val ORGANIZATION = System.getenv("organization") ?: "FORGEROCK LIMITED"
val COUNTRY = System.getenv("country") ?: "GB"

val ISS_CLAIM_VALUE = "CN=${System.getenv("commonName") ?: "0015800001041REAAY"}," +
        "organizationIdentifier=${System.getenv("organizationIdentifier") ?: "PSDGB-OB-Unknown0015800001041REAAY"}," +
        "O=${System.getenv("organization") ?: "FORGEROCK LIMITED"}," +
        "C=${System.getenv("country") ?: "GB"}"

val REDIRECT_URI = System.getenv("redirectUri") ?: "https://www.google.co.uk"
