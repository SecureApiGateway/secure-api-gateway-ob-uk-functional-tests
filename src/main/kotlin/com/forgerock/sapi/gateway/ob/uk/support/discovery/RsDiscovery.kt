package com.forgerock.sapi.gateway.ob.uk.support.discovery

import com.forgerock.sapi.gateway.framework.configuration.MTLS_SERVER
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.isSuccessful
import com.github.kittinunf.fuel.gson.gsonDeserializer
import uk.org.openbanking.datamodel.common.OBExternalPermissions1Code
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberProperties

val rsDiscovery by lazy { getRsConfiguration() }

val accountAndTransaction3_1 by lazy {
    rsDiscovery.Data.AccountAndTransactionAPI?.first { it.Version == "v3.1" }!!
}

val accountAndTransaction3_1_1 by lazy {
    rsDiscovery.Data.AccountAndTransactionAPI?.first { it.Version == ("v3.1.1") }!!
}

val accountAndTransaction3_1_2 by lazy {
    rsDiscovery.Data.AccountAndTransactionAPI?.first { it.Version == ("v3.1.2") }!!
}

val accountAndTransaction3_1_3 by lazy {
    rsDiscovery.Data.AccountAndTransactionAPI?.first { it.Version == ("v3.1.3") }!!
}

val accountAndTransaction3_1_4 by lazy {
    rsDiscovery.Data.AccountAndTransactionAPI?.first { it.Version == ("v3.1.4") }!!
}

val accountAndTransaction3_1_5 by lazy {
    rsDiscovery.Data.AccountAndTransactionAPI?.first { it.Version == ("v3.1.5") }!!
}

val accountAndTransaction3_1_6 by lazy {
    rsDiscovery.Data.AccountAndTransactionAPI?.first { it.Version == ("v3.1.6") }!!
}

val accountAndTransaction3_1_7 by lazy {
    rsDiscovery.Data.AccountAndTransactionAPI?.first { it.Version == ("v3.1.7") }!!
}

val accountAndTransaction3_1_8 by lazy {
    rsDiscovery.Data.AccountAndTransactionAPI?.first { it.Version == ("v3.1.8") }!!
}

val payment3_1 by lazy {
    rsDiscovery.Data.PaymentInitiationAPI?.first { it.Version == ("v3.1") }!!
}

val payment3_1_1 by lazy {
    rsDiscovery.Data.PaymentInitiationAPI?.first { it.Version == ("v3.1.1") }!!
}

val payment3_1_2 by lazy {
    rsDiscovery.Data.PaymentInitiationAPI?.first { it.Version == ("v3.1.2") }!!
}

val payment3_1_3 by lazy {
    rsDiscovery.Data.PaymentInitiationAPI?.first { it.Version == ("v3.1.3") }!!
}

val payment3_1_4 by lazy {
    rsDiscovery.Data.PaymentInitiationAPI?.first { it.Version == ("v3.1.4") }!!
}

val payment3_1_5 by lazy {
    rsDiscovery.Data.PaymentInitiationAPI?.first { it.Version == ("v3.1.5") }!!
}

val payment3_1_6 by lazy {
    rsDiscovery.Data.PaymentInitiationAPI?.first { it.Version == ("v3.1.6") }!!
}

val payment3_1_7 by lazy {
    rsDiscovery.Data.PaymentInitiationAPI?.first { it.Version == ("v3.1.7") }!!
}

val payment3_1_8 by lazy {
    rsDiscovery.Data.PaymentInitiationAPI?.first { it.Version == ("v3.1.8") }!!
}

val accountPermissions by lazy {
    // TODO - limit permission to those enabled on cluster
    OBExternalPermissions1Code.values().asList()
}

// Events notification
val eventsNotification3_0 by lazy {
    rsDiscovery.Data.EventNotificationAPI?.first { it.Version == ("v3.0") }!!
}

val eventsNotification3_1 by lazy {
    rsDiscovery.Data.EventNotificationAPI?.first { it.Version == ("v3.1") }!!
}

