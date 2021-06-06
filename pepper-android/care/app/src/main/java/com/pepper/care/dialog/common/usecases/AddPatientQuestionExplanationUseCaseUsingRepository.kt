package com.pepper.care.dialog.common.usecases

import com.pepper.care.dialog.repo.QuestionRepository

interface AddPatientQuestionExplanationUseCase {
    suspend operator fun invoke(message: String)
}

class AddPatientQuestionExplanationUseCaseUsingRepository(
    private val repository: QuestionRepository
) : AddPatientQuestionExplanationUseCase {

    override suspend fun invoke(message: String) {
        return repository.addExplanation(message)
    }
}