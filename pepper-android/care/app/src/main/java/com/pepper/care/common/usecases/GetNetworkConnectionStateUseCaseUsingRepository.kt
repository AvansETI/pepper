package com.pepper.care.common.usecases

import androidx.lifecycle.MutableLiveData
import com.pepper.care.common.repo.PlatformConnectionRepository
import java.io.Serializable

interface GetNetworkConnectionStateUseCase {

    enum class ConnectionState: Serializable {
        NO_INTERNET_CONNECTION, CONNECTION_VERIFIED
    }

    suspend operator fun invoke(
        connectionStateResult: MutableLiveData<ConnectionState>,
        deviceId: Int
    )
}

class GetNetworkConnectionStateUseCaseUsingRepository(
    private val platformConnectionRepository: PlatformConnectionRepository
) : GetNetworkConnectionStateUseCase {

    override suspend fun invoke(
        connectionStateResult: MutableLiveData<GetNetworkConnectionStateUseCase.ConnectionState>,
        deviceId: Int
    ) {
        platformConnectionRepository.fetchPlatformConnection(connectionStateResult, deviceId)
    }
}