val eventsNotification3_1_1 by lazy {
    rsDiscovery.Data.EventNotificationAPI?.first { it.Version == ("v3.1.1") }!!
}

val eventsNotification3_1_2 by lazy {
    rsDiscovery.Data.EventNotificationAPI?.first { it.Version == ("v3.1.2") }!!
}

val eventsNotification3_1_3 by lazy {
    rsDiscovery.Data.EventNotificationAPI?.first { it.Version == ("v3.1.3") }!!
}

val eventsNotification3_1_4 by lazy {
    rsDiscovery.Data.EventNotificationAPI?.first { it.Version == ("v3.1.4") }!!
}

val eventsNotification3_1_5 by lazy {
    rsDiscovery.Data.EventNotificationAPI?.first { it.Version == ("v3.1.5") }!!
}

val eventsNotification3_1_6 by lazy {
    rsDiscovery.Data.EventNotificationAPI?.first { it.Version == ("v3.1.6") }!!
}

val eventsNotification3_1_7 by lazy {
    rsDiscovery.Data.EventNotificationAPI?.first { it.Version == ("v3.1.7") }!!
}

val eventsNotification3_1_8 by lazy {
    rsDiscovery.Data.EventNotificationAPI?.first { it.Version == ("v3.1.8") }!!
}

// Funds confirmations
val fundsConfirmations3_0 by lazy {
    rsDiscovery.Data.FundsConfirmationAPI?.first { it.Version == ("v3.0") }!!
}

val fundsConfirmations3_1 by lazy {
    rsDiscovery.Data.FundsConfirmationAPI?.first { it.Version == ("v3.1") }!!
}

val fundsConfirmations3_1_1 by lazy {
    rsDiscovery.Data.FundsConfirmationAPI?.first { it.Version == ("v3.1.1") }!!
}

val fundsConfirmations3_1_2 by lazy {
    rsDiscovery.Data.FundsConfirmationAPI?.first { it.Version == ("v3.1.2") }!!
}

val fundsConfirmations3_1_3 by lazy {
    rsDiscovery.Data.FundsConfirmationAPI?.first { it.Version == ("v3.1.3") }!!
}

val fundsConfirmations3_1_4 by lazy {
    rsDiscovery.Data.FundsConfirmationAPI?.first { it.Version == ("v3.1.4") }!!
}

val fundsConfirmations3_1_5 by lazy {
    rsDiscovery.Data.FundsConfirmationAPI?.first { it.Version == ("v3.1.5") }!!
}

val fundsConfirmations3_1_6 by lazy {
    rsDiscovery.Data.FundsConfirmationAPI?.first { it.Version == ("v3.1.6") }!!
}

val fundsConfirmations3_1_7 by lazy {
    rsDiscovery.Data.FundsConfirmationAPI?.first { it.Version == ("v3.1.7") }!!
}

val fundsConfirmations3_1_8 by lazy {
    rsDiscovery.Data.FundsConfirmationAPI?.first { it.Version == ("v3.1.8") }!!
}

val rsDiscoveryMap by lazy {
    val paymentVersions = rsDiscovery.Data.PaymentInitiationAPI?.map { it.Version to it.Links.linkValues }?.toList()
    val accounts = rsDiscovery.Data.AccountAndTransactionAPI?.map { it.Version to it.Links.linkValues }?.toList()
    val funds = rsDiscovery.Data.FundsConfirmationAPI?.map { it.Version to it.Links.linkValues }?.toList()
    val events = rsDiscovery.Data.EventNotificationAPI?.map { it.Version to it.Links.linkValues }?.toList()
    return@lazy mapOf(
        "payments" to paymentVersions,
        "accounts" to accounts,
        "funds" to funds,
        "events" to events
    )
}

