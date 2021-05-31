package com.pepper.care.common.repo

import com.pepper.care.common.AppResult
import org.joda.time.DateTime

interface PatientRepository {
    suspend fun fetchName() : AppResult<String>
    suspend fun fetchBirthDate() : AppResult<DateTime>
    suspend fun fetchAllergies(): AppResult<String>
}

class PatientRepositoryImpl : PatientRepository {

    override suspend fun fetchName(): AppResult<String> {
        return AppResult.Success("Peter")
    }

    override suspend fun fetchBirthDate(): AppResult<DateTime> {
        return AppResult.Success(DateTime.now())
    }

    override suspend fun fetchAllergies(): AppResult<String> {
        return AppResult.Success("Melk")
    }
}