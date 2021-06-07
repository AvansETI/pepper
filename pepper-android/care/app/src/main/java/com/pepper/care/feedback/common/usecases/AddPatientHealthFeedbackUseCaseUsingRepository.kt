package com.pepper.care.feedback.common.usecases

import com.pepper.care.feedback.repo.FeedbackRepository

interface AddPatientHealthFeedbackUseCase {
    suspend operator fun invoke(number: Int, patientId: String, taskId: String)
}

class AddPatientHealthFeedbackUseCaseUsingRepository(
    private val repository: FeedbackRepository
) : AddPatientHealthFeedbackUseCase {

    override suspend fun invoke(number: Int, patientId: String, taskId: String) {
        repository.addState(number, patientId, taskId)
    }
}