// Maps apiName -> version -> links
val rsDiscoveryApiToVersionLinks by lazy {
    val paymentVersions = rsDiscovery.Data.PaymentInitiationAPI?.map { it.Version to it.Links.links }?.toMap()
    val accounts = rsDiscovery.Data.AccountAndTransactionAPI?.map { it.Version to it.Links.links }?.toMap()
    val funds = rsDiscovery.Data.FundsConfirmationAPI?.map { it.Version to it.Links.links }?.toMap()
    val events = rsDiscovery.Data.EventNotificationAPI?.map { it.Version to it.Links.links }?.toMap()
    return@lazy mapOf(
        "payments" to paymentVersions,
        "accounts" to accounts,
        "funds" to funds,
        "events" to events
    )
}

fun getAccountsApiLinks(version: OBVersion): RsDiscovery.RsDiscoveryData.RsDiscoveryAccountAndTransactionAPI.RsDiscoveryAccountAndTransactionAPILinks.Links {
    return rsDiscoveryApiToVersionLinks["accounts"]?.get(version.canonicalName) as RsDiscovery.RsDiscoveryData.RsDiscoveryAccountAndTransactionAPI.RsDiscoveryAccountAndTransactionAPILinks.Links
}

fun getPaymentsApiLinks(version: OBVersion): RsDiscovery.RsDiscoveryData.RsDiscoveryPaymentInitiationAPI.RsDiscoveryPaymentInitiationAPILinks.Links {
    return rsDiscoveryApiToVersionLinks["payments"]?.get(version.canonicalName) as RsDiscovery.RsDiscoveryData.RsDiscoveryPaymentInitiationAPI.RsDiscoveryPaymentInitiationAPILinks.Links
}

fun getFundsApiLinks(version: OBVersion): RsDiscovery.RsDiscoveryData.RsDiscoveryFundsConfirmationAPI.RsDiscoveryFundsConfirmationAPILinks.Links {
    return rsDiscoveryApiToVersionLinks["funds"]?.get(version.canonicalName) as RsDiscovery.RsDiscoveryData.RsDiscoveryFundsConfirmationAPI.RsDiscoveryFundsConfirmationAPILinks.Links
}

fun getEventsApiLinks(version: OBVersion): RsDiscovery.RsDiscoveryData.RsDiscoveryEventNotificationAPI.RsDiscoveryEventNotificationAPILinks.Links {
    return rsDiscoveryApiToVersionLinks["events"]?.get(version.canonicalName) as RsDiscovery.RsDiscoveryData.RsDiscoveryEventNotificationAPI.RsDiscoveryEventNotificationAPILinks.Links
}

/**
 * Map that contains the fields of operations members enabled using reflection
 * - The getter field will be null when the operations is disable
 */
val rsDiscoveryEnabledOperationsMap by lazy {
    /*
     * Each data class is filtered by the not null fields/member/properties (using kotlin reflection) defined in the data class *.Links.links
     * to map these fields/member/properties that represent enabled operations in the RS Discovery configuration
     */
    val payments = rsDiscovery.Data.PaymentInitiationAPI?.map { rsDiscoveryPaymentInitiationAPI ->
        rsDiscoveryPaymentInitiationAPI.Version to rsDiscoveryPaymentInitiationAPI.Links.links::class.memberProperties.filter { paymentLinksField ->
            paymentLinksField.visibility == KVisibility.PUBLIC
                    && paymentLinksField.getter.call(rsDiscoveryPaymentInitiationAPI.Links.links) != null
        }
    }?.toList()

    val accounts = rsDiscovery.Data.AccountAndTransactionAPI?.map { rsDiscoveryAccountAndTransactionAPI ->
        rsDiscoveryAccountAndTransactionAPI.Version to rsDiscoveryAccountAndTransactionAPI.Links.links::class.memberProperties.filter { accountAndTransactionLinksField ->
            accountAndTransactionLinksField.visibility == KVisibility.PUBLIC
                    && accountAndTransactionLinksField.getter.call(rsDiscoveryAccountAndTransactionAPI.Links.links) != null
        }
    }?.toList()

    val funds = rsDiscovery.Data.FundsConfirmationAPI?.map { rsDiscoveryFundsConfirmationAPI ->
        rsDiscoveryFundsConfirmationAPI.Version to rsDiscoveryFundsConfirmationAPI.Links.links::class.memberProperties.filter { fundsConfirmationLinksField ->
            fundsConfirmationLinksField.visibility == KVisibility.PUBLIC
                    && fundsConfirmationLinksField.getter.call(rsDiscoveryFundsConfirmationAPI.Links.links) != null
        }
    }?.toList()

    val events = rsDiscovery.Data.EventNotificationAPI?.map { rsDiscoveryEventNotificationAPI ->
        rsDiscoveryEventNotificationAPI.Version to rsDiscoveryEventNotificationAPI.Links.links::class.memberProperties.filter { eventNotificationLinksField ->
            eventNotificationLinksField.visibility == KVisibility.PUBLIC
                    && eventNotificationLinksField.getter.call(rsDiscoveryEventNotificationAPI.Links.links) != null
        }
    }?.toList()

    return@lazy mapOf(
        "payments" to payments,
        "accounts" to accounts,
        "funds" to funds,
        "events" to events
    )
}

