package com.pepper.care.dialog.common.usecases

import com.pepper.care.core.services.platform.entities.PlatformReminder
import com.pepper.care.dialog.repo.ReminderRepository
import kotlinx.coroutines.flow.Flow

interface GetDailyRemindersUseCase {
    suspend operator fun invoke() : Flow<List<PlatformReminder>>
}

class GetDailyRemindersUseCaseUsingRepository(
    private val repository: ReminderRepository
) : GetDailyRemindersUseCase {

    override suspend fun invoke(): Flow<List<PlatformReminder>> {
        return repository.fetchReminders()
    }
}