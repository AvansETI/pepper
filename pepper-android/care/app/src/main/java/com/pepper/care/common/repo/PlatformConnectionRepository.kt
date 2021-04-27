package com.pepper.care.common.repo

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.pepper.care.common.api.PlatformApi
import com.pepper.care.common.usecases.GetNetworkConnectionStateUseCase

interface PlatformConnectionRepository {

    suspend fun fetchPlatformConnection(
        response: MutableLiveData<GetNetworkConnectionStateUseCase.ConnectionState>,
        deviceId: Int
    )

}

class PlatformConnectionRepositoryImpl(
    private val api: PlatformApi
) : PlatformConnectionRepository {

    override suspend fun fetchPlatformConnection(
        response: MutableLiveData<GetNetworkConnectionStateUseCase.ConnectionState>,
        deviceId: Int
    ) {
        val request = api.getConnectionStatusById(
            deviceId
        )

        if (request.isSuccessful){
            request.body()?.let {
                response.value = when (it.connected){
                    true -> GetNetworkConnectionStateUseCase.ConnectionState.CONNECTION_VERIFIED
                    false -> GetNetworkConnectionStateUseCase.ConnectionState.NO_INTERNET_CONNECTION
                }
            }
        } else {
            Log.e("PlatformConnection", request.errorBody().toString())
        }
    }

}