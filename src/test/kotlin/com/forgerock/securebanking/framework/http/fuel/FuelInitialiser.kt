package com.forgerock.securebanking.framework.http.fuel

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.forgerock.securebanking.framework.data.Tpp
import com.forgerock.securebanking.support.directory.createSoftwareStatement
import com.forgerock.securebanking.support.directory.getSigningKid
import com.forgerock.securebanking.support.directory.getTransportKid
import com.forgerock.securebanking.support.getPrivateCert
import com.forgerock.securebanking.support.getPublicCert
import com.forgerock.securebanking.support.login
import com.forgerock.securebanking.support.registerDirectoryUser
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.ResponseResultOf
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.core.interceptors.LogRequestAsCurlInterceptor
import com.github.kittinunf.fuel.core.interceptors.LogResponseInterceptor
import com.github.kittinunf.fuel.core.response
import com.github.kittinunf.fuel.jackson.jacksonDeserializerOf
import io.r2.simplepemkeystore.MultiFileConcatSource
import io.r2.simplepemkeystore.SimplePemKeyStoreProvider
import org.apache.http.ssl.SSLContextBuilder
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import java.io.InputStream
import java.security.KeyStore
import java.security.Security
import javax.net.ssl.HostnameVerifier

class DateTimeDeserializer : StdDeserializer<DateTime>(DateTime::class.java) {
    override fun deserialize(jp: JsonParser, ctxt: DeserializationContext?): DateTime {
        val date = jp.text
        return DateTime.parse(date, ISODateTimeFormat.dateTime())
    }
}

class DateTimeSerializer : StdSerializer<DateTime>(DateTime::class.java) {
    override fun serialize(value: DateTime?, gen: JsonGenerator?, provider: SerializerProvider?) {
        gen?.writeString(value?.toDateTimeISO()?.toString(ISODateTimeFormat.dateTimeNoMillis()))
    }
}

val serializers: SimpleModule = SimpleModule("CustomSerializer")
    .addDeserializer(DateTime::class.java, DateTimeDeserializer())
    .addSerializer(DateTime::class.java, DateTimeSerializer())

val defaultMapper: ObjectMapper = ObjectMapper().registerKotlinModule()
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
    .registerModules(JodaModule())
    .registerModules(serializers)


fun initFuel(privatePem: InputStream, certificatePem: InputStream) {
    val ks = loadKeystore(privatePem, certificatePem)
    val truststore = object {}.javaClass.getResource("/com/forgerock/securebanking/truststore.jks")
    val sc = SSLContextBuilder()
        .loadKeyMaterial(
            ks,
            "".toCharArray()

        )
        // Force keystore to select hardcoded "server" alias in io.r2.simplepemkeystore.spi.SimplePemKeyStoreSpi see https://github.com/robymus/simple-pem-keystore/issues/2
        { _, _ -> "server" }
        .loadTrustMaterial(truststore.toURI().toURL(), "changeit".toCharArray())
    initFuel(sc)
}

private fun initFuel(
    scb: SSLContextBuilder = SSLContextBuilder().loadTrustMaterial(
        object {}.javaClass.getResource
            ("truststore.jks"), "changeit".toCharArray()
    )
) {
    FuelManager.instance.reset()
    FuelManager.instance.apply {
        socketFactory = scb.build().socketFactory
        hostnameVerifier = HostnameVerifier { _, _ -> true }
        addRequestInterceptor(LogRequestAsCurlInterceptor)
        addResponseInterceptor(LogResponseInterceptor)
        timeoutInMillisecond = 30000
        timeoutReadInMillisecond = 30000
    }
    FuelManager.instance.baseHeaders = mapOf("x-obri-analytics-enabled" to "false")
}

/**
 * Initialise HTTP client Fuel for MTLS
 * @param privatePem private pem resource
 * @param publicPem pem certificate
 */
fun initFuel(
    privatePem: String = "/com/forgerock/securebanking/ob-eidas/obwac.key",
    publicPem: String = "/com/forgerock/securebanking/ob-eidas/obwac.pem"
) {
    val privatePemStream = object {}.javaClass.getResourceAsStream(privatePem)
    val publicPemStream = object {}.javaClass.getResourceAsStream(publicPem)
    initFuel(privatePemStream, publicPemStream)
}

/**
 * Initialise HTTP client Fuel for MTLS with a new TPP
 */
fun initFuelAsNewTpp(): Tpp {
    initFuel()  // Bootstrap truststore for any HTTP requests
    val directoryUser = registerDirectoryUser()
    val sessionToken = login(directoryUser.input.user.username, directoryUser.input.user.userPassword)
    val softwareStatement = createSoftwareStatement(sessionToken)
    val transportKid = getTransportKid(softwareStatement, sessionToken)
    val privateCert = getPrivateCert(softwareStatement, transportKid, sessionToken)
    val publicCert = getPublicCert(softwareStatement, transportKid, sessionToken)
    initFuel(privateCert.byteInputStream(), publicCert.byteInputStream())
    val signingKid = getSigningKid(softwareStatement, sessionToken)!!
    val signingKey = getPrivateCert(softwareStatement, signingKid, sessionToken)
    return Tpp(sessionToken, directoryUser, softwareStatement, privateCert, publicCert, signingKid, signingKey)
}

private fun loadKeystore(privatePem: InputStream, publicPem: InputStream): KeyStore {
    Security.addProvider(SimplePemKeyStoreProvider())
    val ks = KeyStore.getInstance("simplepem")
    ks.load(
        MultiFileConcatSource()
            .add(privatePem)
            .add(publicPem)
            .build(),
        CharArray(0)
    )
    return ks
}

/**
 * Extend Fuel DSL to use our custom (de)serialiser
 */
inline fun <reified T : Any> Request.responseObject(): ResponseResultOf<T> =
    response(jacksonDeserializerOf(defaultMapper))

/**
 * Extend Fuel DSL to use our cus
 */
inline fun <reified T : Any> Request.body(): ResponseResultOf<T> = response(jacksonDeserializerOf(defaultMapper))

inline fun <reified T : Any> Request.jsonBody(src: T) = this.jsonBody(defaultMapper.writeValueAsString(src))

fun Request.jsonBody(src: String) = this.jsonBody(src)
