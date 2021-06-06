package com.pepper.care.feedback.common.usecases

import com.pepper.care.feedback.repo.FeedbackRepository

interface AddPatientGivenHealthFeedbackUseCase {
    suspend operator fun invoke(message: String)
}

class AddPatientGivenHealthFeedbackUseCaseUsingRepository(
    private val repository: FeedbackRepository
) : AddPatientGivenHealthFeedbackUseCase {

    override suspend fun invoke(message: String) {
        repository.addExplanation(message)
    }
}