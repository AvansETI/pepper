package com.pepper.care.common.repo

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.asLiveData
import com.pepper.care.core.services.platform.entities.PlatformMessageBuilder
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import org.joda.time.LocalDate


interface PatientRepository {
    suspend fun fetchId(name: String, birthDate: LocalDate) : StateFlow<String>
    suspend fun fetchName(patientId: String) : StateFlow<String>
    suspend fun fetchName() : StateFlow<String>
}

class PatientRepositoryImpl(
    private val appPreferences: AppPreferencesRepository
) : PatientRepository {

    override suspend fun fetchId(name: String, birthDate: LocalDate): StateFlow<String> {
        val days: Int = (birthDate.toDateTimeAtStartOfDay().millis / 1000.0 / 60.0 / 60.0 / 24.0).toInt() + 1

        appPreferences.updatePublishMessage(
            PlatformMessageBuilder.Builder()
                .person(PlatformMessageBuilder.Person.PATIENT)
                .task(PlatformMessageBuilder.Task.PATIENT_ID)
                .data("${days}%$name")
                .build()
        )

        delay(2000)

        return appPreferences.patientIdState
    }

    override suspend fun fetchName(patientId: String): StateFlow<String> {
        appPreferences.updatePublishMessage(
            PlatformMessageBuilder.Builder()
                .person(PlatformMessageBuilder.Person.PATIENT)
                .personId(patientId)
                .task(PlatformMessageBuilder.Task.PATIENT)
                .build()
        )

        delay(2000)

        return appPreferences.patientNameState
    }

    override suspend fun fetchName(): StateFlow<String> {
        return appPreferences.patientNameState
    }

}