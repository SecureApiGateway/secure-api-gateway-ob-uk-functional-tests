package com.forgerock.uk.openbanking.tests.functional.account.access

import com.forgerock.securebanking.framework.extensions.junit.CreateTppCallback
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion
import com.forgerock.uk.openbanking.tests.functional.account.BaseAccountApi
import com.forgerock.uk.openbanking.tests.functional.account.access.consents.api.v3_1_8.AccountAccessConsent

open class BaseAccountApi3_1_8(
    version: OBVersion,
    tppResource: CreateTppCallback.TppResource
) : BaseAccountApi(version, AccountAccessConsent(version, tppResource), tppResource)