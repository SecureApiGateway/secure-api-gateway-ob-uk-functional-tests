package com.forgerock.sapi.gateway.framework.configuration

import com.forgerock.sapi.gateway.ob.uk.support.registerPSU
import com.forgerock.sapi.gateway.ob.uk.support.registration.UserRegistrationRequest
import java.lang.Boolean

val IG_SERVER = System.getenv("igServer") ?: "https://sapig.dev.forgerock.financial"
// mtls is a subdomain of the gateway domain
val MTLS_SERVER = IG_SERVER.replace("https://", "https://mtls.")

val RCS_DECISION_API_URI = "$IG_SERVER/rcs/api/consent/decision"

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

val ISS_CLAIM_VALUE = System.getenv("obOrganisationId") + "/" + System.getenv("obSoftwareId")

val REDIRECT_URI = System.getenv("redirectUri") ?: "https://www.google.co.uk"

const val AUTH_METHOD_TLS_CLIENT = "tls_client_auth"
const val AUTH_METHOD_PRIVATE_KEY_JWT = "private_key_jwt"
val CLIENT_AUTH_METHOD = System.getenv("clientAuthMethod") ?: AUTH_METHOD_PRIVATE_KEY_JWT

// Fully qualified class names of the factories to use to create OB schema objects
val OBDomesticVRPConsentRequestFactoryClass = System.getenv("OBDomesticVRPConsentRequestFactoryClass") ?: "com.forgerock.sapi.gateway.ob.uk.framework.consent.payment.DefaultOBDomesticVRPConsentRequestFactory"
val OBWriteDomesticConsent4FactoryClass = System.getenv("OBWriteDomesticConsent4FactoryClass") ?: "com.forgerock.sapi.gateway.ob.uk.framework.consent.payment.DefaultOBWriteDomesticConsent4Factory"
val OBWriteDomesticScheduledConsent4Class = System.getenv("OBWriteDomesticScheduledConsent4Class") ?: "com.forgerock.sapi.gateway.ob.uk.framework.consent.payment.DefaultOBWriteDomesticScheduledConsent4Factory"
val OBWriteDomesticStandingOrderConsent5FactoryClass = System.getenv("OBWriteDomesticStandingOrderConsent5Factory") ?: "com.forgerock.sapi.gateway.ob.uk.framework.consent.payment.DefaultOBWriteDomesticStandingOrderConsent5Factory"
val OBWriteInternationalConsent5FactoryClass = System.getenv("OBWriteInternationalConsent5Factory") ?: "com.forgerock.sapi.gateway.ob.uk.framework.consent.payment.DefaultOBWriteInternationalConsent5Factory"
val OBWriteInternationalScheduledConsent5FactoryClass = System.getenv("OBWriteInternationalScheduledConsent5Factory") ?: "com.forgerock.sapi.gateway.ob.uk.framework.consent.payment.DefaultOBWriteInternationalScheduledConsent5Factory"
val OBWriteInternationalStandingOrderConsent6FactoryClass = System.getenv("OBWriteInternationalStandingOrderConsent6Factory") ?: "com.forgerock.sapi.gateway.ob.uk.framework.consent.payment.DefaultOBWriteInternationalStandingOrderConsent6Factory"

// Controls whether the OBRisk1.paymentContextCode field is always set or not in the consents created by the factories, defaults to false.
val requirePaymentContextCode = Boolean.parseBoolean(System.getenv("requirePaymentContextCode"))

