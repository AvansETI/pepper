package com.pepper.care.feedback.common.usecases

import com.pepper.care.core.services.platform.entities.PlatformFeedback
import com.pepper.care.feedback.repo.FeedbackRepository
import java.time.LocalDateTime

interface AddPatientFeedbackUseCase {
    suspend operator fun invoke(feedback: PlatformFeedback)
}

class AddPatientFeedbackUseCaseUsingRepository(
    private val repository: FeedbackRepository
) : AddPatientFeedbackUseCase {

    override suspend fun invoke(feedback: PlatformFeedback) {
//        repository.addTimestamp(timestamp, patientId, taskId)
    }
}