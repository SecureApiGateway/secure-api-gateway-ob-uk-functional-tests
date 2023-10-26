package com.forgerock.sapi.gateway.ob.uk.support.registration

import com.forgerock.sapi.gateway.framework.data.RegistrationResponse
import com.forgerock.sapi.gateway.framework.http.fuel.responseObject
import com.forgerock.sapi.gateway.ob.uk.support.discovery.asDiscovery
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.result.Result

data class UserRegistrationRequest(val user: User) {
    constructor(userName: String, password: String, uid: String) : this(User(userName, password, uid))
    constructor(userName: String, password: String) : this(User(userName, password,null))
}

data class User(val userName: String, val password: String, var uid: String?)

fun unregisterTpp(id: String, registrationAccessToken: String): Triple<Request, Response, Result<String, FuelError>> {
    return Fuel.delete("${asDiscovery.registration_endpoint!!}/$id")
        .authentication()
        .bearer(registrationAccessToken)
        .responseObject()
}

fun registerTpp(signedRegistrationRequest: String): RegistrationResponse {
    val (_, response, result) = Fuel.post(asDiscovery.registration_endpoint!!)
        .body(signedRegistrationRequest)
        .header(Headers.CONTENT_TYPE, "application/jwt")
        .responseObject<RegistrationResponse>()
    if (response.statusCode != 201) {
        val xForgerockTransactionId = response.headers.get("x-forgerock-transactionid")
        throw AssertionError(
            "Registration failed with ${response.statusCode} : ${
                String
                    (response.data)
            } x-forgerock-transactionid: $xForgerockTransactionId", result.component2()
        )
    }
    return result.get()
}
