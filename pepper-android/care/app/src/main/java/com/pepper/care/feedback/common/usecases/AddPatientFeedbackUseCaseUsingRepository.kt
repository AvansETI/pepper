package com.pepper.care.feedback.common.usecases

import com.pepper.care.feedback.repo.FeedbackRepository

interface AddPatientFeedbackUseCase {
    suspend operator fun invoke(status: Int, text: String)
}

class AddPatientFeedbackUseCaseUsingRepository(
    private val repository: FeedbackRepository
) : AddPatientFeedbackUseCase {

    override suspend fun invoke(status: Int, text: String) {
        repository.sendFeedback(status, text)
    }
}