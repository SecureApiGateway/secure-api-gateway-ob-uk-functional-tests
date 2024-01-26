package com.forgerock.sapi.gateway.ob.uk.support.event

import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import uk.org.openbanking.datamodel.event.*
import java.net.URI
import java.util.UUID

class EventsDataFactory {

    companion object {

        fun anEventSubscriptionRequest(version: OBVersion): OBEventSubscription1 {
            return OBEventSubscription1().data(
                    OBEventSubscription1Data()
                            .version(version.canonicalVersion)
                            .callbackUrl(tppEventNotificationsUrl(version))
            )
        }

        fun aValidFRDataEvent(
                events: List<OBEventNotification1>,
                client_id: String
        ): FRDataEvent {
            return FRDataEvent(client_id, events)
        }

        fun aValidOBEventNotification1(version: OBVersion): OBEventNotification1 {
            return OBEventNotification1()
                    .iss("https://examplebank.com/")
                    .iat(1516239022)
                    .jti(UUID.randomUUID().toString())
                    .sub(URI.create("https://examplebank.com/api/open-banking/${version.canonicalName}/pisp/domestic-payments/pmt-7290-003"))
                    .aud("7umx5nTR33811QyQfi")
                    .txn(UUID.randomUUID().toString())
                    .toe(1516239022)
                    .events(
                            OBEvent1().urnColonUkColonOrgColonOpenbankingColonEventsColonResourceUpdate(
                                    OBEventResourceUpdate1().subject(
                                            OBEventSubject1().subjectType("http://openbanking.org.uk/rid_http://openbanking.org.uk/rty")
                                                    .httpColonOpenbankingOrgUkRid("pmt-7290-003")
                                                    .httpColonOpenbankingOrgUkRty("domestic-payment")
                                                    .addHttpColonOpenbankingOrgUkRlkItem(
                                                            OBEventLink1().link("https://examplebank.com/api/open-banking/${version.canonicalName}/pisp/domestic-payments/pmt-7290-003")
                                                                    .version(version.canonicalVersion)
                                                    )
                                    )
                            )
                    )


        }

        private fun tppEventNotificationsUrl(version: OBVersion): URI {
            // An ASPSP will send event notifications to a TPP using the event-notification resource
            // Endpoint POST /event-notifications
            return URI.create("https://tpp.domain.test.net/open-banking/${version.canonicalName}/event-notifications")
        }

        data class FRDataEvent(
                val client_id: String,
                val events: List<OBEventNotification1>
        )
    }
}
