package com.forgerock.uk.openbanking.support.payment

import com.forgerock.securebanking.framework.configuration.PSU_USER_ID
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RsUserDataTest {

    @Test
    fun shouldRetrieveUserDataFromRS(){
        // When
        var userName = rsUserData.userName

        // Then
        assertThat(userName).isEqualTo(PSU_USER_ID)
    }
}