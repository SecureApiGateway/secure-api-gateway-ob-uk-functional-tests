package com.forgerock.sapi.gateway.ob.uk.tests.functional.events.aggregatedpolling.junit.v4_0_0

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.framework.extensions.junit.EnabledIfVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.events.aggregatedpolling.api.v4_0_0.AggregatedPolling
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AggregatedPollingTest(val tppResource: CreateTppCallback.TppResource) {
    lateinit var aggregatedPollingApi: AggregatedPolling

    @BeforeEach
    fun setUp() {
        aggregatedPollingApi = AggregatedPolling(OBVersion.v4_0_0, tppResource)
    }

    @EnabledIfVersion(
            type = "events",
            apiVersion = "v4.0.0",
            operations = ["EventAggregatedPolling"]
    )
    @Test
    fun shouldInitialPollingTest_v4_0_0() {
        aggregatedPollingApi.shouldInitialPollingTest()
    }

    @EnabledIfVersion(
            type = "events",
            apiVersion = "v4.0.0",
            operations = ["EventAggregatedPolling"]
    )
    @Test
    fun shouldAcknowledgeTest_v4_0_0() {
        aggregatedPollingApi.shouldAcknowledgeEventTest()
    }

    @EnabledIfVersion(
            type = "events",
            apiVersion = "v4.0.0",
            operations = ["EventAggregatedPolling"]
    )
    @Test
    fun shouldPollAndAcknowledgeTest_v4_0_0() {
        aggregatedPollingApi.shouldPollAndAcknowledgeEventTest()
    }
}