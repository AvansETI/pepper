package com.pepper.care.dialog.repo

import com.pepper.care.common.repo.AppPreferencesRepository
import com.pepper.care.core.services.platform.entities.PlatformMessageBuilder
import com.pepper.care.core.services.platform.entities.PlatformReminder
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime

interface ReminderRepository {
    suspend fun fetchReminders(): StateFlow<List<PlatformReminder>>
}

class ReminderRepositoryImpl(
    private val appPreferences: AppPreferencesRepository
) : ReminderRepository {

    override suspend fun fetchReminders(): StateFlow<List<PlatformReminder>> {
        val patientId = appPreferences.patientIdState.value

        appPreferences.updatePublishMessage(
            PlatformMessageBuilder.Builder()
                .person(PlatformMessageBuilder.Person.PATIENT)
                .personId(patientId)
                .task(PlatformMessageBuilder.Task.REMINDER_ID)
                .taskId("1")
                .build()
        )

        delay(1000)

        return appPreferences.remindersState
    }

    private fun getMockReminders(): ArrayList<PlatformReminder> {
        return ArrayList<PlatformReminder>(
            listOf(
                PlatformReminder(
                    "0",
                    "0",
                    "Om 16:00 heb je een afspraak staan met de verpleegkundige."
                )
            )
        )
    }
}