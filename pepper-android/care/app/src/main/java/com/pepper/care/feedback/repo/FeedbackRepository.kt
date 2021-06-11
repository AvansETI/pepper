package com.pepper.care.feedback.repo

import com.pepper.care.common.repo.AppPreferencesRepository
import com.pepper.care.core.services.platform.entities.PlatformMessageBuilder
import kotlinx.coroutines.delay
import org.joda.time.Instant


interface FeedbackRepository {
    suspend fun sendFeedback(status: Int, text: String)
}

class FeedbackRepositoryImpl(
    private val appPreferences: AppPreferencesRepository
) : FeedbackRepository {

    override suspend fun sendFeedback(status: Int, text: String) {
        var taskId = "-2"
        appPreferences.updateFeedbackIdState(taskId)

        val patientId = appPreferences.patientIdState.value

        appPreferences.updatePublishMessage(
            PlatformMessageBuilder.Builder()
                .person(PlatformMessageBuilder.Person.PATIENT)
                .personId(patientId)
                .task(PlatformMessageBuilder.Task.FEEDBACK_STATUS)
                .taskId("-1")
                .data("$status")
                .build()
        )

        for (i in 0..100) {
            taskId = appPreferences.feedbackIdState.value

            if (taskId != "-2") {
                break
            }

            delay(20)
        }

        appPreferences.updatePublishMessage(
            PlatformMessageBuilder.Builder()
                .person(PlatformMessageBuilder.Person.PATIENT)
                .personId(patientId)
                .task(PlatformMessageBuilder.Task.FEEDBACK_EXPLANATION)
                .taskId(taskId)
                .data(text)
                .build()
        )

        appPreferences.updatePublishMessage(
            PlatformMessageBuilder.Builder()
                .person(PlatformMessageBuilder.Person.PATIENT)
                .personId(patientId)
                .task(PlatformMessageBuilder.Task.FEEDBACK_TIMESTAMP)
                .taskId(taskId)
                .data("${(Instant.now().millis / 1000.0).toInt()}")
                .build()
        )
    }

}