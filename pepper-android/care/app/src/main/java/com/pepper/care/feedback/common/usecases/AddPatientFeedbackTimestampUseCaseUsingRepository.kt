package com.pepper.care.feedback.common.usecases

import com.pepper.care.feedback.repo.FeedbackRepository
import java.time.LocalDateTime

interface AddPatientFeedbackTimestampUseCase {
    suspend operator fun invoke(timestamp: LocalDateTime, patientId: String, taskId: String)
}

class AddPatientFeedbackTimestampUseCaseUsingRepository(
    private val repository: FeedbackRepository
) : AddPatientFeedbackTimestampUseCase {

    override suspend fun invoke(timestamp: LocalDateTime, patientId: String, taskId: String) {
        repository.addTimestamp(timestamp, patientId, taskId)
    }
}