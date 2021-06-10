package com.pepper.care.dialog.common.usecases

import com.pepper.care.core.services.platform.entities.PlatformQuestion
import com.pepper.care.dialog.repo.QuestionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface GetDailyQuestionsUseCase {
    suspend operator fun invoke() : StateFlow<List<PlatformQuestion>>
}

class GetDailyQuestionsUseCaseUsingRepository(
    private val repository: QuestionRepository
) : GetDailyQuestionsUseCase {

    override suspend fun invoke(): StateFlow<List<PlatformQuestion>> {
        return repository.fetchQuestions()
    }
}