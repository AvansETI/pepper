package com.pepper.care.common.api

import com.pepper.care.common.PlatformMeals
import com.pepper.care.common.entities.PlatformMealsResponse
import retrofit2.Response
import retrofit2.http.GET

interface PlatformApi {
    @GET("zmk9m.json")
    @PlatformMeals
    suspend fun getAvailableMeals(): Response<List<PlatformMealsResponse>>
}