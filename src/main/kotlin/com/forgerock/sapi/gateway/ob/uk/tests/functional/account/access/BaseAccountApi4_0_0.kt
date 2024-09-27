package com.forgerock.sapi.gateway.ob.uk.tests.functional.account.access

import com.forgerock.sapi.gateway.framework.extensions.junit.CreateTppCallback
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.BaseAccountApiV4
import com.forgerock.sapi.gateway.ob.uk.tests.functional.account.access.consents.api.v4_0_0.AccountAccessConsent
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion

open class BaseAccountApi4_0_0(
    version: OBVersion,
    tppResource: CreateTppCallback.TppResource
) : BaseAccountApiV4(version, AccountAccessConsent(version, tppResource), tppResource)