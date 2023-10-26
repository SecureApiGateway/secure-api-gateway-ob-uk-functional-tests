package com.forgerock.sapi.gateway.framework.utils

import com.google.gson.*
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import java.lang.reflect.Type

class GsonUtils {
    companion object {
        val gson: Gson = GsonBuilder()
            .registerTypeAdapter(DateTime::class.java, object : JsonSerializer<DateTime?> {
                override fun serialize(
                    json: DateTime?,
                    typeOfSrc: Type?,
                    context: JsonSerializationContext?
                ): JsonElement? {
                    return JsonPrimitive(ISODateTimeFormat.dateTime().print(json))
                }
            })
            .registerTypeAdapter(DateTime::class.java, object : JsonDeserializer<DateTime?> {
                @Throws(JsonParseException::class)
                override fun deserialize(
                    json: JsonElement,
                    typeOfT: Type?,
                    context: JsonDeserializationContext?
                ): DateTime? {
                    return ISODateTimeFormat.dateTime().parseDateTime(json.asString)
                }
            })
            .create()
    }
}