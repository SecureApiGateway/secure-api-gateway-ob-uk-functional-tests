package com.forgerock.uk.openbanking.support.payment

import com.forgerock.securebanking.framework.configuration.PSU_USER_ID
import com.forgerock.securebanking.framework.configuration.RS_SERVER
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.isSuccessful
import com.github.kittinunf.fuel.gson.gsonDeserializer

class RsUserData {
    inline fun getDebtorAccount(): UserData.AccountData.Account.FinancialAccount? {
        return rsUserData.accountDatas?.get(0)?.account?.Account?.get(0)
    }
}

val rsUserData by lazy { getUserDataFromRS() }


private fun getUserDataFromRS(): UserData {
    val (_, response, result) = Fuel.get("$RS_SERVER/admin/data/user?userId=$PSU_USER_ID")
        .responseObject<UserData>(gsonDeserializer())
    if (!response.isSuccessful) throw AssertionError("Failed to retrieve the User Data", result.component2())
    return result.get()
}

data class UserData(
    val userName: String, // is userId really
    val accountDatas: List<AccountData>?
) {
    data class AccountData(
        val account: Account
    ) {
        data class Account(
            val AccountId: String,
            val Status: String,
            val Nickname: String,
            val Account: List<FinancialAccount>
        ) {
            data class FinancialAccount(
                val SchemeName: String,
                val Identification: String,
                val Name: String,
                val SecondaryIdentification: String?
            )
        }
    }
}