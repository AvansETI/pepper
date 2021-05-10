package com.pepper.care.feedback.common.usecases

import com.pepper.care.feedback.entities.FeedbackType
import com.pepper.care.feedback.repo.FeedbackRepository

interface AddPatientHealthFeedbackUseCase {
    suspend operator fun invoke(type: FeedbackType)
}

class AddPatientHealthFeedbackUseCaseUsingRepository(
    private val repository: FeedbackRepository
) : AddPatientHealthFeedbackUseCase {

    override suspend fun invoke(type: FeedbackType) {
        repository.addFeedbackType(type)
    }
}

