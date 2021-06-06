package com.pepper.care.common.repo

import com.pepper.care.core.services.platform.entities.PlatformMessageBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface PatientRepository {
    suspend fun fetchName() : Flow<String>
}

class PatientRepositoryImpl(
    private val appPreferences: AppPreferencesRepository
) : PatientRepository {

    override suspend fun fetchName(): Flow<String> {
        val tempBday = "10-12-2000"
        val tempName = "Miquel"

        appPreferences.updatePublishMessage(PlatformMessageBuilder.Builder()
            .bot("1")
            .person(PlatformMessageBuilder.PersonType.PATIENT)
            .identification("1")
            .message(PlatformMessageBuilder.MessageType.FETCH_NAME)
            .data("$tempBday^$tempName")
            .build()
            .format()
        )

        return flow {
            emit(tempName)
        }
    }
}