package com.pepper.care.dialog.repo

import com.pepper.care.common.repo.AppPreferencesRepository
import com.pepper.care.core.services.platform.entities.PlatformMessageBuilder
import com.pepper.care.core.services.platform.entities.PlatformReminder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.joda.time.LocalDate

interface ReminderRepository {
    suspend fun fetchReminders(): Flow<List<PlatformReminder>>
}

class ReminderRepositoryImpl(
    private val appPreferences: AppPreferencesRepository
) : ReminderRepository {

    override suspend fun fetchReminders(): Flow<List<PlatformReminder>> {
        appPreferences.updatePublishMessage(
            PlatformMessageBuilder.Builder()
                .person(PlatformMessageBuilder.PersonType.PATIENT)
                .message(PlatformMessageBuilder.MessageType.FETCH_REMINDERS)
                .build()
                .format()
        )

        return flow { emit(getMockReminders()) }
    }

    private fun getMockReminders(): ArrayList<PlatformReminder> {
        return ArrayList<PlatformReminder>(
            listOf(
                PlatformReminder(
                    "0",
                    "0",
                    "Om 16:00 heb je een afspraak staan met de verpleegkundige.",
                    LocalDate.now()
                )
            )
        )
    }
}