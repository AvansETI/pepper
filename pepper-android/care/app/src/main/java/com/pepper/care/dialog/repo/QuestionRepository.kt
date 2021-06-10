package com.pepper.care.dialog.repo

import com.pepper.care.common.repo.AppPreferencesRepository
import com.pepper.care.core.services.platform.entities.PlatformMessageBuilder
import com.pepper.care.core.services.platform.entities.PlatformQuestion
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import org.joda.time.Instant
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import java.lang.IndexOutOfBoundsException

interface QuestionRepository {
    suspend fun fetchQuestions() : StateFlow<List<PlatformQuestion>>
    suspend fun addExplanation(string: String)
}

class QuestionRepositoryImpl(
    private val appPreferences: AppPreferencesRepository
) : QuestionRepository {

    override suspend fun fetchQuestions(): StateFlow<List<PlatformQuestion>> {
        val patientId = appPreferences.patientIdState.value

        appPreferences.updatePublishMessage(
            PlatformMessageBuilder.Builder()
                .person(PlatformMessageBuilder.Person.PATIENT)
                .personId(patientId)
                .task(PlatformMessageBuilder.Task.QUESTION_ID)
                .taskId("1")
                .build()
        )

        delay(2000)

        return appPreferences.questionsState
    }

    private fun getMockQuestions(): ArrayList<PlatformQuestion> {
        return ArrayList<PlatformQuestion>(
            listOf(
                PlatformQuestion(
                    "0",
                    "0",
                    "Heb je nog pijn aan je linkerbeen?"
                )
            )
        )
    }

    override suspend fun addExplanation(string: String) {
        var taskId = "-2"
        appPreferences.updateAnswerIdState(taskId)

        val questionId = try {
            appPreferences.questionsState.value[0].id
        } catch (e: IndexOutOfBoundsException){
            null
        }
        
        val patientId = appPreferences.patientIdState.value

        if (questionId != null) {
            appPreferences.updatePublishMessage(
                PlatformMessageBuilder.Builder()
                    .person(PlatformMessageBuilder.Person.PATIENT)
                    .personId(patientId)
                    .task(PlatformMessageBuilder.Task.ANSWER_QUESTION_ID)
                    .taskId("-1")
                    .data(questionId)
                    .build()
            )

            for (i in 0..100) {
                taskId = appPreferences.answerIdState.value

                if (taskId != "-2") {
                    break
                }

                delay(20)
            }

            appPreferences.updatePublishMessage(
                PlatformMessageBuilder.Builder()
                    .person(PlatformMessageBuilder.Person.PATIENT)
                    .personId(patientId)
                    .task(PlatformMessageBuilder.Task.ANSWER_TEXT)
                    .taskId(taskId)
                    .data(string)
                    .build()
            )

            appPreferences.updatePublishMessage(
                PlatformMessageBuilder.Builder()
                    .person(PlatformMessageBuilder.Person.PATIENT)
                    .personId(patientId)
                    .task(PlatformMessageBuilder.Task.ANSWER_TIMESTAMP)
                    .taskId(taskId)
                    .data("${(Instant.now().millis / 1000.0).toInt()}")
                    .build()
            )
        }

    }
}