package com.forgerock.uk.openbanking.support.registration

import com.forgerock.securebanking.framework.configuration.OB_TPP_EIDAS_SIGNING_KEY_PATH
import com.forgerock.securebanking.framework.configuration.OB_TPP_OB_EIDAS_TEST_SIGNING_KID
import com.forgerock.securebanking.framework.configuration.OB_TPP_PRE_EIDAS_SIGNING_KID
import com.forgerock.securebanking.framework.data.RegistrationRequest
import com.forgerock.securebanking.framework.data.RegistrationResponse
import com.forgerock.securebanking.framework.http.fuel.responseObject
import com.forgerock.securebanking.framework.utils.GsonUtils
import com.forgerock.securebanking.framework.utils.FileUtils
import com.forgerock.uk.openbanking.support.discovery.asDiscovery
import com.forgerock.uk.openbanking.support.loadRsaPrivateKey
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.result.Result
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm

data class UserRegistrationRequest(val user: User) {
    constructor(userName: String, password: String, uid: String) : this(User(userName, password, uid))
    constructor(userName: String, password: String) : this(User(userName, password,null))
}

data class User(val userName: String, val password: String, var uid: String?)

fun unregisterTpp(id: String): Triple<Request, Response, Result<String, FuelError>> {
    return Fuel.delete("${asDiscovery.registration_endpoint!!}/$id")
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

@Deprecated("To delete pre-eidas resources")
//fun signRegistrationRequest(): Pair<String, RegistrationRequest> {
//    val ssa = FileUtils().getStringContent("OB_TPP_PRE_EIDAS_SSA_PATH")
//    val registrationRequest = RegistrationRequest(software_statement = ssa)
//    val privateKey = FileUtils().getStringContent("OB_TPP_PRE_EIDAS_SIGNING_KEY")
//    val key = loadRsaPrivateKey(privateKey)
//    val signedRegistrationRequest = Jwts.builder()
//        .setHeaderParam("kid", OB_TPP_PRE_EIDAS_SIGNING_KID)
//        .setPayload(GsonUtils.gson.toJson(registrationRequest))
//        .signWith(key, SignatureAlgorithm.forName(asDiscovery.request_object_signing_alg_values_supported[0]))
//        .compact()
//    return Pair(signedRegistrationRequest, registrationRequest)
//}

fun signOBEidasRegistrationRequest(): Pair<String, RegistrationRequest> {
    val ssa = FileUtils().getStringContent("OB_TPP_EIDAS_SSA_PATH")
    val registrationRequest = RegistrationRequest(software_statement = ssa)
    val privateKey = FileUtils().getStringContent(OB_TPP_EIDAS_SIGNING_KEY_PATH)
    val key = loadRsaPrivateKey(privateKey)
    val signedRegistrationRequest = Jwts.builder()
        .setHeaderParam("kid", OB_TPP_OB_EIDAS_TEST_SIGNING_KID)
        .setPayload(GsonUtils.gson.toJson(registrationRequest))
        .signWith(key, SignatureAlgorithm.forName(asDiscovery.request_object_signing_alg_values_supported[0]))
        .compact()
    return Pair(signedRegistrationRequest, registrationRequest)
}
