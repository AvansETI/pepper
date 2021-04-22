package com.pepper.care.common.api

import com.pepper.care.common.PlatformConnection
import com.pepper.care.common.entities.PlatformConnectionResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface PlatformConnectionApi {
    @GET("connection/{id}")
    @PlatformConnection
    suspend fun getConnectionStatusById(
        @Path("id") deviceId: Int
    ): Response<PlatformConnectionResponse>
}