package com.forgerock.uk.openbanking.tests.functional.discovery

import assertk.assertThat
import assertk.assertions.isNotNull
import com.forgerock.uk.openbanking.support.discovery.rsDiscovery
import org.junit.jupiter.api.Test


class RsDiscoveryTest {

    @Test
    fun getRsDiscovery() {
        val data = rsDiscovery.Data
        assertThat(data).isNotNull()
    }
}
