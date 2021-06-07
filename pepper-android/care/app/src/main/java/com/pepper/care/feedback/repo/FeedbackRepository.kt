package com.pepper.care.feedback.repo

import android.os.Build
import androidx.annotation.RequiresApi
import com.pepper.care.common.repo.AppPreferencesRepository
import com.pepper.care.core.services.platform.entities.PlatformMessageBuilder
import com.pepper.care.feedback.entities.FeedbackEntity
import java.time.LocalDateTime
import java.time.ZoneOffset

interface FeedbackRepository {
    suspend fun addState(number: Int, patientId: String, taskId: String)
    suspend fun addExplanation(string: String, patientId: String, taskId: String)
    suspend fun addTimestamp(timestamp: LocalDateTime, patientId: String, taskId: String)
}

class FeedbackRepositoryImpl(
    private val appPreferences: AppPreferencesRepository
) : FeedbackRepository {

    override suspend fun addState(number: Int, patientId: String, taskId: String) {
        appPreferences.updatePublishMessage(
            PlatformMessageBuilder.Builder()
                .person(PlatformMessageBuilder.Person.PATIENT)
                .personId(patientId)
                .task(PlatformMessageBuilder.Task.FEEDBACK_STATUS)
                .taskId(taskId)
                .data("$number")
                .build()
        )
    }

    override suspend fun addExplanation(string: String, patientId: String, taskId: String) {
        appPreferences.updatePublishMessage(
            PlatformMessageBuilder.Builder()
                .person(PlatformMessageBuilder.Person.PATIENT)
                .personId(patientId)
                .task(PlatformMessageBuilder.Task.FEEDBACK_EXPLANATION)
                .taskId(taskId)
                .data(string)
                .build()
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun addTimestamp(timestamp: LocalDateTime, patientId: String, taskId: String) {
        appPreferences.updatePublishMessage(
            PlatformMessageBuilder.Builder()
                .person(PlatformMessageBuilder.Person.PATIENT)
                .personId(patientId)
                .task(PlatformMessageBuilder.Task.FEEDBACK_TIMESTAMP)
                .taskId(taskId)
                .data("${timestamp.toEpochSecond(ZoneOffset.UTC)}")
                .build()
        )
    }
}