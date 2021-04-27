package com.pepper.care.common

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pepper.care.common.entities.PlatformConnectionResponse
import com.pepper.care.common.entities.PlatformDataConnectionDeserializer
import com.pepper.care.common.entities.PlatformMealsDeserializer
import com.pepper.care.common.entities.PlatformMealsResponse
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type

class DynamicApiConverterFactory : Converter.Factory() {

    private val platformConnection: Converter.Factory by lazy {
        GsonConverterFactory.create(providePlatformConnectionGsonConverter())
    }

    private val platformMeals: Converter.Factory by lazy {
        GsonConverterFactory.create(providePlatformMealsGsonConverter())
    }

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        annotations.forEach { annotation ->

            return when (annotation.annotationClass) {
                PlatformConnection::class -> platformConnection.responseBodyConverter(type, annotations, retrofit)
                PlatformMeals::class -> platformMeals.responseBodyConverter(type, annotations, retrofit)
                else -> throw NotImplementedError()
            }
        }
        return null
    }

    companion object {
        fun create() = DynamicApiConverterFactory()

        fun providePlatformConnectionGsonConverter(): Gson {
            return GsonBuilder().registerTypeAdapter(
                PlatformConnectionResponse::class.java,
                PlatformDataConnectionDeserializer()
            ).create()
        }

        fun providePlatformMealsGsonConverter(): Gson {
            return GsonBuilder().registerTypeAdapter(
                PlatformMealsResponse::class.java,
                PlatformMealsDeserializer()
            ).create()
        }
    }
}

annotation class PlatformConnection
annotation class PlatformMeals