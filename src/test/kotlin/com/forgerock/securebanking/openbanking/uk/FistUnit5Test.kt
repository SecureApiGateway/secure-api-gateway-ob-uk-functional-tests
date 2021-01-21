package com.forgerock.openbanking

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

class FistUnit5Test {
    @Test
    fun firstTest() {
        System.out.println("tests!!!!!")
        assertThat(1 + 1).isEqualTo(2)
    }
}