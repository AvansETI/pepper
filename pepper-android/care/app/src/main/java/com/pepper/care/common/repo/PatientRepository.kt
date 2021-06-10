package com.pepper.care.common.repo

import com.pepper.care.core.services.platform.entities.PlatformMessageBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import org.joda.time.LocalDate


interface PatientRepository {
    suspend fun fetchName(name: String, birthDate: LocalDate) : StateFlow<String>
    suspend fun fetchName() : StateFlow<String>
}

class PatientRepositoryImpl(
    private val appPreferences: AppPreferencesRepository
) : PatientRepository {

    override suspend fun fetchName(name: String, birthDate: LocalDate) : StateFlow<String> {
        appPreferences.updatePatientNameState("NONE")

        val days: Int = (birthDate.toDateTimeAtStartOfDay().millis / 1000.0 / 60.0 / 60.0 / 24.0).toInt() + 1

        appPreferences.updatePublishMessage(
            PlatformMessageBuilder.Builder()
                .person(PlatformMessageBuilder.Person.PATIENT)
                .task(PlatformMessageBuilder.Task.PATIENT_ID)
                .data("$days%$name")
                .build()
        )

        for (i in 0..100) {
            if (appPreferences.patientNameState.value != "NONE") {
                break
            }

            delay(20)
        }

        return appPreferences.patientNameState
    }

    override suspend fun fetchName(): StateFlow<String> {
        return appPreferences.patientNameState
    }

}