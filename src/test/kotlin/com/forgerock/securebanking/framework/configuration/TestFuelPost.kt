package com.forgerock.securebanking.framework.configuration

import com.forgerock.securebanking.framework.http.fuel.initFuel
import com.forgerock.securebanking.framework.http.fuel.responseObject
import com.forgerock.securebanking.support.SsoCode
import com.forgerock.uk.openbanking.framework.accesstoken.constants.OB_TPP_EIDAS_TRANSPORT_KEY_PATH
import com.forgerock.uk.openbanking.framework.accesstoken.constants.OB_TPP_EIDAS_TRANSPORT_PEM_PATH
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.*
import com.github.kittinunf.fuel.core.extensions.cUrlString
import com.github.kittinunf.fuel.core.interceptors.LogRequestAsCurlInterceptor
import com.github.kittinunf.fuel.core.interceptors.LogResponseInterceptor
import com.github.kittinunf.fuel.httpPost
import com.google.gson.Gson
import org.apache.http.entity.ContentType
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.security.cert.CertPath


// Hello World Program

fun main(args: Array<String>) {
    //val path = "https://postman-echo.com/post"
    val path = "https://openam-forgerock-securebankingaccelerato.forgeblocks.com/am/json/realms/root/realms/alpha/authenticate"
    var response = ""
//  response = callOne(path)
    //response = callFuel(path)
    //response = callFuelTwo(path)
    response = callFuelThree(path)
    println("response\n $response")
}

private fun callFuelThree(path: String): String{
    initFuel()
    initFuel(OB_TPP_EIDAS_TRANSPORT_KEY_PATH, OB_TPP_EIDAS_TRANSPORT_PEM_PATH)

    var request = Fuel.post("$path/")
    request = request
        .header("X-OpenAM-Username", "psu")
        .header("X-OpenAM-Password", "0penBanking!")
        .header("Accept-API-Version", "resource=2.1, protocol=1.0")
        .header(Headers.CONTENT_TYPE, ContentType.DEFAULT_TEXT)
        .header(Headers.CONTENT_LENGTH, 0)

    val result = FuelManager.instance.client.executeRequest(request)

    return result.toString()
}

private fun callFuelTwo(path: String): String{
    initFuel()
    initFuel(OB_TPP_EIDAS_TRANSPORT_KEY_PATH, OB_TPP_EIDAS_TRANSPORT_PEM_PATH)
    val (request, response, result) = path.httpPost()
        .header("X-OpenAM-Username", "psu")
        .header("X-OpenAM-Password", "0penBanking!")
        .header("Accept-API-Version", "resource=2.1, protocol=1.0")
//        .header(Headers.CONTENT_TYPE, ContentType.DEFAULT_TEXT)
//        .header(Headers.CONTENT_LENGTH, 0)
        .responseString()
    if (!response.isSuccessful) throw AssertionError(
        "Failed to get callbacks to Registration Journey",
        result.component2()
    )
    return result.get()
}
private fun callOne(path: String): String{
    val client = HttpClient.newBuilder().build();
    val request = HttpRequest.newBuilder()
        .uri(URI.create(path))
        .header("X-OpenAM-Username", "psu")
        .header("X-OpenAM-Password", "0penBanking!")
        .header("Accept-API-Version", "resource=2.1, protocol=1.0")
        .POST(HttpRequest.BodyPublishers.ofString(""))
        .build()
    val response = client.send(request, HttpResponse.BodyHandlers.ofString());

    return response.body()
}

private fun callFuel(path: String): String{
    initFuel()
    initFuel(OB_TPP_EIDAS_TRANSPORT_KEY_PATH, OB_TPP_EIDAS_TRANSPORT_PEM_PATH)

    var request = Fuel.post(path)
    request = request
//        .header("Content-Length",request.body.toString().length)
        .header("X-OpenAM-Username", "psu")
        .header("X-OpenAM-Password", "0penBanking!")
        .header("Accept-API-Version", "resource=2.1, protocol=1.0")
        .header(Headers.CONTENT_TYPE, ContentType.DEFAULT_TEXT)
//        .header("My-header", "200")
        .header(Headers.CONTENT_LENGTH, 0)
    /*
    curl -i -X POST https://iam.dev.forgerock.financial/am/json/realms/root/realms/alpha/authenticate -H 'X-OpenAM-Password:0penBanking!' -H "X-OpenAM-Username:psu" -H "Accept-API-Version:resource=2.1, protocol=1.0"
     */
    val curl = request.cUrlString()
    val (_, response, result) = request.responseString()
//    val (req, response, result) = Fuel.post("$PLATFORM_SERVER/am/json/realms/root/realms/$realm/authenticate")
//        .header("X-OpenAM-Username", username)
//        .header("X-OpenAM-Password", password)
//        .header("Accept-API-Version", "resource=2.0, protocol=1.0")
//        .header("My-header", "200")
//        .header("Content-Length",req.body.lengh)
//        .responseString()
    if (!response.isSuccessful) throw AssertionError(
        "Failed to get callbacks to Registration Journey",
        result.component2()
    )
    return result.component1().toString()
}
