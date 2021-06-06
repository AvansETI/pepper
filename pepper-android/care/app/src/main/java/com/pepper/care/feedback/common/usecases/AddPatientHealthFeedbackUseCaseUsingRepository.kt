package com.pepper.care.feedback.common.usecases

import com.pepper.care.feedback.entities.FeedbackEntity
import com.pepper.care.feedback.repo.FeedbackRepository

interface AddPatientHealthFeedbackUseCase {
    suspend operator fun invoke(type: FeedbackEntity)
}

class AddPatientHealthFeedbackUseCaseUsingRepository(
    private val repository: FeedbackRepository
) : AddPatientHealthFeedbackUseCase {

    override suspend fun invoke(type: FeedbackEntity) {
        repository.addState(type)
    }
}