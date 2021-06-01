package com.pepper.care.common.entities

import android.util.Log
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type

class PlatformMealsDeserializer : JsonDeserializer<PlatformMealsResponse> {

    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): PlatformMealsResponse {
        val jsonObject = json?.asJsonObject

        return PlatformMealsResponse(
            jsonObject?.get("id")!!.asInt,
            jsonObject.get("name").asString,
            jsonObject.get("description").asString,
            jsonObject.get("type").asString,
            jsonObject.get("allergies").asString,
            jsonObject.get("calories").asInt,
            jsonObject.get("source").asString,
        )
    }
}

data class PlatformMealsResponse(
    val id: Int,
    val name: String,
    val description: String,
    val type: String,
    val allergies: String,
    val calories: Int,
    val source: String
)