private fun getRsConfiguration(): RsDiscovery {
    val (_, response, result) = Fuel.get("$MTLS_SERVER/rs/open-banking/discovery")
        .responseObject<RsDiscovery>(gsonDeserializer())
    if (!response.isSuccessful) throw AssertionError("Failed to load RS Discovery", result.component2())
    return result.get()
}

// Auto generated from JSON
data class RsDiscovery(
    val Data: RsDiscoveryData
) {
    data class RsDiscoveryData(
        val AccountAndTransactionAPI: List<RsDiscoveryAccountAndTransactionAPI>?,
        val EventNotificationAPI: List<RsDiscoveryEventNotificationAPI>?,
        val FinancialId: String?,
        val FundsConfirmationAPI: List<RsDiscoveryFundsConfirmationAPI>?,
        val PaymentInitiationAPI: List<RsDiscoveryPaymentInitiationAPI>?
    ) {
        data class RsDiscoveryPaymentInitiationAPI(
            val Links: RsDiscoveryPaymentInitiationAPILinks,
            val Version: String
        ) {
            data class RsDiscoveryPaymentInitiationAPILinks(
                val `@type`: String,
                val linkValues: List<String>,
                val links: Links
            ) {
                data class Links(
                    val CreateDomesticPayment: String,
                    val CreateDomesticPaymentConsent: String,
                    val CreateDomesticScheduledPayment: String,
                    val CreateDomesticScheduledPaymentConsent: String,
                    val CreateDomesticStandingOrder: String,
                    val CreateDomesticStandingOrderConsent: String,
                    val CreateFilePayment: String,
                    val CreateFilePaymentConsent: String,
                    val CreateFilePaymentFile: String,
                    val CreateInternationalPayment: String,
                    val CreateInternationalPaymentConsent: String,
                    val CreateInternationalScheduledPayment: String,
                    val CreateInternationalScheduledPaymentConsent: String,
                    val CreateInternationalStandingOrder: String,
                    val CreateInternationalStandingOrderConsent: String,
                    val GetDomesticPayment: String,
                    val GetDomesticPaymentDomesticPaymentIdPaymentDetails: String,
                    val GetDomesticPaymentConsent: String,
                    val GetDomesticPaymentConsentsConsentIdFundsConfirmation: String,
                    val GetDomesticScheduledPayment: String,
                    val GetDomesticScheduledPaymentConsent: String,
                    val GetDomesticStandingOrder: String,
                    val GetDomesticStandingOrderConsent: String,
                    val GetFilePayment: String,
                    val GetFilePaymentConsent: String,
                    val GetFilePaymentFile: String,
                    val GetFilePaymentReport: String,
                    val GetInternationalPayment: String,
                    val GetInternationalPaymentConsent: String,
                    val GetInternationalPaymentConsentsConsentIdFundsConfirmation: String,
                    val GetInternationalPaymentInternationalPaymentIdPaymentDetails: String,
                    val GetInternationalScheduledPayment: String,
                    val GetDomesticScheduledPaymentDomesticPaymentIdPaymentDetails: String,
                    val GetInternationalScheduledPaymentConsent: String,
                    val GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation: String,
                    val GetInternationalStandingOrder: String,
                    val GetInternationalStandingOrderConsent: String,
                    val GetDomesticStandingOrderDomesticStandingOrderIdPaymentDetails: String,
                    val GetInternationalScheduledPaymentPaymentIdPaymentDetails: String,
                    val GetInternationalStandingOrderInternationalStandingOrderIdPaymentDetails: String,
                    val CreateDomesticVRPConsent: String,
                    val CreateDomesticVRPPayment: String,
                    val GetDomesticVRPConsent: String,
                    val DeleteDomesticVRPConsent: String,
                    val GetDomesticVRP: String,
                    val GetDomesticVRPPaymentDetails: String,
                    val CreateDomesticVRPConsentsConsentIdFundsConfirmation: String,
                    val GetDomesticVRPPayment: String
                )
            }
        }

        data class RsDiscoveryFundsConfirmationAPI(
            val Links: RsDiscoveryFundsConfirmationAPILinks,
            val Version: String
        ) {
            data class RsDiscoveryFundsConfirmationAPILinks(
                val `@type`: String,
                val linkValues: List<String>,
                val links: Links
            ) {
                data class Links(
                    val CreateFundsConfirmation: String,
                    val GetFundsConfirmationConsent: String,
                    val DeleteFundsConfirmationConsent: String,
                    val CreateFundsConfirmationConsent: String
                )
            }
        }

        data class RsDiscoveryAccountAndTransactionAPI(
            val Links: RsDiscoveryAccountAndTransactionAPILinks,
            val Version: String
        ) {
            data class RsDiscoveryAccountAndTransactionAPILinks(
                val `@type`: String,
                val linkValues: List<String>,
                val links: Links
            ) {
                data class Links(
                    val CreateAccountAccessConsent: String,
                    val DeleteAccountAccessConsent: String,
                    val GetAccount: String,
                    val GetAccountAccessConsent: String,
                    val GetAccountBalances: String,
                    val GetAccountBeneficiaries: String,
                    val GetAccountDirectDebits: String,
                    val GetAccountOffers: String,
                    val GetAccountParties: String,
                    val GetAccountParty: String,
                    val GetAccountProduct: String,
                    val GetAccountScheduledPayments: String,
                    val GetAccountStandingOrders: String,
                    val GetAccountStatement: String,
                    val GetAccountStatementFile: String,
                    val GetAccountStatementTransactions: String,
                    val GetAccountStatements: String,
                    val GetAccountTransactions: String,
                    val GetAccounts: String,
                    val GetBalances: String,
                    val GetBeneficiaries: String,
                    val GetDirectDebits: String,
                    val GetOffers: String,
                    val GetParty: String,
                    val GetProducts: String,
                    val GetScheduledPayments: String,
                    val GetStandingOrders: String,
                    val GetStatements: String,
                    val GetTransactions: String
                )
            }
        }

        data class RsDiscoveryEventNotificationAPI(
            val Links: RsDiscoveryEventNotificationAPILinks,
            val Version: String
        ) {
            data class RsDiscoveryEventNotificationAPILinks(
                val `@type`: String,
                val linkValues: List<String>,
                val links: Links
            ) {
                data class Links(
                    val AmendCallbackUrl: String,
                    val AmendEventSubscription: String,
                    val CreateCallbackUrl: String,
                    val CreateEventSubscription: String,
                    val DeleteCallbackUrl: String,
                    val DeleteEventSubscription: String,
                    val EventAggregatedPolling: String,
                    val GetCallbackUrls: String,
                    val GetEventSubscription: String
                )
            }
        }
    }
}
