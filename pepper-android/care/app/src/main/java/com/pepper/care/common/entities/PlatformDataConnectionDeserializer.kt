package com.pepper.care.common.entities

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type

class PlatformDataConnectionDeserializer : JsonDeserializer<PlatformConnectionResponse> {

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): PlatformConnectionResponse {
        val jsonObj = json?.asJsonObject

        return PlatformConnectionResponse(
            jsonObj?.get("id")?.asInt!!,
            jsonObj.get("connected")?.asBoolean!!
        )
    }
}

data class PlatformConnectionResponse(
    val id : Int,
    val connected : Boolean
)