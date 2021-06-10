package com.pepper.care.dialog.common.usecases

import com.pepper.care.core.services.platform.entities.PlatformReminder
import com.pepper.care.dialog.repo.ReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface GetDailyRemindersUseCase {
    suspend operator fun invoke() : StateFlow<List<PlatformReminder>>
}

class GetDailyRemindersUseCaseUsingRepository(
    private val repository: ReminderRepository
) : GetDailyRemindersUseCase {

    override suspend fun invoke(): StateFlow<List<PlatformReminder>> {
        return repository.fetchReminders()
    }
}