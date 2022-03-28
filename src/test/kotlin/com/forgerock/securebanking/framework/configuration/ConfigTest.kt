package com.forgerock.securebanking.framework.configuration

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test


class ConfigTest {

    @Test
    fun configTest() {
        println("platformServer $PLATFORM_SERVER")
        println("rsServer $RS_SERVER")
        println("rcsServer $RCS_SERVER")
        println("igServer $IG_SERVER")
        println("userPassword $PSU_USERNAME")
        println("username $PSU_PASSWORD")
        println("eidasTestSigningKid $OB_TPP_OB_EIDAS_TEST_SIGNING_KID")
        println("preEidasTestSigningKid $OB_TPP_PRE_EIDAS_SIGNING_KID")
    }
}
