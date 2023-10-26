package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.access

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.BaseAccountApi
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.access.consents.api.v3_1_8.AccountAccessConsent

open class BaseAccountApi3_1_8(
    version: OBVersion,
    tppResource: CreateTppCallback.TppResource
) : BaseAccountApi(version, AccountAccessConsent(version, tppResource), tppResource)