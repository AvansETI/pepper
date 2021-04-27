package com.pepper.care.common.api

import com.pepper.care.common.PlatformConnection
import com.pepper.care.common.PlatformMeals
import com.pepper.care.common.entities.PlatformConnectionResponse
import com.pepper.care.common.entities.PlatformMealsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface PlatformApi {
    @GET("connection/{id}")
    @PlatformConnection
    suspend fun getConnectionStatusById(
        @Path("id") deviceId: Int
    ): Response<PlatformConnectionResponse>

    @GET("/meals")
    @PlatformMeals
    suspend fun getAvailableMeals(): Response<List<PlatformMealsResponse>>
}