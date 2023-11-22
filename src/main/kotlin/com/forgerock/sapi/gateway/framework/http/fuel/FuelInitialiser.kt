package com.forgerock.sapi.gateway.framework.http.fuel

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.forgerock.sapi.gateway.framework.configuration.*
import com.forgerock.sapi.gateway.framework.data.Tpp
import com.forgerock.sapi.gateway.framework.utils.FileUtils
import com.forgerock.sapi.gateway.ob.uk.support.directory.createSoftwareStatement
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.ResponseResultOf
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.core.interceptors.LogRequestAsCurlInterceptor
import com.github.kittinunf.fuel.core.interceptors.LogResponseInterceptor
import com.github.kittinunf.fuel.core.response
import com.github.kittinunf.fuel.jackson.jacksonDeserializerOf
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import io.r2.simplepemkeystore.MultiFileConcatSource
import io.r2.simplepemkeystore.SimplePemKeyStoreProvider
import org.apache.http.ssl.SSLContextBuilder
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.ISODateTimeFormat
import java.io.InputStream
import java.lang.reflect.Type
import java.security.KeyStore
import java.security.Security
import javax.net.ssl.HostnameVerifier
import kotlin.ranges.CharRange.Companion.EMPTY


class DateTimeDeserializer : StdDeserializer<DateTime>(DateTime::class.java) {
    override fun deserialize(jp: JsonParser, ctxt: DeserializationContext?): DateTime {
        val date = jp.text
        return DateTime.parse(date)
    }

    fun deserialize(
        je: JsonElement, type: Type?,
        jdc: JsonDeserializationContext?
    ): DateTime? {
        return if (je.asString.isEmpty()) null
        else
            ISODateTimeFormat.dateTimeParser().parseDateTime(je.asString).withZone(DateTimeZone.UTC)
    }
}

class DateTimeSerializer : StdSerializer<DateTime>(DateTime::class.java) {
    override fun serialize(value: DateTime?, gen: JsonGenerator?, provider: SerializerProvider?) {
        gen?.writeString(value?.toDateTimeISO()?.withZone(DateTimeZone.UTC).toString())
    }

    fun serialize(
        src: DateTime?, typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement? {
        return JsonPrimitive(if (src == null) EMPTY.toString()
        else
            src?.toDateTimeISO()?.withZone(DateTimeZone.UTC).toString())
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
    val truststore = object {}.javaClass.getResource(TRUSTSTORE_PATH)
    val sc = SSLContextBuilder()
        .loadKeyMaterial(
            ks,
            "".toCharArray()

        )
        // Force keystore to select hardcoded "server" alias in io.r2.simplepemkeystore.spi.SimplePemKeyStoreSpi see https://github.com/robymus/simple-pem-keystore/issues/2
        { _, _ -> "server" }
        .loadTrustMaterial(truststore.toURI().toURL(), TRUSTSTORE_PASSWORD.toCharArray())
    initFuel(sc)
}

private fun initFuel(
    scb: SSLContextBuilder = SSLContextBuilder().loadTrustMaterial(
        object {}.javaClass.getResource
            (TRUSTSTORE_PATH), TRUSTSTORE_PASSWORD.toCharArray()
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
    privatePem: String = OB_TPP_EIDAS_TRANSPORT_KEY_PATH,
    publicPem: String = OB_TPP_EIDAS_TRANSPORT_PEM_PATH
) {
    val privatePemStream = FileUtils().getInputStream(privatePem)
    val publicPemStream = FileUtils().getInputStream(publicPem)
    initFuel(privatePemStream, publicPemStream)
}

/**
 * Initialise HTTP client Fuel for MTLS with a new TPP
 */
fun initFuelAsNewTpp(): Tpp {
    initFuel()
    // Bootstrap truststore for any HTTP requests
    initFuel(OB_TPP_EIDAS_TRANSPORT_KEY_PATH, OB_TPP_EIDAS_TRANSPORT_PEM_PATH)
    val privateCert = OB_TPP_EIDAS_TRANSPORT_KEY_PATH
    val publicCert = OB_TPP_EIDAS_TRANSPORT_PEM_PATH

    val softwareStatement = createSoftwareStatement()

    val signingKid = OB_TPP_OB_EIDAS_TEST_SIGNING_KID
    val signingKey = OB_TPP_EIDAS_SIGNING_KEY_PATH
    return Tpp(softwareStatement, privateCert, publicCert, signingKid, signingKey)
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
 * Extend Fuel DSL to use our custom (de)serializer
 */
inline fun <reified T : Any> Request.responseObject(): ResponseResultOf<T> =
    response(jacksonDeserializerOf(defaultMapper))

/**
 * Extend Fuel DSL to use our cus
 */
inline fun <reified T : Any> Request.body(): ResponseResultOf<T> = response(jacksonDeserializerOf(defaultMapper))

inline fun <reified T : Any> Request.jsonBody(src: T) = this.jsonBody(defaultMapper.writeValueAsString(src))

fun Request.jsonBody(src: String) = this.jsonBody(src)
