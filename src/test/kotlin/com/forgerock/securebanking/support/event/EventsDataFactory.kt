package com.forgerock.securebanking.support.event

import com.forgerock.openbanking.common.model.data.FRDataEvent
import com.forgerock.openbanking.common.model.data.OBEventNotification2
import com.forgerock.openbanking.common.model.version.OBVersion
import com.forgerock.securebanking.framework.data.Tpp
import uk.org.openbanking.datamodel.event.*

class EventsDataFactory {

    companion object {
        fun aCallbackUrlRequest(version: OBVersion): OBCallbackUrl1 {
            return OBCallbackUrl1().data(
                OBCallbackUrlData1()
                    .url(tppEventNotificationsUrl(version))
                    .version(version.canonicalVersion)
            )
        }

        fun anEventSubscriptionRequest(version: OBVersion): OBEventSubscription1 {
            return OBEventSubscription1().data(
                OBEventSubscription1Data()
                    .version(version.canonicalVersion)
                    .callbackUrl(tppEventNotificationsUrl(version))
            )
        }

        fun anEventDataRequest(tpp: Tpp, version: OBVersion): FRDataEvent {
            return FRDataEvent().tppId(tpp.registrationResponse.client_id)
                .addOBEventNotification2Item(
                    OBEventNotification2()
                        .iss("https://examplebank.com/")
                        .iat(1516239022)
                        .jti("b460a07c-4962-43d1-85ee-9dc10fbb8f6c")
                        .sub("https://examplebank.com/api/open-banking/${version.canonicalName}/pisp/domestic-payments/pmt-7290-003")
                        .aud("7umx5nTR33811QyQfi")
                        .txn("dfc51628-3479-4b81-ad60-210b43d02306")
                        .toe(1516239022)
                        .events(
                            OBEvent1().urnukorgopenbankingeventsresourceUpdate(
                                OBEventResourceUpdate1().subject(
                                    OBEventSubject1().subjectType("http://openbanking.org.uk/rid_http://openbanking.org.uk/rty")
                                        .httpopenbankingOrgUkrid("pmt-7290-003")
                                        .httpopenbankingOrgUkrty("domestic-payment")
                                        .addHttpopenbankingOrgUkrlkItem(
                                            OBEventLink1().link("https://examplebank.com/api/open-banking/${version.canonicalName}/pisp/domestic-payments/pmt-7290-003")
                                                .version(version.canonicalVersion)
                                        )
                                )
                            )
                        )
                )
        }

        private fun tppEventNotificationsUrl(version: OBVersion): String {
            // An ASPSP will send event notifications to a TPP using the event-notification resource
            // Endpoint POST /event-notifications
            return "https://tpp.domain.test.net/open-banking/${version.canonicalName}/event-notifications"
        }
    }
}
