package com.pepper.care.common.repo

import com.pepper.care.common.AppResult
import com.pepper.care.common.entities.PatientDetails

interface PlatformPatientDetailsRepository {
    suspend fun fetchDetails() : AppResult<PatientDetails>
}

class PlatformPatientDetailsRepositoryImpl : PlatformPatientDetailsRepository {

    override suspend fun fetchDetails(): AppResult<PatientDetails> {
        return AppResult.Success(
            PatientDetails(
                "Peter de Jonge"
            )
        )
    }
}