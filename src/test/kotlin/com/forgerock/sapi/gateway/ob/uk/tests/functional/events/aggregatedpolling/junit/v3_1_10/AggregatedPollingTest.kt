package com.forgerock.sapi.gateway.ob.uk.tests.functional.events.aggregatedpolling.junit.v3_1_10

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.events.aggregatedpolling.api.v3_1_10.AggregatedPolling
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AggregatedPollingTest(val tppResource: CreateTppCallback.TppResource) {
    lateinit var aggregatedPollingApi: AggregatedPolling

    @BeforeEach
    fun setUp() {
        aggregatedPollingApi = AggregatedPolling(OBVersion.v3_1_10, tppResource)
    }

    @EnabledIfVersion(
            type = "events",
            apiVersion = "v3.1.10",
            operations = ["EventAggregatedPolling"]
    )
    @Test
    fun shouldInitialPollingTest_v3_1_10() {
        aggregatedPollingApi.shouldInitialPollingTest()
    }

    @EnabledIfVersion(
            type = "events",
            apiVersion = "v3.1.10",
            operations = ["EventAggregatedPolling"]
    )
    @Test
    fun shouldAcknowledgeTest_v3_1_10() {
        aggregatedPollingApi.shouldAcknowledgeEventTest()
    }

    @EnabledIfVersion(
            type = "events",
            apiVersion = "v3.1.10",
            operations = ["EventAggregatedPolling"]
    )
    @Test
    fun shouldPollAndAcknowledgeTest_v3_1_10() {
        aggregatedPollingApi.shouldPollAndAcknowledgeEventTest()
    }
}