package com.pepper.care.dialog.repo

import com.pepper.care.common.repo.AppPreferencesRepository
import com.pepper.care.core.services.platform.entities.PlatformMessageBuilder
import com.pepper.care.core.services.platform.entities.PlatformQuestion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime

interface QuestionRepository {
    suspend fun fetchQuestions() : Flow<List<PlatformQuestion>>
    suspend fun addExplanation(string: String)
}

class QuestionRepositoryImpl(
    private val appPreferences: AppPreferencesRepository
) : QuestionRepository {

    override suspend fun fetchQuestions(): Flow<List<PlatformQuestion>> {
//        appPreferences.updatePublishMessage(
//            PlatformMessageBuilder.Builder()
//                .message(PlatformMessageBuilder.MessageType.FETCH_QUESTIONS)
//                .build()
//                .format()
//        )

        return flow { emit(getMockQuestions()) }
    }

    private fun getMockQuestions(): ArrayList<PlatformQuestion> {
        return ArrayList<PlatformQuestion>(
            listOf(
                PlatformQuestion(
                    "0",
                    "0",
                    "Heb je nog pijn aan je linkerbeen?",
                    LocalDateTime.now()
                )
            )
        )
    }

    override suspend fun addExplanation(string: String) {
//        appPreferences.updatePublishMessage(
//            PlatformMessageBuilder.Builder()
//                .person(PlatformMessageBuilder.PersonType.PATIENT)
//                .message(PlatformMessageBuilder.MessageType.PUSH_QUESTION_EXPLANATION)
//                .data(string)
//                .build()
//                .format()
//        )
    }
}